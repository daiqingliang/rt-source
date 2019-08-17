package com.sun.corba.se.impl.activation;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.activation.Repository;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.omg.CORBA.ORB;

public class ServerTool {
  static final String helpCommand = "help";
  
  static final String toolName = "servertool";
  
  static final String commandArg = "-cmd";
  
  private static final boolean debug = false;
  
  ORB orb = null;
  
  static Vector handlers = new Vector();
  
  static int maxNameLen;
  
  static int getServerIdForAlias(ORB paramORB, String paramString) throws ServerNotRegistered {
    try {
      Repository repository = RepositoryHelper.narrow(paramORB.resolve_initial_references("ServerRepository"));
      int i = repository.getServerID(paramString);
      return repository.getServerID(paramString);
    } catch (Exception exception) {
      throw new ServerNotRegistered();
    } 
  }
  
  void run(String[] paramArrayOfString) {
    String[] arrayOfString = null;
    for (i = 0; i < paramArrayOfString.length; i++) {
      if (paramArrayOfString[i].equals("-cmd")) {
        int j = paramArrayOfString.length - i - 1;
        arrayOfString = new String[j];
        for (byte b = 0; b < j; b++)
          arrayOfString[b] = paramArrayOfString[++i]; 
        break;
      } 
    } 
    try {
      Properties properties = System.getProperties();
      properties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
      this.orb = ORB.init(paramArrayOfString, properties);
      if (arrayOfString != null) {
        executeCommand(arrayOfString);
      } else {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(CorbaResourceUtil.getText("servertool.banner"));
        while (true) {
          arrayOfString = readCommand(bufferedReader);
          if (arrayOfString != null) {
            executeCommand(arrayOfString);
            continue;
          } 
          printAvailableCommands();
        } 
      } 
    } catch (Exception i) {
      Exception exception;
      System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
      System.out.println();
      exception.printStackTrace();
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    ServerTool serverTool = new ServerTool();
    serverTool.run(paramArrayOfString);
  }
  
  String[] readCommand(BufferedReader paramBufferedReader) {
    System.out.print("servertool > ");
    try {
      byte b = 0;
      String[] arrayOfString = null;
      String str = paramBufferedReader.readLine();
      if (str != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(str);
        if (stringTokenizer.countTokens() != 0) {
          arrayOfString = new String[stringTokenizer.countTokens()];
          while (stringTokenizer.hasMoreTokens())
            arrayOfString[b++] = stringTokenizer.nextToken(); 
        } 
      } 
      return arrayOfString;
    } catch (Exception exception) {
      System.out.println(CorbaResourceUtil.getText("servertool.usage", "servertool"));
      System.out.println();
      exception.printStackTrace();
      return null;
    } 
  }
  
  void printAvailableCommands() {
    System.out.println(CorbaResourceUtil.getText("servertool.shorthelp"));
    for (byte b = 0; b < handlers.size(); b++) {
      CommandHandler commandHandler = (CommandHandler)handlers.elementAt(b);
      System.out.print("\t" + commandHandler.getCommandName());
      for (int i = commandHandler.getCommandName().length(); i < maxNameLen; i++)
        System.out.print(" "); 
      System.out.print(" - ");
      commandHandler.printCommandHelp(System.out, true);
    } 
    System.out.println();
  }
  
  void executeCommand(String[] paramArrayOfString) {
    if (paramArrayOfString[0].equals("help")) {
      if (paramArrayOfString.length == 1) {
        printAvailableCommands();
      } else {
        for (byte b1 = 0; b1 < handlers.size(); b1++) {
          CommandHandler commandHandler = (CommandHandler)handlers.elementAt(b1);
          if (commandHandler.getCommandName().equals(paramArrayOfString[1]))
            commandHandler.printCommandHelp(System.out, false); 
        } 
      } 
      return;
    } 
    for (byte b = 0; b < handlers.size(); b++) {
      CommandHandler commandHandler = (CommandHandler)handlers.elementAt(b);
      if (commandHandler.getCommandName().equals(paramArrayOfString[0])) {
        String[] arrayOfString = new String[paramArrayOfString.length - 1];
        for (b1 = 0; b1 < arrayOfString.length; b1++)
          arrayOfString[b1] = paramArrayOfString[b1 + true]; 
        try {
          System.out.println();
          boolean bool = commandHandler.processCommand(arrayOfString, this.orb, System.out);
          if (bool == true)
            commandHandler.printCommandHelp(System.out, false); 
          System.out.println();
        } catch (Exception b1) {
          Exception exception;
        } 
        return;
      } 
    } 
    printAvailableCommands();
  }
  
  static  {
    handlers.addElement(new RegisterServer());
    handlers.addElement(new UnRegisterServer());
    handlers.addElement(new GetServerID());
    handlers.addElement(new ListServers());
    handlers.addElement(new ListAliases());
    handlers.addElement(new ListActiveServers());
    handlers.addElement(new LocateServer());
    handlers.addElement(new LocateServerForORB());
    handlers.addElement(new ListORBs());
    handlers.addElement(new ShutdownServer());
    handlers.addElement(new StartServer());
    handlers.addElement(new Help());
    handlers.addElement(new Quit());
    maxNameLen = 0;
    for (byte b = 0; b < handlers.size(); b++) {
      CommandHandler commandHandler = (CommandHandler)handlers.elementAt(b);
      int i = commandHandler.getCommandName().length();
      if (i > maxNameLen)
        maxNameLen = i; 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ServerTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */