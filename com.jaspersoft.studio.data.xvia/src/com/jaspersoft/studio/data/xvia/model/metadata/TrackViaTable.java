package com.jaspersoft.studio.data.xvia.model.metadata;

import net.sf.jasperreports.engine.JRConstants;

import org.apache.commons.lang.WordUtils;

import com.jaspersoft.studio.data.xvia.model.TrackViaObjects;
import com.jaspersoft.studio.data.xvia.model.TrackViaApp;
import com.jaspersoft.studio.data.xvia.querydesigner.TrackViaMetadata;

public class TrackViaTable extends TrackViaObjects {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public TrackViaTable(TrackViaApp parent, String value) {
		super(parent, value, "icons/table.png");
	}

	public String getTableCatalog() {
		return ((TrackViaApp) getParent()).getAppId();
	}

	public String getTableSchema() {
		return (String) getParent().getValue();
	}

	@Override
	public String getDisplayText() {
		return WordUtils.capitalizeFully(getValue());
	}

	private transient TrackViaMetadata dbMetadata;

	public void setDbMetadata(TrackViaMetadata dbMetadata) {
		this.dbMetadata = dbMetadata;
	}

	public TrackViaMetadata getDbMetadata() {
		return dbMetadata;
	}
}
