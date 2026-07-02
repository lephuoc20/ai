package com.phuoclv.ai.etl.writer;

import org.springframework.ai.document.Document;

import java.util.List;

public interface DocumentWriterService {
    void write(List<Document> document);
}
