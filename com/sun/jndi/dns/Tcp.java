package com.sun.jndi.dns;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class Tcp {
  private Socket sock;
  
  InputStream in;
  
  OutputStream out;
  
  Tcp(InetAddress paramInetAddress, int paramInt) throws IOException {
    this.sock = new Socket(paramInetAddress, paramInt);
    this.sock.setTcpNoDelay(true);
    this.out = new BufferedOutputStream(this.sock.getOutputStream());
    this.in = new BufferedInputStream(this.sock.getInputStream());
  }
  
  void close() throws IOException { this.sock.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\Tcp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */