package com.v2.competency.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//        .cors().disable();
//    //	http.csrf().and().cors().disable();
//    }
	
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		
		// We don't need CSRF for this example
		httpSecurity.csrf().disable()
		.cors().and()
				// dont authenticate this particular request
				.authorizeRequests().anyRequest().permitAll();
	}
	
//	@Bean
//	WebMvcConfigurer corsConfigurer() {
//	    return new WebMvcConfigurer() {
//	    	@Override
//	        public void addCorsMappings(CorsRegistry registry) {
//	    		registry.addMapping("/**")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedOrigins("http://localhost:5173")
//                .allowedHeaders("Authorization", "authorization");
//	        }
//	    };
//	    
//	}
//	
	
    
//	@Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.addExposedHeader("Authorization");
//        configuration.addExposedHeader("authorization");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//	
//	@Bean
//	public CorsFilter corsFilter() {
//	    CorsConfiguration config = new CorsConfiguration();
//	    config.addAllowedOrigin("http://localhost:5173/");
//	    config.addAllowedOrigin("http://localhost:5173");
//	   // config.addAllowedMethod("*");
//	   // config.addAllowedOrigin("http://localhost:4200");
//	    config.addAllowedHeader("*");
//	    config.setAllowedMethods(Arrays.asList("*"));
//	    config.setAllowCredentials(true);
//
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    source.registerCorsConfiguration("/**", config);
//
//	    return new CorsFilter(source);
//	}

	@Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTH_1) // change region as per your bucket
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("AKIATTNDRE7EMASKPY5A", "TJ8AsCgHkzuRwTNT3ZeQPwahAo91dYDJuhdwTiPd")
                ))
                .build();
    }

   
}