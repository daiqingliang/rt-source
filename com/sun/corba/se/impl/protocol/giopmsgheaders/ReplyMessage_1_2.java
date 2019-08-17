package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class ReplyMessage_1_2 extends Message_1_2 implements ReplyMessage {
  private ORB orb = null;
  
  private ORBUtilSystemException wrapper = null;
  
  private int reply_status = 0;
  
  private ServiceContexts service_contexts = null;
  
  private IOR ior = null;
  
  private String exClassName = null;
  
  private int minorCode = 0;
  
  private CompletionStatus completionStatus = null;
  
  private short addrDisposition = 0;
  
  ReplyMessage_1_2(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  ReplyMessage_1_2(ORB paramORB, int paramInt1, int paramInt2, ServiceContexts paramServiceContexts, IOR paramIOR) {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)1, 0);
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.request_id = paramInt1;
    this.reply_status = paramInt2;
    this.service_contexts = paramServiceContexts;
    this.ior = paramIOR;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public int getReplyStatus() { return this.reply_status; }
  
  public short getAddrDisposition() { return this.addrDisposition; }
  
  public ServiceContexts getServiceContexts() { return this.service_contexts; }
  
  public void setServiceContexts(ServiceContexts paramServiceContexts) { this.service_contexts = paramServiceContexts; }
  
  public SystemException getSystemException(String paramString) { return MessageBase.getSystemException(this.exClassName, this.minorCode, this.completionStatus, paramString, this.wrapper); }
  
  public IOR getIOR() { return this.ior; }
  
  public void setIOR(IOR paramIOR) { this.ior = paramIOR; }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    this.reply_status = paramInputStream.read_long();
    isValidReplyStatus(this.reply_status);
    this.service_contexts = new ServiceContexts((InputStream)paramInputStream);
    ((CDRInputStream)paramInputStream).setHeaderPadding(true);
    if (this.reply_status == 2) {
      String str = paramInputStream.read_string();
      this.exClassName = ORBUtility.classNameOf(str);
      this.minorCode = paramInputStream.read_long();
      int i = paramInputStream.read_long();
      switch (i) {
        case 0:
          this.completionStatus = CompletionStatus.COMPLETED_YES;
          return;
        case 1:
          this.completionStatus = CompletionStatus.COMPLETED_NO;
          return;
        case 2:
          this.completionStatus = CompletionStatus.COMPLETED_MAYBE;
          return;
      } 
      throw this.wrapper.badCompletionStatusInReply(CompletionStatus.COMPLETED_MAYBE, new Integer(i));
    } 
    if (this.reply_status != 1)
      if (this.reply_status == 3 || this.reply_status == 4) {
        CDRInputStream cDRInputStream = (CDRInputStream)paramInputStream;
        this.ior = IORFactories.makeIOR(cDRInputStream);
      } else if (this.reply_status == 5) {
        this.addrDisposition = AddressingDispositionHelper.read(paramInputStream);
      }  
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
    paramOutputStream.write_long(this.reply_status);
    if (this.service_contexts != null) {
      this.service_contexts.write((OutputStream)paramOutputStream, GIOPVersion.V1_2);
    } else {
      ServiceContexts.writeNullServiceContext((OutputStream)paramOutputStream);
    } 
    ((CDROutputStream)paramOutputStream).setHeaderPadding(true);
  }
  
  public static void isValidReplyStatus(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        return;
    } 
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get("rpc.protocol");
    throw oRBUtilSystemException.illegalReplyStatus(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\ReplyMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */