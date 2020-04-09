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

package red.zyc.spring.security.oauth2.client.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zyc
 */
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public Oauth2AuthenticationSuccessHandler(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oAuth2AuthenticationToken.getName());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(oAuth2AuthenticationToken.getName());
        //response.getWriter().write(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
    }
}
