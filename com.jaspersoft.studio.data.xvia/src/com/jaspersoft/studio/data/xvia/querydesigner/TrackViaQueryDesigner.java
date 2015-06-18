package com.jaspersoft.studio.data.xvia.querydesigner;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.data.DataAdapterServiceUtil;

import org.apache.http.client.methods.HttpPost;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.designer.QueryDesigner;
import com.jaspersoft.studio.data.xvia.adapter.TrackViaDataAdapter;
import com.jaspersoft.studio.data.xvia.messages.Messages;
import com.jaspersoft.studio.data.xvia.model.TrackViaRoot;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ModelVisitor;
import com.jaspersoft.studio.swt.widgets.CSashForm;
import com.xvia.fullapi.client.LoginResponse;
import com.xvia.fullapi.client.RequestService;

public class TrackViaQueryDesigner extends QueryDesigner {
	
	// may not need these static strings
	public static final String P_USE_JDBC_QUOTE = "com.jaspersoft.studio.data.sql.prefs.USEJDBCQUOTE"; //$NON-NLS-1$
	public static final String P_IDENTIFIER_QUOTE = "com.jaspersoft.studio.data.sql.prefs.IDENTIFIER_QUOTE"; //$NON-NLS-1$
	public static final String P_IDENTIFIER_QUOTEONLYEXCEPTIONS = "com.jaspersoft.studio.data.sql.prefs.QUOTE_ONLY_EXCEPTIONS"; //$NON-NLS-1$
	public static final String P_JOIN_ON_DND = "com.jaspersoft.studio.data.sql.prefs.join_on_dnd"; //$NON-NLS-1$
	public static final String P_DELSUBQUERY = "com.jaspersoft.studio.data.sql.prefs.delsubquery"; //$NON-NLS-1$
	public static final String P_DEL_SHOWCONFIRMATION = "com.jaspersoft.studio.data.sql.prefs.delSHOWCONFIRMATION"; //$NON-NLS-1$
	
	
	
	public static final String TRACKVIAQUERYDESIGNER = "TRACKVIAQUERYDESIGNER"; //$NON-NLS-1$

	/* Text area where enter the query */
	protected StyledText queryTextArea;
	//private TrackViaQuerySource source;
	
	private TrackViaMetadata dbMetadata;
	private SashForm sashForm;
	private Thread runningthread;
	private IProgressMonitor runningmonitor;
	
	private TrackViaRoot root;
	
	// assuming this class is singleton
	private RequestService requestService;
	
	@Override
	public Control getControl() {
		return sashForm;
	}
	
