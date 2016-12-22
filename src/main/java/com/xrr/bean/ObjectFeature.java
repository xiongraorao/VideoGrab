package com.xrr.bean;

import java.io.Serializable;

/**
 * created by raorao at 2016/12/21
 * detected object features
 * @author ubuntu
 *
 */
public class ObjectFeature implements Serializable{
	//picture local url or web url
    public String url;
    //the url of the video which the picture is grabbed from
    public String video_id;
    
    public String feature;

    public ObjectFeature()
    {

    }

    public ObjectFeature(String url, String video_id, String feature)
    {
        this.url = url;
        this.video_id = video_id;
        this.feature = feature;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ObjectFeature key = (ObjectFeature)obj;
        boolean r1 = (url == null) ? (key.url == null) : (url.equals(key.url));
        boolean r2 = (video_id == null) ? (key.video_id == null) : (video_id.equals(key.video_id));
        boolean r3 = (feature == null) ? (key.feature == null) : (feature.equals(key.feature));
        return r1 && r2 && r3;
    }

    
    public long mHashCode() {
    	long h1 = url == null ? 0 :url.hashCode();
    	long h2 = video_id == null ? 0 : video_id.hashCode();
    	long h3 = feature == null ? 0 : feature.hashCode();
    	long hashCode = h1;
        hashCode = hashCode*31+h2;
        hashCode = hashCode*31+h3;
        return hashCode;
    }
}
