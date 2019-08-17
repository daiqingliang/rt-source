package javax.swing.text;

import java.io.Serializable;

public class TabSet implements Serializable {
  private TabStop[] tabs;
  
  private int hashCode = Integer.MAX_VALUE;
  
  public TabSet(TabStop[] paramArrayOfTabStop) {
    if (paramArrayOfTabStop != null) {
      int i = paramArrayOfTabStop.length;
      this.tabs = new TabStop[i];
      System.arraycopy(paramArrayOfTabStop, 0, this.tabs, 0, i);
    } else {
      this.tabs = null;
    } 
  }
  
  public int getTabCount() { return (this.tabs == null) ? 0 : this.tabs.length; }
  
  public TabStop getTab(int paramInt) {
    int i = getTabCount();
    if (paramInt < 0 || paramInt >= i)
      throw new IllegalArgumentException(paramInt + " is outside the range of tabs"); 
    return this.tabs[paramInt];
  }
  
  public TabStop getTabAfter(float paramFloat) {
    int i = getTabIndexAfter(paramFloat);
    return (i == -1) ? null : this.tabs[i];
  }
  
  public int getTabIndex(TabStop paramTabStop) {
    for (int i = getTabCount() - 1; i >= 0; i--) {
      if (getTab(i) == paramTabStop)
        return i; 
    } 
    return -1;
  }
  
  public int getTabIndexAfter(float paramFloat) {
    int i = 0;
    int j;
    for (j = getTabCount(); i != j; j = k) {
      int k = (j - i) / 2 + i;
      if (paramFloat > this.tabs[k].getPosition()) {
        if (i == k) {
          i = j;
          continue;
        } 
        i = k;
        continue;
      } 
      if (k == 0 || paramFloat > this.tabs[k - 1].getPosition())
        return k; 
    } 
    return -1;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof TabSet) {
      TabSet tabSet = (TabSet)paramObject;
      int i = getTabCount();
      if (tabSet.getTabCount() != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        TabStop tabStop1 = getTab(b);
        TabStop tabStop2 = tabSet.getTab(b);
        if ((tabStop1 == null && tabStop2 != null) || (tabStop1 != null && !getTab(b).equals(tabSet.getTab(b))))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public int hashCode() {
    if (this.hashCode == Integer.MAX_VALUE) {
      this.hashCode = 0;
      int i = getTabCount();
      for (byte b = 0; b < i; b++) {
        TabStop tabStop = getTab(b);
        this.hashCode ^= ((tabStop != null) ? getTab(b).hashCode() : 0);
      } 
      if (this.hashCode == Integer.MAX_VALUE)
        this.hashCode--; 
    } 
    return this.hashCode;
  }
  
  public String toString() {
    int i = getTabCount();
    StringBuilder stringBuilder = new StringBuilder("[ ");
    for (byte b = 0; b < i; b++) {
      if (b)
        stringBuilder.append(" - "); 
      stringBuilder.append(getTab(b).toString());
    } 
    stringBuilder.append(" ]");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\TabSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */