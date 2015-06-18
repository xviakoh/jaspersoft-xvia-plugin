package com.jaspersoft.studio.data.xvia.adapter;

import net.sf.jasperreports.data.DataAdapter;

public interface TrackViaDataAdapter extends DataAdapter {
    public void setTrackViaURI(String trackViaURI);

    public String getTrackViaURI();

    public void setUsername(String username);

    public String getUsername();

    public void setPassword(String password);

    public String getPassword();
}