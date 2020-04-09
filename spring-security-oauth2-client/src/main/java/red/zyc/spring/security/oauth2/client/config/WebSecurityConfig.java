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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import red.zyc.spring.security.oauth2.client.security.CustomizedAccessDeniedHandler;
import red.zyc.spring.security.oauth2.client.security.CustomizedAuthenticationEntryPoint;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthenticationFailureHandler;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthenticationSuccessHandler;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthorizedClientFilter;

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
                .addFilterBefore(new Oauth2AuthorizedClientFilter(oAuth2AuthorizedClientService), OAuth2AuthorizationRequestRedirectFilter.class)
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                        // 认证成功后的处理器
                        .successHandler(new Oauth2AuthenticationSuccessHandler(oAuth2AuthorizedClientService))
                        // 认证失败后的处理器
                        .failureHandler(new Oauth2AuthenticationFailureHandler())
                        // 登录请求url
                        .loginProcessingUrl("/api/login/oauth2/code/*")
                        // 配置授权服务器端点信息
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                // 授权端点的前缀基础url
                                .baseUri("/api/oauth2/authorization")))
                // 配置认证端点和未授权的请求处理器
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(new CustomizedAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomizedAccessDeniedHandler()));

    }


}
