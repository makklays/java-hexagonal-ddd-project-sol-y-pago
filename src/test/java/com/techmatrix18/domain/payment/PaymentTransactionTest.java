package com.techmatrix18.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PaymentTransaction class, focusing on routing logic based on transaction amount and country code.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 25.07.2026
 */
class PaymentTransactionTest {

    private final UUID merchantId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();

    @Test
    @DisplayName("Should route via BIZUM when amount is under 50 EUR and country is Spain (ES)")
    void shouldRouteViaBizumForLowAmountInSpain() {
        // Given
        PaymentTransaction transaction = PaymentTransaction.createNew(
                merchantId, customerId, new BigDecimal("25.00"), "EUR", "ES", PaymentMethod.CARD
        );

        // When
        transaction.assignRoutingProvider();

        // Then
        assertEquals("BIZUM", transaction.getProviderName());
        assertEquals(PaymentStatus.CREATED, transaction.getStatus());
    }

    @Test
    @DisplayName("Should route via STRIPE when amount is 50 EUR or more even if country is Spain (ES)")
    void shouldRouteViaStripeForHighAmountInSpain() {
        // Given
        PaymentTransaction transaction = PaymentTransaction.createNew(
                merchantId, customerId, new BigDecimal("100.00"), "EUR", "ES", PaymentMethod.CARD
        );

        // When
        transaction.assignRoutingProvider();

        // Then
        assertEquals("STRIPE", transaction.getProviderName());
    }

    @Test
    @DisplayName("Should route via STRIPE for low amount if country is not Spain")
    void shouldRouteViaStripeForNonSpanishCustomer() {
        // Given
        PaymentTransaction transaction = PaymentTransaction.createNew(
                merchantId, customerId, new BigDecimal("25.00"), "EUR", "UA", PaymentMethod.CARD
        );

        // When
        transaction.assignRoutingProvider();

        // Then
        assertEquals("STRIPE", transaction.getProviderName());
    }

    @Test
    @DisplayName("Should force BIZUM provider if payment method is explicitly set to BIZUM")
    void shouldForceBizumIfMethodIsBizum() {
        // Given
        PaymentTransaction transaction = PaymentTransaction.createNew(
                merchantId, customerId, new BigDecimal("150.00"), "EUR", "ES", PaymentMethod.BIZUM
        );

        // When
        transaction.assignRoutingProvider();

        // Then
        assertEquals("BIZUM", transaction.getProviderName());
    }

    @Test
    @DisplayName("Should throw exception when creating transaction with negative amount")
    void shouldThrowExceptionForInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () ->
                PaymentTransaction.createNew(merchantId, customerId, new BigDecimal("-10.00"), "EUR", "ES", PaymentMethod.CARD)
        );
    }
}

