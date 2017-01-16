package com.xrr.test;

import org.apache.hadoop.hbase.util.Hash;

/**
 * Created by ubuntu on 17-1-11.
 * function: test Feature differences
 */
public class HashDiffTest {
    /**
     * @param s1
     * @param s2
     */
    public static int diff(String s1, String s2){
        if(s1.length() != s2.length()){
            if(s1.length()>s2.length()){
                for(int i = s2.length(); i < s1.length(); i++){
                    s2 = "0" + s2;
                }
            }else{
                for(int i = s1.length(); i < s2.length(); i++){
                    s1 = "0" + s1;
                }
            }
        }

        int count = 0;
        for(int i = 0; i < s1.length(); i++){
            String ch1 = Integer.toBinaryString(Integer.valueOf(s1.substring(i, i+1),16)) ;
            String ch2 = Integer.toBinaryString(Integer.valueOf(s2.substring(i, i+1),16)) ;
            if(ch1.length() != 4){
                for (int ii = ch1.length(); ii <4; ii++) {
                    ch1 = "0"+ch1;
                }
            }
            if(ch2.length() != 4){
                for (int ii = ch2.length(); ii <4; ii++) {
                    ch2 = "0"+ch2;
                }
            }
            for(int ii=0;ii<ch1.length();ii++){
                //如果相同位置数相同，则补0，否则补1
                if(ch1.charAt(ii)!=ch2.charAt(ii)){
                    count+=1;
                }

            }
        }

        return count;

    }
    public static void main(String[] args){
        String dog1 = "b698e01e3593e8675f66f91528c8bfa1ada7ed0e528a1e7b8b311000e6ab9b76";
        String dog2 = "c3e83754615f9e17033b46f891c4a94d2ec8d5370fdc2df7923531e6086cfd76";
        String person = "4ec472fed35b862f765b0a45ff8b69fc662b3c11757ece0f20cd871d4fcc4e50";
        String cat = "68b3725d109767e38e03d8063dcf38029e4fbcbea2c0ec180fb4b4eaadc413ca";
        //System.out.println(dog1.substring(0,4));
        //System.out.println(Integer.parseInt(dog1.substring(0,1),16));
        //System.out.println(HashDiffTest.diff(dog1.substring(0,255),dog2.substring(0,255)));
        System.out.println(HashDiffTest.diff(person,cat));
        System.out.println(HashDiffTest.diff(dog1,dog2));
        System.out.println(HashDiffTest.diff(cat,dog1));
        System.out.println(HashDiffTest.diff(dog1,cat));



        //System.out.println(dog1.substring(3,4));
    }
}
