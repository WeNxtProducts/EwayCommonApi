package com.maan.eway.jasper.service.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class JasperConfiguration {

	@Value("${draft.file.path}")
	private String draftPath;

	@Value("${image.path}")
	private String imagePath;

	@Value("${policy.file.path}")
	private String policyPath;

	@Value("${proposal.file.path}")
	private String proposalPath;

	@Value("${jasper.datasourceby.jndi}")
	private String datasourcebyjndi;

	@Value("${spring.datasource.primary.jndi-name}")
	private String jndiPrimaryDatasource;

	@Value("${spring.datasource.secondary.jndi-name}")
	private String jndiSecondaryDatasource;

	@Autowired
	@Qualifier("primaryJdbcTemplate")
	private JdbcTemplate primaryTemplate;

	@Autowired
	@Qualifier("secondaryJdbcTemplate")
	private JdbcTemplate secondaryJdbcTemplate;

	public String getDraftPath() {
		return draftPath;
	}

	public String getPolicyPath() {
		return policyPath;
	}

	public String getProposalPath() {
		return proposalPath;
	}

	private static String classpathof;
	static {
		classpathof = (JasperConfiguration.class).getProtectionDomain().getCodeSource().getLocation().getPath();
	}

	public String getImagePath() {
		return (classpathof + "report/images/").replaceAll("%20", " ");
	}

	public String getJasperFilePath() {
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		classPath = classPath.substring(1, classPath.length() - 0);
		return classPath;
	}

	// Enable This 4 Value For run in Application properties
	// MySQL DataBase
	@Value("${spring.datasource.driver-class-name}")
	private String driverclassname;
	@Value("${spring.datasource.jdbc-url}")
	private String datasourceurl;
	@Value("${spring.datasource.username}")
	private String datausername;
	@Value("${spring.datasource.password}")
	private String datapassword;

	// Oracle DataBase
	@Value("${spring.datasource.driver-class-name}")
	private String driverclassname1;
	@Value("${spring.datasource1.jdbc-url}")
	private String datasourceurl1;
	@Value("${spring.datasource1.username}")
	private String datausername1;
	@Value("${spring.datasource1.password}")
	private String datapassword1;

	@Primary
	@Bean(name = "primaryJdbcTemplate")
	JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSourceFromJNDI") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean(name = "secondaryJdbcTemplate")
	JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSourceFromJNDI") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	DataSource primaryDataSourceFromJNDI() {
		try {
			JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
			bean.setJndiName(jndiPrimaryDatasource);
			bean.setProxyInterface(DataSource.class);
			bean.setLookupOnStartup(false);
			bean.afterPropertiesSet();
			return (DataSource) bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource1")
	DataSource secondaryDataSourceFromJNDI() {
		try {
			JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
			bean.setJndiName(jndiSecondaryDatasource);
			bean.setProxyInterface(DataSource.class);
			bean.setLookupOnStartup(false);
			bean.afterPropertiesSet();
			return (DataSource) bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private DataSource getDataSourcePrimaryFromSpring() {
		try {
			DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
			dataSourceBuilder.driverClassName(driverclassname);
			dataSourceBuilder.url(datasourceurl);
			dataSourceBuilder.username(datausername);
			dataSourceBuilder.password(datapassword);
			return dataSourceBuilder.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private DataSource getDataSourceSecondaryFromSpring() {
		try {
			DataSourceBuilder dataSourceBuilder1 = DataSourceBuilder.create();
			dataSourceBuilder1.driverClassName(driverclassname1);
			dataSourceBuilder1.url(datasourceurl1);
			dataSourceBuilder1.username(datausername1);
			dataSourceBuilder1.password(datapassword1);
			return dataSourceBuilder1.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public DataSource getMySQLDataSourceForJasper() {
		if ("N".equals(datasourcebyjndi))
			return getDataSourcePrimaryFromSpring();
		else
			return primaryDataSourceFromJNDI();
	}

	public DataSource getOracleDataSourceForJasper() {
		if ("N".equals(datasourcebyjndi))
			return getDataSourceSecondaryFromSpring();
		else
			return secondaryDataSourceFromJNDI();
	}

}
