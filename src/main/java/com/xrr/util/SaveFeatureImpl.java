package com.xrr.util;

import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HBaseHelper;
import com.xrr.bean.ObjectFeature;

/**
 * create by raorao 2016/12/22
 * @author ubuntu
 *
 */
public class SaveFeatureImpl implements ISaveFeature{
	private HBaseHelper mHelper;
	private FileLogger mLogger;
    private String quorum;
    private int port;
    
    private String tableName;
    private String[] columnFamilies;
    private String[][] columns;
    private String tag;
	
    public SaveFeatureImpl(String quorum, int port,
    		String tableName, String columnFamilies[], String[][] columns){
    	this.quorum = quorum;
    	this.port =port;
    	this.tableName = tableName;
    	this.columnFamilies = columnFamilies;
    	this.columns = columns;
    }
       
	private void initHBase()
    {
        if(mHelper != null)
            return;
        mHelper = new HBaseHelper(quorum, port);
        try {
            mHelper.createTable(tableName, columnFamilies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void prepare() {
        initHBase();
    }
    
    public void close()	{
    	if(mHelper != null)
        {
            mHelper.close();
            mHelper = null;
        }
    	if(mLogger !=null){
    		mLogger.close();
    	}
    }


	public void setLogger(FileLogger log, String tag) {
		// TODO Auto-generated method stub
		mLogger = log;
		this.tag = tag;
	}

	public boolean save(ObjectFeature obj) {
		// TODO Auto-generated method stub
		boolean status = false;
		String[][] values = {{obj.video_id,obj.parent_img},{obj.hash,obj.feature,obj.category,obj.score+"",obj.location}};
        if(mHelper != null)
        {

            try {
                for(int i = 0 ;i < columnFamilies.length; i++){
                    mHelper.addRow(tableName, obj.url, columnFamilies[i],columns[i],
                            values[i]);
                }

                status = true;
                mLogger.log(tag, "add row success!");
            } catch (Exception e) {
                e.printStackTrace(mLogger.getPrintWriter());
                mLogger.getPrintWriter().flush();
                mLogger.log(tag, "add row exception!");
            }
        }
		
		return status;
	}


	
	public boolean save2(ObjectFeature obj) {
		// TODO Auto-generated method stub
		boolean status = false;
		String[][] values = {{obj.url}};
        if(mHelper != null)
        {
            try {
                for(int i = 0 ; i < columnFamilies.length; i++){
                    mHelper.addRow(tableName, obj.hash, columnFamilies[i],columns[i], values[i]);
                }

                status = true;
                mLogger.log(tag, "add row success!");
            } catch (Exception e) {
                e.printStackTrace();
                mLogger.log(tag, "add row exception!");
            }
        }
		
		return status;
	}

}
