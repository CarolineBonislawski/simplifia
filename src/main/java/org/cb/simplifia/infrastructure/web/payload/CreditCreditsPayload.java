package org.cb.simplifia.infrastructure.web.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreditCreditsPayload(
    @NotBlank String bankId,
    @NotBlank String orderId,
    @Min(1) int amount
) {}
