package uk.gov.ons.census.fwmt.jobservice.config;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestLogger implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    ClientHttpResponse response = execution.execute(request, body);

    log.debug(
        "request method: {}, request URI: {}, request headers: {}, request body: {}, response status code: {}, response headers: {}, response body: {}",
        request.getMethod(),
        request.getURI(),
        request.getHeaders(),
        new String(body, StandardCharsets.UTF_8),
        response.getStatusCode(),
        response.getHeaders(),
        new String(ByteStreams.toByteArray(response.getBody()), StandardCharsets.UTF_8));

    return response;
  }
}