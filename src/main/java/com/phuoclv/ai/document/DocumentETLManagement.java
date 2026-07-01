package com.phuoclv.ai.document;

import com.phuoclv.ai.document.reader.DocumentReaderService;
import com.phuoclv.ai.document.transform.DocumentTransform;
import com.phuoclv.ai.document.transform.DocumentTransformService;
import com.phuoclv.ai.document.writer.DocumentWriterService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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
    public void handleResourceSubmitted(ResourceSubmitted resourceSubmitted) {
        var documents = this.documentReaderService.read(resourceSubmitted.resource());
        var docStream = documents.stream().map(document -> new DocumentTransform(document,
                resourceSubmitted.id(),
                resourceSubmitted.creatorName(),
                resourceSubmitted.creatorId(),
                resourceSubmitted.createdAt(),
                resourceSubmitted.tags()));
        var transformedDocuments = documentTransformService.transform(docStream).collect(Collectors.toList());
        documentWriterService.write(transformedDocuments);
        this.eventPublisher.publishEvent(new ResourceStored(resourceSubmitted.id(), resourceSubmitted.creatorId(), resourceSubmitted.creatorName()));
    }
}
