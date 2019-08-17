package sun.rmi.transport.proxy;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public final class CGIHandler {
  static int ContentLength;
  
  static String QueryString;
  
  static String RequestMethod;
  
  static String ServerName;
  
  static int ServerPort;
  
  private static CGICommandHandler[] commands;
  
  private static Hashtable<String, CGICommandHandler> commandLookup;
  
  public static void main(String[] paramArrayOfString) {
    try {
      String str2;
      String str1;
      int i = QueryString.indexOf("=");
      if (i == -1) {
        str1 = QueryString;
        str2 = "";
      } else {
        str1 = QueryString.substring(0, i);
        str2 = QueryString.substring(i + 1);
      } 
      CGICommandHandler cGICommandHandler = (CGICommandHandler)commandLookup.get(str1);
      if (cGICommandHandler != null) {
        try {
          cGICommandHandler.execute(str2);
        } catch (CGIClientException cGIClientException) {
          cGIClientException.printStackTrace();
          returnClientError(cGIClientException.getMessage());
        } catch (CGIServerException cGIServerException) {
          cGIServerException.printStackTrace();
          returnServerError(cGIServerException.getMessage());
        } 
      } else {
        returnClientError("invalid command.");
      } 
    } catch (Exception exception) {
      exception.printStackTrace();
      returnServerError("internal error: " + exception.getMessage());
    } 
    System.exit(0);
  }
  
  private static void returnClientError(String paramString) {
    System.out.println("Status: 400 Bad Request: " + paramString);
    System.out.println("Content-type: text/html");
    System.out.println("");
    System.out.println("<HTML><HEAD><TITLE>Java RMI Client Error</TITLE></HEAD><BODY>");
    System.out.println("<H1>Java RMI Client Error</H1>");
    System.out.println("");
    System.out.println(paramString);
    System.out.println("</BODY></HTML>");
    System.exit(1);
  }
  
  private static void returnServerError(String paramString) {
    System.out.println("Status: 500 Server Error: " + paramString);
    System.out.println("Content-type: text/html");
    System.out.println("");
    System.out.println("<HTML><HEAD><TITLE>Java RMI Server Error</TITLE></HEAD><BODY>");
    System.out.println("<H1>Java RMI Server Error</H1>");
    System.out.println("");
    System.out.println(paramString);
    System.out.println("</BODY></HTML>");
    System.exit(1);
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            CGIHandler.ContentLength = Integer.getInteger("CONTENT_LENGTH", 0).intValue();
            CGIHandler.QueryString = System.getProperty("QUERY_STRING", "");
            CGIHandler.RequestMethod = System.getProperty("REQUEST_METHOD", "");
            CGIHandler.ServerName = System.getProperty("SERVER_NAME", "");
            CGIHandler.ServerPort = Integer.getInteger("SERVER_PORT", 0).intValue();
            return null;
          }
        });
    commands = new CGICommandHandler[] { new CGIForwardCommand(), new CGIGethostnameCommand(), new CGIPingCommand(), new CGITryHostnameCommand() };
    commandLookup = new Hashtable();
    for (byte b = 0; b < commands.length; b++)
      commandLookup.put(commands[b].getName(), commands[b]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\CGIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */