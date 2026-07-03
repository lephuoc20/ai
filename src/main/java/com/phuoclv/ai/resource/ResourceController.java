package com.phuoclv.ai.resource;

import com.phuoclv.ai.resource.internal.ResourceEntity;
import com.phuoclv.ai.resource.internal.ResourceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResourceEntity addResource(@Valid @ModelAttribute SubmitResourceRequest resourceRequest) {
        return resourceService.handleNewResourceSubmit(resourceRequest);
    }
}
