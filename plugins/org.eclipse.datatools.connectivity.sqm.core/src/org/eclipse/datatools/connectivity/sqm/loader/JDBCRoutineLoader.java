/*******************************************************************************
 * Copyright (c) 2006 Sybase, Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: rcernich - initial API and implementation
 ******************************************************************************/
package org.eclipse.datatools.connectivity.sqm.loader;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.datatools.connectivity.sqm.internal.core.connection.ConnectionFilter;
import org.eclipse.datatools.connectivity.sqm.internal.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.internal.core.rte.jdbc.JDBCProcedure;
import org.eclipse.datatools.connectivity.sqm.internal.core.rte.jdbc.JDBCUserDefinedFunction;
import org.eclipse.datatools.modelbase.sql.routines.Routine;
import org.eclipse.datatools.modelbase.sql.routines.SQLRoutinesPackage;
import org.eclipse.datatools.modelbase.sql.schema.Schema;
import org.eclipse.emf.ecore.EClass;

/**
 * Base loader implementation for loading a database's catalog objects. This
 * class may be specialized as necessary to meet a particular vendor's needs.
 * 
 * @author rcernich
 * 
 * Created on Aug 28, 2006
 */
public class JDBCRoutineLoader extends JDBCBaseLoader {

	/**
	 * The column name containing the table name.
	 * 
	 * @see java.sql.DatabaseMetaData.getProcedures()
	 */
	public static final String COLUMN_PROCEDURE_CAT = "PROCEDURE_CAT"; //$NON-NLS-1$

	/**
	 * The column name containing the catalog name.
	 * 
	 * @see java.sql.DatabaseMetaData.getProcedures()
	 */
	public static final String COLUMN_PROCEDURE_SCHEM = "PROCEDURE_SCHEM"; //$NON-NLS-1$

	/**
	 * The column name containing the catalog name.
	 * 
	 * @see java.sql.DatabaseMetaData.getProcedures()
	 */
	public static final String COLUMN_PROCEDURE_NAME = "PROCEDURE_NAME"; //$NON-NLS-1$

	/**
	 * The column name containing the catalog name.
	 * 
	 * @see java.sql.DatabaseMetaData.getProcedures()
	 */
	public static final String COLUMN_REMARKS = "REMARKS"; //$NON-NLS-1$

	/**
	 * The column name containing the catalog name.
	 * 
	 * @see java.sql.DatabaseMetaData.getProcedures()
	 */
	public static final String COLUMN_PROCEDURE_TYPE = "PROCEDURE_TYPE"; //$NON-NLS-1$

	private IRoutineFactory mUserDefinedFunctionFactory;
	private IRoutineFactory mProcedureFactory;

	/**
	 * @param catalogObject the Database object upon which this loader operates.
	 */
	public JDBCRoutineLoader(ICatalogObject catalogObject) {
		this(catalogObject, new SchemaObjectFilterProvider(
				ConnectionFilter.STORED_PROCEDURE_FILTER));
	}

	public JDBCRoutineLoader(
								ICatalogObject catalogObject,
								IConnectionFilterProvider connectionFilterProvider) {
		this(catalogObject, connectionFilterProvider,
				new UserDefinedFunctionFactory(), new ProcedureFactory());
	}

	public JDBCRoutineLoader(
								ICatalogObject catalogObject,
								IConnectionFilterProvider connectionFilterProvider,
								IRoutineFactory udfFactory,
								IRoutineFactory spFactory) {
		super(catalogObject, connectionFilterProvider);
		assert (catalogObject instanceof Schema);

		mUserDefinedFunctionFactory = udfFactory;
		mProcedureFactory = spFactory;
	}

	/**
	 * @param existingCatalogs the catalog objects which were previously loaded
	 * @return
	 * @throws SQLException
	 */
	public List loadRoutines(List existingRoutines) throws SQLException {
		List retVal = new ArrayList(existingRoutines.size());
		ResultSet rs = null;
		try {
			for (rs = createResultSet(); rs.next();) {
				Routine routine = processRow(rs, existingRoutines);
				if (routine != null) {
					retVal.add(routine);
				}
			}
			return retVal;
		}
		finally {
			if (rs != null) {
				closeResultSet(rs);
			}
			clearRoutines(existingRoutines);
		}
	}

	protected void clearRoutines(List routines) {
		routines.clear();
	}

	protected ResultSet createResultSet() throws SQLException {
		Schema schema = getSchema();
		return getCatalogObject().getConnection().getMetaData().getProcedures(
				schema.getCatalog().getName(), schema.getName(),
				getJDBCFilterPattern());
	}

	protected void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		}
		catch (SQLException e) {
		}
	}

	protected Routine processRow(ResultSet rs, List existingRoutines)
			throws SQLException {
		String routineName = rs.getString(COLUMN_PROCEDURE_NAME);
		if (routineName == null || isFiltered(routineName)) {
			return null;
		}
		Routine routine = null;

		IRoutineFactory routineFactory = rs.getShort(COLUMN_PROCEDURE_TYPE) == DatabaseMetaData.procedureNoResult ? mProcedureFactory
				: mUserDefinedFunctionFactory;
		EClass routineClass = routineFactory.getRoutineEClass();
		for (Iterator it = existingRoutines.iterator(); routine != null
				&& it.hasNext();) {
			Object obj = it.next();
			if (routineName.equals(((Routine) obj).getName())
					&& routineClass.isSuperTypeOf(((Routine) obj).eClass())) {
				routine = (Routine) obj;
			}
		}
		if (routine == null) {
			routine = routineFactory.createRoutine(rs);
		}
		else {
			((ICatalogObject) routine).refresh();
			existingRoutines.remove(routine);
		}
		return routine;
	}

	protected Schema getSchema() {
		return (Schema) getCatalogObject();
	}

	public static interface IRoutineFactory {

		EClass getRoutineEClass();

		Routine createRoutine(ResultSet rs) throws SQLException;
	}

	public static class ProcedureFactory implements IRoutineFactory {

		public EClass getRoutineEClass() {
			return SQLRoutinesPackage.eINSTANCE.getProcedure();
		}

		public Routine createRoutine(ResultSet rs) throws SQLException {
			Routine retVal = newRoutine();
			initialize(retVal, rs);
			return retVal;
		}

		protected Routine newRoutine() {
			return new JDBCProcedure();
		}

		protected void initialize(Routine routine, ResultSet rs)
				throws SQLException {
			routine.setName(rs.getString(COLUMN_PROCEDURE_NAME));
			routine.setDescription(rs.getString(COLUMN_REMARKS));
		}

	}

	public static class UserDefinedFunctionFactory extends ProcedureFactory {

		public EClass getRoutineEClass() {
			return SQLRoutinesPackage.eINSTANCE.getUserDefinedFunction();
		}

		protected Routine newRoutine() {
			return new JDBCUserDefinedFunction();
		}
	}

}