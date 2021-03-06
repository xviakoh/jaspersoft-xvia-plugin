/*******************************************************************************
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved. http://www.jaspersoft.com.
 * 
 * Unless you have purchased a commercial license agreement from Jaspersoft, the following license terms apply:
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.jaspersoft.studio.property.descriptors;

import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.JaspersoftStudioPlugin;
import com.jaspersoft.studio.property.descriptor.NullEnum;
import com.jaspersoft.studio.property.section.AbstractSection;
import com.jaspersoft.studio.property.section.widgets.ASPropertyWidget;
import com.jaspersoft.studio.property.section.widgets.SPToolBarEnum;

public class TextHAlignPropertyDescriptor extends NamedEnumPropertyDescriptor<HorizontalTextAlignEnum> {

	public TextHAlignPropertyDescriptor(Object id, String displayName, NullEnum type) {
		super(id, displayName, HorizontalTextAlignEnum.CENTER, type);
	}

	public ASPropertyWidget<NamedEnumPropertyDescriptor<HorizontalTextAlignEnum>> createWidget(Composite parent,
			AbstractSection section) {
		Image[] images = new Image[] {
				JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/left_align.gif"),
				JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/center_align.gif"),
				JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/right_align.gif"),
				JaspersoftStudioPlugin.getInstance().getImage("icons/resources/eclipse/justified_align.gif") };
		return new SPToolBarEnum<NamedEnumPropertyDescriptor<HorizontalTextAlignEnum>>(parent, section, this, images, false);
	}
}
