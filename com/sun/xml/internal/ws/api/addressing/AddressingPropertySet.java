package com.sun.xml.internal.ws.api.addressing;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.PropertySet.Property;

public class AddressingPropertySet extends BasePropertySet {
  public static final String ADDRESSING_FAULT_TO = "com.sun.xml.internal.ws.api.addressing.fault.to";
  
  private String faultTo;
  
  public static final String ADDRESSING_MESSAGE_ID = "com.sun.xml.internal.ws.api.addressing.message.id";
  
  private String messageId;
  
  public static final String ADDRESSING_RELATES_TO = "com.sun.xml.internal.ws.api.addressing.relates.to";
  
  @Property({"com.sun.xml.internal.ws.api.addressing.relates.to"})
  private String relatesTo;
  
  public static final String ADDRESSING_REPLY_TO = "com.sun.xml.internal.ws.api.addressing.reply.to";
  
  @Property({"com.sun.xml.internal.ws.api.addressing.reply.to"})
  private String replyTo;
  
  private static final BasePropertySet.PropertyMap model = parse(AddressingPropertySet.class);
  
  @Property({"com.sun.xml.internal.ws.api.addressing.fault.to"})
  public String getFaultTo() { return this.faultTo; }
  
  public void setFaultTo(String paramString) { this.faultTo = paramString; }
  
  public String getMessageId() { return this.messageId; }
  
  public void setMessageId(String paramString) { this.messageId = paramString; }
  
  public String getRelatesTo() { return this.relatesTo; }
  
  public void setRelatesTo(String paramString) { this.relatesTo = paramString; }
  
  public String getReplyTo() { return this.replyTo; }
  
  public void setReplyTo(String paramString) { this.replyTo = paramString; }
  
  protected BasePropertySet.PropertyMap getPropertyMap() { return model; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\AddressingPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */