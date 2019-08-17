package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Message_1_1 extends MessageBase {
  static final int UPPER_THREE_BYTES_OF_INT_MASK = 255;
  
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
  
  int magic = 0;
  
  GIOPVersion GIOP_version = null;
  
  byte flags = 0;
  
  byte message_type = 0;
  
  int message_size = 0;
  
  Message_1_1() {}
  
  Message_1_1(int paramInt1, GIOPVersion paramGIOPVersion, byte paramByte1, byte paramByte2, int paramInt2) {
    this.magic = paramInt1;
    this.GIOP_version = paramGIOPVersion;
    this.flags = paramByte1;
    this.message_type = paramByte2;
    this.message_size = paramInt2;
  }
  
  public GIOPVersion getGIOPVersion() { return this.GIOP_version; }
  
  public int getType() { return this.message_type; }
  
  public int getSize() { return this.message_size; }
  
  public boolean isLittleEndian() { return ((this.flags & true) == 1); }
  
  public boolean moreFragmentsToFollow() { return ((this.flags & 0x2) == 2); }
  
  public void setThreadPoolToUse(int paramInt) {
    int i = paramInt << 2;
    i &= 0xFF;
    i |= this.flags;
    this.flags = (byte)i;
  }
  
  public void setSize(ByteBuffer paramByteBuffer, int paramInt) {
    this.message_size = paramInt;
    int i = paramInt - 12;
    if (!isLittleEndian()) {
      paramByteBuffer.put(8, (byte)(i >>> 24 & 0xFF));
      paramByteBuffer.put(9, (byte)(i >>> 16 & 0xFF));
      paramByteBuffer.put(10, (byte)(i >>> 8 & 0xFF));
      paramByteBuffer.put(11, (byte)(i >>> 0 & 0xFF));
    } else {
      paramByteBuffer.put(8, (byte)(i >>> 0 & 0xFF));
      paramByteBuffer.put(9, (byte)(i >>> 8 & 0xFF));
      paramByteBuffer.put(10, (byte)(i >>> 16 & 0xFF));
      paramByteBuffer.put(11, (byte)(i >>> 24 & 0xFF));
    } 
  }
  
  public FragmentMessage createFragmentMessage() {
    switch (this.message_type) {
      case 2:
      case 5:
      case 6:
        throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
      case 3:
      case 4:
        if (this.GIOP_version.equals(GIOPVersion.V1_1))
          throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE); 
        break;
    } 
    if (this.GIOP_version.equals(GIOPVersion.V1_1))
      return new FragmentMessage_1_1(this); 
    if (this.GIOP_version.equals(GIOPVersion.V1_2))
      return new FragmentMessage_1_2(this); 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void read(InputStream paramInputStream) {}
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_long(this.magic);
    nullCheck(this.GIOP_version);
    this.GIOP_version.write(paramOutputStream);
    paramOutputStream.write_octet(this.flags);
    paramOutputStream.write_octet(this.message_type);
    paramOutputStream.write_ulong(this.message_size);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\Message_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */