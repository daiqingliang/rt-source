package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class XmlOutputAbstractImpl implements XmlOutput {
  protected int[] nsUriIndex2prefixIndex;
  
  protected NamespaceContextImpl nsContext;
  
  protected XMLSerializer serializer;
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    this.nsUriIndex2prefixIndex = paramArrayOfInt;
    this.nsContext = paramNamespaceContextImpl;
    this.serializer = paramXMLSerializer;
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException { this.serializer = null; }
  
  public void beginStartTag(Name paramName) throws IOException, XMLStreamException { beginStartTag(this.nsUriIndex2prefixIndex[paramName.nsUriIndex], paramName.localName); }
  
  public abstract void beginStartTag(int paramInt, String paramString) throws IOException, XMLStreamException;
  
  public void attribute(Name paramName, String paramString) throws IOException, XMLStreamException {
    short s = paramName.nsUriIndex;
    if (s == -1) {
      attribute(-1, paramName.localName, paramString);
    } else {
      attribute(this.nsUriIndex2prefixIndex[s], paramName.localName, paramString);
    } 
  }
  
  public abstract void attribute(int paramInt, String paramString1, String paramString2) throws IOException, XMLStreamException;
  
  public abstract void endStartTag();
  
  public void endTag(Name paramName) throws IOException, XMLStreamException { endTag(this.nsUriIndex2prefixIndex[paramName.nsUriIndex], paramName.localName); }
  
  public abstract void endTag(int paramInt, String paramString) throws IOException, XMLStreamException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XmlOutputAbstractImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */