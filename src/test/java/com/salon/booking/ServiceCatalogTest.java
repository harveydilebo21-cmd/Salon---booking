package com.salon.booking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ServiceCatalogTest {

    @Test
    void everyServiceHasAPositiveDuration() {
        assertThat(ServiceCatalog.names()).isNotEmpty();
        for (String name : ServiceCatalog.names()) {
            assertThat(ServiceCatalog.minutesFor(name)).isPositive();
        }
    }

    @Test
    void unknownServicesFallBackToOneHour() {
        assertThat(ServiceCatalog.minutesFor("Not a real service")).isEqualTo(60);
    }

    @Test
    void maxMinutesIsAtLeastAsLongAsEveryService() {
        for (String name : ServiceCatalog.names()) {
            assertThat(ServiceCatalog.maxMinutes()).isGreaterThanOrEqualTo(ServiceCatalog.minutesFor(name));
        }
    }
}
