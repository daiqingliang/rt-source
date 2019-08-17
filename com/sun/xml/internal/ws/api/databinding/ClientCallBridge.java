package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.lang.reflect.Method;

public interface ClientCallBridge {
  Packet createRequestPacket(JavaCallInfo paramJavaCallInfo);
  
  JavaCallInfo readResponse(Packet paramPacket, JavaCallInfo paramJavaCallInfo) throws Throwable;
  
  Method getMethod();
  
  JavaMethod getOperationModel();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\ClientCallBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */