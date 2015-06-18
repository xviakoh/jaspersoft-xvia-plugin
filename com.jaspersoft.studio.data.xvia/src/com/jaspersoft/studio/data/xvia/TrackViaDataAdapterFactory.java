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
package com.jaspersoft.studio.data.xvia;

import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.data.DataAdapterDescriptor;
import com.jaspersoft.studio.data.DataAdapterFactory;
import com.jaspersoft.studio.data.adapter.IDataAdapterCreator;
import com.jaspersoft.studio.data.xvia.adapter.TrackViaDataAdapter;
import com.jaspersoft.studio.data.xvia.adapter.TrackViaDataAdapterImpl;
import com.jaspersoft.studio.data.xvia.adapter.TrackViaDataAdapterService;
import com.jaspersoft.studio.data.xvia.messages.Messages;

/**
 * @author gtoffoli
 * 
 */
public class TrackViaDataAdapterFactory implements DataAdapterFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#createDataAdapter()
     */
    public DataAdapterDescriptor createDataAdapter() {
        TrackViaDataAdapterDescriptor descriptor = new TrackViaDataAdapterDescriptor();
        descriptor.getDataAdapter().setTrackViaURI("https://go.trackvia.com"); //$NON-NLS-1$
        descriptor.getDataAdapter().setUsername(""); //$NON-NLS-1$
        descriptor.getDataAdapter().setPassword(""); //$NON-NLS-1$
        return descriptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.jaspersoft.studio.data.DataAdapterFactory#getDataAdapterClassName()
     */
    public String getDataAdapterClassName() {
        return TrackViaDataAdapterImpl.class.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getLabel() {
        return Messages.TrackViaDataAdapterFactory_label;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getDescription()
     */
    public String getDescription() {
        return Messages.TrackViaDataAdapterFactory_description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jaspersoft.studio.data.DataAdapterFactory#getIcon(int)
     */
    public Image getIcon(int size) {
        if (size == 16) {
            return Activator.getDefault().getImage(Activator.ICON_NAME);
        }
        return null;
    }

    public DataAdapterService createDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
        if (dataAdapter instanceof TrackViaDataAdapter) {
        	return new TrackViaDataAdapterService(jasperReportsContext, (TrackViaDataAdapter) dataAdapter);
        }
        return null;
    }

	@Override
	public IDataAdapterCreator iReportConverter() {
		return new TrackViaCreator();
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}
}
