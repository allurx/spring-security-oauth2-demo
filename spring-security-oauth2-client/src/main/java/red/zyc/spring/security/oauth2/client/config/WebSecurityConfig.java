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

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import red.zyc.spring.security.oauth2.client.security.CustomizedAccessDeniedHandler;
import red.zyc.spring.security.oauth2.client.security.CustomizedAuthenticationEntryPoint;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthenticationFailureHandler;
import red.zyc.spring.security.oauth2.client.security.Oauth2AuthenticationSuccessHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zyc
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public WebSecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests().anyRequest().authenticated().and()
                // 通过httpSession保存认证信息
                .addFilter(new SecurityContextPersistenceFilter())
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer

                        // 认证成功后的处理器
                        .successHandler(new Oauth2AuthenticationSuccessHandler())

                        // 认证失败后的处理器
                        .failureHandler(new Oauth2AuthenticationFailureHandler())

                        // 登录请求url
                        .loginProcessingUrl("/api/login/oauth2/code/*")

                        // 配置授权服务器端点信息
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                // 授权端点的前缀基础url
                                .baseUri("/api/oauth2/authorization"))
                        // 配置获取access_token的客户端
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig.accessTokenResponseClient(oAuth2AccessTokenResponseClient()))
                )
                // 配置匿名用户过滤器
                .anonymous().and()
                // 配置认证端点和未授权的请求处理器
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(new CustomizedAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomizedAccessDeniedHandler()));

    }

    /**
     * qq获取access_token返回的结果是类似get请求参数的字符串，这里需要吐槽一下实在是太奇葩了，而spring-security默认远程获取
     * access_token的客户端是DefaultAuthorizationCodeTokenResponseClient，无法解析qq奇葩的响应，所以我们需要
     * 自定义{@link QqoAuth2AccessTokenResponseHttpMessageConverter}注入到这个client中来解析qq的响应消息
     *
     * @return {@link DefaultAuthorizationCodeTokenResponseClient} 同来虎丘access_token的客户端
     */
    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> oAuth2AccessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient client = new DefaultAuthorizationCodeTokenResponseClient();
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(
                new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter(), new QqoAuth2AccessTokenResponseHttpMessageConverter(MediaType.TEXT_HTML)));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        client.setRestOperations(restTemplate);
        return client;
    }

    /**
     * qq获取access_token返回的结果是类似get请求参数的字符串，需要我们自己定义消息转换器来解析响应结果
     *
     * @see OAuth2AccessTokenResponseHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
     * @see OAuth2LoginAuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    private static class QqoAuth2AccessTokenResponseHttpMessageConverter extends OAuth2AccessTokenResponseHttpMessageConverter {

        public QqoAuth2AccessTokenResponseHttpMessageConverter(MediaType... mediaType) {
            setSupportedMediaTypes(Arrays.asList(mediaType));
        }

        @SneakyThrows
        @Override
        protected OAuth2AccessTokenResponse readInternal(Class<? extends OAuth2AccessTokenResponse> clazz, HttpInputMessage inputMessage) {
            String response = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
            Map<String, String> tokenResponseParameters = Arrays.stream(response.split("&")).collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
            tokenResponseParameters.put(OAuth2ParameterNames.TOKEN_TYPE, "bearer");
            return this.tokenResponseConverter.convert(tokenResponseParameters);
        }

        @Override
        protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage) {
            throw new UnsupportedOperationException();
        }
    }



}
