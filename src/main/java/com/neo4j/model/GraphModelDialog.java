package com.neo4j.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;

public class GraphModelDialog extends Dialog  {

  private Text wModelName;
  private CTabFolder wTabs;
  private CTabItem wNodesTab, wRelationsTab;
  private Composite wNodesComp, wRelationsComp;

  private Shell shell;
  private Display display;
  private PropsUI props;

  private boolean ok;

  public GraphModelDialog( Shell parent, int options ) {
    super( parent, options);
    props = PropsUI.getInstance();
    ok = false;
  }

  /**
   * @return true when OK is hit, false when CANCEL
   */
  public boolean open() {

    Shell parent = getParent();
    display = parent.getDisplay();

    shell = new Shell( parent, SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.DIALOG_TRIM );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );
    props.setLook( shell );

    FormLayout formLayout = new FormLayout();

    shell.setLayout( formLayout );
    shell.setText( "Graph Model Editor" );

    Label wlModelName = new Label( shell, SWT.LEFT );
    props.setLook(wlModelName);
    wlModelName.setText( "Model name " );
    FormData fdlModelName = new FormData();
    fdlModelName.left = new FormAttachment( 0, -Const.MARGIN );
    fdlModelName.right = new FormAttachment( Const.MIDDLE_PCT, 0 );
    fdlModelName.top = new FormAttachment( 0, Const.MARGIN );
    wlModelName.setLayoutData( fdlModelName );
    wModelName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    props.setLook( wModelName );
    FormData fdModelName = new FormData();
    fdModelName.left = new FormAttachment( Const.MIDDLE_PCT,  0);
    fdModelName.top = new FormAttachment( 0, Const.MARGIN);
    fdModelName.right = new FormAttachment( 100, 0);
    wModelName.setLayoutData( fdModelName );




    return ok;
  }
}
