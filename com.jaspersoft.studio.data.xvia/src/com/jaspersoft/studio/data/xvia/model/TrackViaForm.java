package com.jaspersoft.studio.data.xvia.model;

import net.sf.jasperreports.engine.JRConstants;

import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IDragable;

public class TrackViaForm extends TrackViaObjects implements IDragable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private String accountId;
	private Long appId;
	private Long tableId;
	private Long formId; 
	
	public TrackViaForm(ANode parent, String formName, String accountId, Long appId, Long tableId, Long formId) {
		super(parent, formName, "icons/table.png");
		this.accountId = accountId;
		this.appId = appId;
		this.tableId = tableId;
		this.formId = formId;
	}

	public TrackViaApp getSchema() {
		if (getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof TrackViaApp)
			return (TrackViaApp) getParent().getParent();
		return null;
	}

	public boolean isCurrentSchema() {
		return getParent() != null && getParent().getParent() != null && getParent().getParent() instanceof TrackViaApp && ((TrackViaApp) getParent().getParent()).isCurrent();
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

}
