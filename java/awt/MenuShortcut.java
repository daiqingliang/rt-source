package java.awt;

import java.awt.event.KeyEvent;
import java.io.Serializable;

public class MenuShortcut implements Serializable {
  int key;
  
  boolean usesShift;
  
  private static final long serialVersionUID = 143448358473180225L;
  
  public MenuShortcut(int paramInt) { this(paramInt, false); }
  
  public MenuShortcut(int paramInt, boolean paramBoolean) {
    this.key = paramInt;
    this.usesShift = paramBoolean;
  }
  
  public int getKey() { return this.key; }
  
  public boolean usesShiftModifier() { return this.usesShift; }
  
  public boolean equals(MenuShortcut paramMenuShortcut) { return (paramMenuShortcut != null && paramMenuShortcut.getKey() == this.key && paramMenuShortcut.usesShiftModifier() == this.usesShift); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof MenuShortcut) ? equals((MenuShortcut)paramObject) : 0; }
  
  public int hashCode() { return this.usesShift ? (this.key ^ 0xFFFFFFFF) : this.key; }
  
  public String toString() {
    int i = 0;
    if (!GraphicsEnvironment.isHeadless())
      i = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(); 
    if (usesShiftModifier())
      i |= 0x1; 
    return KeyEvent.getKeyModifiersText(i) + "+" + KeyEvent.getKeyText(this.key);
  }
  
  protected String paramString() {
    String str = "key=" + this.key;
    if (usesShiftModifier())
      str = str + ",usesShiftModifier"; 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MenuShortcut.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */