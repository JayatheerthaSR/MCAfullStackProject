package com.app.banking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    public WebConfig() {
        System.out.println("====== WebConfig BEAN INITIALIZED ======");
        logger.info("====== WebConfig BEAN INITIALIZED ======");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        System.out.println("====== Adding View Controllers for SPA Fallback ======");
        logger.info("====== Adding View Controllers for SPA Fallback ======");

        // This single line is designed to catch all paths that do NOT start with
        // the specified prefixes (api, static, css, js, images, favicon.ico, error).
        // It then forwards these requests to the root path ("/"), where your
        // index.html is expected to be served.
        // The regex `^(?!...)` is a negative lookahead, matching if the path doesn't start with the listed items.
        // `.*` matches any character zero or more times, and `$` anchors it to the end of the string.
        registry.addViewController("/{path:^(?!api|static|css|js|images|favicon.ico|error).*$}")
                .setViewName("forward:/");

        // It's good practice to explicitly define the root path mapping,
        // although the regex above should cover it too.
        registry.addViewController("/").setViewName("forward:/");

        // IMPORTANT: The following lines are removed as they caused the "Invalid mapping pattern" error:
        // registry.addViewController("/**/{path:[^\\.]*}").setViewName("forward:/");
        // registry.addViewController("/{path:[^\\.]*}").setViewName("forward:/");
    }
}
