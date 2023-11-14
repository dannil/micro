package com.github.dannil.springgraphql.personservice.configuration;

public class PersonRoutingKey {

  private static final String DEFAULT = "person";
  public static final String ADDED = DEFAULT + ".added";
  public static final String DELETED = DEFAULT + ".deleted";

}
