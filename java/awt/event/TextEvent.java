package java.awt.event;

import java.awt.AWTEvent;

public class TextEvent extends AWTEvent {
  public static final int TEXT_FIRST = 900;
  
  public static final int TEXT_LAST = 900;
  
  public static final int TEXT_VALUE_CHANGED = 900;
  
  private static final long serialVersionUID = 6269902291250941179L;
  
  public TextEvent(Object paramObject, int paramInt) { super(paramObject, paramInt); }
  
  public String paramString() {
    switch (this.id) {
      case 900:
        return "TEXT_VALUE_CHANGED";
    } 
    return "unknown type";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\TextEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */