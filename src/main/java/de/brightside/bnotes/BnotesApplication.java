package de.brightside.bnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


@SpringBootApplication
//@PropertySources({
//	@PropertySource(value = "file:${user.home}/.ph-app-config/bnotes/application.properties", ignoreResourceNotFound = true),
//	@PropertySource(value = "file:/app-config/bnotes/application.properties", ignoreResourceNotFound = true)
//	@PropertySource(value = "file:/tomcat/app-conf/bnotes-application.properties", ignoreResourceNotFound = true)
//	@PropertySource(value = "file:/tomcat/bnotes-application.properties", ignoreResourceNotFound = true)
//})
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class BnotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BnotesApplication.class, args);
	}

}
