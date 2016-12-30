package com.xrr.bean;

import java.io.Serializable;

/**
 * created by raorao 2016/12/29
 * function: this is the description of uploadded image info.
 * @author ubuntu
 *
 */

public class ImageInfo implements Serializable{
	
	public String url;
	public String hash;
	
	public ImageInfo(String url, String hash){
		this.url = url;
		this.hash = hash;
	}
	
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ObjectFeature key = (ObjectFeature)obj;
        boolean r1 = (url == null) ? (key.url == null) : (url.equals(key.url));
        boolean r2 = (hash == null) ? (key.hash == null) : (hash.equals(key.hash));
        return r1 && r2;
    }

    public long mHashCode() {
    	long h1 = url == null ? 0 :url.hashCode();
    	long h2 = hash == null ? 0 : hash.hashCode();
    	long hashCode = h1;
        hashCode = hashCode*31+h2;
        return hashCode;
    }
}
