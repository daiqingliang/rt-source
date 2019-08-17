package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListServers implements CommandHandler {
  static final int illegalServerId = -1;
  
  public String getCommandName() { return "list"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.list1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    int i = -1;
    boolean bool = false;
    bool = (paramArrayOfString.length != 0) ? 1 : 0;
    if (paramArrayOfString.length == 2 && paramArrayOfString[0].equals("-serverid"))
      i = Integer.valueOf(paramArrayOfString[1]).intValue(); 
    if (i == -1 && bool)
      return true; 
    try {
      Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      if (bool) {
        try {
          ServerDef serverDef = repository.getServer(i);
          paramPrintStream.println();
          printServerDef(serverDef, i, paramPrintStream);
          paramPrintStream.println();
        } catch (ServerNotRegistered serverNotRegistered) {
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        } 
      } else {
        int[] arrayOfInt = repository.listRegisteredServers();
        paramPrintStream.println(CorbaResourceUtil.getText("servertool.list2"));
        sortServers(arrayOfInt);
        for (byte b = 0; b < arrayOfInt.length; b++) {
          try {
            ServerDef serverDef = repository.getServer(arrayOfInt[b]);
            paramPrintStream.println("\t   " + arrayOfInt[b] + "\t\t" + serverDef.serverName + "\t\t" + serverDef.applicationName);
          } catch (ServerNotRegistered serverNotRegistered) {}
        } 
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
  
  static void printServerDef(ServerDef paramServerDef, int paramInt, PrintStream paramPrintStream) {
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.appname", paramServerDef.applicationName));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.name", paramServerDef.serverName));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.classpath", paramServerDef.serverClassPath));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.args", paramServerDef.serverArgs));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.vmargs", paramServerDef.serverVmArgs));
    paramPrintStream.println(CorbaResourceUtil.getText("servertool.serverid", paramInt));
  }
  
  static void sortServers(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    for (byte b = 0; b < i; b++) {
      byte b1 = b;
      int j;
      for (j = b + true; j < i; j++) {
        if (paramArrayOfInt[j] < paramArrayOfInt[b1])
          b1 = j; 
      } 
      if (b1 != b) {
        j = paramArrayOfInt[b];
        paramArrayOfInt[b] = paramArrayOfInt[b1];
        paramArrayOfInt[b1] = j;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ListServers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */