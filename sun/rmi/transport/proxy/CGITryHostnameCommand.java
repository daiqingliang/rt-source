package sun.rmi.transport.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

final class CGITryHostnameCommand implements CGICommandHandler {
  public String getName() { return "tryhostname"; }
  
  public void execute(String paramString) {
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: text/html");
    System.out.println("");
    System.out.println("<HTML><HEAD><TITLE>Java RMI Server Hostname Info</TITLE></HEAD><BODY>");
    System.out.println("<H1>Java RMI Server Hostname Info</H1>");
    System.out.println("<H2>Local host name available to Java VM:</H2>");
    System.out.print("<P>InetAddress.getLocalHost().getHostName()");
    try {
      String str = InetAddress.getLocalHost().getHostName();
      System.out.println(" = " + str);
    } catch (UnknownHostException unknownHostException) {
      System.out.println(" threw java.net.UnknownHostException");
    } 
    System.out.println("<H2>Server host information obtained through CGI interface from HTTP server:</H2>");
    System.out.println("<P>SERVER_NAME = " + CGIHandler.ServerName);
    System.out.println("<P>SERVER_PORT = " + CGIHandler.ServerPort);
    System.out.println("</BODY></HTML>");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\CGITryHostnameCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */