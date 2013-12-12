package cute.gui;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Dec 25, 2005
 * Time: 11:08:40 AM
 */
public class GenerateJCuteLog {
    private boolean isRecord;
    private PrintWriter out;

    public GenerateJCuteLog(boolean record) {
        isRecord = record;
    }

    public void logCall(String fname, String arg){
        if(isRecord){
            out.println("\t\ttui."+fname+"(\""+arg.replaceAll("\\\\","\\\\\\\\")+"\");");
        }

    }

    public void logCall(String s) {
        if(isRecord){
            out.println("\t\t"+s);
        }
    }

    public void logCall(String fname, String arg1, String arg2) {
        if(isRecord){
            out.println("\t\ttui."+fname+"(\""+arg1.replaceAll("\\\\","\\\\\\\\")+"\",\""+arg2.replaceAll("\\\\","\\\\\\\\")+"\");");
        }
    }

    public void logCall(String fname, boolean arg1) {
        if(isRecord){
            out.println("\t\ttui."+fname+"("+arg1+");");
        }
    }

    public void logCall(String fname, int arg1) {
        if(isRecord){
            out.println("\t\ttui."+fname+"("+arg1+");");
        }
    }

    public void logExitValue(int v,JCuteTextUI tui){
        if(isRecord){
            out.println("\t\ttui.addExitValue(new Integer("+v+"));");
        } else {
            System.out.println("exitv = "+v);
            System.out.println("old = "+((Integer)tui.list.getLast()).intValue());
            assert v==((Integer)tui.list.removeLast()).intValue();
            if(!tui.list.isEmpty() && tui.list.getLast() instanceof String){
                tui.list.removeLast();
                tui.pauseAction();
            }
        }
    }

    public void logPause() {
        if(isRecord){
            out.println("\t\ttui.addExitValue(\"\");");
        }
    }

    public void logInit() {
        try{
            if(isRecord){
                out = new PrintWriter(new BufferedWriter(new FileWriter("JCuteGuiExecLog.java")));
                out.println("class JCuteGuiExecLog {");
                out.println("\tpublic static void main(String args[]){");
                out.println("\t\tcute.gui.JCuteTextUI tui = new cute.gui.JCuteTextUI(false);");
            }
        } catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void logExit(){
        if(isRecord){
            out.println("\t\ttui.quitAction();");
            out.println("\t}");
            out.println("}");
            out.close();
        }
    }
}
