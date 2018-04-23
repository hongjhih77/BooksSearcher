/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jersey;

import io.swagger.api.gen.api.ApiOriginFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@SpringBootApplication
@EntityScan("jpa.domain")
@EnableJpaRepositories("jpa.repositoryimpl")
@ComponentScan({"jersey", "jpa.repositoryimpl", "io"})
@Transactional
public class SampleJerseyApplication extends SpringBootServletInitializer {
  private static Log log = LogFactory.getLog(Logger.GLOBAL_LOGGER_NAME);
  public static void main(String[] args) {
    log.debug("main started~~~~~~~~~~~~~~~~");
    new SampleJerseyApplication()
        .configure(new SpringApplicationBuilder(SampleJerseyApplication.class))
        .run(args);
  }

  @Bean
  public FilterRegistrationBean apiOriginFilterRegistration() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setFilter(new ApiOriginFilter());
    registration.addUrlPatterns("/*");
    registration.addInitParameter("paramName", "paramValue");
    registration.setName("ApiOriginFilter");
    registration.setOrder(1);
    return registration;
  }
}
