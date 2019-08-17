package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.xml.sax.ContentHandler;

public interface Serializer {
  void setOutputStream(OutputStream paramOutputStream);
  
  OutputStream getOutputStream();
  
  void setWriter(Writer paramWriter);
  
  Writer getWriter();
  
  void setOutputFormat(Properties paramProperties);
  
  Properties getOutputFormat();
  
  ContentHandler asContentHandler() throws IOException;
  
  DOMSerializer asDOMSerializer() throws IOException;
  
  boolean reset();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\Serializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */