package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class StartServer implements CommandHandler {
  static final int illegalServerId = -1;
  
  public String getCommandName() { return "startup"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver1"));
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
      activator.activate(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.startserver2"));
    } catch (ServerNotRegistered serverNotRegistered) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.nosuchserver"));
    } catch (ServerAlreadyActive serverAlreadyActive) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.serverup"));
    } catch (ServerHeldDown serverHeldDown) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.helddown"));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\StartServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */