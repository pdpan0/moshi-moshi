package br.com.pdpano.server;

import org.springframework.messaging.rsocket.RSocketRequester;

public record ClientRequester(String client, RSocketRequester requester) {}