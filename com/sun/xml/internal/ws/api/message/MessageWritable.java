package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.ContentType;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.ws.soap.MTOMFeature;

public interface MessageWritable {
  ContentType getContentType();
  
  ContentType writeTo(OutputStream paramOutputStream) throws IOException;
  
  void setMTOMConfiguration(MTOMFeature paramMTOMFeature);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\MessageWritable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */