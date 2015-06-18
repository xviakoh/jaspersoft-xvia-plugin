package com.jaspersoft.studio.data.xvia.model;

import java.util.UUID;

import net.sf.jasperreports.eclipse.JasperReportsPlugin;
import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.resource.ImageDescriptor;

import com.jaspersoft.studio.model.AMapElement;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.utils.Misc;

public class TrackViaObjects extends AMapElement {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	transient private ImageDescriptor icon;
	private String image;
	protected String tooltip;
	private String id;

	public TrackViaObjects(ANode parent, String value, String image) {
		this(parent, value, image, -1);
	}

	public TrackViaObjects(ANode parent, String value, String image, int index) {
		super(parent, index);
		setValue(value);
		this.image = image;
		id = UUID.randomUUID().toString();
	}

	@Override
	public TrackViaRoot getRoot() {
		return (TrackViaRoot) super.getRoot();
	}

	public String getId() {
		return id;
	}

	@Override
	public String getValue() {
		return (String) super.getValue();
	}

	@Override
	public String getToolTip() {
		String name = getValue();
		if (tooltip != null)
			name += "\n" + tooltip;
		return name;
	}

	@Override
	public ImageDescriptor getImagePath() {
		if (icon == null && image != null)
			icon = JasperReportsPlugin.getDefault().getImageDescriptor(image);
		return icon;
	}

	@Override
	public String getDisplayText() {
		return getValue();
	}

	public String toSQLString() {
		String str = getValue();
		if (str == null || str.isEmpty())
			return "";
		ANode p = getParent();
		TrackViaRoot r = getRoot();
		if (r == null)
			return "(...)";
		else {
			String IQ = r.getIdentifierQuote();
			boolean onlyException = r.isQuoteExceptions();
			while (p != null) {
				if (p instanceof TrackViaObjects) {
					if (p instanceof TrackViaApp && (((TrackViaApp) p).isCurrent()))
						return Misc.quote(getValue(), IQ, onlyException);
					String s = ((TrackViaObjects) p).toSQLString();
					if (Misc.isNullOrEmpty(s))
						return Misc.quote(getValue(), IQ, onlyException);
					return s + "." + Misc.quote(getValue(), IQ, onlyException);
				}
				p = p.getParent();
			}
			if (this instanceof TrackViaApp)
				return Misc.quote(getValue(), IQ, onlyException);
			else if (this instanceof TrackViaForm)
				return Misc.quote(getValue(), IQ, onlyException);
		}
		return str;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TrackViaObjects && ((TrackViaObjects) obj).getId().equals(getId());
	}

	public int hashCode() {
		return getId().hashCode();
	};
}

