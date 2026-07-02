package com.phuoclv.ai.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record SubmitResourceRequest(@NotBlank(message = "name is required") String name, Set<String> tags,
                                    @NotNull(message = "file is required") MultipartFile file) {
}
