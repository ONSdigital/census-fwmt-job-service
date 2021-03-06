package uk.gov.ons.census.fwmt.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableRetry
@EnableSwagger2
@ComponentScan({"uk.gov.ons.census.fwmt.jobservice", "uk.gov.ons.census.fwmt.events"})
public class Application {

  public static final String APPLICATION_NAME = "FWMT Gateway - Job Service";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
