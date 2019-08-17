package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Locator;
import com.sun.corba.se.spi.activation.LocatorHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.NoSuchEndPoint;
import com.sun.corba.se.spi.activation.ORBPortInfo;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class LocateServer implements CommandHandler {
  static final int illegalServerId = -1;
  
  public String getCommandName() { return "locate"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    int i = -1;
    String str = "IIOP_CLEAR_TEXT";
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
        if (str1.equals("-endpointType") && b < paramArrayOfString.length)
          str = paramArrayOfString[b++]; 
      } 
      if (i == -1)
        return true; 
      Locator locator = LocatorHelper.narrow(paramORB.resolve_initial_references("ServerLocator"));
      ServerLocation serverLocation = locator.locateServer(i, str);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.locate2", serverLocation.hostname));
      int j = serverLocation.ports.length;
      for (b = 0; b < j; b++) {
        ORBPortInfo oRBPortInfo = serverLocation.ports[b];
        paramPrintStream.println("\t\t" + oRBPortInfo.port + "\t\t" + str + "\t\t" + oRBPortInfo.orbId);
      } 
    } catch (NoSuchEndPoint noSuchEndPoint) {
    
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\LocateServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */