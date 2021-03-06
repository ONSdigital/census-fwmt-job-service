package uk.gov.ons.census.fwmt.jobservice.converter;

import ma.glasnost.orika.MapperFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.census.fwmt.canonical.v1.CancelFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.canonical.v1.UpdateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.CasePauseRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.CaseRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.ModelCase;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.jobservice.converter.impl.HouseholdConverter;
import uk.gov.ons.census.fwmt.jobservice.helper.FieldWorkerJobRequestBuilder;
import uk.gov.ons.census.fwmt.jobservice.rest.client.CometRestClient;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HouseHoldConverterTest {

  @InjectMocks
  HouseholdConverter householdConverter;

  @Mock
  private CometRestClient cometRestClient;

  @Mock
  private ModelCase modelCase;

  @Mock
  private MapperFacade mapperFacade;

  @Mock
  private CaseRequest caseRequest;

  @Test
  public void createConvertRequest() {
    // Given
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .createFieldWorkerJobRequestForConvert();

    // When
    CaseRequest caseRequest = householdConverter.convert(createFieldWorkerJobRequest);

    // Then
    assertEquals(createFieldWorkerJobRequest.getCaseReference(), caseRequest.getReference());
    assertEquals("HH", caseRequest.getType().toString());
    assertEquals(createFieldWorkerJobRequest.getMandatoryResource(), caseRequest.getRequiredOfficer());

  }

  @Test
  public void createConvertRequestWithoutContact() {
    // Given
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .createFieldWorkerJobRequestForConvertWithoutContact();

    // When
    CaseRequest caseRequest = householdConverter.convert(createFieldWorkerJobRequest);

    // Then
    assertEquals(createFieldWorkerJobRequest.getCaseReference(), caseRequest.getReference());
    assertEquals("HH", caseRequest.getType().toString());
    assertEquals(createFieldWorkerJobRequest.getMandatoryResource(), caseRequest.getRequiredOfficer());

  }

  @Test
  public void createConvertPause() {
    // Given
    CancelFieldWorkerJobRequest cancelFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .cancelFieldWorkerJobRequest();

    // When
    CasePauseRequest casePauseRequest = householdConverter.convertCancel(cancelFieldWorkerJobRequest);

    // Then
    assertEquals(cancelFieldWorkerJobRequest.getCaseId().toString(), casePauseRequest.getId());
    assertEquals(cancelFieldWorkerJobRequest.getUntil(), casePauseRequest.getUntil());
  }

  @Test
  public void createConvertUpdatePause() throws GatewayException {
    // Given
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .createFieldWorkerJobRequestForConvert();

    CasePauseRequest casePauseRequest = new CasePauseRequest();
    casePauseRequest.setUntil(OffsetDateTime.parse("2019-05-06T00:00:00+00:00"));
    casePauseRequest.setId("a48bf28e-e7f4-4467-a9fb-e000b6a55676");

    CaseRequest caseRequest = householdConverter.convert(createFieldWorkerJobRequest);
    caseRequest.setPause(casePauseRequest);

    UpdateFieldWorkerJobRequest updateFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .updateFieldWorkerJobRequestWithPause();

    ModelCase modelCase = new ModelCase();
    modelCase.setId(UUID.fromString("a48bf28e-e7f4-4467-a9fb-e000b6a55676"));

    Mockito.when(mapperFacade.map(modelCase, CaseRequest.class)).thenReturn(caseRequest);

    // When
    CaseRequest caseUpdateRequest = householdConverter.convertUpdate(updateFieldWorkerJobRequest, modelCase);

    // Then
    assertEquals(updateFieldWorkerJobRequest.getCaseId().toString(), caseUpdateRequest.getPause().getId());
    assertEquals(updateFieldWorkerJobRequest.getHoldUntil(), caseUpdateRequest.getPause().getUntil());
    assertEquals("HQ Case Pause", casePauseRequest.getReason());
  }

  @Test
  public void createConvertUpdateReinstate() throws GatewayException {
    // Given
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .createFieldWorkerJobRequestForConvert();

    CasePauseRequest casePauseRequest = new CasePauseRequest();
    casePauseRequest.setUntil(OffsetDateTime.parse("2019-05-28T00:00:00+00:00"));
    casePauseRequest.setId("a48bf28e-e7f4-4467-a9fb-e000b6a55676");

    CaseRequest caseRequest = householdConverter.convert(createFieldWorkerJobRequest);
    caseRequest.setPause(casePauseRequest);

    UpdateFieldWorkerJobRequest updateFieldWorkerJobRequest = new FieldWorkerJobRequestBuilder()
        .updateFieldWorkerJobRequestReinstate();

    ModelCase modelCase = new ModelCase();
    modelCase.setId(UUID.fromString("a48bf28e-e7f4-4467-a9fb-e000b6a55676"));

    Mockito.when(mapperFacade.map(modelCase, CaseRequest.class)).thenReturn(caseRequest);

    // When
    CaseRequest caseUpdateRequest = householdConverter.convertUpdate(updateFieldWorkerJobRequest, modelCase);

    // Then
    assertEquals(updateFieldWorkerJobRequest.getCaseId().toString(), caseUpdateRequest.getPause().getId());
    assertEquals(updateFieldWorkerJobRequest.getHoldUntil(), caseUpdateRequest.getPause().getUntil());
    assertEquals("Case reinstated - blank QRE", casePauseRequest.getReason());
  }

}
