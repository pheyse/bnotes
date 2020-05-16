package de.brightside.bnotes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
//@PropertySources({
//	@PropertySource(value = "file:${user.home}/.ph-app-config/bnotes/application.properties", ignoreResourceNotFound = true),
//	@PropertySource(value = "file:/app-config/bnotes/application.properties", ignoreResourceNotFound = true)
//	@PropertySource(value = "file:/tomcat/app-conf/bnotes-application.properties", ignoreResourceNotFound = true)
//	@PropertySource(value = "file:/tomcat/bnotes-application.properties", ignoreResourceNotFound = true)
//})
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class BnotesApplication{
    final Logger LOGGER = LoggerFactory.getLogger(BnotesApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BnotesApplication.class, args);
	}

	/* use this code to encrypt a password for the database:*/
//	@Override
//	public void run(String... args) throws Exception {
//		printHashedPassword("password");
//	}
//
//	private void printHashedPassword(String passwordToHash) {
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(MainConstants.PASSWORD_ENCONDER_STRENGTH);
//		String encrypted = encoder.encode(passwordToHash);
//		LOGGER.info("Encrypted: >>" + encrypted + "<<");
//	}
	/**/
}
