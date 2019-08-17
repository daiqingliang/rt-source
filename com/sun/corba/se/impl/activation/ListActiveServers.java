package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListActiveServers implements CommandHandler {
  public String getCommandName() { return "listactive"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listactive"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listactive1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    try {
      Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      Activator activator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      int[] arrayOfInt = activator.getActiveServers();
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list2"));
      ListServers.sortServers(arrayOfInt);
      for (byte b = 0; b < arrayOfInt.length; b++) {
        try {
          ServerDef serverDef = repository.getServer(arrayOfInt[b]);
          paramPrintStream.println("\t   " + arrayOfInt[b] + "\t\t" + serverDef.serverName + "\t\t" + serverDef.applicationName);
        } catch (ServerNotRegistered serverNotRegistered) {}
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ListActiveServers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */