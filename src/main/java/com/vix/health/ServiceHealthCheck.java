package com.vix.health;

import com.codahale.metrics.health.HealthCheck;
import org.springframework.stereotype.Component;

import javax.ws.rs.ProcessingException;

/**
 * Health check for a service which this savedata service connects to.
 */
@Component
public class ServiceHealthCheck extends HealthCheck {

    static final String UNHEALTHY_MSG = "Connection to the service is not available.";
    private final ServicePlatformChannel service;
    private static final ExpLogger LOGGER = new ExpLogger(ServicePlatformChannel.class);

    /**
     * Health check for  service (or its facade) which this service can connect to.
     *
     * @param service Manages interaction with the  service
     */
    public ServicePlatformChannel(ServicePlatformChannel service) {
        this.service = service;
    }

    @Override
    protected Result check() {
        boolean serviceResponse;
        try {
            serviceResponse = service.ping();
        } catch (ProcessingException e) {
            LOGGER.error("Error pinging the service.", e);
            return Result.unhealthy(UNHEALTHY_MSG);
        }
        if (serviceResponse) {
            return Result.healthy();
        } else {
            LOGGER.error("Non 200 response received from the service ping.");
            return Result.unhealthy(UNHEALTHY_MSG);
        }
    }
}
