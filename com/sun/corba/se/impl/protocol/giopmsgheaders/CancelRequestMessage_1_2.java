package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class CancelRequestMessage_1_2 extends Message_1_1 implements CancelRequestMessage {
  private int request_id = 0;
  
  CancelRequestMessage_1_2() {}
  
  CancelRequestMessage_1_2(int paramInt) {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)2, 4);
    this.request_id = paramInt;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\CancelRequestMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */