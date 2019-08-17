package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_2 extends Message_1_1 {
  protected int request_id = 0;
  
  Message_1_2() {}
  
  Message_1_2(int paramInt1, GIOPVersion paramGIOPVersion, byte paramByte1, byte paramByte2, int paramInt2) { super(paramInt1, paramGIOPVersion, paramByte1, paramByte2, paramInt2); }
  
  public void unmarshalRequestID(ByteBuffer paramByteBuffer) {
    byte b4;
    byte b3;
    byte b2;
    byte b1;
    if (!isLittleEndian()) {
      b1 = paramByteBuffer.get(12) << 24 & 0xFF000000;
      b2 = paramByteBuffer.get(13) << 16 & 0xFF0000;
      b3 = paramByteBuffer.get(14) << 8 & 0xFF00;
      b4 = paramByteBuffer.get(15) << 0 & 0xFF;
    } else {
      b1 = paramByteBuffer.get(15) << 24 & 0xFF000000;
      b2 = paramByteBuffer.get(14) << 16 & 0xFF0000;
      b3 = paramByteBuffer.get(13) << 8 & 0xFF00;
      b4 = paramByteBuffer.get(12) << 0 & 0xFF;
    } 
    this.request_id = b1 | b2 | b3 | b4;
  }
  
  public void write(OutputStream paramOutputStream) {
    if (this.encodingVersion == 0) {
      super.write(paramOutputStream);
      return;
    } 
    GIOPVersion gIOPVersion;
    this.GIOP_version = (gIOPVersion = this.GIOP_version).getInstance((byte)13, this.encodingVersion);
    super.write(paramOutputStream);
    this.GIOP_version = gIOPVersion;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\Message_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */