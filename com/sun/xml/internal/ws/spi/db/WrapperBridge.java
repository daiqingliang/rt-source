package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class WrapperBridge<T> extends Object implements XMLBridge<T> {
  BindingContext parent;
  
  TypeInfo typeInfo;
  
  static final String WrapperPrefix = "w";
  
  static final String WrapperPrefixColon = "w:";
  
  public WrapperBridge(BindingContext paramBindingContext, TypeInfo paramTypeInfo) {
    this.parent = paramBindingContext;
    this.typeInfo = paramTypeInfo;
  }
  
  public BindingContext context() { return this.parent; }
  
  public TypeInfo getTypeInfo() { return this.typeInfo; }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {
    WrapperComposite wrapperComposite = (WrapperComposite)paramT;
    Attributes attributes = new Attributes() {
        public int getLength() { return 0; }
        
        public String getURI(int param1Int) { return null; }
        
        public String getLocalName(int param1Int) { return null; }
        
        public String getQName(int param1Int) { return null; }
        
        public String getType(int param1Int) { return null; }
        
        public String getValue(int param1Int) { return null; }
        
        public int getIndex(String param1String1, String param1String2) { return 0; }
        
        public int getIndex(String param1String) { return 0; }
        
        public String getType(String param1String1, String param1String2) { return null; }
        
        public String getType(String param1String) { return null; }
        
        public String getValue(String param1String1, String param1String2) { return null; }
        
        public String getValue(String param1String) { return null; }
      };
    try {
      paramContentHandler.startPrefixMapping("w", this.typeInfo.tagName.getNamespaceURI());
      paramContentHandler.startElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), "w:" + this.typeInfo.tagName.getLocalPart(), attributes);
    } catch (SAXException sAXException) {
      throw new JAXBException(sAXException);
    } 
    if (wrapperComposite.bridges != null)
      for (byte b = 0; b < wrapperComposite.bridges.length; b++) {
        if (wrapperComposite.bridges[b] instanceof RepeatedElementBridge) {
          RepeatedElementBridge repeatedElementBridge = (RepeatedElementBridge)wrapperComposite.bridges[b];
          Iterator iterator = repeatedElementBridge.collectionHandler().iterator(wrapperComposite.values[b]);
          while (iterator.hasNext())
            repeatedElementBridge.marshal(iterator.next(), paramContentHandler, paramAttachmentMarshaller); 
        } else {
          wrapperComposite.bridges[b].marshal(wrapperComposite.values[b], paramContentHandler, paramAttachmentMarshaller);
        } 
      }  
    try {
      paramContentHandler.endElement(this.typeInfo.tagName.getNamespaceURI(), this.typeInfo.tagName.getLocalPart(), null);
      paramContentHandler.endPrefixMapping("w");
    } catch (SAXException sAXException) {
      throw new JAXBException(sAXException);
    } 
  }
  
  public void marshal(T paramT, Node paramNode) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {}
  
  public final void marshal(T paramT, Result paramResult) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {
    WrapperComposite wrapperComposite = (WrapperComposite)paramT;
    try {
      String str = paramXMLStreamWriter.getPrefix(this.typeInfo.tagName.getNamespaceURI());
      if (str == null)
        str = "w"; 
      paramXMLStreamWriter.writeStartElement(str, this.typeInfo.tagName.getLocalPart(), this.typeInfo.tagName.getNamespaceURI());
      paramXMLStreamWriter.writeNamespace(str, this.typeInfo.tagName.getNamespaceURI());
    } catch (XMLStreamException xMLStreamException) {
      xMLStreamException.printStackTrace();
      throw new DatabindingException(xMLStreamException);
    } 
    if (wrapperComposite.bridges != null)
      for (byte b = 0; b < wrapperComposite.bridges.length; b++) {
        if (wrapperComposite.bridges[b] instanceof RepeatedElementBridge) {
          RepeatedElementBridge repeatedElementBridge = (RepeatedElementBridge)wrapperComposite.bridges[b];
          Iterator iterator = repeatedElementBridge.collectionHandler().iterator(wrapperComposite.values[b]);
          while (iterator.hasNext())
            repeatedElementBridge.marshal(iterator.next(), paramXMLStreamWriter, paramAttachmentMarshaller); 
        } else {
          wrapperComposite.bridges[b].marshal(wrapperComposite.values[b], paramXMLStreamWriter, paramAttachmentMarshaller);
        } 
      }  
    try {
      paramXMLStreamWriter.writeEndElement();
    } catch (XMLStreamException xMLStreamException) {
      throw new DatabindingException(xMLStreamException);
    } 
  }
  
  public final T unmarshal(InputStream paramInputStream) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public final T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public final T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public final T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public boolean supportOutputStream() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\WrapperBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */