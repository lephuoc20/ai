package com.phuoclv.ai.document.writer;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentWriterService {
    void write(List<Document> document);
}
