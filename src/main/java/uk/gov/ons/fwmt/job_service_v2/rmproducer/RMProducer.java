package uk.gov.ons.fwmt.job_service_v2.rmproducer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.fwmt.fwmtgatewaycommon.DummyTMResponse;

import static uk.gov.ons.fwmt.job_service_v2.ApplicationConfig.RM_ADAPTER_QUEUE;

@Slf4j
@Component
public class RMProducer {

  @Autowired
  private RabbitTemplate template;

  @Autowired
  ObjectMapper objectMapper;


  public void send(DummyTMResponse dummyTMResponse) {
    try {
      final String dummyResponseStr = objectMapper.writeValueAsString(dummyTMResponse);
      log.info("Message sent to queue :{}",dummyResponseStr);
      this.template.convertAndSend(RM_ADAPTER_QUEUE, dummyResponseStr);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
