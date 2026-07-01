package com.phuoclv.ai.document.writer.internal;

import com.phuoclv.ai.document.writer.DocumentWriterService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentWriterManagement implements DocumentWriterService {
    private final VectorStore vectorStore;

    public DocumentWriterManagement(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void write(List<Document> documents) {
        this.vectorStore.write(documents);
    }
}
