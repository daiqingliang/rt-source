package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_0 extends Message_1_0 implements LocateRequestMessage {
  private ORB orb = null;
  
  private int request_id = 0;
  
  private byte[] object_key = null;
  
  private ObjectKey objectKey = null;
  
  LocateRequestMessage_1_0(ORB paramORB) { this.orb = paramORB; }
  
  LocateRequestMessage_1_0(ORB paramORB, int paramInt, byte[] paramArrayOfByte) {
    super(1195986768, false, (byte)3, 0);
    this.orb = paramORB;
    this.request_id = paramInt;
    this.object_key = paramArrayOfByte;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public ObjectKey getObjectKey() {
    if (this.objectKey == null)
      this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb); 
    return this.objectKey;
  }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    this.object_key = new byte[i];
    paramInputStream.read_octet_array(this.object_key, 0, i);
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
    nullCheck(this.object_key);
    paramOutputStream.write_long(this.object_key.length);
    paramOutputStream.write_octet_array(this.object_key, 0, this.object_key.length);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */