package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;

public class SAX2StAXEventWriter extends SAX2StAXBaseWriter {
  private XMLEventWriter writer;
  
  private XMLEventFactory eventFactory;
  
  private List namespaceStack = new ArrayList();
  
  private boolean needToCallStartDocument = false;
  
  public SAX2StAXEventWriter() { this.eventFactory = XMLEventFactory.newInstance(); }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter) {
    this.writer = paramXMLEventWriter;
    this.eventFactory = XMLEventFactory.newInstance();
  }
  
  public SAX2StAXEventWriter(XMLEventWriter paramXMLEventWriter, XMLEventFactory paramXMLEventFactory) {
    this.writer = paramXMLEventWriter;
    if (paramXMLEventFactory != null) {
      this.eventFactory = paramXMLEventFactory;
    } else {
      this.eventFactory = XMLEventFactory.newInstance();
    } 
  }
  
  public XMLEventWriter getEventWriter() { return this.writer; }
  
  public void setEventWriter(XMLEventWriter paramXMLEventWriter) { this.writer = paramXMLEventWriter; }
  
  public XMLEventFactory getEventFactory() { return this.eventFactory; }
  
  public void setEventFactory(XMLEventFactory paramXMLEventFactory) { this.eventFactory = paramXMLEventFactory; }
  
  public void startDocument() {
    super.startDocument();
    this.namespaceStack.clear();
    this.eventFactory.setLocation(getCurrentLocation());
    this.needToCallStartDocument = true;
  }
  
  private void writeStartDocument() {
    try {
      if (this.docLocator == null) {
        this.writer.add(this.eventFactory.createStartDocument());
      } else {
        try {
          this.writer.add(this.eventFactory.createStartDocument(((Locator2)this.docLocator).getEncoding(), ((Locator2)this.docLocator).getXMLVersion()));
        } catch (ClassCastException classCastException) {
          this.writer.add(this.eventFactory.createStartDocument());
        } 
      } 
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
    this.needToCallStartDocument = false;
  }
  
  public void endDocument() {
    this.eventFactory.setLocation(getCurrentLocation());
    try {
      this.writer.add(this.eventFactory.createEndDocument());
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
    super.endDocument();
    this.namespaceStack.clear();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.needToCallStartDocument)
      writeStartDocument(); 
    this.eventFactory.setLocation(getCurrentLocation());
    Collection[] arrayOfCollection = { null, null };
    createStartEvents(paramAttributes, arrayOfCollection);
    this.namespaceStack.add(arrayOfCollection[0]);
    try {
      String[] arrayOfString = { null, null };
      parseQName(paramString3, arrayOfString);
      this.writer.add(this.eventFactory.createStartElement(arrayOfString[0], paramString1, arrayOfString[1], arrayOfCollection[1].iterator(), arrayOfCollection[0].iterator()));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } finally {
      super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    super.endElement(paramString1, paramString2, paramString3);
    this.eventFactory.setLocation(getCurrentLocation());
    String[] arrayOfString = { null, null };
    parseQName(paramString3, arrayOfString);
    Collection collection = (Collection)this.namespaceStack.remove(this.namespaceStack.size() - 1);
    Iterator iterator = collection.iterator();
    try {
      this.writer.add(this.eventFactory.createEndElement(arrayOfString[0], paramString1, arrayOfString[1], iterator));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.needToCallStartDocument)
      writeStartDocument(); 
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    this.eventFactory.setLocation(getCurrentLocation());
    try {
      this.writer.add(this.eventFactory.createComment(new String(paramArrayOfChar, paramInt1, paramInt2)));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    try {
      if (!this.isCDATA) {
        this.eventFactory.setLocation(getCurrentLocation());
        this.writer.add(this.eventFactory.createCharacters(new String(paramArrayOfChar, paramInt1, paramInt2)));
      } 
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (this.needToCallStartDocument)
      writeStartDocument(); 
    super.processingInstruction(paramString1, paramString2);
    try {
      this.writer.add(this.eventFactory.createProcessingInstruction(paramString1, paramString2));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
  }
  
  public void endCDATA() {
    this.eventFactory.setLocation(getCurrentLocation());
    try {
      this.writer.add(this.eventFactory.createCData(this.CDATABuffer.toString()));
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException(xMLStreamException);
    } 
    super.endCDATA();
  }
  
  protected void createStartEvents(Attributes paramAttributes, Collection[] paramArrayOfCollection) {
    HashMap hashMap = null;
    ArrayList arrayList = null;
    if (this.namespaces != null) {
      int j = this.namespaces.size();
      for (byte b1 = 0; b1 < j; b1++) {
        String str1 = (String)this.namespaces.elementAt(b1++);
        String str2 = (String)this.namespaces.elementAt(b1);
        Namespace namespace = createNamespace(str1, str2);
        if (hashMap == null)
          hashMap = new HashMap(); 
        hashMap.put(str1, namespace);
      } 
    } 
    String[] arrayOfString = { null, null };
    byte b = 0;
    int i = paramAttributes.getLength();
    while (b < i) {
      parseQName(paramAttributes.getQName(b), arrayOfString);
      String str1 = arrayOfString[0];
      String str2 = arrayOfString[1];
      String str3 = paramAttributes.getQName(b);
      String str4 = paramAttributes.getValue(b);
      String str5 = paramAttributes.getURI(b);
      if ("xmlns".equals(str3) || "xmlns".equals(str1)) {
        if (hashMap == null)
          hashMap = new HashMap(); 
        if (!hashMap.containsKey(str2)) {
          Namespace namespace = createNamespace(str2, str4);
          hashMap.put(str2, namespace);
        } 
      } else {
        Attribute attribute;
        if (str1.length() > 0) {
          attribute = this.eventFactory.createAttribute(str1, str5, str2, str4);
        } else {
          attribute = this.eventFactory.createAttribute(str2, str4);
        } 
        if (arrayList == null)
          arrayList = new ArrayList(); 
        arrayList.add(attribute);
      } 
      b++;
    } 
    paramArrayOfCollection[0] = (hashMap == null) ? Collections.EMPTY_LIST : hashMap.values();
    paramArrayOfCollection[1] = (arrayList == null) ? Collections.EMPTY_LIST : arrayList;
  }
  
  protected Namespace createNamespace(String paramString1, String paramString2) { return (paramString1 == null || paramString1.length() == 0) ? this.eventFactory.createNamespace(paramString2) : this.eventFactory.createNamespace(paramString1, paramString2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXEventWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */