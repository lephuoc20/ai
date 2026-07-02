package com.phuoclv.ai.api.internal;

import com.phuoclv.ai.api.SubmitResourceRequest;

public interface ResourceService {
    ResourceEntity handleNewResourceSubmit(SubmitResourceRequest submitResourceRequest);
}
