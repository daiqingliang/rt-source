package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FragmentMessage_1_2 extends Message_1_2 implements FragmentMessage {
  FragmentMessage_1_2() {}
  
  FragmentMessage_1_2(int paramInt) {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)7, 0);
    this.message_type = 7;
    this.request_id = paramInt;
  }
  
  FragmentMessage_1_2(Message_1_1 paramMessage_1_1) {
    this.magic = paramMessage_1_1.magic;
    this.GIOP_version = paramMessage_1_1.GIOP_version;
    this.flags = paramMessage_1_1.flags;
    this.message_type = 7;
    this.message_size = 0;
    switch (paramMessage_1_1.message_type) {
      case 0:
        this.request_id = ((RequestMessage)paramMessage_1_1).getRequestId();
        break;
      case 1:
        this.request_id = ((ReplyMessage)paramMessage_1_1).getRequestId();
        break;
      case 3:
        this.request_id = ((LocateRequestMessage)paramMessage_1_1).getRequestId();
        break;
      case 4:
        this.request_id = ((LocateReplyMessage)paramMessage_1_1).getRequestId();
        break;
      case 7:
        this.request_id = ((FragmentMessage)paramMessage_1_1).getRequestId();
        break;
    } 
  }
  
  public int getRequestId() { return this.request_id; }
  
  public int getHeaderLength() { return 16; }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\FragmentMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */