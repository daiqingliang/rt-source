package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public final class EndpointMessageContextImpl extends AbstractMap<String, Object> implements MessageContext {
  private Set<Map.Entry<String, Object>> entrySet;
  
  private final Packet packet;
  
  public EndpointMessageContextImpl(Packet paramPacket) { this.packet = paramPacket; }
  
  public Object get(Object paramObject) {
    if (this.packet.supports(paramObject))
      return this.packet.get(paramObject); 
    if (this.packet.getHandlerScopePropertyNames(true).contains(paramObject))
      return null; 
    Object object = this.packet.invocationProperties.get(paramObject);
    if (paramObject.equals("javax.xml.ws.binding.attachments.outbound") || paramObject.equals("javax.xml.ws.binding.attachments.inbound")) {
      Map map = (Map)object;
      if (map == null)
        map = new HashMap(); 
      AttachmentSet attachmentSet = this.packet.getMessage().getAttachments();
      for (Attachment attachment : attachmentSet)
        map.put(attachment.getContentId(), attachment.asDataHandler()); 
      return map;
    } 
    return object;
  }
  
  public Object put(String paramString, Object paramObject) {
    if (this.packet.supports(paramString))
      return this.packet.put(paramString, paramObject); 
    Object object = this.packet.invocationProperties.get(paramString);
    if (object != null) {
      if (this.packet.getHandlerScopePropertyNames(true).contains(paramString))
        throw new IllegalArgumentException("Cannot overwrite property in HANDLER scope"); 
      this.packet.invocationProperties.put(paramString, paramObject);
      return object;
    } 
    this.packet.invocationProperties.put(paramString, paramObject);
    return null;
  }
  
  public Object remove(Object paramObject) {
    if (this.packet.supports(paramObject))
      return this.packet.remove(paramObject); 
    Object object = this.packet.invocationProperties.get(paramObject);
    if (object != null) {
      if (this.packet.getHandlerScopePropertyNames(true).contains(paramObject))
        throw new IllegalArgumentException("Cannot remove property in HANDLER scope"); 
      this.packet.invocationProperties.remove(paramObject);
      return object;
    } 
    return null;
  }
  
  public Set<Map.Entry<String, Object>> entrySet() {
    if (this.entrySet == null)
      this.entrySet = new EntrySet(null); 
    return this.entrySet;
  }
  
  public void setScope(String paramString, MessageContext.Scope paramScope) { throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do setScope()."); }
  
  public MessageContext.Scope getScope(String paramString) { throw new UnsupportedOperationException("All the properties in this context are in APPLICATION scope. Cannot do getScope()."); }
  
  private Map<String, Object> createBackupMap() {
    HashMap hashMap = new HashMap();
    hashMap.putAll(this.packet.createMapView());
    Set set = this.packet.getHandlerScopePropertyNames(true);
    for (Map.Entry entry : this.packet.invocationProperties.entrySet()) {
      if (!set.contains(entry.getKey()))
        hashMap.put(entry.getKey(), entry.getValue()); 
    } 
    return hashMap;
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<String, Object>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<String, Object>> iterator() {
      final Iterator it = EndpointMessageContextImpl.this.createBackupMap().entrySet().iterator();
      return new Iterator<Map.Entry<String, Object>>() {
          Map.Entry<String, Object> cur;
          
          public boolean hasNext() { return it.hasNext(); }
          
          public Map.Entry<String, Object> next() {
            this.cur = (Map.Entry)it.next();
            return this.cur;
          }
          
          public void remove() {
            it.remove();
            EndpointMessageContextImpl.EntrySet.this.this$0.remove(this.cur.getKey());
          }
        };
    }
    
    public int size() { return EndpointMessageContextImpl.this.createBackupMap().size(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\EndpointMessageContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */