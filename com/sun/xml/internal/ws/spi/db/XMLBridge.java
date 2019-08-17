package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
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

public interface XMLBridge<T> {
  @NotNull
  BindingContext context();
  
  void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException;
  
  void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException;
  
  void marshal(T paramT, Node paramNode) throws JAXBException;
  
  void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException;
  
  void marshal(T paramT, Result paramResult) throws JAXBException;
  
  @NotNull
  T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException;
  
  @NotNull
  T unmarshal(@NotNull Source paramSource, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException;
  
  @NotNull
  T unmarshal(@NotNull InputStream paramInputStream) throws JAXBException;
  
  @NotNull
  T unmarshal(@NotNull Node paramNode, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException;
  
  TypeInfo getTypeInfo();
  
  boolean supportOutputStream();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\XMLBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */