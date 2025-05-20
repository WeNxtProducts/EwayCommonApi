package com.maan.eway.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = { "com.maan.eway.workstream.entity", "com.maan.eway.workstream.repository",
		"com.maan.eway.salesLead.bean", "com.maan.eway.salesLead.Repository", "com.maan.eway.repository",
		"com.maan.eway.payment.process.Repository", "com.maan.eway.notification.repository",
		"com.maan.eway.chartaccount","com.maan.eway.notification.bean",
		"com.maan.eway.bean" }, entityManagerFactoryRef = "mySqlEntityManagerFactory", transactionManagerRef = "mySqlTransactionManager")
@EnableTransactionManagement
public class MySqlDBConfig {

	@Primary
	@Bean(name = "mySqlDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	DataSource mySqlDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}
	
	@Bean
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
	    return new EntityManagerFactoryBuilder(
	        new HibernateJpaVendorAdapter(), 
	        new HashMap<>(), 
	        null
	    );
	}
	
	@Primary
	@Bean(name = "mySqlEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean mySqlEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("mySqlDataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.maan.eway.workstream.entity", 
		          "com.maan.eway.salesLead.bean", 
		          "com.maan.eway.bean", 
		          "com.maan.eway.notification.bean",
		          "com.maan.eway.chartaccount").persistenceUnit("mysql")
				.build();
	}
	
	@Primary
	@Bean(name = "mySqlTransactionManager")
	PlatformTransactionManager mySqlTransactionManager(
			@Qualifier("mySqlEntityManagerFactory") EntityManagerFactory localEntityManagerFactory) {
		return new JpaTransactionManager(localEntityManagerFactory);
	}

}
