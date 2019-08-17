package sun.awt.windows;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.peer.ListPeer;

final class WListPeer extends WComponentPeer implements ListPeer {
  private FontMetrics fm;
  
  public boolean isFocusable() { return true; }
  
  public int[] getSelectedIndexes() {
    List list = (List)this.target;
    int i = list.countItems();
    int[] arrayOfInt1 = new int[i];
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      if (isSelected(b2))
        arrayOfInt1[b1++] = b2; 
    } 
    int[] arrayOfInt2 = new int[b1];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b1);
    return arrayOfInt2;
  }
  
  public void add(String paramString, int paramInt) { addItem(paramString, paramInt); }
  
  public void removeAll() { clear(); }
  
  public void setMultipleMode(boolean paramBoolean) { setMultipleSelections(paramBoolean); }
  
  public Dimension getPreferredSize(int paramInt) { return preferredSize(paramInt); }
  
  public Dimension getMinimumSize(int paramInt) { return minimumSize(paramInt); }
  
  public void addItem(String paramString, int paramInt) { addItems(new String[] { paramString }, paramInt, this.fm.stringWidth(paramString)); }
  
  native void addItems(String[] paramArrayOfString, int paramInt1, int paramInt2);
  
  public native void delItems(int paramInt1, int paramInt2);
  
  public void clear() {
    List list = (List)this.target;
    delItems(0, list.countItems());
  }
  
  public native void select(int paramInt);
  
  public native void deselect(int paramInt);
  
  public native void makeVisible(int paramInt);
  
  public native void setMultipleSelections(boolean paramBoolean);
  
  public native int getMaxWidth();
  
  public Dimension preferredSize(int paramInt) {
    if (this.fm == null) {
      List list = (List)this.target;
      this.fm = getFontMetrics(list.getFont());
    } 
    Dimension dimension = minimumSize(paramInt);
    dimension.width = Math.max(dimension.width, getMaxWidth() + 20);
    return dimension;
  }
  
  public Dimension minimumSize(int paramInt) { return new Dimension(20 + this.fm.stringWidth("0123456789abcde"), this.fm.getHeight() * paramInt + 4); }
  
  WListPeer(List paramList) { super(paramList); }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize() {
    List list = (List)this.target;
    this.fm = getFontMetrics(list.getFont());
    Font font = list.getFont();
    if (font != null)
      setFont(font); 
    int i = list.countItems();
    if (i > 0) {
      String[] arrayOfString = new String[i];
      int k = 0;
      int m = 0;
      for (byte b = 0; b < i; b++) {
        arrayOfString[b] = list.getItem(b);
        m = this.fm.stringWidth(arrayOfString[b]);
        if (m > k)
          k = m; 
      } 
      addItems(arrayOfString, 0, k);
    } 
    setMultipleSelections(list.allowsMultipleSelections());
    int[] arrayOfInt = list.getSelectedIndexes();
    int j;
    for (j = 0; j < arrayOfInt.length; j++)
      select(arrayOfInt[j]); 
    j = list.getVisibleIndex();
    if (j < 0 && arrayOfInt.length > 0)
      j = arrayOfInt[0]; 
    if (j >= 0)
      makeVisible(j); 
    super.initialize();
  }
  
  public boolean shouldClearRectBeforePaint() { return false; }
  
  private native void updateMaxItemWidth();
  
  native boolean isSelected(int paramInt);
  
  void _setFont(Font paramFont) {
    super._setFont(paramFont);
    this.fm = getFontMetrics(((List)this.target).getFont());
    updateMaxItemWidth();
  }
  
  void handleAction(final int index, final long when, final int modifiers) {
    final List l = (List)this.target;
    WToolkit.executeOnEventHandlerThread(list, new Runnable() {
          public void run() {
            l.select(index);
            WListPeer.this.postEvent(new ActionEvent(WListPeer.this.target, 1001, l.getItem(index), when, modifiers));
          }
        });
  }
  
  void handleListChanged(final int index) {
    final List l = (List)this.target;
    WToolkit.executeOnEventHandlerThread(list, new Runnable() {
          public void run() { WListPeer.this.postEvent(new ItemEvent(l, 701, Integer.valueOf(index), WListPeer.this.isSelected(index) ? 1 : 2)); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WListPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */