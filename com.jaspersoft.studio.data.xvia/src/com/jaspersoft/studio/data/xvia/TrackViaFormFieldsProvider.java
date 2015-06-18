package com.jaspersoft.studio.data.xvia;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.jasperreports.data.DataAdapterService;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.design.JRDesignField;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jaspersoft.studio.data.fields.IFieldsProvider;
import com.jaspersoft.studio.data.xvia.adapter.TrackViaDataAdapter;
import com.jaspersoft.studio.utils.jasper.JasperReportsConfiguration;
import com.jaspersoft.studio.utils.parameter.ParameterUtil;
import com.xvia.fullapi.client.LoginResponse;
import com.xvia.fullapi.client.RequestService;
import com.xvia.fullapi.client.RequestService.HttpErrorException;
import com.xvia.response.base.form.ContainerElement;
import com.xvia.response.base.form.Element;
import com.xvia.response.base.form.Field;
import com.xvia.response.base.form.FormResponse;

public class TrackViaFormFieldsProvider implements IFieldsProvider {
	protected TrackViaDataAdapter dataAdapter;
	protected RequestService requestService;
	
	public TrackViaFormFieldsProvider(TrackViaDataAdapter dataAdapter) {
		this.dataAdapter = dataAdapter;
		login(dataAdapter);
	}
	
	public boolean supportsGetFieldsOperation(JasperReportsConfiguration jConfig) {
		return true;
	}

	public List<JRDesignField> getFields(DataAdapterService con, JasperReportsConfiguration jConfig, JRDataset dataset) throws JRException, UnsupportedOperationException {
		List<JRDesignField> fields = new ArrayList<JRDesignField>();
		try {
			JRQuery query = dataset.getQuery();
			String queryString = query.getText();
		
			String[] queryParts = queryString.split(":");
			FormResponse fr = requestService.serverRequest(HttpGet.METHOD_NAME, null, "/accounts/" + queryParts[0] + "/apps/" + queryParts[1] + "/tables/" + queryParts[2] + "/forms/" + queryParts[3], "", new TypeReference<FormResponse>(){});
			TreeSet<ContainerElement> fe = fr.getElements();
			for (ContainerElement ce : fe) {
				List<Field> formfields = getContainerElementFields(ce);
				for (Field formField : formfields) {
					System.out.println("--field: " + formField.getName() + ", ID: " + formField.getId());
					
					JRDesignField field = new JRDesignField();
			        field.setName(formField.getName());
			        field.setValueClass(java.lang.String.class);
			        field.setDescription(null);
			        
			        fields.add(field);
				}
			}
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fields;
	}
	
	protected void login (TrackViaDataAdapter trackViaDataAdapter) {		
		// initialize API client
        String username = trackViaDataAdapter.getUsername();
    	String password = trackViaDataAdapter.getPassword();
    	String apiURL = trackViaDataAdapter.getTrackViaURI();
    	
    	requestService = new RequestService(apiURL);
 		Map<String, String> queryParams = new HashMap<String, String>();
 		queryParams.put("username", username);
 		queryParams.put("password", password);
 		queryParams.put("grant_type", "password");
 		queryParams.put("client_id", "xvia-webapp");
 		
 		try {
			LoginResponse response = requestService.serverRequest(HttpPost.METHOD_NAME, queryParams, "/oauth/token", "", new TypeReference<LoginResponse>(){}, true);
			requestService.setLoginResponse(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected List<Field> getContainerElementFields(ContainerElement ce) {
		List<Field> result = new ArrayList<Field>();
		for (Element e  : ce.getElements()) {
			if (e instanceof ContainerElement) {
				result.addAll(getContainerElementFields((ContainerElement)e));
			} else if (e instanceof Field) {
				result.add((Field)e);
			}
		}
		return result;
	}
}
