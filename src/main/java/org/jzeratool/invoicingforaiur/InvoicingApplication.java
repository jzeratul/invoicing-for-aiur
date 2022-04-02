package org.jzeratool.invoicingforaiur;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Comparator;

@Slf4j
@SpringBootApplication
public class InvoicingApplication {

  public static void main(String[] args) {
    SpringApplication.run(InvoicingApplication.class, args);
  }

  @Component
  public static final class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

      log.info("Listing all registered endpoints:");

      ApplicationContext applicationContext = event.getApplicationContext();

      applicationContext.getBean(RequestMappingHandlerMapping.class)
              .getHandlerMethods()
              .entrySet()
              .stream()
              .filter(e -> e.getKey().getPathPatternsCondition() != null)
              .sorted(Comparator.comparing(e -> e.getKey().getPathPatternsCondition().getPatterns().iterator().next()))
              .forEach(
                      entry -> {
                        var iterator = entry.getKey().getMethodsCondition().getMethods().iterator();
                        log.info("{}{} -> {}()", entry.getKey().getPathPatternsCondition().getPatterns(),
                                iterator.hasNext() ? iterator.next().name() : "",
                                entry.getValue().getMethod().getName());
                      }
              );
    }
  }
}
