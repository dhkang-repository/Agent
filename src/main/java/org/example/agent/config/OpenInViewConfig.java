package org.example.agent.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;


@Configuration
public class OpenInViewConfig {
    // Open-in-view 비활성화: Spring이 관리하는 OSIV 필터를 비활성화
    @Bean
    public FilterRegistrationBean<OpenEntityManagerInViewFilter> disableOpenEntityManagerInViewFilter(PersistenceProperties properties) {
        FilterRegistrationBean<OpenEntityManagerInViewFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new OpenEntityManagerInViewFilter());
        filterRegistrationBean.setEnabled(properties.isOpenInView()); // 필터 비활성화
        return filterRegistrationBean;
    }
}
