package com.neo4j.kettle.steps.graph_output;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GraphOutput extends BaseStep implements StepInterface {

  public GraphOutput( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
                      TransMeta transMeta, Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }


  @Override public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {

    GraphOutputMeta meta = (GraphOutputMeta) smi;
    GraphOutputData data = (GraphOutputData)sdi;

    // Connect to Neo4j using info in Neo4j JDBC connection metadata...
    //
    try {

      data.databaseMeta = getTransMeta().findDatabase( meta.getConnectionName() );
      if (data.databaseMeta==null) {
        log.logError( "Connection not found : " +meta.getConnectionName() );
        return false;
      }
      if (!"neo4j".equalsIgnoreCase(data.databaseMeta.getPluginId())) {
        log.logError("WARNING: This is not a Neo4j database connection");
      }
      String hostname = environmentSubstitute( data.databaseMeta.getHostname() );
      String port = environmentSubstitute( Const.NVL(data.databaseMeta.getExtraOptions().get("BOLT_PORT"), "7687") );
      String username = environmentSubstitute( data.databaseMeta.getUsername() );
      String password = environmentSubstitute( data.databaseMeta.getPassword() );

      data.url = "bolt://"+hostname+":"+port;
      log.logBasic( "Neo4j URI : "+data.url );
      data.driver = GraphDatabase.driver( data.url, AuthTokens.basic(username, password));

    } catch(Exception e) {
      log.logError( "Unable to get or create Neo4j database driver for database '"+data.databaseMeta.getName()+"'", e);
      return false;
    }

    return super.init( smi, sdi );
  }

  @Override public void dispose( StepMetaInterface smi, StepDataInterface sdi ) {
    super.dispose( smi, sdi );
  }

  @Override public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {

    GraphOutputMeta meta = (GraphOutputMeta) smi;
    GraphOutputData data = (GraphOutputData)sdi;

    Object[] row = getRow();
    if (row==null) {
      setOutputDone();
      return false;
    }

    if (first) {
      first=false;

      // get the output fields...
      //
      data.outputRowMeta = new RowMeta();
      meta.getFields( data.outputRowMeta, getStepname(), null, getStepMeta(), this, repository, metaStore );

      // Create a session
      //
      data.session = data.driver.session();

      // Get parameter field indexes
      data.fieldIndexes = new int[meta.getParameterMappings().size()];
      for (int i=0;i<meta.getParameterMappings().size();i++) {
        String field = meta.getParameterMappings().get(i).getField();
        data.fieldIndexes[i] = getInputRowMeta().indexOfValue( field );
        if (data.fieldIndexes[i]<0) {
          throw new KettleStepException( "Unable to find parameter field '"+field );
        }
      }

      data.cypher = environmentSubstitute( meta.getCypher() );
    }

    // Assume Strings for now: TODO implement value mapping and conversion
    //
    Map<String, Object> parameters = new HashMap<String, Object>();
    for (int i=0;i<meta.getParameterMappings().size();i++) {
      ParameterMapping mapping = meta.getParameterMappings().get( i );
      parameters.put(mapping.getParameter(), getInputRowMeta().getString( row, data.fieldIndexes[i] ));
    }

    // Execute the cypher with all the parameters...
    //
    StatementResult result = data.session.run( data.cypher, parameters );
    while (result.hasNext()) {
      Record record = result.next();
      log.logBasic( record.toString() );
    }

    // TODO: add result information, possibly values...

    // Pass the rows to the next steps
    //
    putRow( data.outputRowMeta, row );

    return true;
  }
}
