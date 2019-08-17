package sun.awt.windows;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.peer.TextComponentPeer;

abstract class WTextComponentPeer extends WComponentPeer implements TextComponentPeer {
  public void setEditable(boolean paramBoolean) {
    enableEditing(paramBoolean);
    setBackground(((TextComponent)this.target).getBackground());
  }
  
  public native String getText();
  
  public native void setText(String paramString);
  
  public native int getSelectionStart();
  
  public native int getSelectionEnd();
  
  public native void select(int paramInt1, int paramInt2);
  
  WTextComponentPeer(TextComponent paramTextComponent) { super(paramTextComponent); }
  
  void initialize() {
    TextComponent textComponent = (TextComponent)this.target;
    String str = textComponent.getText();
    if (str != null)
      setText(str); 
    select(textComponent.getSelectionStart(), textComponent.getSelectionEnd());
    setEditable(textComponent.isEditable());
    super.initialize();
  }
  
  native void enableEditing(boolean paramBoolean);
  
  public boolean isFocusable() { return true; }
  
  public void setCaretPosition(int paramInt) { select(paramInt, paramInt); }
  
  public int getCaretPosition() { return getSelectionStart(); }
  
  public void valueChanged() { postEvent(new TextEvent(this.target, 900)); }
  
  private static native void initIDs();
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WTextComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */