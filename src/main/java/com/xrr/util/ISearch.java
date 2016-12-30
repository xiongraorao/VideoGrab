package com.xrr.util;

import java.io.Serializable;

import com.persist.util.helper.FileLogger;
import com.xrr.bean.ObjectFeature;

/**
 * created by raorao 2016/12/29
 * @author ubuntu
 *
 */

public interface ISearch extends Serializable{
	void prepare();
	void setLogger(FileLogger log, String tag);
	boolean search(String hash);
	void close();
}
