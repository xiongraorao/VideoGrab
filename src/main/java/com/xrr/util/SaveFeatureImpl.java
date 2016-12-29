package com.xrr.util;

import com.persist.util.helper.FileLogger;
import com.persist.util.helper.HBaseHelper;
import com.xrr.bean.ObjectFeature;

/**
 * create by raora 2016/12/22
 * @author ubuntu
 *
 */
public class SaveFeatureImpl implements ISaveFeature{
	private HBaseHelper mHelper;
	private FileLogger mLogger;
    private String quorum;
    private int port;
    
    private String tableName;
    private String columnFamily;
    private String[] columns;
    private String tag;
	
    public SaveFeatureImpl(String quorum, int port,
    		String tableName, String columnFamily, String[] columns){
    	this.quorum = quorum;
    	this.port =port;
    	this.tableName = tableName;
    	this.columnFamily = columnFamily;
    	this.columns = columns;
    }
       
	private void initHBase()
    {
        if(mHelper != null)
            return;
        mHelper = new HBaseHelper(quorum, port);
        try {
            mHelper.createTable(tableName, new String[]{columnFamily});
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
        if(mHelper != null)
        {
            try {
                mHelper.addRow(tableName, obj.url, columnFamily,columns,
                        new String[]{obj.video_id,obj.parent_img,obj.hash,obj.time});
                status = true;
                mLogger.log(tag, "add row success!");
            } catch (Exception e) {
                e.printStackTrace();
                mLogger.log(tag, "add row exception!");
            }
        }
		
		return status;
	}
	
	public boolean save2(ObjectFeature obj) {
		// TODO Auto-generated method stub
		boolean status = false;
        if(mHelper != null)
        {
            try {
                mHelper.addRow(tableName, obj.hash, columnFamily,columns,
                        new String[]{obj.url});
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
