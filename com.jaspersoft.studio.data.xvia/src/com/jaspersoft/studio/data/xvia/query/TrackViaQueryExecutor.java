package com.jaspersoft.studio.data.xvia.query;

import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;

public class TrackViaQueryExecutor extends JRAbstractQueryExecuter {

	protected TrackViaQueryExecutor(JasperReportsContext jasperReportsContext,
			JRDataset dataset,
			Map<String, ? extends JRValueParameter> parametersMap) {
		super(jasperReportsContext, dataset, parametersMap);
	}

	@Override
	public JRDataSource createDatasource() throws JRException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean cancelQuery() throws JRException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getParameterReplacement(String parameterName) {
		// TODO Auto-generated method stub
		return null;
	}

}
