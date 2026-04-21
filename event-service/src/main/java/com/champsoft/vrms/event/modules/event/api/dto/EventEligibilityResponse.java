package com.champsoft.vrms.event.modules.event.api.dto;

public record EventEligibilityResponse(
        String eventId,
        boolean eligible
) {}