package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateRequestMessage_1_2 extends Message_1_2 implements LocateRequestMessage {
  private ORB orb = null;
  
  private ObjectKey objectKey = null;
  
  private TargetAddress target = null;
  
  LocateRequestMessage_1_2(ORB paramORB) { this.orb = paramORB; }
  
  LocateRequestMessage_1_2(ORB paramORB, int paramInt, TargetAddress paramTargetAddress) {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)3, 0);
    this.orb = paramORB;
    this.request_id = paramInt;
    this.target = paramTargetAddress;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public ObjectKey getObjectKey() {
    if (this.objectKey == null)
      this.objectKey = MessageBase.extractObjectKey(this.target, this.orb); 
    return this.objectKey;
  }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    this.target = TargetAddressHelper.read(paramInputStream);
    getObjectKey();
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
    nullCheck(this.target);
    TargetAddressHelper.write(paramOutputStream, this.target);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateRequestMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */