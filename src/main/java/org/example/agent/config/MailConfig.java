package org.example.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Profile("home")
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", mailProperties.getProtocol());
        props.put("mail.smtp.auth", mailProperties.getAuth());
        props.put("mail.smtp.starttls.enable", mailProperties.getEnable()); // TLS
        props.put("mail.debug", mailProperties.getDebug());

        return mailSender;
    }
}
