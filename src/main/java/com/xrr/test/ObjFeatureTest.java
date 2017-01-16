package com.xrr.test;

import com.xrr.bean.ObjectFeature;

/**
 * Created by ubuntu on 17-1-7.
 * test ObjFeature
 */
public class ObjFeatureTest {
    public static void main(String[] args){
        ObjectFeature[] objs = new ObjectFeature[2];
        for(int i = 0 ; i < objs.length; i++){
            objs[i] = new ObjectFeature();
            objs[i].hash = "asdfasddsaf";
        }

        for(int i = 0 ; i < objs.length; i++){
            System.out.println(objs[i].hash);
        }
    }
}
