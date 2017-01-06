package com.xrr.bean;

import java.io.Serializable;

/**
 * created by raorao 2016/1/4
 * this class is the detected object description
 * @author ubuntu
 *
 */

public class DetectObjectsInfo implements Serializable{
	public String category;
	public double score;
	public String hash;
	public String location;
	public String feature;
	public DetectObjectsInfo(){
		
	}
}
