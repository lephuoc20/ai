package com.phuoclv.ai.etl;

import com.phuoclv.ai.etl.reader.DocumentReaderService;
import com.phuoclv.ai.etl.transform.DocumentTransform;
import com.phuoclv.ai.etl.transform.DocumentTransformService;
import com.phuoclv.ai.etl.writer.DocumentWriterService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
class DocumentETLManagement implements DocumentEtlService {
    private final ApplicationEventPublisher eventPublisher;
    private final DocumentReaderService documentReaderService;
    private final DocumentTransformService documentTransformService;
    private final DocumentWriterService documentWriterService;

    public DocumentETLManagement(ApplicationEventPublisher eventPublisher,
                                 DocumentReaderService documentReaderService,
                                 DocumentTransformService documentTransformService,
                                 DocumentWriterService documentWriterService) {
        this.eventPublisher = eventPublisher;
        this.documentReaderService = documentReaderService;
        this.documentTransformService = documentTransformService;
        this.documentWriterService = documentWriterService;
    }

    @Override
    @TransactionalEventListener
    public void handleResourceSubmitted(ResourceSubmitted resourceSubmitted) {
        Resource fileResource = new FileSystemResource(Paths.get(resourceSubmitted.filePath()));
        var documents = this.documentReaderService.read(fileResource);
        var docStream = documents.stream().map(document -> new DocumentTransform(document,
                resourceSubmitted.id(),
                resourceSubmitted.creatorName(),
                resourceSubmitted.creatorId(),
                resourceSubmitted.createdAt(),
                resourceSubmitted.tags()));
        var transformedDocuments = documentTransformService.transform(docStream).collect(Collectors.toList());
        documentWriterService.write(transformedDocuments);
        this.eventPublisher.publishEvent(new ResourceProcessedEvent(resourceSubmitted.id(), resourceSubmitted.creatorId(), resourceSubmitted.creatorName()));
    }
}
