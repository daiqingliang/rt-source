package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ForkXmlOutput extends XmlOutputAbstractImpl {
  private final XmlOutput lhs;
  
  private final XmlOutput rhs;
  
  public ForkXmlOutput(XmlOutput paramXmlOutput1, XmlOutput paramXmlOutput2) {
    this.lhs = paramXmlOutput1;
    this.rhs = paramXmlOutput2;
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    this.lhs.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    this.rhs.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    this.lhs.endDocument(paramBoolean);
    this.rhs.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName) throws IOException, XMLStreamException {
    this.lhs.beginStartTag(paramName);
    this.rhs.beginStartTag(paramName);
  }
  
  public void attribute(Name paramName, String paramString) throws IOException, XMLStreamException {
    this.lhs.attribute(paramName, paramString);
    this.rhs.attribute(paramName, paramString);
  }
  
  public void endTag(Name paramName) throws IOException, XMLStreamException {
    this.lhs.endTag(paramName);
    this.rhs.endTag(paramName);
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException {
    this.lhs.beginStartTag(paramInt, paramString);
    this.rhs.beginStartTag(paramInt, paramString);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException {
    this.lhs.attribute(paramInt, paramString1, paramString2);
    this.rhs.attribute(paramInt, paramString1, paramString2);
  }
  
  public void endStartTag() throws IOException, SAXException {
    this.lhs.endStartTag();
    this.rhs.endStartTag();
  }
  
  public void endTag(int paramInt, String paramString) throws IOException, XMLStreamException {
    this.lhs.endTag(paramInt, paramString);
    this.rhs.endTag(paramInt, paramString);
  }
  
  public void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    this.lhs.text(paramString, paramBoolean);
    this.rhs.text(paramString, paramBoolean);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    this.lhs.text(paramPcdata, paramBoolean);
    this.rhs.text(paramPcdata, paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\ForkXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */