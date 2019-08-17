package java.net;

import java.io.IOException;
import sun.net.sdp.SdpSupport;

class SdpSocketImpl extends PlainSocketImpl {
  protected void create(boolean paramBoolean) throws IOException {
    if (!paramBoolean)
      throw new UnsupportedOperationException("Must be a stream socket"); 
    this.fd = SdpSupport.createSocket();
    if (this.socket != null)
      this.socket.setCreated(); 
    if (this.serverSocket != null)
      this.serverSocket.setCreated(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\SdpSocketImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */