package sun.awt.windows;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.ChoicePeer;
import sun.awt.SunToolkit;

final class WChoicePeer extends WComponentPeer implements ChoicePeer {
  private WindowListener windowListener;
  
  public Dimension getMinimumSize() {
    FontMetrics fontMetrics = getFontMetrics(((Choice)this.target).getFont());
    Choice choice = (Choice)this.target;
    int i = 0;
    int j = choice.getItemCount();
    while (j-- > 0)
      i = Math.max(fontMetrics.stringWidth(choice.getItem(j)), i); 
    return new Dimension(28 + i, Math.max(fontMetrics.getHeight() + 6, 15));
  }
  
  public boolean isFocusable() { return true; }
  
  public native void select(int paramInt);
  
  public void add(String paramString, int paramInt) { addItem(paramString, paramInt); }
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  public native void removeAll();
  
  public native void remove(int paramInt);
  
  public void addItem(String paramString, int paramInt) { addItems(new String[] { paramString }, paramInt); }
  
  public native void addItems(String[] paramArrayOfString, int paramInt);
  
  public native void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  WChoicePeer(Choice paramChoice) { super(paramChoice); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    Choice choice = (Choice)this.target;
    int i = choice.getItemCount();
    if (i > 0) {
      String[] arrayOfString = new String[i];
      for (byte b = 0; b < i; b++)
        arrayOfString[b] = choice.getItem(b); 
      addItems(arrayOfString, 0);
      if (choice.getSelectedIndex() >= 0)
        select(choice.getSelectedIndex()); 
    } 
    Window window = SunToolkit.getContainingWindow((Component)this.target);
    if (window != null) {
      WWindowPeer wWindowPeer = (WWindowPeer)window.getPeer();
      if (wWindowPeer != null) {
        this.windowListener = new WindowAdapter() {
            public void windowIconified(WindowEvent param1WindowEvent) { WChoicePeer.this.closeList(); }
            
            public void windowClosing(WindowEvent param1WindowEvent) { WChoicePeer.this.closeList(); }
          };
        wWindowPeer.addWindowListener(this.windowListener);
      } 
    } 
    super.initialize();
  }
  
  protected void disposeImpl() {
    Window window = SunToolkit.getContainingWindow((Component)this.target);
    if (window != null) {
      WWindowPeer wWindowPeer = (WWindowPeer)window.getPeer();
      if (wWindowPeer != null)
        wWindowPeer.removeWindowListener(this.windowListener); 
    } 
    super.disposeImpl();
  }
  
  void handleAction(final int index) {
    final Choice c = (Choice)this.target;
    WToolkit.executeOnEventHandlerThread(choice, new Runnable() {
          public void run() {
            c.select(index);
            WChoicePeer.this.postEvent(new ItemEvent(c, 701, c.getItem(index), 1));
          }
        });
  }
  
  int getDropDownHeight() {
    Choice choice = (Choice)this.target;
    FontMetrics fontMetrics = getFontMetrics(choice.getFont());
    int i = Math.min(choice.getItemCount(), 8);
    return fontMetrics.getHeight() * i;
  }
  
  native void closeList();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WChoicePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */