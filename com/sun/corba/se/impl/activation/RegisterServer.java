package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.PrintStream;
import org.omg.CORBA.ORB;

class RegisterServer implements CommandHandler {
  public String getCommandName() { return "register"; }
  
  public void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean) {
    if (!paramBoolean) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register"));
    } else {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register1"));
    } 
  }
  
  public boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream) {
    byte b = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    String str4 = "";
    String str5 = "";
    int i = 0;
    while (b < paramArrayOfString.length) {
      String str = paramArrayOfString[b++];
      if (str.equals("-server")) {
        if (b < paramArrayOfString.length) {
          str2 = paramArrayOfString[b++];
          continue;
        } 
        return true;
      } 
      if (str.equals("-applicationName")) {
        if (b < paramArrayOfString.length) {
          str1 = paramArrayOfString[b++];
          continue;
        } 
        return true;
      } 
      if (str.equals("-classpath")) {
        if (b < paramArrayOfString.length) {
          str3 = paramArrayOfString[b++];
          continue;
        } 
        return true;
      } 
      if (str.equals("-args")) {
        while (b < paramArrayOfString.length && !paramArrayOfString[b].equals("-vmargs")) {
          str4 = str4.equals("") ? paramArrayOfString[b] : (str4 + " " + paramArrayOfString[b]);
          b++;
        } 
        if (str4.equals(""))
          return true; 
        continue;
      } 
      if (str.equals("-vmargs")) {
        while (b < paramArrayOfString.length && !paramArrayOfString[b].equals("-args")) {
          str5 = str5.equals("") ? paramArrayOfString[b] : (str5 + " " + paramArrayOfString[b]);
          b++;
        } 
        if (str5.equals(""))
          return true; 
        continue;
      } 
      return true;
    } 
    if (str2.equals(""))
      return true; 
    try {
      Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      ServerDef serverDef = new ServerDef(str1, str2, str3, str4, str5);
      i = repository.registerServer(serverDef);
      Activator activator = ActivatorHelper.narrow(paramORB.resolve_initial_references("ServerActivator"));
      activator.activate(i);
      activator.install(i);
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register2", i));
    } catch (ServerNotRegistered serverNotRegistered) {
    
    } catch (ServerAlreadyActive serverAlreadyActive) {
    
    } catch (ServerHeldDown serverHeldDown) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register3", i));
    } catch (ServerAlreadyRegistered serverAlreadyRegistered) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.register4", i));
    } catch (BadServerDefinition badServerDefinition) {
      paramPrintStream.println(CorbaResourceUtil.getText("servertool.baddef", badServerDefinition.reason));
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\RegisterServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */