package com.lti.cast.imaging;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import com.lti.cast.imaging.util.ImagingServiceUtil;


@SpringBootApplication
// Enable Aspect
@EnableAspectJAutoProxy(proxyTargetClass=true)

public class CastImagingApplication {
		
	public static void main(String[] args) {
		 SpringApplication.run(CastImagingApplication.class, args);
		/*
		 * final String secretKey = "castimagingapi";
		 * 
		 * String originalString = "admin"; System.out.println( new
		 * ImagingServiceUtil().encrypt(originalString, secretKey) );
		 * System.out.println( new ImagingServiceUtil().decrypt(originalString,
		 * secretKey) );
		 */
		
	}	
	
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
