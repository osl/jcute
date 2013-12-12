package cute;

import cute.concolic.Call;
import cute.gui.JCuteGui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

/**
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 21, 2005
 * Time: 11:26:18 AM
 */
public class RunOnce {
    public static Properties getProperties() throws IOException {
        Properties ret= new Properties();
        String configFile = System.getProperty("cute.config.file");
        if(configFile!=null && (new File(configFile).exists())){
            ret.load(new BufferedInputStream(new FileInputStream(configFile)));
            return ret;
        }
        configFile = "cute.config";
        if(new File(configFile).exists()){
            ret.load(new BufferedInputStream(new FileInputStream(configFile)));
            return ret;
        }

        String tmp = JCuteGui.class.getResource( "JCuteGui.class" ).getPath();
        if(tmp.indexOf('!')>0){
            String javaClassPath = tmp.substring(5, tmp.indexOf('!'));
            File tmp2 = new File(javaClassPath);
            javaClassPath = tmp2.getAbsolutePath();
            javaClassPath = javaClassPath.replaceAll("\\\\","/");
            if(!javaClassPath.endsWith("jcute.jar") || !(new File(javaClassPath)).exists()){
                System.err.println("It seems that you have invoked CUTE for Java without using jcute.jar." +
                        " Don't do this!");
                System.exit(1);
            }
            String jcuteHome = javaClassPath.substring(0,javaClassPath.length()-9);
            configFile = jcuteHome+configFile;
            if(new File(configFile).exists()){
                ret.load(new BufferedInputStream(new FileInputStream(configFile)));
                return ret;
            }
        }
        return ret;
    }

//    public static void init(){
//        Properties p = null;
//        try {
//            p = getProperties();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            System.exit(1);
//        }
//        String value;
//
//        Globals.globals.depth = 0;
//        Globals.globals.seed = (int)System.currentTimeMillis();
//        Globals.globals.random = false;
//        Globals.globals.searchMode = Globals.SEARCH_DFS;
//        Globals.globals.mode = 0;
//        Globals.globals.debugLevel = 0;
//        Globals.globals.recordCoverage = true;
//        Globals.globals.optimalDistributed = true;
//        Globals.globals.printTraceAndInputs = false;
//        Globals.globals.generateJUnit = false;
//        Cute.N = 0;
//
//        String key;
//
//        key = "cute.dfs.depth";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.depth = Integer.parseInt(value);
//        }
//        key = "cute.random.seed";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.seed = Integer.parseInt(value);
//        }
//        key = "cute.random.inputs";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.random = value.equals("true");
//        }
//        key = "cute.search.mode";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            if(value.equals("dfs")){
//                Globals.globals.searchMode = Globals.SEARCH_DFS;
//            }
//            if(value.equals("random")){
//                Globals.globals.searchMode = Globals.SEARCH_RANDOM;
//            }
//            if(value.equals("quick")){
//                Globals.globals.searchMode = Globals.SEARCH_QUICK;
//            }
//        }
//        key = "cute.mode";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            if(value.equals("start"))
//                Globals.globals.mode = 2;
//            if(value.equals("replay"))
//                Globals.globals.mode = 1;
//            if(value.equals("continue"))
//                Globals.globals.mode = 0;
//        }
//        key = "cute.log.coverage";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.recordCoverage = value.equals("true");
//        }
//        key = "cute.log.inputAndTrace";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.printTraceAndInputs = value.equals("true");
//        }
//        key = "cute.distributed.optimal";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.optimalDistributed = value.equals("true");
//        }
//        key = "cute.generateJUnitTestCases";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.generateJUnit = value.equals("true");
//        }
//        key = "cute.arg.N";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Cute.N = Integer.parseInt(value);
//        }
//
//        key = "cute.print.instrumentationCallTrace";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?1:0;
//        }
//        key = "cute.print.instrumentationCallConcurrency";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?2:0;
//        }
//        key = "cute.print.inputRaw";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?4:0;
//        }
//        key = "cute.print.path.history";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?8:0;
//        }
//        key = "cute.print.state";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?16:0;
//        }
//        key = "cute.print.path.constraint";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?32:0;
//        }
//        key = "cute.print.path.history.final";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?64:0;
//        }
//        key = "cute.print.input.final";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?128:0;
//        }
//        key = "cute.print.path.constraint.final";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?256:0;
//        }
//        key = "cute.print.line.number";
//        value = System.getProperty(key,p.getProperty(key));
//        if(value!=null){
//            Globals.globals.debugLevel += value.equals("true")?512:0;
//        }
//        Globals.globals.initialize();
//        Globals.globals.initialized = true;
//        Globals.globals.sched.setPriority(Thread.MIN_PRIORITY);
//        Globals.globals.sched.start();
//
//    }

    public static void main(String[] args) throws Throwable {
        if(args.length<1){
            System.err.println("Function to be tested is not provided as argument");
            System.exit(1);
        }
        if(args[0].indexOf('.')<0){
            System.err.println("Function name "+args[0]+" must be of the form classname.functionname");
            System.exit(1);
        }

        String[] names = new String[2];
        names[0] = args[0].substring(0,args[0].lastIndexOf('.'));
        names[1] = args[0].substring(args[0].lastIndexOf('.')+1);
        Class c = null;
        try {
            c = Class.forName(names[0]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Method[] methods = c.getMethods();
        boolean flag = false;
        Method method = null;
        for (int i = 0; !flag && i < methods.length; i++) {
            method = methods[i];
            if(method.getName().equals(names[1])){
                flag = true;
            }
        }
        if(flag){
//            if(System.getProperty("cute.args")==null){
//                init();
//            }
            try {
                if(Modifier.isStatic(method.getModifiers())){
                    if(method.toString().endsWith("main(java.lang.String[])")){
                        String [] params = new String[args.length-1];
                        for (int i = 1; i < args.length; i++) {
                            params[i-1] = args[i];
                        }
                        Object param[] = new Object[1];
                        param[0] = params;
                        method.invoke(null,param);
                    } else {
                        method.invoke(null,null);
                        Call.endBefore(-1);
                    }
                } else {
                    method.invoke(c.newInstance(),null);
                    Call.endBefore(-1);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            } catch (InvocationTargetException e) {
                //e.getCause().printStackTrace();
                Call.threadException(e.getCause());
                throw e.getCause();
            } catch(InstantiationException e){
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(1);
            }
        }
    }
}
