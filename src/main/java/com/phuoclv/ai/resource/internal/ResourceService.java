package com.phuoclv.ai.resource.internal;

import com.phuoclv.ai.resource.SubmitResourceRequest;

public interface ResourceService {
    ResourceEntity handleNewResourceSubmit(SubmitResourceRequest submitResourceRequest);
}
