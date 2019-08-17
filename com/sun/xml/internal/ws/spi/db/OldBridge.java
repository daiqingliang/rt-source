package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.BridgeContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class OldBridge<T> extends Object {
  protected final JAXBContextImpl context;
  
  protected OldBridge(JAXBContextImpl paramJAXBContextImpl) { this.context = paramJAXBContextImpl; }
  
  @NotNull
  public BindingContext getContext() { return null; }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter) throws JAXBException { marshal(paramT, paramXMLStreamWriter, null); }
  
  public final void marshal(T paramT, XMLStreamWriter paramXMLStreamWriter, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {
    Marshaller marshaller = (Marshaller)this.context.marshallerPool.take();
    marshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(marshaller, paramT, paramXMLStreamWriter);
    marshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(marshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, XMLStreamWriter paramXMLStreamWriter) throws JAXBException { marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramXMLStreamWriter); }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, XMLStreamWriter paramXMLStreamWriter) throws JAXBException;
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException { marshal(paramT, paramOutputStream, paramNamespaceContext, null); }
  
  public void marshal(T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {
    Marshaller marshaller = (Marshaller)this.context.marshallerPool.take();
    marshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(marshaller, paramT, paramOutputStream, paramNamespaceContext);
    marshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(marshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException { marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramOutputStream, paramNamespaceContext); }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException;
  
  public final void marshal(T paramT, Node paramNode) throws JAXBException {
    Marshaller marshaller = (Marshaller)this.context.marshallerPool.take();
    marshal(marshaller, paramT, paramNode);
    this.context.marshallerPool.recycle(marshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Node paramNode) throws JAXBException { marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramNode); }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Node paramNode) throws JAXBException;
  
  public final void marshal(T paramT, ContentHandler paramContentHandler) throws JAXBException { marshal(paramT, paramContentHandler, null); }
  
  public final void marshal(T paramT, ContentHandler paramContentHandler, AttachmentMarshaller paramAttachmentMarshaller) throws JAXBException {
    Marshaller marshaller = (Marshaller)this.context.marshallerPool.take();
    marshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
    marshal(marshaller, paramT, paramContentHandler);
    marshaller.setAttachmentMarshaller(null);
    this.context.marshallerPool.recycle(marshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, ContentHandler paramContentHandler) throws JAXBException { marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramContentHandler); }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, ContentHandler paramContentHandler) throws JAXBException;
  
  public final void marshal(T paramT, Result paramResult) throws JAXBException {
    Marshaller marshaller = (Marshaller)this.context.marshallerPool.take();
    marshal(marshaller, paramT, paramResult);
    this.context.marshallerPool.recycle(marshaller);
  }
  
  public final void marshal(@NotNull BridgeContext paramBridgeContext, T paramT, Result paramResult) throws JAXBException { marshal(((BridgeContextImpl)paramBridgeContext).marshaller, paramT, paramResult); }
  
  public abstract void marshal(@NotNull Marshaller paramMarshaller, T paramT, Result paramResult) throws JAXBException;
  
  private T exit(T paramT, Unmarshaller paramUnmarshaller) {
    paramUnmarshaller.setAttachmentUnmarshaller(null);
    this.context.unmarshallerPool.recycle(paramUnmarshaller);
    return paramT;
  }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader) throws JAXBException { return (T)unmarshal(paramXMLStreamReader, null); }
  
  @NotNull
  public final T unmarshal(@NotNull XMLStreamReader paramXMLStreamReader, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException {
    Unmarshaller unmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    unmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(unmarshaller, paramXMLStreamReader), unmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull XMLStreamReader paramXMLStreamReader) throws JAXBException { return (T)unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramXMLStreamReader); }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull XMLStreamReader paramXMLStreamReader) throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource) throws JAXBException { return (T)unmarshal(paramSource, null); }
  
  @NotNull
  public final T unmarshal(@NotNull Source paramSource, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException {
    Unmarshaller unmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    unmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(unmarshaller, paramSource), unmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Source paramSource) throws JAXBException { return (T)unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramSource); }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Source paramSource) throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull InputStream paramInputStream) throws JAXBException {
    Unmarshaller unmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    return (T)exit(unmarshal(unmarshaller, paramInputStream), unmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull InputStream paramInputStream) throws JAXBException { return (T)unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramInputStream); }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull InputStream paramInputStream) throws JAXBException;
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode) throws JAXBException { return (T)unmarshal(paramNode, null); }
  
  @NotNull
  public final T unmarshal(@NotNull Node paramNode, @Nullable AttachmentUnmarshaller paramAttachmentUnmarshaller) throws JAXBException {
    Unmarshaller unmarshaller = (Unmarshaller)this.context.unmarshallerPool.take();
    unmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
    return (T)exit(unmarshal(unmarshaller, paramNode), unmarshaller);
  }
  
  @NotNull
  public final T unmarshal(@NotNull BridgeContext paramBridgeContext, @NotNull Node paramNode) throws JAXBException { return (T)unmarshal(((BridgeContextImpl)paramBridgeContext).unmarshaller, paramNode); }
  
  @NotNull
  public abstract T unmarshal(@NotNull Unmarshaller paramUnmarshaller, @NotNull Node paramNode) throws JAXBException;
  
  public abstract TypeInfo getTypeReference();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\OldBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */