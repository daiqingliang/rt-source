package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeImpl<T> extends InternalBridge<T> {
  private final Name tagName;
  
  private final JaxBeanInfo<T> bi;
  
  private final TypeReference typeRef;
  
  public BridgeImpl(JAXBContextImpl paramJAXBContextImpl, Name paramName, JaxBeanInfo<T> paramJaxBeanInfo, TypeReference paramTypeReference) {
    super(paramJAXBContextImpl);
    this.tagName = paramName;
    this.bi = paramJaxBeanInfo;
    this.typeRef = paramTypeReference;
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter) throws JAXBException {
    MarshallerImpl marshallerImpl = (MarshallerImpl)paramMarshaller;
    marshallerImpl.write(this.tagName, this.bi, paramT, XMLStreamWriterOutput.create(paramXMLStreamWriter, this.context, marshallerImpl.getEscapeHandler()), new StAXPostInitAction(paramXMLStreamWriter, marshallerImpl.serializer));
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException {
    MarshallerImpl marshallerImpl = (MarshallerImpl)paramMarshaller;
    StAXPostInitAction stAXPostInitAction = null;
    if (paramNamespaceContext != null)
      stAXPostInitAction = new StAXPostInitAction(paramNamespaceContext, marshallerImpl.serializer); 
    marshallerImpl.write(this.tagName, this.bi, paramT, marshallerImpl.createWriter(paramOutputStream), stAXPostInitAction);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Node paramNode) throws JAXBException {
    MarshallerImpl marshallerImpl = (MarshallerImpl)paramMarshaller;
    marshallerImpl.write(this.tagName, this.bi, paramT, new SAXOutput(new SAX2DOMEx(paramNode)), new DomPostInitAction(paramNode, marshallerImpl.serializer));
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler) throws JAXBException {
    MarshallerImpl marshallerImpl = (MarshallerImpl)paramMarshaller;
    marshallerImpl.write(this.tagName, this.bi, paramT, new SAXOutput(paramContentHandler), null);
  }
  
  public void marshal(Marshaller paramMarshaller, T paramT, Result paramResult) throws JAXBException {
    MarshallerImpl marshallerImpl = (MarshallerImpl)paramMarshaller;
    marshallerImpl.write(this.tagName, this.bi, paramT, marshallerImpl.createXmlOutput(paramResult), marshallerImpl.createPostInitAction(paramResult));
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader) throws JAXBException {
    UnmarshallerImpl unmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)unmarshallerImpl.unmarshal0(paramXMLStreamReader, this.bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, Source paramSource) throws JAXBException {
    UnmarshallerImpl unmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)unmarshallerImpl.unmarshal0(paramSource, this.bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream) throws JAXBException {
    UnmarshallerImpl unmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)unmarshallerImpl.unmarshal0(paramInputStream, this.bi)).getValue();
  }
  
  @NotNull
  public T unmarshal(Unmarshaller paramUnmarshaller, Node paramNode) throws JAXBException {
    UnmarshallerImpl unmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    return (T)((JAXBElement)unmarshallerImpl.unmarshal0(paramNode, this.bi)).getValue();
  }
  
  public TypeReference getTypeReference() { return this.typeRef; }
  
  public void marshal(T paramT, XMLSerializer paramXMLSerializer) throws IOException, SAXException, XMLStreamException {
    paramXMLSerializer.startElement(this.tagName, null);
    if (paramT == null) {
      paramXMLSerializer.writeXsiNilTrue();
    } else {
      paramXMLSerializer.childAsXsiType(paramT, null, this.bi, false);
    } 
    paramXMLSerializer.endElement();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */