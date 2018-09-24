package uk.gov.ons.fwmt.job_service_v2.service.rm;

import com.consiliumtechnologies.schemas.mobile._2009._03.visitstypes.VisitIdentityType;
import com.consiliumtechnologies.schemas.mobile._2009._09.compositemessages.CompositeVisitRequest;
import com.consiliumtechnologies.schemas.mobile._2009._09.compositemessages.ObjectFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.DummyTMResponse;
import uk.gov.ons.fwmt.job_service_v2.common.error.CTPException;
import uk.gov.ons.fwmt.job_service_v2.rmproducer.RMProducer;

import javax.xml.bind.JAXBElement;

import static org.junit.Assert.assertEquals;
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RMJobConverterServiceImplTest {

  @Mock
  RMProducer rmProducer;

  @Captor
  ArgumentCaptor argCaptor;

  @Test
  public void transformRequest() throws CTPException {
    // TODO rewrite test to use newly added converter
    //Given
    ObjectFactory factory = new ObjectFactory();
    CompositeVisitRequest request = factory.createCompositeVisitRequest();

    VisitIdentityType visitIdentityType = new VisitIdentityType();
    visitIdentityType.setGuid("doug");
    visitIdentityType.setCompany("doug");
    visitIdentityType.setReference("doug");
    visitIdentityType.setWorkType("doug");
    request.setIdentity(visitIdentityType);

    JAXBElement<CompositeVisitRequest> input = factory.createCompositeVisitRequest(request);

    //When
    //rmJobConverterService.transformRequest(input);

    //Then
    Mockito.verify(rmProducer).send((DummyTMResponse) argCaptor.capture());
    DummyTMResponse result = (DummyTMResponse) argCaptor.getValue();
    assertEquals(visitIdentityType.getGuid(), result.getIdentity());
  }
}