package com.jaspersoft.studio.data.xvia.adapter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.data.AbstractDataAdapterService;
import net.sf.jasperreports.data.DataAdapter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xvia.fullapi.client.LoginResponse;
import com.xvia.fullapi.client.RequestService;
import com.xvia.fullapi.client.RequestService.HttpErrorException;

public class TrackViaDataAdapterService extends AbstractDataAdapterService {
	
    public TrackViaDataAdapterService(JasperReportsContext jasperReportsContext, DataAdapter dataAdapter) {
    	super(jasperReportsContext, dataAdapter);
    }
    
    @Override
    public void contributeParameters(Map<String, Object> parameters) throws JRException {
    	
    }
    
    @Override
    public void test() throws JRException {
    	TrackViaDataAdapter dataAdapter = (TrackViaDataAdapter) this.getDataAdapter();
    	String username = dataAdapter.getUsername();
    	String password = dataAdapter.getPassword();
    	String apiURL = dataAdapter.getTrackViaURI();
    	
    	RequestService requestService = new RequestService(apiURL);
 		Map<String, String> queryParams = new HashMap<String, String>();
 		queryParams.put("username", username);
 		queryParams.put("password", password);
 		queryParams.put("grant_type", "password");
 		queryParams.put("client_id", "xvia-webapp");
 		
 		try {
			LoginResponse response = requestService.serverRequest(HttpPost.METHOD_NAME, queryParams, "/oauth/token", "", new TypeReference<LoginResponse>(){}, true);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
    }
}
