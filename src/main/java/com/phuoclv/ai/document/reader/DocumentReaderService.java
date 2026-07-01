package com.phuoclv.ai.document.reader;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentReaderService {
    List<Document> read(Resource resource);
}
