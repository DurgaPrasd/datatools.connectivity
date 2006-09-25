/*******************************************************************************
 * Copyright (c) 2004-2005 Sybase, Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: shongxum - initial API and implementation
 ******************************************************************************/
package org.eclipse.datatools.connectivity.ui.actions;

import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.datatools.connectivity.internal.ui.dialogs.ExceptionHandler;
import org.eclipse.datatools.connectivity.internal.ui.refactoring.ConnectionProfileDeleteProcessor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.core.refactoring.CheckConditionsOperation;
import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;

/**
 * @author shongxum, brianf
 */
public class DeleteAction extends Action implements IActionDelegate {

	private Shell mParentShell = null;
	private Iterator mIterator = null;

	/**
	 * 
	 */
	public DeleteAction() {
		Display display = Display.getCurrent();
		mParentShell = display.getActiveShell();

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run() {
		Object object;
		IConnectionProfile profile;
		while (mIterator != null && mIterator.hasNext()) {
			object = mIterator.next();
			if (object instanceof IConnectionProfile) {
				profile = (IConnectionProfile) object;
			}
			else {
				continue;
			}

			boolean ret = MessageDialog.openQuestion(mParentShell,
					ConnectivityUIPlugin.getDefault().getResourceString(
							"actions.delete.title"), ConnectivityUIPlugin //$NON-NLS-1$
							.getDefault().getResourceString(
									"actions.delete.confirm", //$NON-NLS-1$
									new String[] { profile.getName()}));
			if (!ret)
				return;
			try {
				refactor(profile);
//				ProfileManager.getInstance().deleteProfile(profile);
//			} catch (ConnectionProfileException e) {
//				ExceptionHandler.showException(mParentShell,
//						ConnectivityUIPlugin.getDefault().getResourceString(
//								"dialog.title.error"), e //$NON-NLS-1$
//								.getMessage(), e);
			} catch (CoreException e) {
				ExceptionHandler.showException(mParentShell, ConnectivityUIPlugin
				.getDefault().getResourceString("dialog.title.error"), e //$NON-NLS-1$
				.getMessage(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		mIterator = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			action.setEnabled(structuredSelection.size() > 0);
			if (structuredSelection.size() > 0) {
				mIterator = structuredSelection.iterator();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();
	}

	private void refactor (IConnectionProfile profile) throws CoreException {
    	//  Refactor for rename
    	PerformRefactoringOperation refOperation = new PerformRefactoringOperation(
    			new DeleteRefactoring(new ConnectionProfileDeleteProcessor(profile)), 
    				CheckConditionsOperation.ALL_CONDITIONS);
    	try 
    	{
    		ResourcesPlugin.getWorkspace().run(refOperation, null);
    	}
    	catch (OperationCanceledException oce) 
    	{
    		throw new OperationCanceledException();			
    	}
    	catch (CoreException ce) 
    	{
    		throw ce;
    	}	
	}
}