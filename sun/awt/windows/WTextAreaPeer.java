package sun.awt.windows;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.TextArea;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextAreaPeer;

final class WTextAreaPeer extends WTextComponentPeer implements TextAreaPeer {
  public Dimension getMinimumSize() { return getMinimumSize(10, 60); }
  
  public void insert(String paramString, int paramInt) { replaceRange(paramString, paramInt, paramInt); }
  
  public native void replaceRange(String paramString, int paramInt1, int paramInt2);
  
  public Dimension getPreferredSize(int paramInt1, int paramInt2) { return getMinimumSize(paramInt1, paramInt2); }
  
  public Dimension getMinimumSize(int paramInt1, int paramInt2) {
    FontMetrics fontMetrics = getFontMetrics(((TextArea)this.target).getFont());
    return new Dimension(fontMetrics.charWidth('0') * paramInt2 + 20, fontMetrics.getHeight() * paramInt1 + 20);
  }
  
  public InputMethodRequests getInputMethodRequests() { return null; }
  
  WTextAreaPeer(TextArea paramTextArea) { super(paramTextArea); }
  
  native void create(WComponentPeer paramWComponentPeer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WTextAreaPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */