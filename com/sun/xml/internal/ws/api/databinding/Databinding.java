package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public interface Databinding extends Databinding {
  EndpointCallBridge getEndpointBridge(Packet paramPacket) throws DispatchException;
  
  ClientCallBridge getClientBridge(Method paramMethod);
  
  void generateWSDL(WSDLGenInfo paramWSDLGenInfo);
  
  ContentType encode(Packet paramPacket, OutputStream paramOutputStream) throws IOException;
  
  void decode(InputStream paramInputStream, String paramString, Packet paramPacket) throws IOException;
  
  MessageContextFactory getMessageContextFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\Databinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */