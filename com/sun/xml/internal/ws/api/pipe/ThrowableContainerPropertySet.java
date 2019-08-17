package com.sun.xml.internal.ws.api.pipe;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;

public class ThrowableContainerPropertySet extends BasePropertySet {
  public static final String FIBER_COMPLETION_THROWABLE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable";
  
  private Throwable throwable;
  
  public static final String FAULT_MESSAGE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message";
  
  private Message faultMessage;
  
  public static final String RESPONSE_PACKET = "com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet";
  
  private Packet responsePacket;
  
  public static final String IS_FAULT_CREATED = "com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created";
  
  private boolean isFaultCreated = false;
  
  private static final BasePropertySet.PropertyMap model = parse(ThrowableContainerPropertySet.class);
  
  public ThrowableContainerPropertySet(Throwable paramThrowable) { this.throwable = paramThrowable; }
  
  @Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable"})
  public Throwable getThrowable() { return this.throwable; }
  
  public void setThrowable(Throwable paramThrowable) { this.throwable = paramThrowable; }
  
  @Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message"})
  public Message getFaultMessage() { return this.faultMessage; }
  
  public void setFaultMessage(Message paramMessage) { this.faultMessage = paramMessage; }
  
  @Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet"})
  public Packet getResponsePacket() { return this.responsePacket; }
  
  public void setResponsePacket(Packet paramPacket) { this.responsePacket = paramPacket; }
  
  @Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created"})
  public boolean isFaultCreated() { return this.isFaultCreated; }
  
  public void setFaultCreated(boolean paramBoolean) { this.isFaultCreated = paramBoolean; }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\ThrowableContainerPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */