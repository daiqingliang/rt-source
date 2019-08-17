package com.sun.corba.se.impl.activation;

import java.io.PrintStream;
import org.omg.CORBA.ORB;

public interface CommandHandler {
  public static final boolean shortHelp = true;
  
  public static final boolean longHelp = false;
  
  public static final boolean parseError = true;
  
  public static final boolean commandDone = false;
  
  String getCommandName();
  
  void printCommandHelp(PrintStream paramPrintStream, boolean paramBoolean);
  
  boolean processCommand(String[] paramArrayOfString, ORB paramORB, PrintStream paramPrintStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\CommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */