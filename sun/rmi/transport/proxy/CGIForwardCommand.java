package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

final class CGIForwardCommand implements CGICommandHandler {
  public String getName() { return "forward"; }
  
  private String getLine(DataInputStream paramDataInputStream) throws IOException { return paramDataInputStream.readLine(); }
  
  public void execute(String paramString) throws CGIClientException, CGIServerException {
    String str2;
    DataInputStream dataInputStream2;
    Socket socket;
    int i;
    if (!CGIHandler.RequestMethod.equals("POST"))
      throw new CGIClientException("can only forward POST requests"); 
    try {
      i = Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new CGIClientException("invalid port number.", numberFormatException);
    } 
    if (i <= 0 || i > 65535)
      throw new CGIClientException("invalid port: " + i); 
    if (i < 1024)
      throw new CGIClientException("permission denied for port: " + i); 
    try {
      socket = new Socket(InetAddress.getLocalHost(), i);
    } catch (IOException iOException) {
      throw new CGIServerException("could not connect to local port", iOException);
    } 
    DataInputStream dataInputStream1 = new DataInputStream(System.in);
    byte[] arrayOfByte = new byte[CGIHandler.ContentLength];
    try {
      dataInputStream1.readFully(arrayOfByte);
    } catch (EOFException null) {
      throw new CGIClientException("unexpected EOF reading request body", dataInputStream2);
    } catch (IOException null) {
      throw new CGIClientException("error reading request body", dataInputStream2);
    } 
    try {
      dataInputStream2 = new DataOutputStream(socket.getOutputStream());
      dataInputStream2.writeBytes("POST / HTTP/1.0\r\n");
      dataInputStream2.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
      dataInputStream2.write(arrayOfByte);
      dataInputStream2.flush();
    } catch (IOException null) {
      throw new CGIServerException("error writing to server", dataInputStream2);
    } 
    try {
      dataInputStream2 = new DataInputStream(socket.getInputStream());
    } catch (IOException iOException) {
      throw new CGIServerException("error reading from server", iOException);
    } 
    String str1 = "Content-length:".toLowerCase();
    boolean bool = false;
    int j = -1;
    do {
      try {
        str2 = getLine(dataInputStream2);
      } catch (IOException iOException) {
        throw new CGIServerException("error reading from server", iOException);
      } 
      if (str2 == null)
        throw new CGIServerException("unexpected EOF reading server response"); 
      if (!str2.toLowerCase().startsWith(str1))
        continue; 
      if (bool)
        throw new CGIServerException("Multiple Content-length entries found."); 
      j = Integer.parseInt(str2.substring(str1.length()).trim());
      bool = true;
    } while (str2.length() != 0 && str2.charAt(0) != '\r' && str2.charAt(0) != '\n');
    if (!bool || j < 0)
      throw new CGIServerException("missing or invalid content length in server response"); 
    arrayOfByte = new byte[j];
    try {
      dataInputStream2.readFully(arrayOfByte);
    } catch (EOFException eOFException) {
      throw new CGIServerException("unexpected EOF reading server response", eOFException);
    } catch (IOException iOException) {
      throw new CGIServerException("error reading from server", iOException);
    } 
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: application/octet-stream");
    System.out.println("");
    try {
      System.out.write(arrayOfByte);
    } catch (IOException iOException) {
      throw new CGIServerException("error writing response", iOException);
    } 
    System.out.flush();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\CGIForwardCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */