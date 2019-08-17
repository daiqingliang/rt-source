package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.peer.LabelPeer;

final class WLabelPeer extends WComponentPeer implements LabelPeer {
  public Dimension getMinimumSize() {
    FontMetrics fontMetrics = getFontMetrics(((Label)this.target).getFont());
    String str = ((Label)this.target).getText();
    if (str == null)
      str = ""; 
    return new Dimension(fontMetrics.stringWidth(str) + 14, fontMetrics.getHeight() + 8);
  }
  
  native void lazyPaint();
  
  void start() {
    super.start();
    lazyPaint();
  }
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  public native void setText(String paramString);
  
  public native void setAlignment(int paramInt);
  
  WLabelPeer(Label paramLabel) { super(paramLabel); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    Label label = (Label)this.target;
    String str = label.getText();
    if (str != null)
      setText(str); 
    int i = label.getAlignment();
    if (i != 0)
      setAlignment(i); 
    Color color = ((Component)this.target).getBackground();
    if (color != null)
      setBackground(color); 
    super.initialize();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WLabelPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */