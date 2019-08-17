package sun.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer implements Runnable, Cloneable {
  public Socket clientSocket = null;
  
  private Thread serverInstance;
  
  private ServerSocket serverSocket;
  
  public PrintStream clientOutput;
  
  public InputStream clientInput;
  
  public void close() throws IOException {
    this.clientSocket.close();
    this.clientSocket = null;
    this.clientInput = null;
    this.clientOutput = null;
  }
  
  public boolean clientIsOpen() { return (this.clientSocket != null); }
  
  public final void run() throws IOException {
    if (this.serverSocket != null) {
      Thread.currentThread().setPriority(10);
      try {
        while (true) {
          Socket socket = this.serverSocket.accept();
          NetworkServer networkServer = (NetworkServer)clone();
          networkServer.serverSocket = null;
          networkServer.clientSocket = socket;
          (new Thread(networkServer)).start();
        } 
      } catch (Exception exception) {
        System.out.print("Server failure\n");
        exception.printStackTrace();
        try {
          this.serverSocket.close();
        } catch (IOException iOException) {}
        System.out.print("cs=" + this.serverSocket + "\n");
      } 
    } else {
      try {
        this.clientOutput = new PrintStream(new BufferedOutputStream(this.clientSocket.getOutputStream()), false, "ISO8859_1");
        this.clientInput = new BufferedInputStream(this.clientSocket.getInputStream());
        serviceRequest();
      } catch (Exception exception) {}
      try {
        close();
      } catch (IOException iOException) {}
    } 
  }
  
  public final void startServer(int paramInt) throws IOException {
    this.serverSocket = new ServerSocket(paramInt, 50);
    this.serverInstance = new Thread(this);
    this.serverInstance.start();
  }
  
  public void serviceRequest() throws IOException {
    byte[] arrayOfByte = new byte[300];
    this.clientOutput.print("Echo server " + getClass().getName() + "\n");
    this.clientOutput.flush();
    int i;
    while ((i = this.clientInput.read(arrayOfByte, 0, arrayOfByte.length)) >= 0)
      this.clientOutput.write(arrayOfByte, 0, i); 
  }
  
  public static void main(String[] paramArrayOfString) {
    try {
      (new NetworkServer()).startServer(8888);
    } catch (IOException iOException) {
      System.out.print("Server failed: " + iOException + "\n");
    } 
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\NetworkServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */