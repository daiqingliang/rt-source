package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class RequestMessage_1_2 extends Message_1_2 implements RequestMessage {
  private ORB orb = null;
  
  private ORBUtilSystemException wrapper = null;
  
  private byte response_flags = 0;
  
  private byte[] reserved = null;
  
  private TargetAddress target = null;
  
  private String operation = null;
  
  private ServiceContexts service_contexts = null;
  
  private ObjectKey objectKey = null;
  
  RequestMessage_1_2(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  RequestMessage_1_2(ORB paramORB, int paramInt, byte paramByte, byte[] paramArrayOfByte, TargetAddress paramTargetAddress, String paramString, ServiceContexts paramServiceContexts) {
    super(1195986768, GIOPVersion.V1_2, (byte)0, (byte)0, 0);
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.request_id = paramInt;
    this.response_flags = paramByte;
    this.reserved = paramArrayOfByte;
    this.target = paramTargetAddress;
    this.operation = paramString;
    this.service_contexts = paramServiceContexts;
  }
  
  public int getRequestId() { return this.request_id; }
  
  public boolean isResponseExpected() { return ((this.response_flags & true) == 1); }
  
  public byte[] getReserved() { return this.reserved; }
  
  public ObjectKey getObjectKey() {
    if (this.objectKey == null)
      this.objectKey = MessageBase.extractObjectKey(this.target, this.orb); 
    return this.objectKey;
  }
  
  public String getOperation() { return this.operation; }
  
  public Principal getPrincipal() { return null; }
  
  public ServiceContexts getServiceContexts() { return this.service_contexts; }
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    this.response_flags = paramInputStream.read_octet();
    this.reserved = new byte[3];
    for (byte b = 0; b < 3; b++)
      this.reserved[b] = paramInputStream.read_octet(); 
    this.target = TargetAddressHelper.read(paramInputStream);
    getObjectKey();
    this.operation = paramInputStream.read_string();
    this.service_contexts = new ServiceContexts((InputStream)paramInputStream);
    ((CDRInputStream)paramInputStream).setHeaderPadding(true);
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    paramOutputStream.write_ulong(this.request_id);
    paramOutputStream.write_octet(this.response_flags);
    nullCheck(this.reserved);
    if (this.reserved.length != 3)
      throw this.wrapper.badReservedLength(CompletionStatus.COMPLETED_MAYBE); 
    for (byte b = 0; b < 3; b++)
      paramOutputStream.write_octet(this.reserved[b]); 
    nullCheck(this.target);
    TargetAddressHelper.write(paramOutputStream, this.target);
    paramOutputStream.write_string(this.operation);
    if (this.service_contexts != null) {
      this.service_contexts.write((OutputStream)paramOutputStream, GIOPVersion.V1_2);
    } else {
      ServiceContexts.writeNullServiceContext((OutputStream)paramOutputStream);
    } 
    ((CDROutputStream)paramOutputStream).setHeaderPadding(true);
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\RequestMessage_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */