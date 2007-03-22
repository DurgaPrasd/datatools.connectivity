/*
 *************************************************************************
 * Copyright (c) 2006, 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation - initial API and implementation
 *  
 *************************************************************************
 */

package org.eclipse.datatools.connectivity.oda.design.ui.wizards;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceEditorPageCore;
import org.eclipse.swt.widgets.Composite;

/**
 * The abstract base class for implementation of a customized
 * ODA data source property page.  
 * <br>An ODA data source UI extension must extend this base class to
 * provide customized property page with page control 
 * and related behavior, and implements the 
 * <code>org.eclipse.ui.propertyPages</code> extension point.
 */
public abstract class DataSourceEditorPage extends DataSourceEditorPageCore
{    
    /**
     * Creates and initializes custom control for this wizard page 
     * under the given parent composite.
     * @param parent        parent composite
     * @param profileProps  current custom properties and values 
     *                      to initialize the custom controls
     */
    protected abstract void createAndInitCustomControl( Composite parent,
                                        Properties profileProps );

    /**
     * Returns data source properties and corresponding values collected in 
     * custom page(s).
     * @param dataSourceProps  the current set of custom and inherited properties,
     *                      before any updates in this page
     * @return  the updated data source properties and corresponding values
     */
    public abstract Properties collectCustomProperties( Properties dataSourceProps );

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceEditorPageCore#collectDataSourceDesign(org.eclipse.datatools.connectivity.oda.design.DataSourceDesign)
     */
    protected DataSourceDesign collectDataSourceDesign( DataSourceDesign design )
    {
        // default implementation does nothing;
        // sub-class may override to update the given data source design
        return design;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceEditorPageCore#cleanup()
     */
    protected void cleanup()
    {
        // default implementation does nothing;
        // sub-class may override        
    }

    public DataSourceEditorPage()
    {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#createCustomContents(org.eclipse.swt.widgets.Composite)
     */
    protected void createCustomContents( Composite parent )
    {
        Properties props = getDataSourceProperties();
        // calls abstract method provided by custom page extension
        createAndInitCustomControl( parent, props );
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.wizards.ProfileDetailsPropertyPage#collectProperties()
     */
    protected Properties collectProperties()
    {
        Properties props = getDataSourceProperties();
        // calls abstract method provided by custom page extension
        return collectCustomProperties( props );
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceEditorPageCore#getOdaDataSourceId()
     */
    public String getOdaDataSourceId()
    {
        return super.getOdaDataSourceId();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceEditorPageCore#refresh(java.util.Properties)
	 * @since 3.0.4
     */
    protected void refresh( Properties customConnectionProps )
    {
        // default implementation does nothing;
        // sub-class may override
    }
    
}
