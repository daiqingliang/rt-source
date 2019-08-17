package java.awt.event;

import java.awt.AWTEvent;

public class ActionEvent extends AWTEvent {
  public static final int SHIFT_MASK = 1;
  
  public static final int CTRL_MASK = 2;
  
  public static final int META_MASK = 4;
  
  public static final int ALT_MASK = 8;
  
  public static final int ACTION_FIRST = 1001;
  
  public static final int ACTION_LAST = 1001;
  
  public static final int ACTION_PERFORMED = 1001;
  
  String actionCommand;
  
  long when;
  
  int modifiers;
  
  private static final long serialVersionUID = -7671078796273832149L;
  
  public ActionEvent(Object paramObject, int paramInt, String paramString) { this(paramObject, paramInt, paramString, 0); }
  
  public ActionEvent(Object paramObject, int paramInt1, String paramString, int paramInt2) { this(paramObject, paramInt1, paramString, 0L, paramInt2); }
  
  public ActionEvent(Object paramObject, int paramInt1, String paramString, long paramLong, int paramInt2) {
    super(paramObject, paramInt1);
    this.actionCommand = paramString;
    this.when = paramLong;
    this.modifiers = paramInt2;
  }
  
  public String getActionCommand() { return this.actionCommand; }
  
  public long getWhen() { return this.when; }
  
  public int getModifiers() { return this.modifiers; }
  
  public String paramString() {
    switch (this.id) {
      case 1001:
        str = "ACTION_PERFORMED";
        return str + ",cmd=" + this.actionCommand + ",when=" + this.when + ",modifiers=" + KeyEvent.getKeyModifiersText(this.modifiers);
    } 
    String str = "unknown type";
    return str + ",cmd=" + this.actionCommand + ",when=" + this.when + ",modifiers=" + KeyEvent.getKeyModifiersText(this.modifiers);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\ActionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */