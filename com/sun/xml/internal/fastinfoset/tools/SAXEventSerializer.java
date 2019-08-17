package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer extends DefaultHandler implements LexicalHandler {
  private Writer _writer;
  
  private boolean _charactersAreCDATA;
  
  private StringBuffer _characters;
  
  private Stack _namespaceStack = new Stack();
  
  protected List _namespaceAttributes;
  
  public SAXEventSerializer(OutputStream paramOutputStream) throws IOException {
    this._writer = new OutputStreamWriter(paramOutputStream);
    this._charactersAreCDATA = false;
  }
  
  public void startDocument() throws SAXException {
    try {
      this._writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
      this._writer.write("<startDocument/>\n");
      this._writer.flush();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endDocument() throws SAXException {
    try {
      this._writer.write("<endDocument/>\n");
      this._writer.write("</sax>");
      this._writer.flush();
      this._writer.close();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (this._namespaceAttributes == null)
      this._namespaceAttributes = new ArrayList(); 
    String str = (paramString1.length() == 0) ? "xmlns" : ("xmlns" + paramString1);
    AttributeValueHolder attributeValueHolder = new AttributeValueHolder(str, paramString1, paramString2, null, null);
    this._namespaceAttributes.add(attributeValueHolder);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    try {
      outputCharacters();
      if (this._namespaceAttributes != null) {
        AttributeValueHolder[] arrayOfAttributeValueHolder2 = new AttributeValueHolder[0];
        AttributeValueHolder[] arrayOfAttributeValueHolder1 = (AttributeValueHolder[])this._namespaceAttributes.toArray(arrayOfAttributeValueHolder2);
        quicksort(arrayOfAttributeValueHolder1, 0, arrayOfAttributeValueHolder1.length - 1);
        for (byte b = 0; b < arrayOfAttributeValueHolder1.length; b++) {
          this._writer.write("<startPrefixMapping prefix=\"" + (arrayOfAttributeValueHolder1[b]).localName + "\" uri=\"" + (arrayOfAttributeValueHolder1[b]).uri + "\"/>\n");
          this._writer.flush();
        } 
        this._namespaceStack.push(arrayOfAttributeValueHolder1);
        this._namespaceAttributes = null;
      } else {
        this._namespaceStack.push(null);
      } 
      AttributeValueHolder[] arrayOfAttributeValueHolder = new AttributeValueHolder[paramAttributes.getLength()];
      byte b1;
      for (b1 = 0; b1 < paramAttributes.getLength(); b1++)
        arrayOfAttributeValueHolder[b1] = new AttributeValueHolder(paramAttributes.getQName(b1), paramAttributes.getLocalName(b1), paramAttributes.getURI(b1), paramAttributes.getType(b1), paramAttributes.getValue(b1)); 
      quicksort(arrayOfAttributeValueHolder, 0, arrayOfAttributeValueHolder.length - 1);
      b1 = 0;
      byte b2;
      for (b2 = 0; b2 < arrayOfAttributeValueHolder.length; b2++) {
        if (!(arrayOfAttributeValueHolder[b2]).uri.equals("http://www.w3.org/2000/xmlns/"))
          b1++; 
      } 
      if (b1 == 0) {
        this._writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
        return;
      } 
      this._writer.write("<startElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\">\n");
      for (b2 = 0; b2 < arrayOfAttributeValueHolder.length; b2++) {
        if (!(arrayOfAttributeValueHolder[b2]).uri.equals("http://www.w3.org/2000/xmlns/"))
          this._writer.write("  <attribute qName=\"" + (arrayOfAttributeValueHolder[b2]).qName + "\" localName=\"" + (arrayOfAttributeValueHolder[b2]).localName + "\" uri=\"" + (arrayOfAttributeValueHolder[b2]).uri + "\" value=\"" + (arrayOfAttributeValueHolder[b2]).value + "\"/>\n"); 
      } 
      this._writer.write("</startElement>\n");
      this._writer.flush();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    try {
      outputCharacters();
      this._writer.write("<endElement uri=\"" + paramString1 + "\" localName=\"" + paramString2 + "\" qName=\"" + paramString3 + "\"/>\n");
      this._writer.flush();
      AttributeValueHolder[] arrayOfAttributeValueHolder = (AttributeValueHolder[])this._namespaceStack.pop();
      if (arrayOfAttributeValueHolder != null)
        for (byte b = 0; b < arrayOfAttributeValueHolder.length; b++) {
          this._writer.write("<endPrefixMapping prefix=\"" + (arrayOfAttributeValueHolder[b]).localName + "\"/>\n");
          this._writer.flush();
        }  
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 == 0)
      return; 
    if (this._characters == null)
      this._characters = new StringBuffer(); 
    this._characters.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void outputCharacters() throws SAXException {
    if (this._characters == null)
      return; 
    try {
      this._writer.write("<characters>" + (this._charactersAreCDATA ? "<![CDATA[" : "") + this._characters + (this._charactersAreCDATA ? "]]>" : "") + "</characters>\n");
      this._writer.flush();
      this._characters = null;
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    try {
      outputCharacters();
      this._writer.write("<processingInstruction target=\"" + paramString1 + "\" data=\"" + paramString2 + "\"/>\n");
      this._writer.flush();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void endDTD() throws SAXException {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() throws SAXException { this._charactersAreCDATA = true; }
  
  public void endCDATA() throws SAXException { this._charactersAreCDATA = false; }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      outputCharacters();
      this._writer.write("<comment>" + new String(paramArrayOfChar, paramInt1, paramInt2) + "</comment>\n");
      this._writer.flush();
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
  }
  
  private void quicksort(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      int i = partition(paramArrayOfAttributeValueHolder, paramInt1, paramInt2);
      quicksort(paramArrayOfAttributeValueHolder, paramInt1, i);
      paramInt1 = i + 1;
    } 
  }
  
  private int partition(AttributeValueHolder[] paramArrayOfAttributeValueHolder, int paramInt1, int paramInt2) {
    AttributeValueHolder attributeValueHolder = paramArrayOfAttributeValueHolder[paramInt1 + paramInt2 >>> 1];
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    while (true) {
      if (attributeValueHolder.compareTo(paramArrayOfAttributeValueHolder[--j]) < 0)
        continue; 
      while (attributeValueHolder.compareTo(paramArrayOfAttributeValueHolder[++i]) > 0);
      if (i < j) {
        AttributeValueHolder attributeValueHolder1 = paramArrayOfAttributeValueHolder[i];
        paramArrayOfAttributeValueHolder[i] = paramArrayOfAttributeValueHolder[j];
        paramArrayOfAttributeValueHolder[j] = attributeValueHolder1;
        continue;
      } 
      break;
    } 
    return j;
  }
  
  public static class AttributeValueHolder implements Comparable {
    public final String qName;
    
    public final String localName;
    
    public final String uri;
    
    public final String type;
    
    public final String value;
    
    public AttributeValueHolder(String param1String1, String param1String2, String param1String3, String param1String4, String param1String5) {
      this.qName = param1String1;
      this.localName = param1String2;
      this.uri = param1String3;
      this.type = param1String4;
      this.value = param1String5;
    }
    
    public int compareTo(Object param1Object) {
      try {
        return this.qName.compareTo(((AttributeValueHolder)param1Object).qName);
      } catch (Exception exception) {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      } 
    }
    
    public boolean equals(Object param1Object) {
      try {
        return (param1Object instanceof AttributeValueHolder && this.qName.equals(((AttributeValueHolder)param1Object).qName));
      } catch (Exception exception) {
        throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
      } 
    }
    
    public int hashCode() {
      null = 7;
      return 97 * null + ((this.qName != null) ? this.qName.hashCode() : 0);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\SAXEventSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */