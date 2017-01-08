package com.xrr.bean;

import java.io.Serializable;

/**
 * created by raorao at 2016/12/21
 * function:detected object features
 * @author ubuntu
 *
 */
public class ObjectFeature implements Serializable{
	//picture local url or web url
    public String url;
    //the url of the video which the picture is grabbed from
    public String video_id;
    //hash code as the test feature

    public String time;
    public String parent_img;//which image is this object-image from

    //object detect attributes in python dictionary output
    public String category;
    public String score;
    public String hash;
    public String location;
    public String feature;
    public String dir;
    public String fileName;
    
    public ObjectFeature()
    {

    }

    public ObjectFeature(String url, String video_id, String time, String parent_img, String category,
                         String score, String hash, String location, String feature, String dir, String fileName) {
        this.url = url;
        this.video_id = video_id;
        this.time = time;
        this.parent_img = parent_img;
        this.category = category;
        this.score = score;
        this.hash = hash;
        this.location = location;
        this.feature = feature;
        this.dir = dir;
        this.fileName = fileName;
    }

    public ObjectFeature(String url, String video_id, String parent_img, String hash, String time)
    {
        this.url = url;
        this.video_id = video_id;
        this.hash = hash;
        this.time = time;
        this.parent_img = parent_img;
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
        boolean r3 = (hash == null) ? (key.hash == null) : (hash.equals(key.hash));
        boolean r4 = (parent_img == null) ? (key.parent_img == null) : (parent_img.equals(key.parent_img));
        return r1 && r2 && r3 && r4;
    }

    
    public long mHashCode() {
    	long h1 = url == null ? 0 :url.hashCode();
    	long h2 = video_id == null ? 0 : video_id.hashCode();
    	long h3 = hash == null ? 0 : hash.hashCode();
    	long h4 = parent_img == null ? 0: parent_img.hashCode();
    	long hashCode = h1;
        hashCode = hashCode*31+h2;
        hashCode = hashCode*31+h3;
        hashCode = hashCode*31+h4;
        return hashCode;
    }
}
