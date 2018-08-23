package uk.gov.ons.fwmt.job_service_v2.service.tm.Impl;

import com.consiliumtechnologies.schemas.services.mobile._2009._03.messaging.SendCreateJobRequestMessage;
import com.consiliumtechnologies.schemas.services.mobile._2009._03.messaging.SendDeleteJobRequestMessage;
import org.junit.Test;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.Address;
import uk.gov.ons.fwmt.fwmtgatewaycommon.data.FWMTCreateJobRequest;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TMJobConverterServiceImplTest {

    TMJobConverterServiceImpl tmJobConverterService = new TMJobConverterServiceImpl();

    @Test
    public void createJobTest() {
        String user = "bob.smith";
        FWMTCreateJobRequest ingest = new FWMTCreateJobRequest();
        Address address = new Address();
        ingest.setActionType("Create");
        ingest.setJobIdentity("1234");
        ingest.setSurveyType("HH");
        ingest.setPreallocatedJob(true);
        ingest.setMandatoryResourceAuthNo("1234");
        ingest.setDueDate(LocalDate.parse("2018-08-16"));
        address.setLine1("886");
        address.setLine2("Prairie Rose");
        address.setLine3("Trail");
        address.setLine4("RU");
        address.setTownName("Borodinskiy");
        address.setPostCode("188961");
        address.setLatitude(BigDecimal.valueOf(61.7921776));
        address.setLongitude(BigDecimal.valueOf(34.3739957));
        ingest.setAddress(address);

        SendCreateJobRequestMessage request = tmJobConverterService.createJob(ingest, user);

        assertEquals(request.getCreateJobRequest().getJob().getIdentity().getReference(), "1234");
        assertEquals(request.getCreateJobRequest().getJob().getContact().getName(), "188961");
        assertEquals(request.getCreateJobRequest().getJob().getDueDate(),
            XMLGregorianCalendarImpl.parse("2018-08-16T23:59:59.000Z"));

        assertEquals(request.getSendMessageRequestInfo().getQueueName(), "\\OPTIMISE\\INPUT");
        assertEquals(request.getSendMessageRequestInfo().getKey(), "1234");
    }

    @Test
    public void addAddressLinesTest() {
        List<String> addressLines = new ArrayList<String>();
        String addressLine1 = "number";
        String addressLine2 = "street";
        String addressLine3 = "town";
        String addressLine4 = "city";

        tmJobConverterService.addAddressLines(addressLines, addressLine1);
        tmJobConverterService.addAddressLines(addressLines, addressLine2);
        tmJobConverterService.addAddressLines(addressLines, addressLine3);
        tmJobConverterService.addAddressLines(addressLines, addressLine4);

        assertEquals(4, addressLines.size());
    }

    @Test
    public void checkNumberOfAddressLinesTest() {
        List<String> addressLines = new ArrayList<>();
        String addressLine1 = "number";
        String addressLine2 = "street";
        String addressLine3 = "street";
        String addressLine4 = "street";
        String addressLine5 = "town";
        String addressLine6 = "city";

        tmJobConverterService.addAddressLines(addressLines, addressLine1);
        tmJobConverterService.addAddressLines(addressLines, addressLine2);
        tmJobConverterService.addAddressLines(addressLines, addressLine3);
        tmJobConverterService.addAddressLines(addressLines, addressLine4);
        tmJobConverterService.addAddressLines(addressLines, addressLine5);
        tmJobConverterService.addAddressLines(addressLines, addressLine6);

        assertEquals(6, addressLines.size());

        tmJobConverterService.checkNumberOfAddressLines(addressLines);

        assertEquals(5, addressLines.size());
    }

    @Test
    public void createJobRequestFromIngestTest() {

    }

    @Test
    public void deleteJobTest() {
        SendDeleteJobRequestMessage request = tmJobConverterService.deleteJob("1234", "wrong address");

        assertEquals(request.getDeleteJobRequest().getDeletionReason(), "wrong address");
        assertEquals(request.getDeleteJobRequest().getIdentity().getReference(), "1234");
    }
}