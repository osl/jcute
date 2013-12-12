package cute.gui;

import javax.swing.*;
import java.awt.*;

public class MessageBox implements MessageLogger {
    private Component parent;

    public MessageBox(Component parent) {
        this.parent = parent;
    }

    public void ask(String msg){
        JOptionPane.showMessageDialog(parent,msg,"Message",JOptionPane.ERROR_MESSAGE);
    }

} // End class
