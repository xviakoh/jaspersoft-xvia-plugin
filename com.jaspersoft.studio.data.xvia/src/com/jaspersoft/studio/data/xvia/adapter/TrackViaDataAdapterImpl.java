package com.jaspersoft.studio.data.xvia.adapter;

import net.sf.jasperreports.data.AbstractDataAdapter;

public class TrackViaDataAdapterImpl extends AbstractDataAdapter implements TrackViaDataAdapter {
    
	private String trackViaURI;

    private String username;

    private String password;
    
	@Override
	public void setTrackViaURI(String trackViaURI) {
		this.trackViaURI = trackViaURI;
	}

	@Override
	public String getTrackViaURI() {
		return this.trackViaURI;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

}