	public Control createControl(Composite parent) {
		sashForm = new CSashForm(parent, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		sashForm.setLayout(new GridLayout());

		dbMetadata = new TrackViaMetadata(this);
		Control c = dbMetadata.createControl(sashForm);
		c.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		return sashForm;
	}
	
	public TrackViaMetadata getDbMetadata() {
		return dbMetadata;
	}

	public TrackViaRoot getRoot() {
		return root;
	}
	
	private Set<TrackViaRoot> roots = new HashSet<TrackViaRoot>();

	public TrackViaRoot createRoot(TrackViaRoot oldRoot) {
		if (oldRoot != null) {
			oldRoot.getPropertyChangeSupport().removePropertyChangeListener(
					tblListener);
			roots.remove(oldRoot);
		}
		TrackViaRoot rt = new TrackViaRoot(null, getjDataset());
		if (jConfig != null)
			rt.setIdentifierQuote(jConfig.getProperty(
					P_IDENTIFIER_QUOTE, "")); //$NON-NLS-1$
		roots.add(rt);
		rt.getPropertyChangeSupport().addPropertyChangeListener(tblListener);
		return rt;
	}
	

	private PropertyChangeListener tblListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent arg0) {
			if (getjDataset() == null)
				return;
			final Set<String> tables = new HashSet<String>();
			new ModelVisitor<String>((INode) arg0.getSource()) {

				@Override
				public boolean visit(INode n) {
/*					if (n instanceof MFromTable) {
						MFromTable ft = (MFromTable) n;
						Object x = ((MFromTable) n)
								.getPropertyActualValue(MFromTable.PROP_X);
						Object y = ((MFromTable) n)
								.getPropertyActualValue(MFromTable.PROP_Y);
						if (x != null && y != null)
							tables.add(ft.getValue().toSQLString()
									+ ft.getAliasKeyString() + "," + x + ","
									+ y + "," + ft.getId() + ";");
					}*/
					return true;
				}
			};
			if (!tables.isEmpty()) {
				String input = "";
				for (String t : tables)
					input += t;
			}
		}
	};
	
	public void doRefreshRoots(boolean updateText) {
		String iq = jConfig.getProperty(
				P_IDENTIFIER_QUOTE, ""); //$NON-NLS-1$

		boolean quoteExceptions = jConfig
				.getPropertyBoolean(
						P_IDENTIFIER_QUOTEONLYEXCEPTIONS,
						true); //$NON-NLS-1$
		for (TrackViaRoot r : roots) {
			r.setIdentifierQuote(iq);
			r.setQuoteExceptions(quoteExceptions);
		}
		if (updateText)
			refreshQueryText();
	}
	
	private boolean refreshMetadata = false;

	public void refreshedMetadata() {
	//	if (tabFolder.getSelectionIndex() == 0)
		//	source.setDirty(true);
	}
	
	public void setRefreshMetadata(boolean refreshMetadata) {
		this.refreshMetadata = refreshMetadata;
	}
	
	private boolean isModelRefresh = false;

	@Override
	protected void updateQueryText(String txt) {
		if (refreshMetadata)
			return;
		//if (source != null)
			//source.setQuery(txt);
		if (!isModelRefresh)
			refreshQueryModel();
	}

	public void refreshQueryModel() {
		if (true) {
			
		}
		//if (source != null)
			//Text2Model.text2model(this, source.getXTextDocument());
	}
	
	public void refreshQueryText() {
		if (refreshMetadata)
			return;
		if (root != null) {
			isModelRefresh = true;
			isModelRefresh = false;
		}
	}

	@Override
	public void doSourceTextChanged() {
		super.doSourceTextChanged();
	}
	
	private DataAdapterDescriptor da;
	private String username;
	private String password;
	private String apiURL;
	
	@Override
	public void setDataAdapter(final DataAdapterDescriptor da) {
		if (this.da == da)
			return;
		this.da = da;
		super.setDataAdapter(da);
		
		// initialize API client
		TrackViaDataAdapter trackViaDataAdapter = (TrackViaDataAdapter) da.getDataAdapter();
        username = trackViaDataAdapter.getUsername();
    	password = trackViaDataAdapter.getPassword();
    	apiURL = trackViaDataAdapter.getTrackViaURI();
    	
    	requestService = new RequestService(apiURL);
 		Map<String, String> queryParams = new HashMap<String, String>();
 		queryParams.put("username", username);
 		queryParams.put("password", password);
 		queryParams.put("grant_type", "password");
 		queryParams.put("client_id", "xvia-webapp");
 		
 		try {
			LoginResponse response = requestService.serverRequest(HttpPost.METHOD_NAME, queryParams, "/oauth/token", "", new TypeReference<LoginResponse>(){}, true);
			requestService.setLoginResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
 		
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (runningmonitor != null)
					runningmonitor.setCanceled(true);
				updateMetadata();
			}
		});
	}
	
	public void updateMetadata() {
		try {
			container.run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					if (runningthread != null) {
						runningmonitor.setCanceled(true);
						if (runningthread != null)
							runningthread.join();
					}
					runningmonitor = monitor;
					runningthread = Thread.currentThread();
					try {
						monitor.beginTask(
								Messages.TrackViaQueryDesigner_readmetadata,
								IProgressMonitor.UNKNOWN);
						DataAdapterService das = DataAdapterServiceUtil
								.getInstance(jConfig).getService(
										da.getDataAdapter());
						dbMetadata.updateMetadata(da, das, monitor);
					} finally {
						monitor.done();
						runningthread = null;
						runningmonitor = null;
					}
				}
			});
		} catch (Exception ex) {
			container.getQueryStatus().showError(ex);
			runningthread = null;
			runningmonitor = null;
		}
	}
	
	public RequestService getRequestService() {
		return this.requestService;
	}
	
	@Override
	public String getQuery() {
		return dbMetadata.getQuery();
	}
}
