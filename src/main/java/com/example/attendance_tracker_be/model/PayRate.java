package com.example.attendance_tracker_be.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Embedded inside User — not its own Mongo collection.
 * Matches Phase 2 feature #18 (different pay rates: regular/overtime/weekend/holiday),
 * but the field lives on User from day one since the admin payrate endpoint needs
 * somewhere to write to.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRate {

    @Builder.Default
    private BigDecimal regular = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal overtime = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal weekend = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal holiday = BigDecimal.ZERO;
}
