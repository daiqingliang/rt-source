package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.ContentType;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.addressing.WsaPropertyBag;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.DistributedPropertySet;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.PropertySet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class Packet extends BaseDistributedPropertySet implements MessageContext, MessageMetadata {
  private Message message;
  
  private WSDLOperationMapping wsdlOperationMapping = null;
  
  private QName wsdlOperation;
  
  public boolean wasTransportSecure;
  
  public static final String INBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers";
  
  public static final String OUTBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
  
  public static final String HA_INFO = "com.sun.xml.internal.ws.api.message.packet.hainfo";
  
  @Property({"com.sun.xml.internal.ws.handler.config"})
  public HandlerConfiguration handlerConfig;
  
  @Property({"com.sun.xml.internal.ws.client.handle"})
  public BindingProvider proxy;
  
  public boolean isAdapterDeliversNonAnonymousResponse;
  
  public boolean packetTakesPriorityOverRequestContext = false;
  
  public EndpointAddress endpointAddress;
  
  public ContentNegotiation contentNegotiation;
  
  public String acceptableMimeTypes;
  
  public WebServiceContextDelegate webServiceContextDelegate;
  
  @Nullable
  public TransportBackChannel transportBackChannel;
  
  public Component component;
  
  @Property({"com.sun.xml.internal.ws.api.server.WSEndpoint"})
  public WSEndpoint endpoint;
  
  @Property({"javax.xml.ws.soap.http.soapaction.uri"})
  public String soapAction;
  
  @Property({"com.sun.xml.internal.ws.server.OneWayOperation"})
  public Boolean expectReply;
  
  @Deprecated
  public Boolean isOneWay;
  
  public Boolean isSynchronousMEP;
  
  public Boolean nonNullAsyncHandlerGiven;
  
  private Boolean isRequestReplyMEP;
  
  private Set<String> handlerScopePropertyNames;
  
  public final Map<String, Object> invocationProperties;
  
  private static final BasePropertySet.PropertyMap model = parse(Packet.class);
  
  private static final Logger LOGGER = Logger.getLogger(Packet.class.getName());
  
  public Codec codec = null;
  
  private ContentType contentType;
  
  private Boolean mtomRequest;
  
  private Boolean mtomAcceptable;
  
  private MTOMFeature mtomFeature;
  
  Boolean checkMtomAcceptable;
  
  private Boolean fastInfosetAcceptable;
  
  private State state = State.ServerRequest;
  
  private boolean isFastInfosetDisabled;
  
  public Packet(Message paramMessage) {
    this();
    this.message = paramMessage;
    if (this.message != null)
      this.message.setMessageMedadata(this); 
  }
  
  public Packet() { this.invocationProperties = new HashMap(); }
  
  private Packet(Packet paramPacket) {
    relatePackets(paramPacket, true);
    this.invocationProperties = paramPacket.invocationProperties;
  }
  
  public Packet copy(boolean paramBoolean) {
    Packet packet = new Packet(this);
    if (paramBoolean && this.message != null)
      packet.message = this.message.copy(); 
    if (packet.message != null)
      packet.message.setMessageMedadata(packet); 
    return packet;
  }
  
  public Message getMessage() {
    if (this.message != null && !(this.message instanceof MessageWrapper))
      this.message = new MessageWrapper(this, this.message); 
    return this.message;
  }
  
  public Message getInternalMessage() { return (this.message instanceof MessageWrapper) ? ((MessageWrapper)this.message).delegate : this.message; }
  
  public WSBinding getBinding() { return (this.endpoint != null) ? this.endpoint.getBinding() : ((this.proxy != null) ? (WSBinding)this.proxy.getBinding() : null); }
  
  public void setMessage(Message paramMessage) {
    this.message = paramMessage;
    if (paramMessage != null)
      this.message.setMessageMedadata(this); 
  }
  
  @Property({"javax.xml.ws.wsdl.operation"})
  @Nullable
  public final QName getWSDLOperation() {
    if (this.wsdlOperation != null)
      return this.wsdlOperation; 
    if (this.wsdlOperationMapping == null)
      this.wsdlOperationMapping = getWSDLOperationMapping(); 
    if (this.wsdlOperationMapping != null)
      this.wsdlOperation = this.wsdlOperationMapping.getOperationName(); 
    return this.wsdlOperation;
  }
  
  public WSDLOperationMapping getWSDLOperationMapping() {
    if (this.wsdlOperationMapping != null)
      return this.wsdlOperationMapping; 
    OperationDispatcher operationDispatcher = null;
    if (this.endpoint != null) {
      operationDispatcher = this.endpoint.getOperationDispatcher();
    } else if (this.proxy != null) {
      operationDispatcher = ((Stub)this.proxy).getOperationDispatcher();
    } 
    if (operationDispatcher != null)
      try {
        this.wsdlOperationMapping = operationDispatcher.getWSDLOperationMapping(this);
      } catch (DispatchException dispatchException) {} 
    return this.wsdlOperationMapping;
  }
  
  public void setWSDLOperation(QName paramQName) { this.wsdlOperation = paramQName; }
  
  @Property({"javax.xml.ws.service.endpoint.address"})
  public String getEndPointAddressString() { return (this.endpointAddress == null) ? null : this.endpointAddress.toString(); }
  
  public void setEndPointAddressString(String paramString) {
    if (paramString == null) {
      this.endpointAddress = null;
    } else {
      this.endpointAddress = EndpointAddress.create(paramString);
    } 
  }
  
  @Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
  public String getContentNegotiationString() { return (this.contentNegotiation != null) ? this.contentNegotiation.toString() : null; }
  
  public void setContentNegotiationString(String paramString) {
    if (paramString == null) {
      this.contentNegotiation = null;
    } else {
      try {
        this.contentNegotiation = ContentNegotiation.valueOf(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {
        this.contentNegotiation = ContentNegotiation.none;
      } 
    } 
  }
  
  @Property({"javax.xml.ws.reference.parameters"})
  @NotNull
  public List<Element> getReferenceParameters() {
    Message message1 = getMessage();
    ArrayList arrayList = new ArrayList();
    if (message1 == null)
      return arrayList; 
    MessageHeaders messageHeaders = message1.getHeaders();
    for (Header header : messageHeaders.asList()) {
      String str = header.getAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter");
      if (str != null && (str.equals("true") || str.equals("1"))) {
        Document document = DOMUtil.createDom();
        SAX2DOMEx sAX2DOMEx = new SAX2DOMEx(document);
        try {
          header.writeTo(sAX2DOMEx, XmlUtil.DRACONIAN_ERROR_HANDLER);
          arrayList.add((Element)document.getLastChild());
        } catch (SAXException sAXException) {
          throw new WebServiceException(sAXException);
        } 
      } 
    } 
    return arrayList;
  }
  
  @Property({"com.sun.xml.internal.ws.api.message.HeaderList"})
  MessageHeaders getHeaderList() {
    Message message1 = getMessage();
    return (message1 == null) ? null : message1.getHeaders();
  }
  
  public TransportBackChannel keepTransportBackChannelOpen() {
    TransportBackChannel transportBackChannel1 = this.transportBackChannel;
    this.transportBackChannel = null;
    return transportBackChannel1;
  }
  
  public Boolean isRequestReplyMEP() { return this.isRequestReplyMEP; }
  
  public void setRequestReplyMEP(Boolean paramBoolean) { this.isRequestReplyMEP = paramBoolean; }
  
  public final Set<String> getHandlerScopePropertyNames(boolean paramBoolean) {
    Set set = this.handlerScopePropertyNames;
    if (set == null) {
      if (paramBoolean)
        return Collections.emptySet(); 
      set = new HashSet();
      this.handlerScopePropertyNames = set;
    } 
    return set;
  }
  
  public final Set<String> getApplicationScopePropertyNames(boolean paramBoolean) {
    assert false;
    return new HashSet();
  }
  
  @Deprecated
  public Packet createResponse(Message paramMessage) {
    Packet packet = new Packet(this);
    packet.setMessage(paramMessage);
    return packet;
  }
  
  public Packet createClientResponse(Message paramMessage) {
    Packet packet = new Packet(this);
    packet.setMessage(paramMessage);
    finishCreateRelateClientResponse(packet);
    return packet;
  }
  
  public Packet relateClientResponse(Packet paramPacket) {
    paramPacket.relatePackets(this, true);
    finishCreateRelateClientResponse(paramPacket);
    return paramPacket;
  }
  
  private void finishCreateRelateClientResponse(Packet paramPacket) {
    paramPacket.soapAction = null;
    paramPacket.setState(State.ClientResponse);
  }
  
  public Packet createServerResponse(@Nullable Message paramMessage, @Nullable WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding) {
    Packet packet = createClientResponse(paramMessage);
    return relateServerResponse(packet, paramWSDLPort, paramSEIModel, paramWSBinding);
  }
  
  public void copyPropertiesTo(@Nullable Packet paramPacket) { relatePackets(paramPacket, false); }
  
  private void relatePackets(@Nullable Packet paramPacket, boolean paramBoolean) {
    Packet packet2;
    Packet packet1;
    if (!paramBoolean) {
      packet1 = this;
      packet2 = paramPacket;
      packet2.soapAction = null;
      packet2.invocationProperties.putAll(packet1.invocationProperties);
      if (getState().equals(State.ServerRequest))
        packet2.setState(State.ServerResponse); 
    } else {
      packet1 = paramPacket;
      packet2 = this;
      packet2.soapAction = packet1.soapAction;
      packet2.setState(packet1.getState());
    } 
    packet1.copySatelliteInto(packet2);
    packet2.isAdapterDeliversNonAnonymousResponse = packet1.isAdapterDeliversNonAnonymousResponse;
    packet2.handlerConfig = packet1.handlerConfig;
    packet2.handlerScopePropertyNames = packet1.handlerScopePropertyNames;
    packet2.contentNegotiation = packet1.contentNegotiation;
    packet2.wasTransportSecure = packet1.wasTransportSecure;
    packet2.transportBackChannel = packet1.transportBackChannel;
    packet2.endpointAddress = packet1.endpointAddress;
    packet2.wsdlOperation = packet1.wsdlOperation;
    packet2.wsdlOperationMapping = packet1.wsdlOperationMapping;
    packet2.acceptableMimeTypes = packet1.acceptableMimeTypes;
    packet2.endpoint = packet1.endpoint;
    packet2.proxy = packet1.proxy;
    packet2.webServiceContextDelegate = packet1.webServiceContextDelegate;
    packet2.expectReply = packet1.expectReply;
    packet2.component = packet1.component;
    packet2.mtomAcceptable = packet1.mtomAcceptable;
    packet2.mtomRequest = packet1.mtomRequest;
  }
  
  public Packet relateServerResponse(@Nullable Packet paramPacket, @Nullable WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding) {
    relatePackets(paramPacket, false);
    paramPacket.setState(State.ServerResponse);
    AddressingVersion addressingVersion = paramWSBinding.getAddressingVersion();
    if (addressingVersion == null)
      return paramPacket; 
    if (getMessage() == null)
      return paramPacket; 
    String str = AddressingUtils.getAction(getMessage().getHeaders(), addressingVersion, paramWSBinding.getSOAPVersion());
    if (str == null)
      return paramPacket; 
    if (paramPacket.getMessage() == null || (paramWSDLPort != null && getMessage().isOneWay(paramWSDLPort)))
      return paramPacket; 
    populateAddressingHeaders(paramWSBinding, paramPacket, paramWSDLPort, paramSEIModel);
    return paramPacket;
  }
  
  public Packet createServerResponse(@Nullable Message paramMessage, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString) {
    Packet packet = createClientResponse(paramMessage);
    packet.setState(State.ServerResponse);
    if (paramAddressingVersion == null)
      return packet; 
    String str = AddressingUtils.getAction(getMessage().getHeaders(), paramAddressingVersion, paramSOAPVersion);
    if (str == null)
      return packet; 
    populateAddressingHeaders(packet, paramAddressingVersion, paramSOAPVersion, paramString, false);
    return packet;
  }
  
  public void setResponseMessage(@NotNull Packet paramPacket, @Nullable Message paramMessage, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString) {
    Packet packet = paramPacket.createServerResponse(paramMessage, paramAddressingVersion, paramSOAPVersion, paramString);
    setMessage(packet.getMessage());
  }
  
  private void populateAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, String paramString, boolean paramBoolean) {
    if (paramAddressingVersion == null)
      return; 
    if (paramPacket.getMessage() == null)
      return; 
    MessageHeaders messageHeaders = paramPacket.getMessage().getHeaders();
    WsaPropertyBag wsaPropertyBag = (WsaPropertyBag)getSatellite(WsaPropertyBag.class);
    Message message1 = getMessage();
    WSEndpointReference wSEndpointReference1 = null;
    Header header1 = AddressingUtils.getFirstHeader(message1.getHeaders(), paramAddressingVersion.replyToTag, true, paramSOAPVersion);
    Header header2 = messageHeaders.get(paramAddressingVersion.toTag, false);
    boolean bool = true;
    try {
      if (header1 != null)
        wSEndpointReference1 = header1.readAsEPR(paramAddressingVersion); 
      if (header2 != null && wSEndpointReference1 == null)
        bool = false; 
    } catch (XMLStreamException xMLStreamException) {
      throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), xMLStreamException);
    } 
    if (wSEndpointReference1 == null)
      wSEndpointReference1 = AddressingUtils.getReplyTo(message1.getHeaders(), paramAddressingVersion, paramSOAPVersion); 
    if (AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), paramAddressingVersion, paramSOAPVersion) == null)
      messageHeaders.add(new StringHeader(paramAddressingVersion.actionTag, paramString, paramSOAPVersion, paramBoolean)); 
    if (paramPacket.getMessage().getHeaders().get(paramAddressingVersion.messageIDTag, false) == null) {
      String str1 = Message.generateMessageID();
      messageHeaders.add(new StringHeader(paramAddressingVersion.messageIDTag, str1));
    } 
    String str = null;
    if (wsaPropertyBag != null)
      str = wsaPropertyBag.getMessageID(); 
    if (str == null)
      str = AddressingUtils.getMessageID(message1.getHeaders(), paramAddressingVersion, paramSOAPVersion); 
    if (str != null)
      messageHeaders.addOrReplace(new RelatesToHeader(paramAddressingVersion.relatesToTag, str)); 
    WSEndpointReference wSEndpointReference2 = null;
    if (paramPacket.getMessage().isFault()) {
      if (wsaPropertyBag != null)
        wSEndpointReference2 = wsaPropertyBag.getFaultToFromRequest(); 
      if (wSEndpointReference2 == null)
        wSEndpointReference2 = AddressingUtils.getFaultTo(message1.getHeaders(), paramAddressingVersion, paramSOAPVersion); 
      if (wSEndpointReference2 == null)
        wSEndpointReference2 = wSEndpointReference1; 
    } else {
      wSEndpointReference2 = wSEndpointReference1;
    } 
    if (bool && wSEndpointReference2 != null) {
      messageHeaders.addOrReplace(new StringHeader(paramAddressingVersion.toTag, wSEndpointReference2.getAddress()));
      wSEndpointReference2.addReferenceParametersToList(messageHeaders);
    } 
  }
  
  private void populateAddressingHeaders(WSBinding paramWSBinding, Packet paramPacket, WSDLPort paramWSDLPort, SEIModel paramSEIModel) {
    AddressingVersion addressingVersion = paramWSBinding.getAddressingVersion();
    if (addressingVersion == null)
      return; 
    WsaTubeHelper wsaTubeHelper = addressingVersion.getWsaHelper(paramWSDLPort, paramSEIModel, paramWSBinding);
    String str = paramPacket.getMessage().isFault() ? wsaTubeHelper.getFaultAction(this, paramPacket) : wsaTubeHelper.getOutputAction(this);
    if (str == null) {
      LOGGER.info("WSA headers are not added as value for wsa:Action cannot be resolved for this message");
      return;
    } 
    populateAddressingHeaders(paramPacket, addressingVersion, paramWSBinding.getSOAPVersion(), str, AddressingVersion.isRequired(paramWSBinding));
  }
  
  public String toShortString() { return super.toString(); }
  
  public String toString() {
    String str;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(super.toString());
    try {
      Message message1 = getMessage();
      if (message1 != null) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter xMLStreamWriter = XMLStreamWriterFactory.create(byteArrayOutputStream, "UTF-8");
        message1.copy().writeTo(xMLStreamWriter);
        xMLStreamWriter.flush();
        xMLStreamWriter.close();
        byteArrayOutputStream.flush();
        XMLStreamWriterFactory.recycle(xMLStreamWriter);
        byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
        str = new String(arrayOfByte, "UTF-8");
      } else {
        str = "<none>";
      } 
    } catch (Throwable throwable) {
      throw new WebServiceException(throwable);
    } 
    stringBuilder.append(" Content: ").append(str);
    return stringBuilder.toString();
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
  
  public Map<String, Object> asMapIncludingInvocationProperties() {
    final Map asMap = asMap();
    return new AbstractMap<String, Object>() {
        public Object get(Object param1Object) {
          Object object = asMap.get(param1Object);
          return (object != null) ? object : Packet.this.invocationProperties.get(param1Object);
        }
        
        public int size() { return asMap.size() + Packet.this.invocationProperties.size(); }
        
        public boolean containsKey(Object param1Object) { return asMap.containsKey(param1Object) ? true : Packet.this.invocationProperties.containsKey(param1Object); }
        
        public Set<Map.Entry<String, Object>> entrySet() {
          final Set asMapEntries = asMap.entrySet();
          final Set ipEntries = Packet.this.invocationProperties.entrySet();
          return new AbstractSet<Map.Entry<String, Object>>() {
              public Iterator<Map.Entry<String, Object>> iterator() {
                final Iterator asMapIt = asMapEntries.iterator();
                final Iterator ipIt = ipEntries.iterator();
                return new Iterator<Map.Entry<String, Object>>() {
                    public boolean hasNext() { return (asMapIt.hasNext() || ipIt.hasNext()); }
                    
                    public Map.Entry<String, Object> next() { return asMapIt.hasNext() ? (Map.Entry)asMapIt.next() : (Map.Entry)ipIt.next(); }
                    
                    public void remove() { throw new UnsupportedOperationException(); }
                  };
              }
              
              public int size() { return asMap.size() + Packet.this.invocationProperties.size(); }
            };
        }
        
        public Object put(String param1String, Object param1Object) { return Packet.this.supports(param1String) ? asMap.put(param1String, param1Object) : Packet.this.invocationProperties.put(param1String, param1Object); }
        
        public void clear() {
          asMap.clear();
          Packet.this.invocationProperties.clear();
        }
        
        public Object remove(Object param1Object) { return Packet.this.supports(param1Object) ? asMap.remove(param1Object) : Packet.this.invocationProperties.remove(param1Object); }
      };
  }
  
  public SOAPMessage getSOAPMessage() throws SOAPException { return getAsSOAPMessage(); }
  
  public SOAPMessage getAsSOAPMessage() throws SOAPException {
    Message message1 = getMessage();
    if (message1 == null)
      return null; 
    if (message1 instanceof MessageWritable)
      ((MessageWritable)message1).setMTOMConfiguration(this.mtomFeature); 
    return message1.readAsSOAPMessage(this, getState().isInbound());
  }
  
  public Codec getCodec() {
    if (this.codec != null)
      return this.codec; 
    if (this.endpoint != null)
      this.codec = this.endpoint.createCodec(); 
    WSBinding wSBinding = getBinding();
    if (wSBinding != null)
      this.codec = wSBinding.getBindingId().createEncoder(wSBinding); 
    return this.codec;
  }
  
  public ContentType writeTo(OutputStream paramOutputStream) throws IOException {
    Message message1 = getInternalMessage();
    if (message1 instanceof MessageWritable) {
      ((MessageWritable)message1).setMTOMConfiguration(this.mtomFeature);
      return ((MessageWritable)message1).writeTo(paramOutputStream);
    } 
    return getCodec().encode(this, paramOutputStream);
  }
  
  public ContentType writeTo(WritableByteChannel paramWritableByteChannel) { return getCodec().encode(this, paramWritableByteChannel); }
  
  public Boolean getMtomRequest() { return this.mtomRequest; }
  
  public void setMtomRequest(Boolean paramBoolean) { this.mtomRequest = paramBoolean; }
  
  public Boolean getMtomAcceptable() { return this.mtomAcceptable; }
  
  public void checkMtomAcceptable() {
    if (this.checkMtomAcceptable == null)
      if (this.acceptableMimeTypes == null || this.isFastInfosetDisabled) {
        this.checkMtomAcceptable = Boolean.valueOf(false);
      } else {
        this.checkMtomAcceptable = Boolean.valueOf((this.acceptableMimeTypes.indexOf("application/xop+xml") != -1));
      }  
    this.mtomAcceptable = this.checkMtomAcceptable;
  }
  
  public Boolean getFastInfosetAcceptable(String paramString) {
    if (this.fastInfosetAcceptable == null)
      if (this.acceptableMimeTypes == null || this.isFastInfosetDisabled) {
        this.fastInfosetAcceptable = Boolean.valueOf(false);
      } else {
        this.fastInfosetAcceptable = Boolean.valueOf((this.acceptableMimeTypes.indexOf(paramString) != -1));
      }  
    return this.fastInfosetAcceptable;
  }
  
  public void setMtomFeature(MTOMFeature paramMTOMFeature) { this.mtomFeature = paramMTOMFeature; }
  
  public MTOMFeature getMtomFeature() {
    WSBinding wSBinding = getBinding();
    return (wSBinding != null) ? (MTOMFeature)wSBinding.getFeature(MTOMFeature.class) : this.mtomFeature;
  }
  
  public ContentType getContentType() {
    if (this.contentType == null)
      this.contentType = getInternalContentType(); 
    if (this.contentType == null)
      this.contentType = getCodec().getStaticContentType(this); 
    if (this.contentType == null);
    return this.contentType;
  }
  
  public ContentType getInternalContentType() {
    Message message1 = getInternalMessage();
    return (message1 instanceof MessageWritable) ? ((MessageWritable)message1).getContentType() : this.contentType;
  }
  
  public void setContentType(ContentType paramContentType) { this.contentType = paramContentType; }
  
  public State getState() { return this.state; }
  
  public void setState(State paramState) { this.state = paramState; }
  
  public boolean shouldUseMtom() { return getState().isInbound() ? isMtomContentType() : shouldUseMtomOutbound(); }
  
  private boolean shouldUseMtomOutbound() {
    MTOMFeature mTOMFeature = getMtomFeature();
    if (mTOMFeature != null && mTOMFeature.isEnabled()) {
      if (getMtomAcceptable() == null && getMtomRequest() == null)
        return true; 
      if (getMtomAcceptable() != null && getMtomAcceptable().booleanValue() && getState().equals(State.ServerResponse))
        return true; 
      if (getMtomRequest() != null && getMtomRequest().booleanValue() && getState().equals(State.ServerResponse))
        return true; 
      if (getMtomRequest() != null && getMtomRequest().booleanValue() && getState().equals(State.ClientRequest))
        return true; 
    } 
    return false;
  }
  
  private boolean isMtomContentType() { return (getInternalContentType() != null && getInternalContentType().getContentType().contains("application/xop+xml")); }
  
  public void addSatellite(@NotNull PropertySet paramPropertySet) { addSatellite(paramPropertySet); }
  
  public void addSatellite(@NotNull Class paramClass, @NotNull PropertySet paramPropertySet) { addSatellite(paramClass, paramPropertySet); }
  
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet) { copySatelliteInto(paramDistributedPropertySet); }
  
  public void removeSatellite(PropertySet paramPropertySet) { removeSatellite(paramPropertySet); }
  
  public void setFastInfosetDisabled(boolean paramBoolean) { this.isFastInfosetDisabled = paramBoolean; }
  
  public enum State {
    ServerRequest(true),
    ClientRequest(false),
    ServerResponse(false),
    ClientResponse(true);
    
    private boolean inbound;
    
    State(boolean param1Boolean1) { this.inbound = param1Boolean1; }
    
    public boolean isInbound() { return this.inbound; }
  }
  
  public enum Status {
    Request, Response, Unknown;
    
    public boolean isRequest() { return Request.equals(this); }
    
    public boolean isResponse() { return Response.equals(this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\Packet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */