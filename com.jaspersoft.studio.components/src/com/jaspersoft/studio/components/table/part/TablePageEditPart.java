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
package com.jaspersoft.studio.components.table.part;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.design.JRDesignElement;

import org.eclipse.draw2d.geometry.Dimension;

import com.jaspersoft.studio.callout.MCallout;
import com.jaspersoft.studio.components.table.model.MTable;
import com.jaspersoft.studio.editor.gef.figures.APageFigure;
import com.jaspersoft.studio.editor.gef.figures.ContainerPageFigure;
import com.jaspersoft.studio.editor.gef.parts.PageEditPart;
import com.jaspersoft.studio.model.IGraphicElement;
import com.jaspersoft.studio.model.INode;
import com.jaspersoft.studio.model.util.ShowChildrenModelVisitor;
import com.jaspersoft.studio.property.dataset.dialog.IDatasetDialogSupport;

public class TablePageEditPart extends PageEditPart implements
		IDatasetDialogSupport {

	@Override
	protected void setupPageFigure(APageFigure figure2) {
		updateContainerSize();
		((ContainerPageFigure) figure2).setContainerSize(getContaierSize());

		setupPagePreferences(figure2);
	}

	private Dimension containerSize;

	public Dimension getContaierSize() {
		return containerSize;
	}

	private void updateContainerSize() {
		MTable table = null;
		for (INode n : getPage().getChildren()) {
			if (n instanceof MTable) {
				table = (MTable) n;
				break;
			}
		}
		if (table != null) {
			Dimension d = table.getTableManager().getSize();
			d.height = Math.max(d.height, (Integer) table
					.getPropertyValue(JRDesignElement.PROPERTY_HEIGHT));
			d.width = Math.max(d.width, (Integer) table
					.getPropertyValue(JRDesignElement.PROPERTY_WIDTH));
			containerSize = d;
		} else
			containerSize = new Dimension(4000, 4000);
	}
	
	@Override
	protected List<Object> getModelChildren() {
		final List<Object> list = new ArrayList<Object>();
		new ShowChildrenModelVisitor<Object>(getPage()) {

			@Override
			public boolean visit(INode n) {
				if (n instanceof MCallout) {
					list.add(n);
					for (INode node : n.getChildren())
						list.add(node);
				} else if (n instanceof IGraphicElement && n.getValue() != null)
					list.add(n);

				return true;
			}
		};
		return list;
	}
}
