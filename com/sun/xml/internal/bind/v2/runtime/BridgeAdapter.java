package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

final class BridgeAdapter<OnWire, InMemory> extends InternalBridge<InMemory> {
  private final InternalBridge<OnWire> core;
  
  private final Class<? extends XmlAdapter<OnWire, InMemory>> adapter;
  
  public BridgeAdapter(InternalBridge<OnWire> paramInternalBridge, Class<? extends XmlAdapter<OnWire, InMemory>> paramClass) {
    super(paramInternalBridge.getContext());
    this.core = paramInternalBridge;
    this.adapter = paramClass;
  }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, XMLStreamWriter paramXMLStreamWriter) throws JAXBException { this.core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramXMLStreamWriter); }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException { this.core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramOutputStream, paramNamespaceContext); }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, Node paramNode) throws JAXBException { this.core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramNode); }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, ContentHandler paramContentHandler) throws JAXBException { this.core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramContentHandler); }
  
  public void marshal(Marshaller paramMarshaller, InMemory paramInMemory, Result paramResult) throws JAXBException { this.core.marshal(paramMarshaller, adaptM(paramMarshaller, paramInMemory), paramResult); }
  
  private OnWire adaptM(Marshaller paramMarshaller, InMemory paramInMemory) throws JAXBException {
    xMLSerializer = ((MarshallerImpl)paramMarshaller).serializer;
    xMLSerializer.pushCoordinator();
    try {
      object = _adaptM(xMLSerializer, paramInMemory);
      return (OnWire)object;
    } finally {
      xMLSerializer.popCoordinator();
    } 
  }
  
  private OnWire _adaptM(XMLSerializer paramXMLSerializer, InMemory paramInMemory) throws MarshalException {
    XmlAdapter xmlAdapter = paramXMLSerializer.getAdapter(this.adapter);
    try {
      return (OnWire)xmlAdapter.marshal(paramInMemory);
    } catch (Exception exception) {
      paramXMLSerializer.handleError(exception, paramInMemory, null);
      throw new MarshalException(exception);
    } 
  }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, XMLStreamReader paramXMLStreamReader) throws JAXBException { return (InMemory)adaptU(paramUnmarshaller, this.core.unmarshal(paramUnmarshaller, paramXMLStreamReader)); }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, Source paramSource) throws JAXBException { return (InMemory)adaptU(paramUnmarshaller, this.core.unmarshal(paramUnmarshaller, paramSource)); }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, InputStream paramInputStream) throws JAXBException { return (InMemory)adaptU(paramUnmarshaller, this.core.unmarshal(paramUnmarshaller, paramInputStream)); }
  
  @NotNull
  public InMemory unmarshal(Unmarshaller paramUnmarshaller, Node paramNode) throws JAXBException { return (InMemory)adaptU(paramUnmarshaller, this.core.unmarshal(paramUnmarshaller, paramNode)); }
  
  public TypeReference getTypeReference() { return this.core.getTypeReference(); }
  
  @NotNull
  private InMemory adaptU(Unmarshaller paramUnmarshaller, OnWire paramOnWire) throws JAXBException {
    unmarshallerImpl = (UnmarshallerImpl)paramUnmarshaller;
    XmlAdapter xmlAdapter = unmarshallerImpl.coordinator.getAdapter(this.adapter);
    unmarshallerImpl.coordinator.pushCoordinator();
    try {
      object = xmlAdapter.unmarshal(paramOnWire);
      return (InMemory)object;
    } catch (Exception exception) {
      throw new UnmarshalException(exception);
    } finally {
      unmarshallerImpl.coordinator.popCoordinator();
    } 
  }
  
  void marshal(InMemory paramInMemory, XMLSerializer paramXMLSerializer) throws IOException, SAXException, XMLStreamException {
    try {
      this.core.marshal(_adaptM(XMLSerializer.getInstance(), paramInMemory), paramXMLSerializer);
    } catch (MarshalException marshalException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */