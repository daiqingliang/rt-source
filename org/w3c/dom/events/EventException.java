package org.w3c.dom.events;

public class EventException extends RuntimeException {
  public short code;
  
  public static final short UNSPECIFIED_EVENT_TYPE_ERR = 0;
  
  public EventException(short paramShort, String paramString) {
    super(paramString);
    this.code = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\events\EventException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */