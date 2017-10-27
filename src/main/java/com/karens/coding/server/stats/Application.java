package com.karens.coding.server.stats;


import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Application extends WebMvcConfigurerAdapter {
	/** UTF-8 Character set name */
	private static final String UTF_8 = "UTF-8";
	
	/** Logger */
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(Application.class);
	
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
	
	
	/**
	 * Creates and gets the LocaleResolver
	 * @return LocaleResolver
	 */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault());
        //logger.debug("Setting locale to: " + Locale.getDefault());
        return slr;
    }
 
    /**
     * Creates and gets the LocaleChangeInterceptor
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        //logger.debug("localeChangeInterceptor called "  + Locale.getDefault());
        return lci;
    }
 
    /**
     * Adds Spring MVC lifecycle interceptors for pre- and post-processing of controller method invocations. 
     * Interceptors can be registered to apply to all requests or be limited to a subset of URL patterns.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    
    
    /**
     * Creates and gets the FilterRegistrationBean
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(UTF_8);
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }
    
}

