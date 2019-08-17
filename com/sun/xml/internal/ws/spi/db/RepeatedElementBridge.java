package com.sun.xml.internal.ws.spi.db;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public class RepeatedElementBridge<T> extends Object implements XMLBridge<T> {
  XMLBridge<T> delegate;
  
  CollectionHandler collectionHandler;
  
  static final CollectionHandler ListHandler = new BaseCollectionHandler(List.class) {
      public Object convert(List param1List) { return param1List; }
    };
  
  static final CollectionHandler HashSetHandler = new BaseCollectionHandler(HashSet.class) {
      public Object convert(List param1List) { return new HashSet(param1List); }
    };
  
  public RepeatedElementBridge(TypeInfo paramTypeInfo, XMLBridge paramXMLBridge) {
    this.delegate = paramXMLBridge;
    this.collectionHandler = create(paramTypeInfo);
  }
  
  public CollectionHandler collectionHandler() { return this.collectionHandler; }
  
  public BindingContext context() { return this.delegate.context(); }
  
  public void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException { this.delegate.marshal(paramT, paramXMLStreamWriter, paramAttachmentMarshaller); }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException { this.delegate.marshal(paramT, paramOutputStream, paramNamespaceContext, paramAttachmentMarshaller); }
  
  public void marshal(T paramT, Node paramNode) throws JAXBException { this.delegate.marshal(paramT, paramNode); }
  
  public void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException { this.delegate.marshal(paramT, paramContentHandler, paramAttachmentMarshaller); }
  
  public void marshal(T paramT, Result paramResult) throws JAXBException { this.delegate.marshal(paramT, paramResult); }
  
  public T unmarshal(XMLStreamReader paramXMLStreamReader, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { return (T)this.delegate.unmarshal(paramXMLStreamReader, paramAttachmentUnmarshaller); }
  
  public T unmarshal(Source paramSource, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { return (T)this.delegate.unmarshal(paramSource, paramAttachmentUnmarshaller); }
  
  public T unmarshal(InputStream paramInputStream) throws JAXBException { return (T)this.delegate.unmarshal(paramInputStream); }
  
  public T unmarshal(Node paramNode, AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException { return (T)this.delegate.unmarshal(paramNode, paramAttachmentUnmarshaller); }
  
  public TypeInfo getTypeInfo() { return this.delegate.getTypeInfo(); }
  
  public boolean supportOutputStream() { return this.delegate.supportOutputStream(); }
  
  public static CollectionHandler create(TypeInfo paramTypeInfo) {
    Class clazz = (Class)paramTypeInfo.type;
    return clazz.isArray() ? new ArrayHandler((Class)(paramTypeInfo.getItemType()).type) : ((List.class.equals(clazz) || Collection.class.equals(clazz)) ? ListHandler : ((java.util.Set.class.equals(clazz) || HashSet.class.equals(clazz)) ? HashSetHandler : new BaseCollectionHandler(clazz)));
  }
  
  static class ArrayHandler implements CollectionHandler {
    Class componentClass;
    
    public ArrayHandler(Class param1Class) { this.componentClass = param1Class; }
    
    public int getSize(Object param1Object) { return Array.getLength(param1Object); }
    
    public Object convert(List param1List) {
      Object object = Array.newInstance(this.componentClass, param1List.size());
      for (byte b = 0; b < param1List.size(); b++)
        Array.set(object, b, param1List.get(b)); 
      return object;
    }
    
    public Iterator iterator(final Object c) { return new Iterator() {
          int index = 0;
          
          public boolean hasNext() { return (c == null || Array.getLength(c) == 0) ? false : ((this.index != Array.getLength(c))); }
          
          public Object next() throws NoSuchElementException {
            Object object = null;
            try {
              object = Array.get(c, this.index++);
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
              throw new NoSuchElementException();
            } 
            return object;
          }
          
          public void remove() {}
        }; }
  }
  
  static class BaseCollectionHandler implements CollectionHandler {
    Class type;
    
    BaseCollectionHandler(Class param1Class) { this.type = param1Class; }
    
    public int getSize(Object param1Object) { return ((Collection)param1Object).size(); }
    
    public Object convert(List param1List) {
      try {
        Object object = this.type.newInstance();
        ((Collection)object).addAll(param1List);
        return object;
      } catch (Exception exception) {
        exception.printStackTrace();
        return param1List;
      } 
    }
    
    public Iterator iterator(Object param1Object) { return ((Collection)param1Object).iterator(); }
  }
  
  public static interface CollectionHandler {
    int getSize(Object param1Object);
    
    Iterator iterator(Object param1Object);
    
    Object convert(List param1List);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\RepeatedElementBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */