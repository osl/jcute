package cute.gui;

import ccl.util.FileUtil;

import java.io.*;
import java.util.Properties;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 30, 2005
 * Time: 12:07:56 AM
 */
public class OptionReader {
    private JCuteTextUI tui;
    final public static String startsWith = "//@jcute.";
    final public static String sStartsWith = "setOption";
    final public static String gStartsWith = "getOption";
    final public static String iStartsWith = "isOption";
    final public static String argPrefix = "option";
    final public static String comment
    = "//@The following comments are auto-generated to save options for testing the current file";

    public OptionReader(JCuteTextUI tui) {
        this.tui = tui;
    }

    public void readOptions(){
        try {
            BufferedReader in = new BufferedReader(new FileReader(tui.getSrcFileName()) );
            String line,keyValue,key,value;
            Properties p = new Properties();
            int index;
            while((line=in.readLine())!=null){
                if(line.startsWith(startsWith)){
                    keyValue = line.substring(startsWith.length());
                    index = keyValue.indexOf('=');
                    key = keyValue.substring(0,index);
                    if(keyValue.length()==index+1)
                        value = "";
                    else
                        value = keyValue.substring(index+1);
                    p.put(key.trim(),value.trim());
                }
            }
            in.close();
            Class tuiClass = tui.getClass();
            Method[] methods = tuiClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if(method.getName().startsWith(sStartsWith)){
                    keyValue = argPrefix+method.getName().substring(sStartsWith.length());
                    if(p.containsKey(keyValue)){
                        keyValue = p.getProperty(keyValue);
                        Object[] arg = new Object[1];
                        try {
                            if(method.getParameterTypes()[0].getName().equals("java.lang.String")){
                                arg[0] = keyValue;
                            } else if(method.getParameterTypes()[0].getName().equals("int")){
                                arg[0] = new Integer(Integer.parseInt(keyValue));
                            } else if(method.getParameterTypes()[0].getName().equals("boolean")){
                                arg[0] = Boolean.valueOf(keyValue);
                            } else {
                                System.err.println("Unknown type "+method.getParameterTypes()[0].getName());
                            }
                            System.out.println("Method "+method.getName()+" value "
                                    +arg[0]+" read "+keyValue);
                            try {
                                method.invoke(tui,arg);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to read options.  Using default values.");
        }
        tui.setOptionJUnitOutputFolderName(System.getProperty("user.dir"));
    }

    public void writeOptions(){
        try {
            BufferedReader in = new BufferedReader(new FileReader(tui.getSrcFileName()) );
            PrintWriter out = new PrintWriter(new FileWriter(tui.getSrcFileName()+".tmp"));
            String line;
            while((line=in.readLine())!=null){
                if(!line.startsWith(startsWith) && !line.startsWith(comment)){
                    out.println(line);
                }
            }
            in.close();
            out.println(comment);
            Class tuiClass = tui.getClass();
            Method[] methods = tuiClass.getMethods();
            Object arg[] = new Object[0];
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                String name = method.getName();
                if(name.startsWith(gStartsWith) || name.startsWith(iStartsWith)){
                    out.print(startsWith);
                    out.print(argPrefix);
                    if(name.startsWith(gStartsWith)){
                        out.print(name.substring(gStartsWith.length()));
                    }
                    if(name.startsWith(iStartsWith)){
                        out.print(name.substring(iStartsWith.length()));
                    }
                    out.print('=');
                    try {
                        Object ret = method.invoke(tui,arg);
                        out.println(ret);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
            out.close();
            FileUtil.move(tui.getSrcFileName()+".tmp",tui.getSrcFileName());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
