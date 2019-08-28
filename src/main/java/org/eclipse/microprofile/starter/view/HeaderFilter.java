/*
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.microprofile.starter.view;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@WebFilter(urlPatterns = "*.xhtml")
public class HeaderFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        servletResponse.setHeader("X-XSS-Protection", "1; mode=block");
        servletResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        servletResponse.setHeader("X-Content-Type-Options", "nosniff");
        servletResponse.setHeader("X-Frame-Options", "DENY");
        servletResponse.setHeader("Content-Security-Policy", "default-src: https: font-src 'https://fonts.googleapis.com';" +
                "img-src 'self' https://microprofile.io; style-src 'self'; frame-ancestors 'none';");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Silence
    }

    @Override
    public void destroy() {
        // Silence
    }
}
