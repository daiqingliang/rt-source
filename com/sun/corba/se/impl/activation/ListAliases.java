package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class ListAliases implements CommandHandler {
  public String getCommandName() { return "listappnames"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    try {
      Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      String[] arrayOfString = repository.getApplicationNames();
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.listappnames2"));
      paramPrintStream.println();
      for (byte b = 0; b < arrayOfString.length; b++)
        paramPrintStream.println("\t" + arrayOfString[b]); 
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ListAliases.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */