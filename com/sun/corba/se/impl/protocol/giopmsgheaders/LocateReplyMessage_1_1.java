package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class LocateReplyMessage_1_1 extends Message_1_1 implements LocateReplyMessage {
  private ORB orb = null;
  
  private int request_id = 0;
  
  private int reply_status = 0;
  
  private IOR ior = null;
  
  LocateReplyMessage_1_1(ORB paramORB) { this.orb = paramORB; }
  
  LocateReplyMessage_1_1(ORB paramORB, int paramInt1, int paramInt2, IOR paramIOR) {
    super(1195986768, GIOPVersion.V1_1, (byte)0, (byte)4, 0);
    this.orb = paramORB;
    this.request_id = paramInt1;
    this.reply_status = paramInt2;
    this.ior = paramIOR;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public int getReplyStatus() { return this.reply_status; }
  
  public short getAddrDisposition() { return 0; }
  
  public SystemException getSystemException(String paramString) { return null; }
  
  public IOR getIOR() { return this.ior; }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    this.reply_status = paramInputStream.read_long();
    isValidReplyStatus(this.reply_status);
    if (this.reply_status == 2) {
      CDRInputStream cDRInputStream = (CDRInputStream)paramInputStream;
      this.ior = IORFactories.makeIOR(cDRInputStream);
    } 
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
    paramOutputStream.write_long(this.reply_status);
  }
  
  public static void isValidReplyStatus(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        return;
    } 
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("rpc.protocol");
    throw oRBUtilSystemException.illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\LocateReplyMessage_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */