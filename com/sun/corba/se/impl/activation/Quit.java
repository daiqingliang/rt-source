package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class Quit implements CommandHandler {
  public String getCommandName() { return "quit"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.quit"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.quit1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    System.exit(0);
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\Quit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */