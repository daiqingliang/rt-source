package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.net.URL;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class OneWayFeature extends WebServiceFeature {
  public static final String ID = "http://java.sun.com/xml/ns/jaxws/addressing/oneway";
  
  private String messageId;
  
  private WSEndpointReference replyTo;
  
  private WSEndpointReference sslReplyTo;
  
  private WSEndpointReference from;
  
  private WSEndpointReference faultTo;
  
  private WSEndpointReference sslFaultTo;
  
  private String relatesToID;
  
  private boolean useAsyncWithSyncInvoke = false;
  
  public OneWayFeature() {}
  
  public OneWayFeature(boolean paramBoolean) {}
  
  public OneWayFeature(boolean paramBoolean, WSEndpointReference paramWSEndpointReference) { this.replyTo = paramWSEndpointReference; }
  
  @FeatureConstructor({"enabled", "replyTo", "from", "relatesTo"})
  public OneWayFeature(boolean paramBoolean, WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2, String paramString) {
    this.replyTo = paramWSEndpointReference1;
    this.from = paramWSEndpointReference2;
    this.relatesToID = paramString;
  }
  
  public OneWayFeature(AddressingPropertySet paramAddressingPropertySet, AddressingVersion paramAddressingVersion) {
    this.messageId = paramAddressingPropertySet.getMessageId();
    this.relatesToID = paramAddressingPropertySet.getRelatesTo();
    this.replyTo = makeEPR(paramAddressingPropertySet.getReplyTo(), paramAddressingVersion);
    this.faultTo = makeEPR(paramAddressingPropertySet.getFaultTo(), paramAddressingVersion);
  }
  
  private WSEndpointReference makeEPR(String paramString, AddressingVersion paramAddressingVersion) { return (paramString == null) ? null : new WSEndpointReference(paramString, paramAddressingVersion); }
  
  public String getMessageId() { return this.messageId; }
  
  @ManagedAttribute
  public String getID() { return "http://java.sun.com/xml/ns/jaxws/addressing/oneway"; }
  
  public boolean hasSslEprs() { return (this.sslReplyTo != null || this.sslFaultTo != null); }
  
  @ManagedAttribute
  public WSEndpointReference getReplyTo() { return this.replyTo; }
  
  public WSEndpointReference getReplyTo(boolean paramBoolean) { return (paramBoolean && this.sslReplyTo != null) ? this.sslReplyTo : this.replyTo; }
  
  public void setReplyTo(WSEndpointReference paramWSEndpointReference) { this.replyTo = paramWSEndpointReference; }
  
  public WSEndpointReference getSslReplyTo() { return this.sslReplyTo; }
  
  public void setSslReplyTo(WSEndpointReference paramWSEndpointReference) { this.sslReplyTo = paramWSEndpointReference; }
  
  @ManagedAttribute
  public WSEndpointReference getFrom() { return this.from; }
  
  public void setFrom(WSEndpointReference paramWSEndpointReference) { this.from = paramWSEndpointReference; }
  
  @ManagedAttribute
  public String getRelatesToID() { return this.relatesToID; }
  
  public void setRelatesToID(String paramString) { this.relatesToID = paramString; }
  
  public WSEndpointReference getFaultTo() { return this.faultTo; }
  
  public WSEndpointReference getFaultTo(boolean paramBoolean) { return (paramBoolean && this.sslFaultTo != null) ? this.sslFaultTo : this.faultTo; }
  
  public void setFaultTo(WSEndpointReference paramWSEndpointReference) { this.faultTo = paramWSEndpointReference; }
  
  public WSEndpointReference getSslFaultTo() { return this.sslFaultTo; }
  
  public void setSslFaultTo(WSEndpointReference paramWSEndpointReference) { this.sslFaultTo = paramWSEndpointReference; }
  
  public boolean isUseAsyncWithSyncInvoke() { return this.useAsyncWithSyncInvoke; }
  
  public void setUseAsyncWithSyncInvoke(boolean paramBoolean) { this.useAsyncWithSyncInvoke = paramBoolean; }
  
  public static WSEndpointReference enableSslForEpr(@NotNull WSEndpointReference paramWSEndpointReference, @Nullable String paramString, int paramInt) {
    if (!paramWSEndpointReference.isAnonymous()) {
      URL uRL;
      String str1 = paramWSEndpointReference.getAddress();
      try {
        uRL = new URL(str1);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
      String str2 = uRL.getProtocol();
      if (!str2.equalsIgnoreCase("https")) {
        str2 = "https";
        String str = uRL.getHost();
        if (paramString != null)
          str = paramString; 
        int i = uRL.getPort();
        if (paramInt > 0)
          i = paramInt; 
        try {
          uRL = new URL(str2, str, i, uRL.getFile());
        } catch (Exception exception) {
          throw new RuntimeException(exception);
        } 
        str1 = uRL.toExternalForm();
        return new WSEndpointReference(str1, paramWSEndpointReference.getVersion());
      } 
    } 
    return paramWSEndpointReference;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\OneWayFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */