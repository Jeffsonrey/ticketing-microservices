package com.champsoft.vrms.ticketordermanagement.modules.ticketordermanagement.web;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {}

