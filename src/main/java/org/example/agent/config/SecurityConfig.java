package org.example.agent.config;

import lombok.RequiredArgsConstructor;
import org.example.agent.global.filter.CorsLoggingFilter;
import org.example.agent.global.filter.HttpLoggingFilter;
import org.example.agent.global.filter.JwtAuthenticationFilter;
import org.example.agent.global.security.CustomAccessDeniedHandler;
import org.example.agent.global.security.CustomAuthenticationEntryPoint;
import org.example.agent.global.security.SecurityUserDetailsService;
import org.example.agent.global.security.oauth.CustomOAuth2UserService;
import org.example.agent.global.security.oauth.OAuth2LoginFailureHandler;
import org.example.agent.global.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.example.agent.global.constrant.GlobalConst.BASE_URL;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   SecurityUserDetailsService userDetailsService,
                                                   CustomAuthenticationEntryPoint authenticationEntryPoint,
                                                   CustomAccessDeniedHandler accessDeniedHandler,
                                                   HttpLoggingFilter httpLoggingFilter,
                                                   CorsLoggingFilter corsLoggingFilter,
                                                   CustomOAuth2UserService customOAuth2UserService,
                                                   OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                                                   OAuth2LoginFailureHandler oAuth2LoginFailureHandler) throws Exception {
        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        httpSecurity.userDetailsService(userDetailsService);

        httpSecurity.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint) // 401 처리
                .accessDeniedHandler(accessDeniedHandler) // 403 처리
        );

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(httpLoggingFilter, CorsFilter.class);
        httpSecurity.addFilterBefore(corsLoggingFilter, CorsFilter.class);

        httpSecurity.authorizeHttpRequests(a ->
                        a.requestMatchers(
                                        BASE_URL + "/auth/login" ,
                                        BASE_URL + "/auth/refresh" ,
                                        BASE_URL + "/user",
//                                        BASE_URL + "/geo/*" ,
                                        "/oauth2/**",
                                        "/login/oauth2/**",
                                        "/login/oauth2/code/",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"

                                )
                                .permitAll()
                                .anyRequest().authenticated()
        );

        httpSecurity.oauth2Login(oauth -> oauth
                .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler)
        );

        return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(Arrays.stream(HttpMethod.values()).map(HttpMethod::name).toList());
        config.setAllowedOriginPatterns(List.of(
                "http://10.10.83.27:*",
                "http://10.10.83.25:*",
                "http://10.10.83.144",
                "http://10.10.83.144/",
                "http://54.180.11.49:*",
                "http://localhost:*",
                "http://127.0.0.1:*"
        )); // 허용할 origin
        config.setExposedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
