/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.connectivity.sqm.core.internal.ui.explorer.providers.content.virtual;

import org.eclipse.datatools.connectivity.sqm.core.internal.ui.explorer.virtual.IConstraintNode;
import org.eclipse.datatools.connectivity.sqm.core.internal.ui.icons.ImageDescription;
import org.eclipse.datatools.connectivity.sqm.core.internal.ui.util.resources.ResourceLoader;
import org.eclipse.datatools.connectivity.sqm.core.ui.explorer.providers.content.virtual.VirtualNode;
import org.eclipse.datatools.connectivity.sqm.internal.core.containment.GroupID;
import org.eclipse.datatools.modelbase.sql.constraints.SQLConstraintsPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * @author ljulien
 */
public class ConstraintNode extends VirtualNode implements IConstraintNode
{
	/**
	 * @param name
	 * @param displayName
	 */
	public ConstraintNode(String name, String displayName, Object parent)
	{
		super(name, displayName, parent);
	}

    public String getGroupID ()
    {
        return GroupID.CONSTRAINT;
    }

    //@Override
	public ImageDescriptor[] getCreateImageDescriptor() {
		return new ImageDescriptor[] { 
				ImageDescription.getTableCheckConstraintDescriptor(),
				ImageDescription.getTableCheckConstraintDescriptor(),
				ImageDescription.getFKDecorationDescriptor()
		};
	}

	//@Override
	public String[] getCreateLabel() {
		return new String[] {
				ResourceLoader.getResourceLoader().queryString("SCHEMA_MANAGEMENT_ADD_CHECK_CONSTRAINT"),
				ResourceLoader.getResourceLoader().queryString("SCHEMA_MANAGEMENT_ADD_UNIQUE_CONSTRAINT"),
				ResourceLoader.getResourceLoader().queryString("SCHEMA_MANAGEMENT_ADD_FOREIGN_KEY")
		};
	}

	//@Override
	public EClass[] getCreateType() {
		return new EClass[] {
				SQLConstraintsPackage.eINSTANCE.getCheckConstraint(),
				SQLConstraintsPackage.eINSTANCE.getUniqueConstraint(),
				SQLConstraintsPackage.eINSTANCE.getForeignKey()
		};
	}

	//@Override
	public boolean shouldDisplayCreate() {
		return false;
	}

	//@Override
	public boolean shouldDisplayAdd() {
		return true;
	}

}
