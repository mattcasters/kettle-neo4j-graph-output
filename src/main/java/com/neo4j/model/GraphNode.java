package com.neo4j.model;

import org.pentaho.metastore.persist.MetaStoreAttribute;

import java.util.List;

public class GraphNode {

  @MetaStoreAttribute
  private String name;

  @MetaStoreAttribute
  private List<String> labels;

  @MetaStoreAttribute
  private List<GraphNodeProperty> properties;

}
