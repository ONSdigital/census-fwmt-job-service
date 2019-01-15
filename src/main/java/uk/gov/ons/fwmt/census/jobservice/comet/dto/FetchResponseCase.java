package uk.gov.ons.fwmt.census.jobservice.comet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class FetchResponseCase {
  private List<ModelCase> results = new ArrayList<ModelCase>();
  private PagingInfo paging = null;
  private FetchCriteria criteria = null;

}

