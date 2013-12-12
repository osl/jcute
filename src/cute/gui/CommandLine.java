package cute.gui;

import java.io.*;

/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 29, 2005
 * Time: 9:10:13 PM
 */
public class CommandLine extends Thread {
    private static boolean optionPrintOutput;
    private InputStream is;
    private OutputLogger output;
    private String style;


    public static int executeOnce(String s,String[] envp,File workingDir,
                                  OutputLogger output,ProcessOwner po,boolean optionPrintOutputA){
        optionPrintOutput = optionPrintOutputA;
        try {
            Runtime rt = Runtime.getRuntime();
            if(optionPrintOutput) output.appendText("cd "+workingDir.getAbsolutePath(),"command");
            if(optionPrintOutput) output.appendText(s,"command");
            Process proc = rt.exec(s,envp,workingDir);
            po.setProcess(proc);
            CommandLine errorProcessor = new
                    CommandLine(proc.getErrorStream(),output,"error");
            CommandLine outputProcessor = new
                    CommandLine(proc.getInputStream(),output,"regular");

            errorProcessor.start();
            outputProcessor.start();
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())));
            out.print(s);
            out.close();
            proc.waitFor();
            po.setProcess(null);
            outputProcessor.join();
            errorProcessor.join();
            if(optionPrintOutput) output.appendText("Exit "+proc.exitValue()+"\n","command");
            return proc.exitValue();
        } catch(IOException ioe){
            ioe.printStackTrace();
            return 1;
        } catch(InterruptedException ie){
            ie.printStackTrace();
            return 1;
        }
    }


    public CommandLine(InputStream is,OutputLogger out,String style){
        this.is = is;
        this.output = out;
        this.style = style;
    }

    public void run(){
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;

            while ( (line = br.readLine()) != null){
                if(optionPrintOutput) output.appendText(line,style);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
