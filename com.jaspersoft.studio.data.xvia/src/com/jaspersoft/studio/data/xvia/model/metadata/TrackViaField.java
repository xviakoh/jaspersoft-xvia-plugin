package com.jaspersoft.studio.data.xvia.model.metadata;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRConstants;

import org.eclipse.jface.resource.ImageDescriptor;

import com.jaspersoft.studio.data.xvia.model.TrackViaObjects;
import com.jaspersoft.studio.model.ANode;
import com.jaspersoft.studio.model.IDragable;

public class TrackViaField extends TrackViaObjects implements IDragable {
	public static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public TrackViaField(ANode parent, String value, ResultSet rs) {
		super(parent, value, null);
		try {
			if (rs != null) {
				typeName = rs.getString("TYPE_NAME");
				columnSize = rs.getInt("COLUMN_SIZE");
				scale = rs.getInt("DECIMAL_DIGITS");
				precission = rs.getInt("NUM_PREC_RADIX");
				nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
				tooltip = formatedType();
				remarks = rs.getString("REMARKS");
				if (remarks != null)
					tooltip += "\n" + remarks;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String remarks;
	private String typeName;
	private int columnSize;
	private int precission;
	private int scale;
	private boolean nullable;

	public String getRemarks() {
		return remarks;
	}

	@Override
	public String getToolTip() {
		return "tool tip - TODO change this";
	}

	@Override
	public ImageDescriptor getImagePath() {
		return super.getImagePath();
	}

	public String getTypeName() {
		return formatedType();
	}

	private String formatedType() {
		if (typeName == null)
			return "";
		String tname = "\n" + typeName;
		if (typeName.equalsIgnoreCase("VARCHAR") || typeName.equalsIgnoreCase("CHAR") || typeName.equalsIgnoreCase("CHARACTER") || typeName.equalsIgnoreCase("NATIONAL CHARACTER")
				|| typeName.equalsIgnoreCase("NCHAR") || typeName.equalsIgnoreCase("CHARACTER VARYING") || typeName.equalsIgnoreCase("NATIONAL CHARACTER VARYING") || typeName.equalsIgnoreCase("NVARCHAR")
				|| typeName.equalsIgnoreCase("BIT") || typeName.equalsIgnoreCase("BIT VARYING") || typeName.equalsIgnoreCase(" TEXT") || typeName.equalsIgnoreCase("STRING")
				|| typeName.equalsIgnoreCase("BINARY") || typeName.equalsIgnoreCase("VARBINARY") || typeName.equalsIgnoreCase("LONGVARBINARY") || typeName.equalsIgnoreCase("NVARCHAR2"))
			tname += "(" + columnSize + ")";
		else if (typeName.equalsIgnoreCase("NUMERIC") || typeName.equalsIgnoreCase("DECIMAL") || typeName.equalsIgnoreCase("NUMBER"))
			tname += "(" + precission + ", " + scale + ")";
		if (!nullable)
			tname += " NOT NULL";
		return tname;
	}
}
