package uk.gov.ons.fwmt.census.jobservice.comet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Link {
  private String rel = null;
  private String href = null;
}

