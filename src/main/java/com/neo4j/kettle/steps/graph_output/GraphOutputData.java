package com.neo4j.kettle.steps.graph_output;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import java.util.List;

public class GraphOutputData extends BaseStepData implements StepDataInterface {

  public RowMetaInterface outputRowMeta;
  public DatabaseMeta databaseMeta;
  public String url;
  public Driver driver;
  public Session session;
  public int[] fieldIndexes;
  public String cypher;
}
