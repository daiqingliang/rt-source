package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResponseContext extends AbstractMap<String, Object> {
  private final Packet packet;
  
  private Set<Map.Entry<String, Object>> entrySet;
  
  public ResponseContext(Packet paramPacket) { this.packet = paramPacket; }
  
  public boolean containsKey(Object paramObject) { return this.packet.supports(paramObject) ? this.packet.containsKey(paramObject) : (this.packet.invocationProperties.containsKey(paramObject) ? (!this.packet.getHandlerScopePropertyNames(true).contains(paramObject) ? 1 : 0) : 0); }
  
  public Object get(Object paramObject) {
    if (this.packet.supports(paramObject))
      return this.packet.get(paramObject); 
    if (this.packet.getHandlerScopePropertyNames(true).contains(paramObject))
      return null; 
    Object object = this.packet.invocationProperties.get(paramObject);
    if (paramObject.equals("javax.xml.ws.binding.attachments.inbound")) {
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
  
  public Object put(String paramString, Object paramObject) { throw new UnsupportedOperationException(); }
  
  public Object remove(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
  
  public Set<Map.Entry<String, Object>> entrySet() {
    if (this.entrySet == null) {
      HashMap hashMap = new HashMap();
      hashMap.putAll(this.packet.invocationProperties);
      hashMap.keySet().removeAll(this.packet.getHandlerScopePropertyNames(true));
      hashMap.putAll(this.packet.createMapView());
      this.entrySet = Collections.unmodifiableSet(hashMap.entrySet());
    } 
    return this.entrySet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\ResponseContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */