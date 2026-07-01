package com.phuoclv.ai.document.transform.internal;

import com.phuoclv.ai.document.transform.DocumentTransform;
import com.phuoclv.ai.document.transform.DocumentTransformService;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
class DocumentTransformerService implements DocumentTransformService {
    private final TokenTextSplitter tokenTextSplitter;

    DocumentTransformerService(TokenTextSplitter tokenTextSplitter) {
        this.tokenTextSplitter = tokenTextSplitter;
    }

    private Stream<Document> transform(DocumentTransform documentTransform) {
        Document document = documentTransform.document();
        var metaData = document.getMetadata();
        metaData.put("processed_timestamp", System.currentTimeMillis());
        metaData.put("creator_id", documentTransform.creatorId());
        metaData.put("creator_name", documentTransform.creatorName());
        metaData.put("requested_at", documentTransform.createdAt());
        return tokenTextSplitter.split(document).stream();
    }

    @Override
    public Stream<Document> transform(Stream<DocumentTransform> documentTransformStream) {
        return documentTransformStream
                .flatMap(this::transform);
    }
}
