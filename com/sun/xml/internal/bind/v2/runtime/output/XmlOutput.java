package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface XmlOutput {
  void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException;
  
  void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException;
  
  void beginStartTag(Name paramName) throws IOException, XMLStreamException;
  
  void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException;
  
  void attribute(Name paramName, String paramString) throws IOException, XMLStreamException;
  
  void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException;
  
  void endStartTag() throws IOException, SAXException;
  
  void endTag(Name paramName) throws IOException, XMLStreamException;
  
  void endTag(int paramInt, String paramString) throws IOException, XMLStreamException;
  
  void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException;
  
  void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */