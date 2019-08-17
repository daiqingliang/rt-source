package java.util;

import java.io.Serializable;

public class EventObject implements Serializable {
  private static final long serialVersionUID = 5516075349620653480L;
  
  protected Object source;
  
  public EventObject(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("null source"); 
    this.source = paramObject;
  }
  
  public Object getSource() { return this.source; }
  
  public String toString() { return getClass().getName() + "[source=" + this.source + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\EventObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */