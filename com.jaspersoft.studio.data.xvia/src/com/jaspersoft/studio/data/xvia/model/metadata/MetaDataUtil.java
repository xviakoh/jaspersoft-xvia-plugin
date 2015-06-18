package com.jaspersoft.studio.data.xvia.model.metadata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.client.methods.HttpGet;
import org.eclipse.core.runtime.IProgressMonitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jaspersoft.studio.data.xvia.model.TrackViaForm;
import com.jaspersoft.studio.data.xvia.model.TrackViaApp;
import com.jaspersoft.studio.model.INode;
import com.xvia.fullapi.client.RequestService;
import com.xvia.response.base.form.FormListResponse;
import com.xvia.response.base.form.TableFormResponse;

public class MetaDataUtil {
	public synchronized static void readTables(RequestService requestService, String accountId, TrackViaApp schema, IProgressMonitor monitor, List<String> tableTypes) {
		try {
			schema.removeChildren();
			schema.setNotInMetadata(false);
			
			TreeSet<TableFormResponse> tfrs = requestService.serverRequest(HttpGet.METHOD_NAME, null, "/accounts/" + accountId + "/apps" + "/" + schema.getAppId() + "/forms", "", new TypeReference<TreeSet<TableFormResponse>>(){});
			
			for (TableFormResponse tfr : tfrs) {
				new TrackViaTable(schema, tfr.getName());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public synchronized static void readTableForms(RequestService requestService, String accountId, TrackViaApp schema, LinkedHashMap<String, TrackViaForm> tables,
			IProgressMonitor monitor) {
		try {
			for (INode n : schema.getChildren())
				if (n instanceof TrackViaTable) {
					((TrackViaTable) n).setDbMetadata(schema.getDbMetadata());
					MetaDataUtil.readForms(requestService, ((TrackViaTable) n).getValue(), accountId, schema.getAppId(), (TrackViaTable) n, tables, monitor);
				}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void readForms(RequestService requestService, String tableName, String accountId, String appId, TrackViaTable mview, LinkedHashMap<String, TrackViaForm> tblMap, IProgressMonitor monitor) {
		try {
			TreeSet<TableFormResponse> tfrs = requestService.serverRequest(HttpGet.METHOD_NAME, null, "/accounts/" + accountId + "/apps" + "/" + appId + "/forms", "", new TypeReference<TreeSet<TableFormResponse>>(){});
			
			for (TableFormResponse tfr : tfrs) {
				if (tfr.getName().equals(tableName)) {
					TreeSet<FormListResponse> forms = tfr.getForms();
					for (FormListResponse flr : forms) {
						String formName = flr.getName();
						if (formName == null) {
							// default form
							formName = tableName + " Default Form";
						}
						new TrackViaForm(mview, formName, accountId, flr.getAppId(), flr.getTableId(), flr.getId());
					}
				}
				if (monitor.isCanceled())
					break;
			}		
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

