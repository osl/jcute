package cute.concolic.logging;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import cute.concolic.Information;
import cute.gui.GetImageIcon;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 16, 2005
 * Time: 1:28:35 PM
 */
public class JUnitTestGenerator {
    private Information information;

    private HashMap omap = new HashMap();
    private Vector stmts = new Vector();
    private String type;
    private String var;
    private int i = 0;
    private int sz = 0;
    public static final String junitInputFile = "cuteJUnitInput";

    public JUnitTestGenerator(Information information) {
        this.information = information;
    }

    public void assignToInput(String type) {
        if(information.generateJUnit){
            this.type = type;
            this.var = "input[i++]";
            sz++;
        }
    }

    public void assignTo(String varName, String field, String type) {
        if(information.generateJUnit){
            this.type = type;
            this.var = varName +"."+field;
        }
    }

    public String valueObjectNull() {
        if(information.generateJUnit){
            stmts.add(var + " = null;");
        }
        return null;
    }

    public String valueObject(int oid, String type) {
        return valueObject(oid,type,null);
    }

    public String valueObject(int oid, String type, Object ret) {
        if(information.generateJUnit){
            Integer tmp=new Integer(oid);
            String tmpstr = "tmp"+(++i);
            if(omap.containsKey(tmp)){
                stmts.add(type+" "+tmpstr+" = "+omap.get(tmp)+";");
            } else {
                if(ret == null)
                    stmts.add(type+" "+tmpstr+" = new "+type+"();");
                else if(ret.equals("java.lang.Integer") ||
                        ret.equals("java.lang.Long") ||
                        ret.equals("java.lang.Short") ||
                        ret.equals("java.lang.Byte") ||
                        ret.equals("java.lang.Boolean") ||
                        ret.equals("java.lang.Character") ||
                        ret.equals("java.lang.Double") ||
                        ret.equals("java.lang.Float"))
                    stmts.add(type+" "+tmpstr+" = new "+type+"("+ret+");");
                else
                    stmts.add(type+" "+tmpstr+" = new "+type+"(\""+ret+"\");"); 
                omap.put(tmp,tmpstr);
            }
            stmts.add(var+" = "+tmpstr+";");
            return tmpstr;
        } else
            return null;

    }

    public void valuePrimitive(Object ret) {
        if(information.generateJUnit){
            if(var.indexOf('.')>0){
                stmts.add(var+" = "+ret+";");
            } else {
                stmts.add(var+" = new "+type+"("+ret+");");
            }
        }
    }

    public void printAll(){
        if(information.generateJUnit){
            try {
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(junitInputFile)));
                out.println("input = new Object["+sz+"];");
                for (Iterator iterator = stmts.iterator(); iterator.hasNext();) {
                    String s = (String) iterator.next();
                    out.println(s);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void deleteJunitTestCase(String dir,String pkg,
                                           String cName,String fname){
        File f = getJUnitFileName(dir,cName,fname);
        if(f!=null && f.exists()){
            f.delete();
        }
    }

    public static File getJUnitFileName(String dir,String cName,String fname){
        if(cName!=null && fname!=null){
            StringBuffer sb = new StringBuffer(cName.replaceAll("\\.","_"));
            sb.append('_').append(fname).append('_').append("Test.java");
            String fileName = sb.toString();
            return new File(dir.trim().equals("")?null:dir,fileName);
        }
        return null;

    }

    public static void appendToJunitTestCase(String dir,String pkg,
                                             String cName,String fname,int i,File lastDir){
        File f = getJUnitFileName(dir,cName,fname);
        if(!f.exists()){
            createFile(f,pkg,cName,fname);
        }
        try {
            if(isTestPresent(f,"test"+i))
                return;
            RandomAccessFile rf = new RandomAccessFile(f,"rws");
            byte[] buf = new byte[1024];
            int count = 0;
            int lastPos=-1;
            int len;
            while((len=rf.read(buf))!=-1){
                for(int j=0;j<len;j++){
                    if(buf[j]=='}'){
                        lastPos = count;
                    }
                    count++;
                }
            }
            if(lastPos>=0){
                rf.setLength(lastPos);
                rf.seek(lastPos);
            }
            appendFile(rf,i,cName,fname,lastDir);
            rf.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        // append test input to test case

    }

    private static boolean isTestPresent(File f,String testFun){
        JavaSource source;
        boolean flag = false;
        JavaDocBuilder builder = new JavaDocBuilder();
        try {
            builder.addSource(f);

            source = builder.getSources()[0];
            JavaClass[] javaClasses = source.getClasses();
            for (int i = 0; !flag && i < javaClasses.length; i++) {
                JavaClass javaClass = javaClasses[i];
                JavaMethod[] methods = javaClass.getMethods();
                for (int j = 0; !flag && j < methods.length; j++) {
                    JavaMethod method = methods[j];
                    if(method.getName().equals(testFun)){
                        JavaParameter[] parameters = method.getParameters();
                        if(parameters.length==0){
                            flag = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

private static void appendFile(RandomAccessFile rf, int i,String cName,String fname,File lastDir) {
        try {
        String str;

            BufferedReader in = new BufferedReader(
                    new FileReader(
                            new File(lastDir,junitInputFile)));
            rf.writeBytes("    public void test"+i+"(){\n");
            rf.writeBytes("        i=0;\n");
            while((str=in.readLine())!=null){
                rf.writeBytes("        ");
                rf.writeBytes(str);
                rf.writeByte('\n');
            }
            in.close();
            rf.writeBytes("        i=0;\n");
            rf.writeBytes("        cute.Cute.input = this;\n");
            if(fname.equals("main")){
                rf.writeBytes("        "+cName+"."+fname+"(null);\n    }\n\n}\n");
            } else {
                rf.writeBytes("        "+cName+"."+fname+"();\n    }\n\n}\n");
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private static void createFile(File f, String pkg, String cName, String fname) {
        try {
            String str;
            String rep=cName.replaceAll("\\.","_")+"_"+fname+"_Test";
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    GetImageIcon.getUrl("files/Template.java").openStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            if(pkg!=null && !pkg.trim().equals("")){
                out.println("package "+pkg+";");
            }
            while((str=in.readLine())!=null){
                str = str.replaceAll("\\$Template\\$",rep);
                out.println(str);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
