package com.neo4j.kettle.steps.graph_output;

public class ParameterMapping {

  private String parameter;
  private String field;

  public ParameterMapping() {
  }

  public ParameterMapping( String parameter, String field ) {
    this.parameter = parameter;
    this.field = field;
  }

  public String getParameter() {
    return parameter;
  }

  public void setParameter( String parameter ) {
    this.parameter = parameter;
  }

  public String getField() {
    return field;
  }

  public void setField( String field ) {
    this.field = field;
  }
}
