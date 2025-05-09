package org.BSB.com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Serve any request to /uploads/** from the file-system folder
    // /absolute/path/to/uploads/
    registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations("file:uploads/");
    // If your uploads dir is at the project root, use "file:uploads/".
    // If it’s elsewhere, give an absolute path, e.g. "file:/var/app/uploads/"
  }
}