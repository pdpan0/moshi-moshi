package br.com.pdpano.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.messaging.rsocket.RSocketStrategies;

@Configuration
public class ClientConfiguration {
    @Bean
    public RSocketStrategies configureRSocketStrategies() {
        return RSocketStrategies.builder().decoders(decoders -> decoders.add(new Jackson2JsonDecoder())).build();
    }
}
