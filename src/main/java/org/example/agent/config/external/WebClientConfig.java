package org.example.agent.config.external;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient(ExternalApiProperties externalApiProperties) {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                // maxInMemorySize 50M
//                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
                // maxInMemorySize 무제한
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(writer -> ((LoggingCodecSupport) writer).setEnableLoggingRequestDetails(true));

        //                            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("## WEBCLIENT Request Header ## {} : {}", name, value)));
        //                            clientRequest.cookies().forEach((name, values) -> values.forEach(value -> log.debug("## WEBCLIENT Request Cookie ## {} : {}", name, value)));
        //                            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("## WEBCLIENT Response Header ## {} : {}", name, value)));
        //                            clientResponse.cookies().forEach((name, values) -> values.forEach(value -> log.debug("## WEBCLIENT Response Cookie ## {} : {}", name, value)));
        return WebClient.builder()
                .baseUrl(externalApiProperties.getUrl())
                .defaultHeader(externalApiProperties.getKey(), externalApiProperties.getValue())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create()
                                        // 이중화
                                        .resolver(DefaultAddressResolverGroup.INSTANCE)
//                                        .secure(
//                                                ThrowingConsumer.unchecked(
//                                                        sslContextSpec -> sslContextSpec.sslContext(
//                                                                SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
//                                                        )
//                                                )
//                                        )
                                        // Connection Timeout 120s
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120_000)
                                        .doOnConnected(conn -> conn
                                                // Read Timeout 180s
                                                .addHandlerLast(new ReadTimeoutHandler(180))
                                                // Write Timeout 180s
                                                .addHandlerLast(new WriteTimeoutHandler(180))
                                        )
//                                        .wiretap(true)
                                        // reactor-netty 버전업 해야 동작함
                                        .wiretap("reactor.netty.http.client.HttpClient",
                                                LogLevel.INFO,
                                                AdvancedByteBufFormat.TEXTUAL)
                        )
                )
                .exchangeStrategies(exchangeStrategies)
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        Mono::just
                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        Mono::just
                ))
                .build();
    }
}
