package com.karens.coding.server.stats.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.hot.reloading.HotReloadingExtension;
import org.jtwig.spring.JtwigViewResolver;
import org.jtwig.spring.boot.config.JtwigViewResolverConfigurer;
import org.jtwig.translate.spring.SpringTranslateExtension;
import org.jtwig.translate.spring.SpringTranslateExtensionConfiguration;
import org.jtwig.web.servlet.JtwigRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;



@Configuration("JTwigConfig")
public class JTwigConfig implements JtwigViewResolverConfigurer {
	/** Logger */
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(JTwigConfig.class);
	
	@Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;

	@Override
	public void configure(JtwigViewResolver viewResolver) {
		
		viewResolver.setSuffix(".twig");
        viewResolver.setRenderer(new JtwigRenderer(EnvironmentConfigurationBuilder
                .configuration()
                .resources().withDefaultInputCharset(Charset.forName(StandardCharsets.UTF_8.name())).and()
                .extensions().add(new HotReloadingExtension(TimeUnit.SECONDS, 1))
                .add(new SpringTranslateExtension(SpringTranslateExtensionConfiguration
                        .builder(messageSource)
                        .withLocaleResolver(localeResolver)
                        .build())).and()
                .build()));
    
	}

	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @return the localeResolver
	 */
	public LocaleResolver getLocaleResolver() {
		return localeResolver;
	}

	/**
	 * @param localeResolver the localeResolver to set
	 */
	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}
	

	
}
