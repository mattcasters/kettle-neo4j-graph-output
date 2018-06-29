package com.neo4j.kettle.steps.graph_output;

import com.sun.research.ws.wadl.Param;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Step(
  id = "Neo4jGraphOutput",
  name = "Neo4j Graph Output",
  description = "Write fields to a nodes and relationships in a Neo4j Graph",
  image = "TOP.svg",
  categoryDescription = "Neo4j"
)
public class GraphOutputMeta extends BaseStepMeta implements StepMetaInterface {

  private String connectionName;
  private String cypher;
  private List<ParameterMapping> parameterMappings;

 public GraphOutputMeta() {
   super();
   parameterMappings = new ArrayList<ParameterMapping>();
  }

  @Override public void setDefault() {

  }

  @Override public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int i, TransMeta transMeta, Trans trans ) {
    return new GraphOutput( stepMeta, stepDataInterface, i, transMeta, trans );
  }

  @Override public StepDataInterface getStepData() {
    return new GraphOutputData();
  }

  @Override public String getDialogClassName() {
    return GraphOutputDialog.class.getName();
  }

  @Override public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space,
                                   Repository repository, IMetaStore metaStore ) {

   // No output fields for now
  }

  @Override public String getXML() {
    StringBuilder xml = new StringBuilder( );
    xml.append( XMLHandler.addTagValue( "connection", connectionName) );
    xml.append( XMLHandler.addTagValue( "cypher", cypher) );
    xml.append( XMLHandler.openTag( "mappings") );
    for (ParameterMapping parameterMapping : parameterMappings) {
      xml.append( XMLHandler.openTag( "mapping") );
      xml.append( XMLHandler.addTagValue( "parameter", parameterMapping.getParameter()) );
      xml.append( XMLHandler.addTagValue( "field", parameterMapping.getField() ) );
      xml.append( XMLHandler.closeTag( "mapping") );
    }
    xml.append( XMLHandler.closeTag( "mappings") );
    return xml.toString();
  }

  @Override public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    connectionName = XMLHandler.getTagValue( stepnode, "connection" );
    cypher = XMLHandler.getTagValue( stepnode, "cypher" );
    Node mappingsNode = XMLHandler.getSubNode( stepnode, "mappings" );
    List<Node> mappingNodes = XMLHandler.getNodes( mappingsNode, "mapping" );
    parameterMappings = new ArrayList<ParameterMapping>();
    for (Node mappingNode : mappingNodes) {
      String parameter = XMLHandler.getTagValue( mappingNode, "parameter" );
      String field = XMLHandler.getTagValue( mappingNode, "field" );
      parameterMappings.add(new ParameterMapping( parameter, field ));
    }

    super.loadXML( stepnode, databases, metaStore );
  }

  @Override public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step ) throws KettleException {
    rep.saveStepAttribute( id_transformation, id_step, "connection", connectionName);
    rep.saveStepAttribute( id_transformation, id_step, "cypher", cypher);
    for (int i=0;i<parameterMappings.size();i++) {
      ParameterMapping parameterMapping = parameterMappings.get( i );
      rep.saveStepAttribute( id_transformation, id_step, i, "parameter",  parameterMapping.getParameter());
      rep.saveStepAttribute( id_transformation, id_step, i, "field",  parameterMapping.getField() );
    }
  }

  @Override public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases ) throws KettleException {
    connectionName = rep.getStepAttributeString( id_step, "connection" );
    cypher = rep.getStepAttributeString( id_step, "cypher" );
    int nrMappings = rep.countNrStepAttributes( id_step, "parameter" );
    parameterMappings = new ArrayList<ParameterMapping>();
    for (int i=0;i<nrMappings;i++) {
      String parameter = rep.getStepAttributeString( id_step, i, "parameter" );
      String cypher = rep.getStepAttributeString( id_step, i, "field" );
      parameterMappings.add( new ParameterMapping( parameter, cypher) );
    }
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName( String connectionName ) {
    this.connectionName = connectionName;
  }

  public String getCypher() {
    return cypher;
  }

  public void setCypher( String cypher ) {
    this.cypher = cypher;
  }

  public List<ParameterMapping> getParameterMappings() {
    return parameterMappings;
  }

  public void setParameterMappings( List<ParameterMapping> parameterMappings ) {
    this.parameterMappings = parameterMappings;
  }
}
