package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class GetServerID implements CommandHandler {
  public String getCommandName() { return "getserverid"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    if (paramArrayOfString.length == 2 && paramArrayOfString[0].equals("-applicationName")) {
      String str = paramArrayOfString[1];
      try {
        Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
        try {
          int i = repository.getServerID(str);
          paramPrintStream.println();
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.getserverid2", str, Integer.toString(i)));
          paramPrintStream.println();
        } catch (ServerNotRegistered serverNotRegistered) {
          paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
        } 
      } catch (Exception exception) {
        exception.printStackTrace();
      } 
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\GetServerID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */