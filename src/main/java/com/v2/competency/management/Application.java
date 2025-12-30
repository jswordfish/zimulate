package com.v2.competency.management;

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
public class Application implements CommandLineRunner{
	static Log log = LogFactory.getLog(Application.class.getName());
	

	public static void main(String[] args) {
		log.info("starting app");
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
//		User admin = new User();
//		admin.setUserId("admin.user");
//		admin.setPassword("12345");
//		admin.setRole("ADMIN");
//			if(userRepository.findByUserId(admin.getUserId())  == null){
//				userRepository.save(admin);
//			}
//		
//		
//		User customer = new User();
//		customer.setUserId("John");
//		customer.setPassword("12345");
//		customer.setRole("CLIENT");
//		if(userRepository.findByUserId(customer.getUserId())  == null){
//			userRepository.save(customer);
//		}
		
		
	}
	
	
	@Bean
	   public Docket productApi() {
	      return new Docket(DocumentationType.SWAGGER_2).select()
	         .apis(RequestHandlerSelectors.basePackage("com.v2.competency.management.webservices"))
	         .paths(PathSelectors.any())
	         .build();
	   }
	
//	@Bean
//	public Docket api() {
//	    return new Docket(DocumentationType.SWAGGER_2)
//	            .host("http://localhost:8090/interviews/") // Replace with your actual base URL
//	            .select()
//	            .apis(RequestHandlerSelectors.basePackage("com.v2.competency.management.webservices"))
//	            .paths(PathSelectors.any())
//	            .build();
//	}
	
	@Bean
	  public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(2);
	    executor.setMaxPoolSize(5);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("AI_InsightsGenerator-");
	    executor.initialize();
	    return executor;
	  }
	
	
	
	

}