package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Properties;
import org.omg.CORBA.ORB;

public class ServerMain {
  public static final int OK = 0;
  
  public static final int MAIN_CLASS_NOT_FOUND = 1;
  
  public static final int NO_MAIN_METHOD = 2;
  
  public static final int APPLICATION_ERROR = 3;
  
  public static final int UNKNOWN_ERROR = 4;
  
  public static final int NO_SERVER_ID = 5;
  
  public static final int REGISTRATION_FAILED = 6;
  
  private static final boolean debug = false;
  
  public static String printResult(int paramInt) {
    switch (paramInt) {
      case 0:
        return "Server terminated normally";
      case 1:
        return "main class not found";
      case 2:
        return "no main method";
      case 3:
        return "application error";
      case 5:
        return "server ID not defined";
      case 6:
        return "server registration failed";
    } 
    return "unknown error";
  }
  
  private void redirectIOStreams() {
    try {
      String str1 = System.getProperty("com.sun.CORBA.activation.DbDir") + System.getProperty("file.separator") + "logs" + System.getProperty("file.separator");
      File file = new File(str1);
      String str2 = System.getProperty("com.sun.CORBA.POA.ORBServerId");
      FileOutputStream fileOutputStream1 = new FileOutputStream(str1 + str2 + ".out", true);
      FileOutputStream fileOutputStream2 = new FileOutputStream(str1 + str2 + ".err", true);
      PrintStream printStream1 = new PrintStream(fileOutputStream1, true);
      PrintStream printStream2 = new PrintStream(fileOutputStream2, true);
      System.setOut(printStream1);
      System.setErr(printStream2);
      logInformation("Server started");
    } catch (Exception exception) {}
  }
  
  private static void writeLogMessage(PrintStream paramPrintStream, String paramString) {
    Date date = new Date();
    paramPrintStream.print("[" + date.toString() + "] " + paramString + "\n");
  }
  
  public static void logInformation(String paramString) { writeLogMessage(System.out, "        " + paramString); }
  
  public static void logError(String paramString) {
    writeLogMessage(System.out, "ERROR:  " + paramString);
    writeLogMessage(System.err, "ERROR:  " + paramString);
  }
  
  public static void logTerminal(String paramString, int paramInt) {
    if (paramInt == 0) {
      writeLogMessage(System.out, "        " + paramString);
    } else {
      writeLogMessage(System.out, "FATAL:  " + printResult(paramInt) + ": " + paramString);
      writeLogMessage(System.err, "FATAL:  " + printResult(paramInt) + ": " + paramString);
    } 
    System.exit(paramInt);
  }
  
  private Method getMainMethod(Class paramClass) {
    Class[] arrayOfClass = { String[].class };
    Method method = null;
    try {
      method = paramClass.getDeclaredMethod("main", arrayOfClass);
    } catch (Exception exception) {
      logTerminal(exception.getMessage(), 2);
    } 
    if (!isPublicStaticVoid(method))
      logTerminal("", 2); 
    return method;
  }
  
  private boolean isPublicStaticVoid(Method paramMethod) {
    int i = paramMethod.getModifiers();
    if (!Modifier.isPublic(i) || !Modifier.isStatic(i)) {
      logError(paramMethod.getName() + " is not public static");
      return false;
    } 
    if (paramMethod.getExceptionTypes().length != 0) {
      logError(paramMethod.getName() + " declares exceptions");
      return false;
    } 
    if (!paramMethod.getReturnType().equals(void.class)) {
      logError(paramMethod.getName() + " does not have a void return type");
      return false;
    } 
    return true;
  }
  
  private Method getNamedMethod(Class paramClass, String paramString) {
    Class[] arrayOfClass = { ORB.class };
    Method method = null;
    try {
      method = paramClass.getDeclaredMethod(paramString, arrayOfClass);
    } catch (Exception exception) {
      return null;
    } 
    return !isPublicStaticVoid(method) ? null : method;
  }
  
  private void run(String[] paramArrayOfString) {
    try {
      redirectIOStreams();
      String str = System.getProperty("com.sun.CORBA.POA.ORBServerName");
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null)
        classLoader = ClassLoader.getSystemClassLoader(); 
      Class clazz = null;
      try {
        clazz = Class.forName(str);
      } catch (ClassNotFoundException classNotFoundException) {
        clazz = Class.forName(str, true, classLoader);
      } 
      Method method = getMainMethod(clazz);
      boolean bool = Boolean.getBoolean("com.sun.CORBA.activation.ORBServerVerify");
      if (bool)
        if (method == null) {
          logTerminal("", 2);
        } else {
          logTerminal("", 0);
        }  
      registerCallback(clazz);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = paramArrayOfString;
      method.invoke(null, arrayOfObject);
    } catch (ClassNotFoundException classNotFoundException) {
      logTerminal("ClassNotFound exception: " + classNotFoundException.getMessage(), 1);
    } catch (Exception exception) {
      logTerminal("Exception: " + exception.getMessage(), 3);
    } 
  }
  
  public static void main(String[] paramArrayOfString) {
    ServerMain serverMain = new ServerMain();
    serverMain.run(paramArrayOfString);
  }
  
  private int getServerId() {
    Integer integer = Integer.getInteger("com.sun.CORBA.POA.ORBServerId");
    if (integer == null)
      logTerminal("", 5); 
    return integer.intValue();
  }
  
  private void registerCallback(Class paramClass) {
    Method method1 = getNamedMethod(paramClass, "install");
    Method method2 = getNamedMethod(paramClass, "uninstall");
    Method method3 = getNamedMethod(paramClass, "shutdown");
    Properties properties = new Properties();
    properties.put("org.omg.CORBA.ORBClass", "com.sun.corba.se.impl.orb.ORBImpl");
    properties.put("com.sun.CORBA.POA.ORBActivated", "false");
    String[] arrayOfString = null;
    ORB oRB = ORB.init(arrayOfString, properties);
    ServerCallback serverCallback = new ServerCallback(oRB, method1, method2, method3);
    int i = getServerId();
    try {
      Activator activator = ActivatorHelper.narrow(oRB.resolve_initial_references("ServerActivator"));
      activator.active(i, serverCallback);
    } catch (Exception exception) {
      logTerminal("exception " + exception.getMessage(), 6);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\activation\ServerMain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */