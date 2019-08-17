package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.ws.handler.MessageContext;

public abstract class MessageUpdatableContext implements MessageContext {
  final Packet packet;
  
  private MessageContextImpl ctxt;
  
  public MessageUpdatableContext(Packet paramPacket) {
    this.ctxt = new MessageContextImpl(paramPacket);
    this.packet = paramPacket;
  }
  
  abstract void updateMessage();
  
  Message getPacketMessage() {
    updateMessage();
    return this.packet.getMessage();
  }
  
  abstract void setPacketMessage(Message paramMessage);
  
  public final void updatePacket() { updateMessage(); }
  
  MessageContextImpl getMessageContext() { return this.ctxt; }
  
  public void setScope(String paramString, MessageContext.Scope paramScope) { this.ctxt.setScope(paramString, paramScope); }
  
  public MessageContext.Scope getScope(String paramString) { return this.ctxt.getScope(paramString); }
  
  public void clear() { this.ctxt.clear(); }
  
  public boolean containsKey(Object paramObject) { return this.ctxt.containsKey(paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.ctxt.containsValue(paramObject); }
  
  public Set<Map.Entry<String, Object>> entrySet() { return this.ctxt.entrySet(); }
  
  public Object get(Object paramObject) { return this.ctxt.get(paramObject); }
  
  public boolean isEmpty() { return this.ctxt.isEmpty(); }
  
  public Set<String> keySet() { return this.ctxt.keySet(); }
  
  public Object put(String paramString, Object paramObject) { return this.ctxt.put(paramString, paramObject); }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap) { this.ctxt.putAll(paramMap); }
  
  public Object remove(Object paramObject) { return this.ctxt.remove(paramObject); }
  
  public int size() { return this.ctxt.size(); }
  
  public Collection<Object> values() { return this.ctxt.values(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\handler\MessageUpdatableContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */