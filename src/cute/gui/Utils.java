package cute.gui;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 10, 2005
 * Time: 9:44:26 AM
 */
public class Utils {
    public static void populateTestableFunctions(String srcDirName,String srcFileName,
                                                 DefaultComboBoxModel funModel,JComboBox funT){
//        String mainClassName;
        JavaSource source;
        int selectionIndex=-1;

        try {
            funModel.removeAllElements();
            if((new File(srcFileName)).exists() && (new File(srcDirName)).exists() && srcFileName.startsWith(srcDirName)){
                JavaDocBuilder builder = new JavaDocBuilder();
                builder.addSource(new File(srcFileName));
                source = builder.getSources()[0];
                JavaClass[] javaClasses = source.getClasses();
                for (int i = 0; i < javaClasses.length; i++) {
                    JavaClass javaClass = javaClasses[i];
                    JavaMethod[] methods = javaClass.getMethods();
                    for (int j = 0; j < methods.length; j++) {
                        JavaMethod method = methods[j];
                        if(method.isPublic() && method.isStatic()){
                            JavaParameter[] parameters = method.getParameters();
                            if(parameters.length==0){
                                funModel.addElement(new StringBuffer().
                                        append(javaClass.getFullyQualifiedName()).
                                        append(".").
                                        append(method.getName()).toString());
                            }
                            if(method.getName().equals("main")){
                                if(parameters.length==1){
                                    Type type = parameters[0].getType();
                                    if(type.getValue().equals("java.lang.String")
                                            && type.isArray()
                                            &&  type.getDimensions()==1){
                                        selectionIndex = funModel.getSize();
                                        funModel.addElement(new StringBuffer().
                                                append(javaClass.getFullyQualifiedName()).
                                                append(".").append(method.getName()).toString());
                                    }
                                }
                            }
                        }
                    }
                }

            }
            else {
                if(!(new File(srcFileName)).exists()){
                    System.err.println("File "+srcFileName+ " not found!");
                }
                if(!(new File(srcDirName)).exists()){
                    System.err.println("Directory "+srcDirName+ " not found!");
                }
                if(!srcFileName.startsWith(srcDirName)){
                    System.err.println(srcFileName+" must be in the directory "+srcDirName);
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        if(selectionIndex>=0)
            funT.setSelectedIndex(selectionIndex);
    }

    public static boolean isPackageNameOk(String srcDirName,String srcFileName,MessageLogger box){
        JavaSource source;
        String packageName = "";
        String fileSeparator=System.getProperty("file.separator");

        try {
            if((new File(srcFileName)).exists() && (new File(srcDirName)).exists() && srcFileName.startsWith(srcDirName)){
                JavaDocBuilder builder = new JavaDocBuilder();
                builder.addSource(new File(srcFileName));
                source = builder.getSources()[0];
                packageName = source.getPackage();
                if(packageName==null){
                    packageName = "";
                }
            }
        } catch (IOException ioe){
            ioe.printStackTrace();
            return false;
        }
        String tmp,tmp2;
        // detect package name from the srcDirName and srcFileName
        tmp = srcFileName.substring(srcDirName.length());
        tmp = tmp.substring(1,tmp.lastIndexOf('.'));
        tmp = tmp.replaceAll("\\\\",".");
        tmp = tmp.replaceAll("/",".");
        if(tmp.indexOf('.')>0){
            tmp = tmp.substring(0,tmp.lastIndexOf('.'));
        } else {
            tmp = "";
        }

        tmp2 = srcFileName;
        tmp2 = tmp2.replaceAll("\\\\"," ");
        tmp2 = tmp2.replaceAll("/"," ");
        if(tmp2.indexOf(' ')>0)
            tmp2 = tmp2.substring(tmp2.lastIndexOf(' ')+1);

        if(!tmp.equals(packageName)){
            box.ask(tmp2+" must be in the directory "
                    +srcDirName+fileSeparator+packageName.replaceAll("\\.","/"));
            return false;
        }
        return true;
    }
}
