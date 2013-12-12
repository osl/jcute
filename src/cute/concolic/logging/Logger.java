package cute.concolic.logging;

import cute.concolic.Information;

import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Koushik Sen (ksen@cs.uiuc.edu)
 * Date: Nov 20, 2005
 * Time: 8:21:02 PM
 */
public class Logger {
    private PrintWriter out;
    private Information information;

    public Logger(Information information, PrintWriter out) {
        this.information = information;
        this.out = out;
    }

    public void info(int type,String start,Printable printable){
        // begin and end for all instruented function calls
        if((information.debugLevel & type)==1){
            out.println(start);
            out.flush();
        }
        if((information.debugLevel & type)==2){
            out.println(start);
            out.flush();
        }
        if((information.debugLevel & type)==4){
            //globals.input.print(out);
            printable.print(out);
        }
        if((information.debugLevel & type)==8){
            //globals.history.print(out);
            printable.print(out);
        }
        if((information.debugLevel & type)==16){
            //globals.state.print(out);
            printable.print(out);
        }
        if((information.debugLevel & type) ==32){
            //globals.path.print(out);
            printable.print(out);
        }
        if((information.debugLevel & type)==64){
            out.println(start);
            //globals.history.print(out);
            printable.print(out);
        }
        if((information.debugLevel & type)==128){
            out.println(start);
            printable.print(out);
            //globals.input.print(out);
        }
        if((information.debugLevel & type)==256){
            out.println(start);
            printable.print(out);
            //globals.path.print(out);
        }
        if((information.debugLevel & type)==512){
            out.print(start);
            out.flush();
        }
    }

}
