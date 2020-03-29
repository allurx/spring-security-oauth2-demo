/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.zyc.spring.security.oauth2.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthorizedClientFilter;
import red.zyc.spring.security.oauth2.client.security.Oauth2LoginSuccessHandler;

/**
 * @author zyc
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public WebSecurityConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        super(true);
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests().anyRequest().authenticated().and()
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new Oauth2AuthorizedClientFilter(oAuth2AuthorizedClientService), OAuth2AuthorizationRequestRedirectFilter.class)
                .oauth2Login(httpSecurityOauth2LoginConfigurer ->
                        httpSecurityOauth2LoginConfigurer.successHandler(new Oauth2LoginSuccessHandler(oAuth2AuthorizedClientService))
                                .loginProcessingUrl("/api/login/oauth2/code/*")
                                .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig.baseUri("/api/oauth2/authorization")));

    }


}
