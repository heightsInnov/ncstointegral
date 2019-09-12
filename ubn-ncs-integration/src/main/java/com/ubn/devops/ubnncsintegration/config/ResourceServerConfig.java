package com.ubn.devops.ubnncsintegration.config;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	
	private String PUBLIC_KEY = null;
	
	 @Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/","/v2/api-docs/**","/swagger-resources/**"
					,"/swagger-ui.html","/webjars/**"
					,"/resources/**","/get-value").permitAll()
			.anyRequest()
			.authenticated();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer config) {
		config.tokenServices(tokenServices());
	}

	@Bean
	public BCryptPasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public TokenStore tokenStore(){
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter(){
		Resource publicresource = new ClassPathResource("publickey.txt");
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

		try{
			PUBLIC_KEY = IOUtils.toString(publicresource.getInputStream());
		} catch (final IOException e){
			throw new RuntimeException(e);
		}
		//----Verifies user's signature;
		converter.setVerifierKey(PUBLIC_KEY);
		return converter;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices(){
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		return defaultTokenServices;
	}
}
