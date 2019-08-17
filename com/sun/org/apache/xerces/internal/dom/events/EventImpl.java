package com.sun.org.apache.xerces.internal.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event {
  public String type = null;
  
  public EventTarget target;
  
  public EventTarget currentTarget;
  
  public short eventPhase;
  
  public boolean initialized = false;
  
  public boolean bubbles = true;
  
  public boolean cancelable = false;
  
  public boolean stopPropagation = false;
  
  public boolean preventDefault = false;
  
  protected long timeStamp = System.currentTimeMillis();
  
  public void initEvent(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    this.type = paramString;
    this.bubbles = paramBoolean1;
    this.cancelable = paramBoolean2;
    this.initialized = true;
  }
  
  public boolean getBubbles() { return this.bubbles; }
  
  public boolean getCancelable() { return this.cancelable; }
  
  public EventTarget getCurrentTarget() { return this.currentTarget; }
  
  public short getEventPhase() { return this.eventPhase; }
  
  public EventTarget getTarget() { return this.target; }
  
  public String getType() { return this.type; }
  
  public long getTimeStamp() { return this.timeStamp; }
  
  public void stopPropagation() { this.stopPropagation = true; }
  
  public void preventDefault() { this.preventDefault = true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\events\EventImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */