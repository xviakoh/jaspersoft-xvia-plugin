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
package com.jaspersoft.studio.editor.preview.view.report.swt.action;

import net.sf.jasperreports.eclipse.viewer.IReportViewer;
import net.sf.jasperreports.eclipse.viewer.action.AReportAction;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.messages.Messages;

public class LastPageAction extends AReportAction {

	public LastPageAction(IReportViewer rviewer) {
		super(rviewer);
		setText(Messages.LastPageAction_actionName); 
		setToolTipText(Messages.LastPageAction_actionTooltip); 
		setImageDescriptor(
				JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/nav/last.gif"));//$NON-NLS-1$
		setDisabledImageDescriptor(
				JaspersoftStudioPlugin.getInstance().getImageDescriptor("icons/resources/nav/lastd.gif"));//$NON-NLS-1$
	}

	public boolean isActionEnabled() {
		return rviewer.canGotoLastPage();
	}

	@Override
	public void run() {
		rviewer.gotoLastPage();
	}

}
