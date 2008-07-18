/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.datatools.connectivity.ui.wizards;

import java.util.Enumeration;
import java.util.Properties;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.DriverListCombo;
import org.eclipse.datatools.connectivity.internal.ui.IHelpConstants;
import org.eclipse.datatools.connectivity.internal.ui.wizards.DriverUIContributorComposite;
import org.eclipse.datatools.help.HelpUtil;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ExtensibleProfileDetailsPropertyPage extends
		ProfileDetailsPropertyPage implements IDriverUIContributorInformation {
	private String driverCategoryID = ""; //$NON-NLS-1$

	private DriverListCombo driverCombo = null;

	private DriverUIContributorComposite contributedUIComposite = null;

	private Properties properties = null;

	public ExtensibleProfileDetailsPropertyPage(String driverCategoryID) {
		super();
		noDefaultAndApplyButton();
		this.driverCategoryID = driverCategoryID;
	}

	protected Properties collectProperties() {
		return getProperties();
	}

	protected void createCustomContents(Composite parent, boolean isReadOnly) {
		parent.setLayout(new GridLayout());

		driverCombo = new DriverListCombo(isReadOnly);
		driverCombo
				.setLabelText(ConnectivityUIPlugin
						.getDefault()
						.getResourceString(
								"ExtensibleProfileDetailsWizardPage.driverCombo.label")); //$NON-NLS-1$
		driverCombo.setCategory(this.driverCategoryID);
		driverCombo.setNullDriverIsValid(false);
		driverCombo.createContents(parent);

		contributedUIComposite = new DriverUIContributorComposite(parent, this,
				this, isReadOnly);

		driverCombo.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				handleDriverComboSelectionChangeEvent(e);
			}
		});

		if (driverCombo.getErrorMessage() != null) {
			setErrorMessage(driverCombo.getErrorMessage());
		}

		initialize();
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		HelpUtil.setHelp( getControl(), HelpUtil.getContextId(IHelpConstants.GENERIC_DB_PROFILE_PROPERTY_PAGE, ConnectivityUIPlugin.getDefault().getBundle().getSymbolicName()));
		return contents;
	}

	private void initialize() {
		IConnectionProfile connectionProfile = getConnectionProfile();
		String driverDefinitionID = connectionProfile.getBaseProperties()
				.getProperty(
						ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID);
		if (driverDefinitionID != null) {
			driverCombo.setSelectionToID(driverDefinitionID);
			handleDriverComboSelectionChangeEvent(new ChangeEvent(driverCombo.getCombo()));
		}
	}

	private void handleDriverComboSelectionChangeEvent(ChangeEvent e) {
		if (driverCombo.getErrorMessage() != null) {
			setErrorMessage(driverCombo.getErrorMessage());
		}
		if (driverCombo.getSelectedDriverInstance() != null) {
			this.properties = copyProperties(getConnectionProfile()
					.getBaseProperties());
			this.properties.setProperty(
					ConnectionProfileConstants.PROP_DRIVER_DEFINITION_ID,
					driverCombo.getSelectedDriverID());
		}
		contributedUIComposite.setDriverTemplateID(driverCombo
				.getSelectedDriverID());
	}

	public boolean determinePageCompletion() {
		boolean isComplete = contributedUIComposite
				.determineContributorCompletion();
		if (isComplete) {
			setErrorMessage(null);
		}
		this.setPingButtonEnabled(isComplete);
		return isComplete;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		determinePageCompletion();
	}

	private Properties copyProperties(Properties properties) {
		Properties copy = new Properties();
		Enumeration propertyKeys = properties.keys();
		while (propertyKeys.hasMoreElements()) {
			Object key = propertyKeys.nextElement();
			copy.put(key, properties.get(key));
		}
		return copy;
	}
}