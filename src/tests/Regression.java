package tests;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Jul 19, 2006
 * Time: 8:31:14 PM
 */

public class Regression {
    public static void test1(){
        Struct p = (Struct)cute.Cute.input.Object("tests.Struct");
        int y = cute.Cute.input.Integer();
        if(y>0){
            if(p!=null){
                if(2*y+1==p.x){
                    if(p.next==p)
                        System.out.println("OK");;
                }
            }
        }
    }

    public static void test2(){
        int y = cute.Cute.input.Integer();
        int x = cute.Cute.input.Integer();

        int z = (x+1)*(y+1);
        if(z==4)
            System.out.println("OK");;
    }

    public static void test3(){
        int y = cute.Cute.input.Integer();
        int x = cute.Cute.input.Integer();

        int z = 2*x+6*y+8;
        if(z==4)
            System.out.println("OK");;
    }

    public static void test4(){
        Struct p = (Struct)cute.Cute.input.Object("tests.Struct");
        if(p!=null){
            HashMap hm = new HashMap();
            hm.put(new Integer(1),p);
            Struct p1 = (Struct)hm.get(new Integer(1));
            if(p1.x==100)
                System.out.println("OK");;
        }
    }

    public static void test5(){
        String s = (String)cute.Cute.input.ObjectShallow("java.lang.String");
        if(s!=null && s.equals("ab"))
            System.out.println(s+" OK");
    }

    public static void test6(){
        double i = cute.Cute.input.Double();
        if(i==844.534)
            System.out.println("OK");
    }

    public static void test7(){
        long l = cute.Cute.input.Long();
        if(l<=844)
            System.out.println("OK");
    }

    public static void test8(){
        double d = cute.Cute.input.Double();
        float f = cute.Cute.input.Float();


        if(d*34+f*74.5>=844.534)
            System.out.println("OK");
    }

    public static void test9(){
        long l1 = cute.Cute.input.Long();
        int l2 = cute.Cute.input.Integer();

        if(l1*34+l2*74>=844)
            System.out.println("OK");
    }

    public static void test10(){
        String s = (String)cute.Cute.input.ObjectShallow("java.lang.String");
        String ret = "";
        if(s!=null && ret.equals(s))
            System.out.println(s+" OK");
    }

    public static void test11(){
        String s = (String)cute.Cute.input.ObjectShallow("java.lang.String");
        if(s!=null && s.length()>10)
            System.out.println(s+" OK");
    }

    public static void test12(){
        int i = cute.Cute.input.Integer();

        Integer I = new Integer(i+2);
        if(I.intValue()==3)
            System.out.println("OK");
    }

    public static void test13(){
        Integer I = (Integer)cute.Cute.input.ObjectShallow("java.lang.Integer");
        if(I!=null && I.intValue()==10)
            System.out.println(" OK");
    }

    public static void test14(){
        long l = cute.Cute.input.Long();

        Long I = new Long(l+2);
        if(I.longValue()==9)
            System.out.println("OK");
    }

    public static void test15(){
        Long I = (Long)cute.Cute.input.ObjectShallow("java.lang.Long");
        if(I!=null && I.longValue()==10)
            System.out.println(" OK");
    }

    public static void test16(){
        boolean b= cute.Cute.input.Boolean();

        Boolean B = new Boolean(!b);
        if(B.booleanValue())
            System.out.println("OK");
    }

    public static void test17(){
        Boolean B = (Boolean)cute.Cute.input.ObjectShallow("java.lang.Boolean");
        if(B!=null && !B.booleanValue())
            System.out.println(" OK");
    }

    public static void test18(){
        int len = cute.Cute.input.Integer();
        if(len<5){
            double arr[] = new double[len];
            for(int i=0;i<arr.length;i++){
                arr[i] = i;
            }
        }
        System.out.println("len = " + len);
    }

}
//@The following comments are auto-generated to save options for testing the current file
//@jcute.optionPrintOutput=true
//@jcute.optionLogPath=true
//@jcute.optionLogTraceAndInput=true
//@jcute.optionGenerateJUnit=false
//@jcute.optionExtraOptions=
//@jcute.optionJUnitOutputFolderName=d:\sync\work\cute\java
//@jcute.optionJUnitPkgName=
//@jcute.optionNumberOfPaths=100
//@jcute.optionLogLevel=2
//@jcute.optionDepthForDFS=0
//@jcute.optionSearchStrategy=0
//@jcute.optionSequential=true
//@jcute.optionQuickSearchThreshold=100
//@jcute.optionLogRace=true
//@jcute.optionLogDeadlock=true
//@jcute.optionLogException=true
//@jcute.optionLogAssertion=true
//@jcute.optionUseRandomInputs=false
