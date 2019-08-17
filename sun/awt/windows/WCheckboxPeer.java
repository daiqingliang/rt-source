package sun.awt.windows;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxPeer;

final class WCheckboxPeer extends WComponentPeer implements CheckboxPeer {
  public native void setState(boolean paramBoolean);
  
  public native void setCheckboxGroup(CheckboxGroup paramCheckboxGroup);
  
  public native void setLabel(String paramString);
  
  private static native int getCheckMarkSize();
  
  public Dimension getMinimumSize() {
    String str = ((Checkbox)this.target).getLabel();
    int i = getCheckMarkSize();
    if (str == null)
      str = ""; 
    FontMetrics fontMetrics = getFontMetrics(((Checkbox)this.target).getFont());
    return new Dimension(fontMetrics.stringWidth(str) + i / 2 + i, Math.max(fontMetrics.getHeight() + 8, i));
  }
  
  public boolean isFocusable() { return true; }
  
  WCheckboxPeer(Checkbox paramCheckbox) { super(paramCheckbox); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    Checkbox checkbox = (Checkbox)this.target;
    setState(checkbox.getState());
    setCheckboxGroup(checkbox.getCheckboxGroup());
    Color color = ((Component)this.target).getBackground();
    if (color != null)
      setBackground(color); 
    super.initialize();
  }
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  void handleAction(final boolean state) {
    final Checkbox cb = (Checkbox)this.target;
    WToolkit.executeOnEventHandlerThread(checkbox, new Runnable() {
          public void run() {
            CheckboxGroup checkboxGroup = cb.getCheckboxGroup();
            if (checkboxGroup != null && cb == checkboxGroup.getSelectedCheckbox() && cb.getState())
              return; 
            cb.setState(state);
            WCheckboxPeer.this.postEvent(new ItemEvent(cb, 701, cb.getLabel(), state ? 1 : 2));
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WCheckboxPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */