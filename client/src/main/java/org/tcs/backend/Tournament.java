package org.tcs.backend;

import java.sql.Timestamp;

public interface Tournament {
  interface Id {}

  Id getId();
  String getName();
  Timestamp getStart();
  Timestamp getEnd();
  City.Id getCity();
}
