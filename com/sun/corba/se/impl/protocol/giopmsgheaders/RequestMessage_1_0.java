package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.io.IOException;
import org.omg.CORBA.Principal;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class RequestMessage_1_0 extends Message_1_0 implements RequestMessage {
  private ORB orb = null;
  
  private ServiceContexts service_contexts = null;
  
  private int request_id = 0;
  
  private boolean response_expected = false;
  
  private byte[] object_key = null;
  
  private String operation = null;
  
  private Principal requesting_principal = null;
  
  private ObjectKey objectKey = null;
  
  RequestMessage_1_0(ORB paramORB) { this.orb = paramORB; }
  
  RequestMessage_1_0(ORB paramORB, ServiceContexts paramServiceContexts, int paramInt, boolean paramBoolean, byte[] paramArrayOfByte, String paramString, Principal paramPrincipal) {
    super(1195986768, false, (byte)0, 0);
    this.orb = paramORB;
    this.service_contexts = paramServiceContexts;
    this.request_id = paramInt;
    this.response_expected = paramBoolean;
    this.object_key = paramArrayOfByte;
    this.operation = paramString;
    this.requesting_principal = paramPrincipal;
  }
  
  public ServiceContexts getServiceContexts() { return this.service_contexts; }
  
  public int getRequestId() { return this.request_id; }
  
  public boolean isResponseExpected() { return this.response_expected; }
  
  public byte[] getReserved() { return null; }
  
  public ObjectKey getObjectKey() {
    if (this.objectKey == null)
      this.objectKey = MessageBase.extractObjectKey(this.object_key, this.orb); 
    return this.objectKey;
  }
  
  public String getOperation() { return this.operation; }
  
  public Principal getPrincipal() { return this.requesting_principal; }
  
  public void setThreadPoolToUse(int paramInt) {}
  
  public void read(InputStream paramInputStream) {
    super.read(paramInputStream);
    this.service_contexts = new ServiceContexts((InputStream)paramInputStream);
    this.request_id = paramInputStream.read_ulong();
    this.response_expected = paramInputStream.read_boolean();
    int i = paramInputStream.read_long();
    this.object_key = new byte[i];
    paramInputStream.read_octet_array(this.object_key, 0, i);
    this.operation = paramInputStream.read_string();
    this.requesting_principal = paramInputStream.read_Principal();
  }
  
  public void write(OutputStream paramOutputStream) {
    super.write(paramOutputStream);
    if (this.service_contexts != null) {
      this.service_contexts.write((OutputStream)paramOutputStream, GIOPVersion.V1_0);
    } else {
      ServiceContexts.writeNullServiceContext((OutputStream)paramOutputStream);
    } 
    paramOutputStream.write_ulong(this.request_id);
    paramOutputStream.write_boolean(this.response_expected);
    nullCheck(this.object_key);
    paramOutputStream.write_long(this.object_key.length);
    paramOutputStream.write_octet_array(this.object_key, 0, this.object_key.length);
    paramOutputStream.write_string(this.operation);
    if (this.requesting_principal != null) {
      paramOutputStream.write_Principal(this.requesting_principal);
    } else {
      paramOutputStream.write_long(0);
    } 
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\RequestMessage_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */