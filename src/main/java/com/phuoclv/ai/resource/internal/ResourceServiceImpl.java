package com.phuoclv.ai.resource.internal;

import com.phuoclv.ai.resource.SubmitResourceRequest;
import com.phuoclv.ai.etl.ResourceProcessedEvent;
import com.phuoclv.ai.etl.ResourceSubmitted;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
class ResourceServiceImpl implements ResourceService {
    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final ApplicationEventPublisher eventPublisher;
    private final JdbcClient jdbcClient;

    private Collection<Object> extractTags(ResultSet rs, String columnName) throws SQLException {
        Array pgArray = rs.getArray(columnName);
        if (pgArray == null) {
            return Collections.emptyList();
        }
        String[] arrayData = (String[]) pgArray.getArray();
        return Arrays.stream(arrayData).collect(Collectors.toList());
    }

    private final RowMapper<ResourceEntity> resourceEntityRowMapper = (rs, rowNumber) -> {
        return new ResourceEntity(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("file_name"),
                rs.getString("creator_id"),
                rs.getString("creator_name"),
                extractTags(rs, "tags"),
                rs.getObject("created_at", OffsetDateTime.class)
        );
    };
    private final String fileStoragePath;

    private void validateFileStoragePath(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalStateException("Startup failed: Storage path does not exist -> " + path);
        }
        if (!Files.isDirectory(path)) {
            throw new IllegalStateException("Startup failed: Path is a file, not a directory -> " + path);
        }
        if (!Files.isWritable(path)) {
            throw new IllegalStateException("Startup failed: No write permissions for -> " + path);
        }
    }

    @PostConstruct
    public void init() {
        var path = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new IllegalStateException("Could not create storage directory during bean post-construction", e);
            }
        }
        validateFileStoragePath(path);
    }

    public ResourceServiceImpl(ApplicationEventPublisher eventPublisher,
                               JdbcClient jdbcClient, @Value("${service.resource.path}") String fileStoragePath) {
        this.eventPublisher = eventPublisher;
        this.jdbcClient = jdbcClient;
        this.fileStoragePath = fileStoragePath;
    }

    private Optional<ResourceEntity> findById(Long id) {
        var sql = """
                select id, name, file_name, tags, creator_id, creator_name, created_at from resources where id = :id;
                """;
        return jdbcClient.sql(sql)
                .param("id", id)
                .query(resourceEntityRowMapper)
                .optional();
    }

    private String storeFileToLocal(Long id, MultipartFile file) {
        Path folderPath = Paths.get(this.fileStoragePath, LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        Path finalDestination = null;
        try {
            if (Files.exists(folderPath)) {
                log.info("folder is already existed {}", folderPath);
            } else {
                Files.createDirectories(folderPath);
            }
            finalDestination = folderPath.resolve(String.format("%d-%s", id, Optional.ofNullable(file.getOriginalFilename()).orElse("")));
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, finalDestination, StandardCopyOption.REPLACE_EXISTING);
            }
            return finalDestination.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("IOException while writing file {}", finalDestination);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public ResourceEntity handleNewResourceSubmit(SubmitResourceRequest submitResourceRequest) {
        String sql = """
                insert into resources (name, file_name, status , tags, creator_id, creator_name) 
                              values (:name, :fileName, :status, :tags, :creatorId, :creatorName);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param("name", submitResourceRequest.name())
                .param("status", ResourceStatus.SUBMITTED.name())
                .param("fileName", submitResourceRequest.file().getOriginalFilename())
                .param("tags", submitResourceRequest.tags().toArray(new String[0]))
                .param("creatorId", "1")
                .param("creatorName", "TEST")
                .update(keyHolder, "id");
        Number key = keyHolder.getKey();
        if (key == null) {
            log.error("key is null");
            throw new RuntimeException("Key is null, save failed");
        }
        long orderId = key.longValue();
        Optional<ResourceEntity> resourceEntityOptional = findById(orderId);
        if (resourceEntityOptional.isEmpty()) {
            log.error("Could not save resource");
            throw new RuntimeException("resource not found with id " + orderId);
        }
        var pathUrl = this.storeFileToLocal(orderId, submitResourceRequest.file());
        ResourceEntity resourceEntity = resourceEntityOptional.get();
        eventPublisher.publishEvent(from(resourceEntity, pathUrl));
        return resourceEntity;
    }

    @TransactionalEventListener
    void handleResourceProcessed(ResourceProcessedEvent event) {
        String sql = """
                update resource set status = :status where id = :id;
                """;
        var updatedRows = jdbcClient.sql(sql)
                .param("status", ResourceStatus.PROCESSED.name())
                .param("id", event.resourceId())
                .update();
        if (updatedRows != 1) {
            log.warn("Failed to update resource status as processed {}", event);
        }
    }

    private static ResourceSubmitted from(ResourceEntity resourceEntity, String pathUrl) {
        return new ResourceSubmitted(resourceEntity.id(),
                pathUrl, resourceEntity.creatorName(), resourceEntity.creatorId(), resourceEntity.createdAt(), resourceEntity.tags());
    }
}
