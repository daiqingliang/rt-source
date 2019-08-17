package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.encoding.HasEncoding;
import java.io.OutputStream;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterUtil {
  @Nullable
  public static OutputStream getOutputStream(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    Object object = null;
    XMLStreamWriter xMLStreamWriter = (paramXMLStreamWriter instanceof XMLStreamWriterFactory.HasEncodingWriter) ? ((XMLStreamWriterFactory.HasEncodingWriter)paramXMLStreamWriter).getWriter() : paramXMLStreamWriter;
    if (xMLStreamWriter instanceof Map)
      object = ((Map)xMLStreamWriter).get("sjsxp-outputstream"); 
    if (object == null)
      try {
        object = paramXMLStreamWriter.getProperty("com.ctc.wstx.outputUnderlyingStream");
      } catch (Exception exception) {} 
    if (object == null)
      try {
        object = paramXMLStreamWriter.getProperty("http://java.sun.com/xml/stream/properties/outputstream");
      } catch (Exception exception) {} 
    if (object != null) {
      paramXMLStreamWriter.writeCharacters("");
      paramXMLStreamWriter.flush();
      return (OutputStream)object;
    } 
    return null;
  }
  
  @Nullable
  public static String getEncoding(XMLStreamWriter paramXMLStreamWriter) { return (paramXMLStreamWriter instanceof HasEncoding) ? ((HasEncoding)paramXMLStreamWriter).getEncoding() : null; }
  
  public static String encodeQName(XMLStreamWriter paramXMLStreamWriter, QName paramQName, PrefixFactory paramPrefixFactory) {
    try {
      String str1 = paramQName.getNamespaceURI();
      String str2 = paramQName.getLocalPart();
      if (str1 == null || str1.equals(""))
        return str2; 
      String str3 = paramXMLStreamWriter.getPrefix(str1);
      if (str3 == null) {
        str3 = paramPrefixFactory.getPrefix(str1);
        paramXMLStreamWriter.writeNamespace(str3, str1);
      } 
      return str3 + ":" + str2;
    } catch (XMLStreamException xMLStreamException) {
      throw new RuntimeException(xMLStreamException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\XMLStreamWriterUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */