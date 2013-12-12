package cute.gui;



/**
 *  .
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Oct 30, 2005
 * Time: 9:29:11 PM
 */
public class ExecutionTask extends Thread {
    private JCuteGui gui;
    private String taskName;

    public ExecutionTask(JCuteGui gui,String taskName) {
        this.gui = gui;
        this.taskName = taskName;
    }

    public void run() {
        if(taskName.equals("file")){
            gui.browseMainFileAction();
        }
        if(taskName.equals("dir")){
            gui.browseSrcDirAction();
        }
        if(taskName.equals("compile")){
            gui.compileAction();
        }
        if(taskName.equals("start")){
            gui.deleteAction();
            gui.continueAction();
        }
        if(taskName.equals("continue")){
            gui.continueAction();
        }
        if(taskName.equals("replay")){
            gui.replayAction();
        }
        if(taskName.equals("options")){

        }
        if(taskName.equals("quit")){
            gui.quitAction();
        }
        if(taskName.equals("cancel")){

        }
    }
}
