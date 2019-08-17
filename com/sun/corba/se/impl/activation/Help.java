package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class Help implements CommandHandler {
  public String getCommandName() { return "help"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.help"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.help1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\Help.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */