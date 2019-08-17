package com.sun.xml.internal.ws.dump;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class MessageDumpingFeature extends WebServiceFeature {
  public static final String ID = "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature";
  
  private static final Level DEFAULT_MSG_LOG_LEVEL = Level.FINE;
  
  private final Queue<String> messageQueue;
  
  private final AtomicBoolean messageLoggingStatus;
  
  private final String messageLoggingRoot;
  
  private final Level messageLoggingLevel;
  
  public MessageDumpingFeature() { this(null, null, true); }
  
  public MessageDumpingFeature(String paramString, Level paramLevel, boolean paramBoolean) {
    this.messageQueue = paramBoolean ? new ConcurrentLinkedQueue() : null;
    this.messageLoggingStatus = new AtomicBoolean(true);
    this.messageLoggingRoot = (paramString != null && paramString.length() > 0) ? paramString : "com.sun.xml.internal.ws.messagedump";
    this.messageLoggingLevel = (paramLevel != null) ? paramLevel : DEFAULT_MSG_LOG_LEVEL;
    this.enabled = true;
  }
  
  public MessageDumpingFeature(boolean paramBoolean) {
    this();
    this.enabled = paramBoolean;
  }
  
  @FeatureConstructor({"enabled", "messageLoggingRoot", "messageLoggingLevel", "storeMessages"})
  public MessageDumpingFeature(boolean paramBoolean1, String paramString1, String paramString2, boolean paramBoolean2) {
    this(paramString1, Level.parse(paramString2), paramBoolean2);
    this.enabled = paramBoolean1;
  }
  
  @ManagedAttribute
  public String getID() { return "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature"; }
  
  public String nextMessage() { return (this.messageQueue != null) ? (String)this.messageQueue.poll() : null; }
  
  public void enableMessageLogging() { this.messageLoggingStatus.set(true); }
  
  public void disableMessageLogging() { this.messageLoggingStatus.set(false); }
  
  @ManagedAttribute
  public boolean getMessageLoggingStatus() { return this.messageLoggingStatus.get(); }
  
  @ManagedAttribute
  public String getMessageLoggingRoot() { return this.messageLoggingRoot; }
  
  @ManagedAttribute
  public Level getMessageLoggingLevel() { return this.messageLoggingLevel; }
  
  boolean offerMessage(String paramString) { return (this.messageQueue != null) ? this.messageQueue.offer(paramString) : 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumpingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */