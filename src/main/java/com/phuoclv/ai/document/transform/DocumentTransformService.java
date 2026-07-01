package com.phuoclv.ai.document.transform;

import org.springframework.ai.document.Document;

import java.util.stream.Stream;

public interface DocumentTransformService {
    Stream<Document> transform(Stream<DocumentTransform> documentTransforms);
}
