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
        // This should appear very early in the startup logs if the bean is loaded
        System.out.println("====== WebConfig BEAN INITIALIZED ======");
        logger.info("====== WebConfig BEAN INITIALIZED ======");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // This should appear when view controllers are being added
        System.out.println("====== Adding View Controllers for SPA Fallback ======");
        logger.info("====== Adding View Controllers for SPA Fallback ======");

        // Your current regex
        registry.addViewController("/{path:^(?!api|static|css|js|images|favicon.ico).*}")
                .setViewName("forward:/");

        // Explicitly handle the root
        registry.addViewController("/").setViewName("forward:/");

        // You might also want to try the simpler and often robust patterns if the above doesn't work
        // For multi-level paths (e.g., /users/profile)
        registry.addViewController("/**/{path:[^\\.]*}") // Matches any path not containing a dot
                .setViewName("forward:/");
        // For single-level paths (e.g., /dashboard)
        registry.addViewController("/{path:[^\\.]*}") // Matches a single path segment not containing a dot
                .setViewName("forward:/");
    }
}
