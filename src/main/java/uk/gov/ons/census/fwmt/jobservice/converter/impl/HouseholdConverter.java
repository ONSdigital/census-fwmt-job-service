package uk.gov.ons.census.fwmt.jobservice.converter.impl;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.ons.census.fwmt.canonical.v1.CancelFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.canonical.v1.UpdateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.CasePauseRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.CaseRequest;
import uk.gov.ons.census.fwmt.common.data.modelcase.Contact;
import uk.gov.ons.census.fwmt.common.data.modelcase.Location;
import uk.gov.ons.census.fwmt.common.data.modelcase.ModelCase;
import uk.gov.ons.census.fwmt.jobservice.converter.CometConverter;

import static uk.gov.ons.census.fwmt.common.data.modelcase.CaseRequest.TypeEnum.HH;
import static uk.gov.ons.census.fwmt.jobservice.utils.JobServiceUtils.setAddress;

@Component("HH")
public class HouseholdConverter implements CometConverter {

  @Autowired
  private MapperFacade mapperFacade;

  @Override
  public CaseRequest convert(CreateFieldWorkerJobRequest ingest) {
    CaseRequest caseRequest = new CaseRequest();
    caseRequest.setReference(ingest.getCaseReference());
    caseRequest.setType(HH);
    caseRequest.setSurveyType(ingest.getCaseType());
    // TODO: Category is not yet in the feed - will it ever?
    caseRequest.setCategory("Household");
    //    caseRequest.setCategory(ingest.getCategory());
    caseRequest.setRequiredOfficer(ingest.getMandatoryResource());
    caseRequest.setEstabType(ingest.getEstablishmentType());
    caseRequest.setCoordCode(ingest.getCoordinatorId());

    Contact contact = new Contact();
    contact.setName(ingest.getAddress().getPostCode());
    if (ingest.getContact() != null) {
      contact.setOrganisationName(ingest.getContact().getOrganisationName());

      contact.setPhone(ingest.getContact().getPhoneNumber());
      contact.setEmail(ingest.getContact().getEmailAddress());
    }
    caseRequest.setContact(contact);

    caseRequest.setAddress(setAddress(ingest));

    Location location = new Location();
    location.setLat(ingest.getAddress().getLatitude().floatValue());
    location.set_long(ingest.getAddress().getLongitude().floatValue());
    caseRequest.setLocation(location);

    // TODO: These are still needed as part of a create household, unsure where they are derived from.
    caseRequest.setDescription("CENSUS");
    caseRequest.setSpecialInstructions("Special Instructions");
    //    caseRequest.setDescription(ingest.getDescription()) ;
    //    caseRequest.setSpecialInstructions(ingest.getSpecialInstructions());
    caseRequest.setUaa(ingest.isUua());
    caseRequest.setSai(ingest.isSai());

    return caseRequest;
  }

  @Override
  public CasePauseRequest convertCancel(CancelFieldWorkerJobRequest ingest) {
    CasePauseRequest pauseRequest = new CasePauseRequest();
    pauseRequest.setId(String.valueOf(ingest.getCaseId()));
    pauseRequest.setUntil(ingest.getUntil());
    pauseRequest.setReason(ingest.getReason());

    return pauseRequest;
  }

  @Override
  public CaseRequest convertUpdate(UpdateFieldWorkerJobRequest ingest,
      ModelCase modelCase) {
    CaseRequest updateRequest = mapperFacade.map(modelCase, CaseRequest.class);
    CasePauseRequest casePauseRequest = new CasePauseRequest();

    int dateComparison;
    if (ingest.getAddressType().equals("HH")) {

      if (StringUtils.isEmpty(updateRequest.getPause())) {
        updateRequest.setPause(casePauseRequest);
        dateComparison = 1;
      } else {
        dateComparison = ingest.getHoldUntil().compareTo(updateRequest.getPause().getUntil());
      }

      if (!ingest.isBlankFormReturned() && dateComparison > 0) {
        updateRequest.getPause().setUntil(ingest.getHoldUntil());
        updateRequest.getPause().setReason("HQ Case Pause");
      } else if (ingest.isBlankFormReturned() && !updateRequest.isBlankFormReturned()) {
        updateRequest.getPause().setUntil(ingest.getHoldUntil());
        updateRequest.getPause().setReason("Case reinstated - blank QRE");
      }
    }

    if (ingest.getAddressType().equals("CE")) {
      updateRequest.getCe().setActualResponses(ingest.getCeActualResponses());
      updateRequest.getCe().setCe1Complete(ingest.isCe1Complete());
      updateRequest.getCe().setExpectedResponses(ingest.getCeExpectedResponses());
    }

    return updateRequest;
  }

}
