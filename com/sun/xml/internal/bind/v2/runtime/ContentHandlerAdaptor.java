package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.SAXException2;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class ContentHandlerAdaptor extends DefaultHandler {
  private final FinalArrayList<String> prefixMap = new FinalArrayList();
  
  private final XMLSerializer serializer;
  
  private final StringBuffer text = new StringBuffer();
  
  ContentHandlerAdaptor(XMLSerializer paramXMLSerializer) { this.serializer = paramXMLSerializer; }
  
  public void startDocument() { this.prefixMap.clear(); }
  
  public void startPrefixMapping(String paramString1, String paramString2) {
    this.prefixMap.add(paramString1);
    this.prefixMap.add(paramString2);
  }
  
  private boolean containsPrefixMapping(String paramString1, String paramString2) {
    for (byte b = 0; b < this.prefixMap.size(); b += 2) {
      if (((String)this.prefixMap.get(b)).equals(paramString1) && ((String)this.prefixMap.get(b + 1)).equals(paramString2))
        return true; 
    } 
    return false;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    try {
      flushText();
      int i = paramAttributes.getLength();
      String str = getPrefix(paramString3);
      if (containsPrefixMapping(str, paramString1)) {
        this.serializer.startElementForce(paramString1, paramString2, str, null);
      } else {
        this.serializer.startElement(paramString1, paramString2, str, null);
      } 
      byte b;
      for (b = 0; b < this.prefixMap.size(); b += 2)
        this.serializer.getNamespaceContext().force((String)this.prefixMap.get(b + true), (String)this.prefixMap.get(b)); 
      for (b = 0; b < i; b++) {
        String str1 = paramAttributes.getQName(b);
        if (!str1.startsWith("xmlns") && paramAttributes.getURI(b).length() != 0) {
          String str2 = getPrefix(str1);
          this.serializer.getNamespaceContext().declareNamespace(paramAttributes.getURI(b), str2, true);
        } 
      } 
      this.serializer.endNamespaceDecls(null);
      for (b = 0; b < i; b++) {
        if (!paramAttributes.getQName(b).startsWith("xmlns"))
          this.serializer.attribute(paramAttributes.getURI(b), paramAttributes.getLocalName(b), paramAttributes.getValue(b)); 
      } 
      this.prefixMap.clear();
      this.serializer.endAttributes();
    } catch (IOException iOException) {
      throw new SAXException2(iOException);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException2(xMLStreamException);
    } 
  }
  
  private String getPrefix(String paramString) {
    int i = paramString.indexOf(':');
    return (i == -1) ? "" : paramString.substring(0, i);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      flushText();
      this.serializer.endElement();
    } catch (IOException iOException) {
      throw new SAXException2(iOException);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException2(xMLStreamException);
    } 
  }
  
  private void flushText() {
    if (this.text.length() != 0) {
      this.serializer.text(this.text.toString(), null);
      this.text.setLength(0);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.text.append(paramArrayOfChar, paramInt1, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ContentHandlerAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */