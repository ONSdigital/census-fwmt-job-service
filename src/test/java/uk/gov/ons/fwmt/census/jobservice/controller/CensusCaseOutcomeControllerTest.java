package uk.gov.ons.fwmt.census.jobservice.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.fwmt.census.jobservice.message.RMProducer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CensusCaseOutcomeControllerTest {
  @Mock private RMProducer rmProducer;
  @InjectMocks private CensusCaseOutcomeController censusCaseOutcomeController;
  private MockMvc mockMvc;
  private static final String CASE_OUTCOME_JSON = "{\n"
      + "  \"caseId\": \"1234567890\",\n"
      + "  \"caseReference\": \"qwertyuiop\",\n"
      + "  \"outcome\": \"asdfghjkl\",\n"
      + "  \"outcomeCategory\": \"test\",\n"
      + "  \"outcomeNote\": \"test\"\n"
      + "}";

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(censusCaseOutcomeController).build();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void censusCaseOutcomeResponseTest() throws Exception {
    mockMvc.perform(post("/CensusCaseOutcome").contentType(MediaType.APPLICATION_JSON).content(CASE_OUTCOME_JSON)).andExpect(status().is2xxSuccessful());
  }


}
