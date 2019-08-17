package com.sun.xml.internal.ws.client;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.PropertySet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class RequestContext extends BaseDistributedPropertySet {
  private static final Logger LOGGER = Logger.getLogger(RequestContext.class.getName());
  
  private static ContentNegotiation defaultContentNegotiation = ContentNegotiation.obtainFromSystemProperty();
  
  @NotNull
  private EndpointAddress endpointAddress;
  
  public ContentNegotiation contentNegotiation = defaultContentNegotiation;
  
  private String soapAction;
  
  private Boolean soapActionUse;
  
  private static final BasePropertySet.PropertyMap propMap = parse(RequestContext.class);
  
  public void addSatellite(@NotNull PropertySet paramPropertySet) { addSatellite(paramPropertySet); }
  
  @Property({"javax.xml.ws.service.endpoint.address"})
  public String getEndPointAddressString() { return (this.endpointAddress != null) ? this.endpointAddress.toString() : null; }
  
  public void setEndPointAddressString(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    this.endpointAddress = EndpointAddress.create(paramString);
  }
  
  public void setEndpointAddress(@NotNull EndpointAddress paramEndpointAddress) { this.endpointAddress = paramEndpointAddress; }
  
  @NotNull
  public EndpointAddress getEndpointAddress() { return this.endpointAddress; }
  
  @Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
  public String getContentNegotiationString() { return this.contentNegotiation.toString(); }
  
  public void setContentNegotiationString(String paramString) {
    if (paramString == null) {
      this.contentNegotiation = ContentNegotiation.none;
    } else {
      try {
        this.contentNegotiation = ContentNegotiation.valueOf(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {
        this.contentNegotiation = ContentNegotiation.none;
      } 
    } 
  }
  
  @Property({"javax.xml.ws.soap.http.soapaction.uri"})
  public String getSoapAction() { return this.soapAction; }
  
  public void setSoapAction(String paramString) { this.soapAction = paramString; }
  
  @Property({"javax.xml.ws.soap.http.soapaction.use"})
  public Boolean getSoapActionUse() { return this.soapActionUse; }
  
  public void setSoapActionUse(Boolean paramBoolean) { this.soapActionUse = paramBoolean; }
  
  RequestContext() {}
  
  private RequestContext(RequestContext paramRequestContext) {
    for (Map.Entry entry : paramRequestContext.asMapLocal().entrySet()) {
      if (!propMap.containsKey(entry.getKey()))
        asMap().put(entry.getKey(), entry.getValue()); 
    } 
    this.endpointAddress = paramRequestContext.endpointAddress;
    this.soapAction = paramRequestContext.soapAction;
    this.soapActionUse = paramRequestContext.soapActionUse;
    this.contentNegotiation = paramRequestContext.contentNegotiation;
    paramRequestContext.copySatelliteInto(this);
  }
  
  public Object get(Object paramObject) { return supports(paramObject) ? super.get(paramObject) : asMap().get(paramObject); }
  
  public Object put(String paramString, Object paramObject) { return supports(paramString) ? super.put(paramString, paramObject) : asMap().put(paramString, paramObject); }
  
  public void fill(Packet paramPacket, boolean paramBoolean) {
    if (this.endpointAddress != null)
      paramPacket.endpointAddress = this.endpointAddress; 
    paramPacket.contentNegotiation = this.contentNegotiation;
    fillSOAPAction(paramPacket, paramBoolean);
    mergeRequestHeaders(paramPacket);
    HashSet hashSet = new HashSet();
    copySatelliteInto(paramPacket);
    for (String str : asMapLocal().keySet()) {
      if (!supportsLocal(str))
        hashSet.add(str); 
      if (!propMap.containsKey(str)) {
        Object object = asMapLocal().get(str);
        if (paramPacket.supports(str)) {
          paramPacket.put(str, object);
          continue;
        } 
        paramPacket.invocationProperties.put(str, object);
      } 
    } 
    if (!hashSet.isEmpty())
      paramPacket.getHandlerScopePropertyNames(false).addAll(hashSet); 
  }
  
  private void mergeRequestHeaders(Packet paramPacket) {
    Headers headers = (Headers)paramPacket.invocationProperties.get("javax.xml.ws.http.request.headers");
    Map map = (Map)asMap().get("javax.xml.ws.http.request.headers");
    if (headers != null && map != null) {
      for (Map.Entry entry : map.entrySet()) {
        String str = (String)entry.getKey();
        if (str != null && str.trim().length() != 0) {
          List list = (List)headers.get(str);
          if (list != null) {
            list.addAll((Collection)entry.getValue());
            continue;
          } 
          headers.put(str, map.get(str));
        } 
      } 
      asMap().put("javax.xml.ws.http.request.headers", headers);
    } 
  }
  
  private void fillSOAPAction(Packet paramPacket, boolean paramBoolean) {
    boolean bool = paramPacket.packetTakesPriorityOverRequestContext;
    String str = bool ? paramPacket.soapAction : this.soapAction;
    Boolean bool1 = bool ? (Boolean)paramPacket.invocationProperties.get("javax.xml.ws.soap.http.soapaction.use") : this.soapActionUse;
    if (((bool1 != null && bool1.booleanValue()) || (bool1 == null && paramBoolean)) && str != null)
      paramPacket.soapAction = str; 
    if (!paramBoolean && (bool1 == null || !bool1.booleanValue()) && str != null)
      LOGGER.warning("BindingProvider.SOAPACTION_URI_PROPERTY is set in the RequestContext but is ineffective, Either set BindingProvider.SOAPACTION_USE_PROPERTY to true or enable AddressingFeature"); 
  }
  
  public RequestContext copy() { return new RequestContext(this); }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return propMap; }
  
  protected boolean mapAllowsAdditionalProperties() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\RequestContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */