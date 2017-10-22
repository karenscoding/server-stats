package com.karens.coding.server.stats.config;

import java.nio.charset.StandardCharsets;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class ApplicationConfig {
	public MessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setBasename("/resources");
		return messageSource;
	}
	
	   
	/*public DataSource dataSource() 
	{     
		return new EmbeddedDatabaseBuilder().addScript("schema.sql").build();   
	}
	
	  
	public DataSourceTransactionManager transactionManager() 
	{     
		return new DataSourceTransactionManager(dataSource());   
	}
	
	   
	public SqlSessionFactory sqlSessionFactory() throws Exception 
	{     
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean(); sessionFactory.setDataSource(dataSource());    
		return sessionFactory.getObject();   
	}*/
}
