package sun.awt.windows;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.ButtonPeer;

final class WButtonPeer extends WComponentPeer implements ButtonPeer {
  public Dimension getMinimumSize() {
    FontMetrics fontMetrics = getFontMetrics(((Button)this.target).getFont());
    String str = ((Button)this.target).getLabel();
    if (str == null)
      str = ""; 
    return new Dimension(fontMetrics.stringWidth(str) + 14, fontMetrics.getHeight() + 8);
  }
  
  public boolean isFocusable() { return true; }
  
  public native void setLabel(String paramString);
  
  WButtonPeer(Button paramButton) { super(paramButton); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  public void handleAction(final long when, final int modifiers) { WToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
          public void run() { WButtonPeer.this.postEvent(new ActionEvent(WButtonPeer.this.target, 1001, ((Button)WButtonPeer.this.target).getActionCommand(), when, modifiers)); }
        }paramLong); }
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  private static native void initIDs();
  
  public boolean handleJavaKeyEvent(KeyEvent paramKeyEvent) {
    switch (paramKeyEvent.getID()) {
      case 402:
        if (paramKeyEvent.getKeyCode() == 32)
          handleAction(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()); 
        break;
    } 
    return false;
  }
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WButtonPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */