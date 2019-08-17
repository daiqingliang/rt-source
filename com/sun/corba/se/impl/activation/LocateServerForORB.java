package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.EndPointInfo;
import com.sun.corba.se.spi.activation.InvalidORBid;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class LocateServerForORB implements CommandHandler {
  static final int illegalServerId = -1;
  
  public String getCommandName() { return "locateperorb"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    int i = -1;
    String str = "";
    try {
      byte b = 0;
      while (b < paramArrayOfString.length) {
        String str1 = paramArrayOfString[b++];
        if (str1.equals("-serverid")) {
          if (b < paramArrayOfString.length) {
            i = Integer.valueOf(paramArrayOfString[b++]).intValue();
            continue;
          } 
          return true;
        } 
        if (str1.equals("-applicationName")) {
          if (b < paramArrayOfString.length) {
            i = ServerTool.getServerIdForAlias(paramORB, paramArrayOfString[b++]);
            continue;
          } 
          return true;
        } 
        if (str1.equals("-orbid") && b < paramArrayOfString.length)
          str = paramArrayOfString[b++]; 
      } 
      if (i == -1)
        return true; 
      Locator locator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
      ServerLocationPerORB serverLocationPerORB = locator.locateServerForORB(i, str);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locateorb2", serverLocationPerORB.hostname));
      int j = serverLocationPerORB.ports.length;
      for (b = 0; b < j; b++) {
        EndPointInfo endPointInfo = serverLocationPerORB.ports[b];
        paramPrintStream.println("\t\t" + endPointInfo.port + "\t\t" + endPointInfo.endpointType + "\t\t" + str);
      } 
    } catch (InvalidORBid invalidORBid) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchorb"));
    } catch (ServerHeldDown serverHeldDown) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.helddown"));
    } catch (ServerNotRegistered serverNotRegistered) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\LocateServerForORB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */