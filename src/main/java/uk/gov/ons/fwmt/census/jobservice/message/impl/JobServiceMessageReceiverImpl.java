package uk.gov.ons.fwmt.census.jobservice.message.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.ons.fwmt.census.jobservice.message.JobServiceMessageReceiver;
import uk.gov.ons.fwmt.census.jobservice.service.JobService;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCancelJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import uk.gov.ons.fwmt.fwmtgatewaycommon.error.CTPException;

import java.io.IOException;

@Slf4j
@Component
public class JobServiceMessageReceiverImpl implements JobServiceMessageReceiver {

  @Autowired
  private JobService jobService;

  @Autowired
  private ObjectMapper mapper;

  public void receiveMessage(String message) throws CTPException {
    log.info("received a message from RM-Adapter");
    processMessage(message);
  }

  private void processMessage(String message) throws CTPException {
    if (message.contains("Create")) {
      FWMTCreateJobRequest fwmtCreateJobRequest = convertMessageToDTO(FWMTCreateJobRequest.class, message);
      jobService.createJob(fwmtCreateJobRequest);
      log.info("Sending Create job to TM");
    } else if (message.contains("Cancel")) {
      FWMTCancelJobRequest fwmtCancelJobRequest = convertMessageToDTO(FWMTCancelJobRequest.class, message);
      jobService.cancelJob(fwmtCancelJobRequest);
      log.info("Sending Cancel job to TM");
    }
  }

  private <T> T convertMessageToDTO(Class<T> klass, String message) throws CTPException {
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    T dto;
    try {
      dto = mapper.readValue(message, klass);
    } catch (IOException e) {
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Failed to convert message into DTO.", e);
    }
    return dto;
  }
}