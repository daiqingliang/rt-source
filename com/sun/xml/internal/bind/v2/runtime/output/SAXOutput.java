package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.util.AttributesImpl;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public class SAXOutput extends XmlOutputAbstractImpl {
  protected final ContentHandler out;
  
  private String elementNsUri;
  
  private String elementLocalName;
  
  private String elementQName;
  
  private char[] buf = new char[256];
  
  private final AttributesImpl atts = new AttributesImpl();
  
  public SAXOutput(ContentHandler paramContentHandler) {
    this.out = paramContentHandler;
    paramContentHandler.setDocumentLocator(new LocatorImpl());
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws SAXException, IOException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean)
      this.out.startDocument(); 
  }
  
  public void endDocument(boolean paramBoolean) throws SAXException, IOException, XMLStreamException {
    if (!paramBoolean)
      this.out.endDocument(); 
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString) {
    this.elementNsUri = this.nsContext.getNamespaceURI(paramInt);
    this.elementLocalName = paramString;
    this.elementQName = getQName(paramInt, paramString);
    this.atts.clear();
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) {
    String str2;
    String str1;
    if (paramInt == -1) {
      str2 = "";
      str1 = paramString1;
    } else {
      str2 = this.nsContext.getNamespaceURI(paramInt);
      String str = this.nsContext.getPrefix(paramInt);
      if (str.length() == 0) {
        str1 = paramString1;
      } else {
        str1 = str + ':' + paramString1;
      } 
    } 
    this.atts.addAttribute(str2, paramString1, str1, "CDATA", paramString2);
  }
  
  public void endStartTag() throws SAXException {
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    if (element != null) {
      int i = element.count();
      for (byte b = 0; b < i; b++) {
        String str1 = element.getPrefix(b);
        String str2 = element.getNsUri(b);
        if (str2.length() != 0 || element.getBase() != 1)
          this.out.startPrefixMapping(str1, str2); 
      } 
    } 
    this.out.startElement(this.elementNsUri, this.elementLocalName, this.elementQName, this.atts);
  }
  
  public void endTag(int paramInt, String paramString) {
    this.out.endElement(this.nsContext.getNamespaceURI(paramInt), paramString, getQName(paramInt, paramString));
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    if (element != null) {
      int i = element.count();
      for (int j = i - 1; j >= 0; j--) {
        String str1 = element.getPrefix(j);
        String str2 = element.getNsUri(j);
        if (str2.length() != 0 || element.getBase() != 1)
          this.out.endPrefixMapping(str1); 
      } 
    } 
  }
  
  private String getQName(int paramInt, String paramString) {
    String str1;
    String str2 = this.nsContext.getPrefix(paramInt);
    if (str2.length() == 0) {
      str1 = paramString;
    } else {
      str1 = str2 + ':' + paramString;
    } 
    return str1;
  }
  
  public void text(String paramString, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    int i = paramString.length();
    if (this.buf.length <= i)
      this.buf = new char[Math.max(this.buf.length * 2, i + 1)]; 
    if (paramBoolean) {
      paramString.getChars(0, i, this.buf, 1);
      this.buf[0] = ' ';
    } else {
      paramString.getChars(0, i, this.buf, 0);
    } 
    this.out.characters(this.buf, 0, i + (paramBoolean ? 1 : 0));
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    int i = paramPcdata.length();
    if (this.buf.length <= i)
      this.buf = new char[Math.max(this.buf.length * 2, i + 1)]; 
    if (paramBoolean) {
      paramPcdata.writeTo(this.buf, 1);
      this.buf[0] = ' ';
    } else {
      paramPcdata.writeTo(this.buf, 0);
    } 
    this.out.characters(this.buf, 0, i + (paramBoolean ? 1 : 0));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\SAXOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */