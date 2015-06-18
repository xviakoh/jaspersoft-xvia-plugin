/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 * 
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.data.xvia.querydesigner;

import java.lang.reflect.InvocationTargetException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.eclipse.ui.util.UIUtils;

import org.apache.http.client.methods.HttpGet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.PluginTransfer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.xvia.messages.Messages;
import com.jaspersoft.studio.data.xvia.model.TrackViaRoot;
import com.jaspersoft.studio.data.xvia.model.TrackViaApp;
import com.jaspersoft.studio.data.xvia.model.TrackViaForm;
import com.jaspersoft.studio.data.xvia.model.metadata.INotInMetadata;
import com.jaspersoft.studio.data.xvia.model.metadata.MetaDataUtil;
import com.jaspersoft.studio.dnd.NodeDragListener;
import com.jaspersoft.studio.dnd.NodeTransfer;
import com.jaspersoft.studio.model.IDragable;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.MRoot;
import com.jaspersoft.studio.outline.ReportTreeContetProvider;
import com.jaspersoft.studio.outline.ReportTreeLabelProvider;
import com.jaspersoft.studio.utils.ModelUtils;
import com.xvia.fullapi.client.RequestService;
import com.xvia.response.base.AppResponse;
import com.xvia.response.base.GlobalUserResponse;
import com.xvia.response.base.user.SimpleAccountResponse;

public class TrackViaMetadata {
	private TreeViewer treeViewer;
	private TrackViaQueryDesigner designer;
	private TrackViaRoot root;
	private String accountId;
	private TrackViaForm selectedForm;
	
	public TrackViaMetadata(TrackViaQueryDesigner designer) {
		this.designer = designer;
	}

	public Control createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		composite.setLayout(stackLayout);

		mcmp = new Composite(composite, SWT.BORDER);
		mcmp.setLayout(new GridLayout());

		msg = new Label(mcmp, SWT.WRAP | SWT.CENTER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.CENTER;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.CENTER;
		gd.horizontalIndent = 20;
		msg.setLayoutData(gd);
		msg.setText(Messages.DBMetadata_0);
		msg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				doRefreshMetadata();
			}
		});

		treeViewer = new TreeViewer(composite, SWT.SINGLE | SWT.BORDER);
		treeViewer.setContentProvider(new ReportTreeContetProvider() {
			@Override
			public Object[] getChildren(Object parentElement) {
				if (parentElement instanceof INode) {
					INode node = (INode) parentElement;
					List<INode> children = node.getChildren();
					List<INode> newchildren = new ArrayList<INode>();
					for (INode n : children) {
						if (n instanceof INotInMetadata
								&& ((INotInMetadata) n).isNotInMetadata())
							continue;
						if (n.getValue() instanceof String
								&& ((String) n.getValue()).isEmpty())
							continue;
						newchildren.add(n);
					}
					if (!newchildren.isEmpty())
						return newchildren.toArray();
				}
				return EMPTY_ARRAY;
			}
		});
		treeViewer.setLabelProvider(new ReportTreeLabelProvider());

		ColumnViewerToolTipSupport.enableFor(treeViewer);

		treeViewer.addDragSupport(
				DND.DROP_COPY | DND.DROP_MOVE,
				new Transfer[] { NodeTransfer.getInstance(),
						PluginTransfer.getInstance() }, new NodeDragListener(
						treeViewer) {
					@Override
					public void dragStart(DragSourceEvent event) {
						TreeSelection s = (TreeSelection) treeViewer
								.getSelection();
						for (TreePath tp : s.getPaths()) {
							if (!(tp.getLastSegment() instanceof IDragable)) {
								event.doit = false;
								return;
							}
						}
					}

					@Override
					public void dragFinished(DragSourceEvent event) {
						treeViewer.refresh(true);
						if (!event.doit)
							return;
					}
				});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeSelection ts = (TreeSelection) treeViewer.getSelection();
				Object el = ts.getFirstElement();

				if (treeViewer.getExpandedState(el))
					treeViewer.collapseToLevel(el, 2);
				else {
					if (el instanceof TrackViaApp)
						loadApp((TrackViaApp) el);
					
					treeViewer.expandToLevel(el, 1);
				}
			}
		});
		treeViewer.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				final Object element = event.getElement();
				if (element instanceof TrackViaApp)
					UIUtils.getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							loadApp((TrackViaApp) element);
						}
					});
			}
		});
		treeViewer.addSelectionChangedListener(new SelectionChangedListener(this));
		
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		menuMgr.add(new Action(Messages.DBMetadata_1) {
			@Override
			public void run() {
				doRefreshMetadata();
			}
		});
		treeViewer.getControl().setMenu(menu);

		stackLayout.topControl = mcmp;

		root = designer.createRoot(root);
		updateUI(root);

		return composite;
	}

	public TrackViaForm getSelectedForm() {
		return this.selectedForm;
	}
	
	public void setSelectedForm(Object element) {
		this.selectedForm = (TrackViaForm) element;
	}

	private boolean running = false;
	private Label msg;
	private StackLayout stackLayout;
	private Composite mcmp;
	private Composite composite;
	private DataAdapterService das;

	public void updateMetadata(final DataAdapterDescriptor da,
			DataAdapterService das, final IProgressMonitor monitor) {
		if (running)
			return;
		this.das = das;
		monitors.add(monitor);
		running = true;
		UIUtils.getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				if (msg.isDisposed())
					return;
				msg.setText(Messages.DBMetadata_2 + da.getName()
						+ Messages.DBMetadata_3);
				stackLayout.topControl = mcmp;
				mcmp.layout(true);
				composite.layout(true);
			}
		});
		root.removeChildren();
		if (tblMap != null) {
			tblMap.clear();
		}
		
		try {
			RequestService requestService = designer.getRequestService();
			GlobalUserResponse gru = requestService.serverRequest(HttpGet.METHOD_NAME, null,
					"/users", "", new TypeReference<GlobalUserResponse>() {
					});

			SimpleAccountResponse sar = gru.getAccounts().iterator().next();
			this.accountId = sar.getId().toString();
			
			List<AppResponse> appresponse = requestService.serverRequest(
					HttpGet.METHOD_NAME, null, "/accounts/" + sar.getId()
							+ "/apps", "",
					new TypeReference<List<AppResponse>>() {
					});

			List<TrackViaApp> mcurrent = new ArrayList<TrackViaApp>();
			for (AppResponse ar : appresponse) {
				new TrackViaApp(root, ar.getName(),
						ar.getId().toString());
			}

			updateUI(root);

			for (TrackViaApp mcs : mcurrent) {
				readApp(mcs, monitor, true);
			}
			
			updateItermediateUI();
			
		} catch (Throwable e) {
			updateUI(root);
			designer.showError(e);
		}
		
		updateItermediateUI();
		UIUtils.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO how to refresh tables (or views) ?
				//Util.refreshTables(root, designer.getRoot(), designer);
				updateItermediateUI();
			}
		});
		monitors.remove(monitor);
		running = false;
	}

	public DatabaseMetaData getMetadata() throws SQLException {
/*		connection = getConnection(das, true);
		return connection.getMetaData();*/
		// TODO
		return null;
	}

	public void loadApp(final TrackViaApp mschema) {
		if (das != null && ModelUtils.isEmpty(mschema)) {
			try {
				designer.run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						monitor.beginTask(Messages.DBMetadata_5,
								IProgressMonitor.UNKNOWN);
						try {
							monitors.add(monitor);
							mschema.setDbMetadata(TrackViaMetadata.this);
							readApp(mschema, monitor, false);
						} catch (Throwable e) {
							designer.showError(e);
						} finally {
							monitors.remove(monitor);
							monitor.done();
						}
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void readApp(TrackViaApp schema,
			IProgressMonitor monitor, boolean firstSelection) {
		try {
			MetaDataUtil.readTables(designer.getRequestService(), this.accountId, schema, monitor, tableTypes);
			updateItermediateUI(false);
			if (monitor.isCanceled())
				return;
			schema.setDbMetadata(this);
			MetaDataUtil.readTableForms(designer.getRequestService(), this.accountId, schema, getTables(), monitor);
			updateItermediateUI();
			if (monitor.isCanceled())
				return;
			if (firstSelection)
				setFirstSelection();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (monitor.isCanceled())
			return;
	}

	public MRoot getRoot() {
		return root;
	}
	
	private String[] schema;

	public String[] getCurrentSchema() {
		return schema;
	}

	protected void updateUI(final TrackViaRoot root) {
		UIUtils.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (treeViewer.getControl().isDisposed())
					return;
				TrackViaMetadata.this.root = root;
				if (TrackViaMetadata.this.root == null)
					TrackViaMetadata.this.root = designer.createRoot(root);
				treeViewer.setInput(TrackViaMetadata.this.root);
				designer.refreshQueryModel();
				setFirstSelection();
				if (isEmptySchema(root)) {
					msg.setText(Messages.DBMetadata_6);
					stackLayout.topControl = mcmp;
				} else
					stackLayout.topControl = treeViewer.getControl();
				composite.layout(true);
			}
		});
	}

	public static boolean isEmptySchema(MRoot root) {
		if (root.getChildren().isEmpty())
			return true;
		for (INode n : root.getChildren())
			if (n instanceof TrackViaApp && !((TrackViaApp) n).isNotInMetadata())
				return false;
		return true;
	}

	protected void updateItermediateUI() {
		updateItermediateUI(true);
	}

	protected void updateItermediateUI(final boolean refreshMetadata) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!treeViewer.getControl().isDisposed()) {
					treeViewer.refresh(true);
					if (refreshMetadata)
						designer.refreshedMetadata();
				}
			}
		});
	}

	protected void setFirstSelection() {
		if (schema == null)
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				for (INode n : TrackViaMetadata.this.root.getChildren()) {
					if (n instanceof TrackViaApp && n.getValue().equals(schema)) {
						((TrackViaApp) n).setCurrent(true);
						treeViewer.expandToLevel((TrackViaApp) n, 1);
						break;
					}
				}
			}
		});
	}

	private LinkedHashMap<String, TrackViaForm> tblMap;
	private List<IProgressMonitor> monitors = new ArrayList<IProgressMonitor>();
	private List<String> tableTypes;

	public LinkedHashMap<String, TrackViaForm> getTables() {
		if (tblMap == null)
			tblMap = new LinkedHashMap<String, TrackViaForm>();
		return tblMap;
	}

	public void dispose() {
		if (monitors != null)
			for (IProgressMonitor m : monitors)
				m.setCanceled(true);
	}

	protected void doRefreshMetadata() {
		if (!running) {
			designer.showInfo(""); //$NON-NLS-1$
			designer.updateMetadata();
		}
	}

	public static List<String> readTableTypes(DatabaseMetaData meta)
			throws SQLException {
		List<String> tableTypes = new ArrayList<String>();
		ResultSet rs = meta.getTableTypes();
		while (rs.next())
			tableTypes.add(rs.getString("TABLE_TYPE")); //$NON-NLS-1$
		rs.close();
		return tableTypes;
	}
	
	public String getQuery() {
		if (selectedForm == null) {
			// throw exception???
			return "";
		}
		
		return selectedForm.getAccountId() + ":" + selectedForm.getAppId() + ":" + selectedForm.getTableId() + ":" + selectedForm.getFormId();
	}

	public class SelectionChangedListener implements ISelectionChangedListener {
		
		TrackViaMetadata tvMetadata;
		
		public SelectionChangedListener(TrackViaMetadata tvMetadata) {
			this.tvMetadata = tvMetadata;
		}
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TreeSelection element = (TreeSelection) event.getSelection();
			Object selection = element.getFirstElement();

			if (selection instanceof TrackViaForm) {
				// specific form has been selected. we want to mark it 
				tvMetadata.setSelectedForm(selection);
			}
		}
	}
}
