package com.jaspersoft.studio.data.xvia.model;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.viewers.StyledString;

import com.jaspersoft.studio.data.xvia.querydesigner.TrackViaMetadata;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.preferences.fonts.utils.FontUtils;
import com.jaspersoft.studio.utils.Misc;

public class TrackViaApp extends TrackViaObjects {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	private String appId;
	private boolean isNotInMetadata = false;

	public TrackViaApp(ANode parent, String value, String appId,
			boolean isNotInMetadata) {
		this(parent, value, appId);
		this.isNotInMetadata = isNotInMetadata;
	}

	public TrackViaApp(ANode parent, String value, String appId) {
		super(parent, value, "icons/database.png");
		this.appId = appId;
	}

	public boolean isNotInMetadata() {
		return isNotInMetadata;
	}

	public void setNotInMetadata(boolean isNotInMetadata) {
		this.isNotInMetadata = isNotInMetadata;
	}

	public String getAppId() {
		return appId;
	}

	private boolean isCurrent;

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	@Override
	public String getToolTip() {
		String tt = super.getToolTip();
		if (isCurrent)
			tt += " (CURRENT)";
		return tt;
	}

	@Override
	public String getDisplayText() {
		String dt = super.getDisplayText();
		if (isCurrent)
			dt += " (CURRENT)";
		return dt;
	}

	@Override
	public StyledString getStyledDisplayText() {
		StyledString dt = new StyledString(Misc.nvl(super.getDisplayText()));
		if (isCurrent)
			dt.append(" (CURRENT)", FontUtils.FIELD_STYLER);
		return dt;
	}

	private transient TrackViaMetadata dbMetadata;

	public void setDbMetadata(TrackViaMetadata dbMetadata) {
		this.dbMetadata = dbMetadata;
	}

	public TrackViaMetadata getDbMetadata() {
		return dbMetadata;
	}
}