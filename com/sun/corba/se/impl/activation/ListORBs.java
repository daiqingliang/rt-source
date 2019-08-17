package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListORBs implements CommandHandler {
  static final int illegalServerId = -1;
  
  public String getCommandName() { return "orblist"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    int i = -1;
    try {
      if (paramArrayOfString.length == 2)
        if (paramArrayOfString[0].equals("-serverid")) {
          i = Integer.valueOf(paramArrayOfString[1]).intValue();
        } else if (paramArrayOfString[0].equals("-applicationName")) {
          i = ServerTool.getServerIdForAlias(paramORB, paramArrayOfString[1]);
        }  
      if (i == -1)
        return true; 
      Activator activator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      String[] arrayOfString = activator.getORBNames(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.orbidmap2"));
      for (byte b = 0; b < arrayOfString.length; b++)
        paramPrintStream.println("\t " + arrayOfString[b]); 
    } catch (ServerNotRegistered serverNotRegistered) {
      paramPrintStream.println("\tno such server found.");
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ListORBs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */