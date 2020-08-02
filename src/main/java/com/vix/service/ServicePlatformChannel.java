package com.vix.service;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Class that interacts with the service and the platform related.
 */
public class ServicePlatformChannel {
    private static final ExpLogger LOGGER = new ExpLogger(ServicePlatformChannel.class);
    private static final String PATH_PLATFORM_PING = "/platform/ping";
    private final String serviceUrl;
    private final Client client;
    private final int platformPort;
    private static final String SERVICE_NAME = "service";
    private static final String PING_SERVICE_NAME = "ping";

    /**
     * Handles the interaction with the service
     * @param serviceUrl   service url.
     * @param client       the channel for interacting with the service.
     * @param platformPort the platform port
     */
    public ServicePlatformChannel(String serviceUrl, Client client, int platformPort) {
        this.platformPort = platformPort;
        this.serviceUrl = serviceUrl;
        this.client = client;
    }

    /**
     * Checks that the service is reachable.
     * @return The response from the service if it is reachable.
     */
    @Timed(name = "healthcheck")
    public boolean ping() {
        Response response = serviceUnavailableMapper(() -> client.target(UriBuilder.fromUri(serviceUrl)
                        .port(platformPort)
                        .path(PATH_PLATFORM_PING))
                        .request()
                        .get(),
                SERVICE_NAME, PING_SERVICE_NAME);

        Response.StatusType statusInfo = response.getStatusInfo();
        SaveDataService.create(statusInfo)
        boolean success = isSuccess(statusInfo);
        if (success) {
            LOGGER.debug("The service ping responded with status {}", response.getStatus());
        } else {
            LOGGER.warn("The service ping responded with status {}", response.getStatus());
        }
        return success;
    }

    private static boolean isSuccess(Response.StatusType serviceStatusType) {
        return Response.Status.Family.SUCCESSFUL == serviceStatusType.getFamily();
    }


}
