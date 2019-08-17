package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

class MessageContextImpl implements MessageContext {
  private final Set<String> handlerScopeProps;
  
  private final Packet packet;
  
  private final Map<String, Object> asMapIncludingInvocationProperties;
  
  public MessageContextImpl(Packet paramPacket) {
    this.packet = paramPacket;
    this.asMapIncludingInvocationProperties = paramPacket.asMapIncludingInvocationProperties();
    this.handlerScopeProps = paramPacket.getHandlerScopePropertyNames(false);
  }
  
  protected void updatePacket() { throw new UnsupportedOperationException("wrong call"); }
  
  public void setScope(String paramString, MessageContext.Scope paramScope) {
    if (!containsKey(paramString))
      throw new IllegalArgumentException("Property " + paramString + " does not exist."); 
    if (paramScope == MessageContext.Scope.APPLICATION) {
      this.handlerScopeProps.remove(paramString);
    } else {
      this.handlerScopeProps.add(paramString);
    } 
  }
  
  public MessageContext.Scope getScope(String paramString) {
    if (!containsKey(paramString))
      throw new IllegalArgumentException("Property " + paramString + " does not exist."); 
    return this.handlerScopeProps.contains(paramString) ? MessageContext.Scope.HANDLER : MessageContext.Scope.APPLICATION;
  }
  
  public int size() { return this.asMapIncludingInvocationProperties.size(); }
  
  public boolean isEmpty() { return this.asMapIncludingInvocationProperties.isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return this.asMapIncludingInvocationProperties.containsKey(paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.asMapIncludingInvocationProperties.containsValue(paramObject); }
  
  public Object put(String paramString, Object paramObject) {
    if (!this.asMapIncludingInvocationProperties.containsKey(paramString))
      this.handlerScopeProps.add(paramString); 
    return this.asMapIncludingInvocationProperties.put(paramString, paramObject);
  }
  
  public Object get(Object paramObject) {
    if (paramObject == null)
      return null; 
    Object object = this.asMapIncludingInvocationProperties.get(paramObject);
    if (paramObject.equals("javax.xml.ws.binding.attachments.outbound") || paramObject.equals("javax.xml.ws.binding.attachments.inbound")) {
      Map map = (Map)object;
      if (map == null)
        map = new HashMap(); 
      AttachmentSet attachmentSet = this.packet.getMessage().getAttachments();
      for (Attachment attachment : attachmentSet) {
        String str = attachment.getContentId();
        if (str.indexOf("@jaxws.sun.com") == -1) {
          Object object1 = map.get(str);
          if (object1 == null) {
            object1 = map.get("<" + str + ">");
            if (object1 == null)
              map.put(attachment.getContentId(), attachment.asDataHandler()); 
          } 
          continue;
        } 
        map.put(attachment.getContentId(), attachment.asDataHandler());
      } 
      return map;
    } 
    return object;
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap) {
    for (String str : paramMap.keySet()) {
      if (!this.asMapIncludingInvocationProperties.containsKey(str))
        this.handlerScopeProps.add(str); 
    } 
    this.asMapIncludingInvocationProperties.putAll(paramMap);
  }
  
  public void clear() { this.asMapIncludingInvocationProperties.clear(); }
  
  public Object remove(Object paramObject) {
    this.handlerScopeProps.remove(paramObject);
    return this.asMapIncludingInvocationProperties.remove(paramObject);
  }
  
  public Set<String> keySet() { return this.asMapIncludingInvocationProperties.keySet(); }
  
  public Set<Map.Entry<String, Object>> entrySet() { return this.asMapIncludingInvocationProperties.entrySet(); }
  
  public Collection<Object> values() { return this.asMapIncludingInvocationProperties.values(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\MessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */