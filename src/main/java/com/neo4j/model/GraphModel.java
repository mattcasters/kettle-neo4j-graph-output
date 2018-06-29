package com.neo4j.model;


import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;

import java.util.List;

@MetaStoreElementType(
  name = "Neo4j Graph Model",
  description = "Description of the nodes, relationships, indexes... of a Neo4j graph")
public class GraphModel {
  private String name;

  @MetaStoreAttribute
  private String connection;

  private List<GraphNode> nodes;


}
