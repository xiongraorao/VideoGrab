package com.xrr.test;

/**
 * Created by ubuntu on 17-1-6.
 */
public class StringTest {
    public static void main(String[] args){
        String s = "1498320990-00000-1483696477800-0.png";
        int len = s.length();
        String s1 = s.replace("0.png","3.png");
        System.out.println(s);
        System.out.println(s1);
        //System.out.print(s.length());
//        int len = s.split("-").length;
//        String[] ss = s.split("-");
//        String s2 = "4" +".png";
//        ss[len-1] = s2;

        String[][] ss = {{"hello","world"},{"hello", "java","nice day"}};
        for(int i = 0 ; i < ss.length; i++ ){
            for(int ii = 0 ; ii < ss[i].length; ii++ ) {
                System.out.println(ss[i][ii]);
            }
        }


    }
}
