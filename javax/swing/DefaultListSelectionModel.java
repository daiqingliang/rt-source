package javax.swing;

import java.beans.Transient;
import java.io.Serializable;
import java.util.BitSet;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class DefaultListSelectionModel implements ListSelectionModel, Cloneable, Serializable {
  private static final int MIN = -1;
  
  private static final int MAX = 2147483647;
  
  private int selectionMode = 2;
  
  private int minIndex = Integer.MAX_VALUE;
  
  private int maxIndex = -1;
  
  private int anchorIndex = -1;
  
  private int leadIndex = -1;
  
  private int firstAdjustedIndex = Integer.MAX_VALUE;
  
  private int lastAdjustedIndex = -1;
  
  private boolean isAdjusting = false;
  
  private int firstChangedIndex = Integer.MAX_VALUE;
  
  private int lastChangedIndex = -1;
  
  private BitSet value = new BitSet(32);
  
  protected EventListenerList listenerList = new EventListenerList();
  
  protected boolean leadAnchorNotificationEnabled = true;
  
  public int getMinSelectionIndex() { return isSelectionEmpty() ? -1 : this.minIndex; }
  
  public int getMaxSelectionIndex() { return this.maxIndex; }
  
  public boolean getValueIsAdjusting() { return this.isAdjusting; }
  
  public int getSelectionMode() { return this.selectionMode; }
  
  public void setSelectionMode(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        this.selectionMode = paramInt;
        return;
    } 
    throw new IllegalArgumentException("invalid selectionMode");
  }
  
  public boolean isSelectedIndex(int paramInt) { return (paramInt < this.minIndex || paramInt > this.maxIndex) ? false : this.value.get(paramInt); }
  
  public boolean isSelectionEmpty() { return (this.minIndex > this.maxIndex); }
  
  public void addListSelectionListener(ListSelectionListener paramListSelectionListener) { this.listenerList.add(ListSelectionListener.class, paramListSelectionListener); }
  
  public void removeListSelectionListener(ListSelectionListener paramListSelectionListener) { this.listenerList.remove(ListSelectionListener.class, paramListSelectionListener); }
  
  public ListSelectionListener[] getListSelectionListeners() { return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class); }
  
  protected void fireValueChanged(boolean paramBoolean) {
    if (this.lastChangedIndex == -1)
      return; 
    int i = this.firstChangedIndex;
    int j = this.lastChangedIndex;
    this.firstChangedIndex = Integer.MAX_VALUE;
    this.lastChangedIndex = -1;
    fireValueChanged(i, j, paramBoolean);
  }
  
  protected void fireValueChanged(int paramInt1, int paramInt2) { fireValueChanged(paramInt1, paramInt2, getValueIsAdjusting()); }
  
  protected void fireValueChanged(int paramInt1, int paramInt2, boolean paramBoolean) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListSelectionEvent listSelectionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListSelectionListener.class) {
        if (listSelectionEvent == null)
          listSelectionEvent = new ListSelectionEvent(this, paramInt1, paramInt2, paramBoolean); 
        ((ListSelectionListener)arrayOfObject[i + 1]).valueChanged(listSelectionEvent);
      } 
    } 
  }
  
  private void fireValueChanged() {
    if (this.lastAdjustedIndex == -1)
      return; 
    if (getValueIsAdjusting()) {
      this.firstChangedIndex = Math.min(this.firstChangedIndex, this.firstAdjustedIndex);
      this.lastChangedIndex = Math.max(this.lastChangedIndex, this.lastAdjustedIndex);
    } 
    int i = this.firstAdjustedIndex;
    int j = this.lastAdjustedIndex;
    this.firstAdjustedIndex = Integer.MAX_VALUE;
    this.lastAdjustedIndex = -1;
    fireValueChanged(i, j);
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  private void markAsDirty(int paramInt) {
    if (paramInt == -1)
      return; 
    this.firstAdjustedIndex = Math.min(this.firstAdjustedIndex, paramInt);
    this.lastAdjustedIndex = Math.max(this.lastAdjustedIndex, paramInt);
  }
  
  private void set(int paramInt) {
    if (this.value.get(paramInt))
      return; 
    this.value.set(paramInt);
    markAsDirty(paramInt);
    this.minIndex = Math.min(this.minIndex, paramInt);
    this.maxIndex = Math.max(this.maxIndex, paramInt);
  }
  
  private void clear(int paramInt) {
    if (!this.value.get(paramInt))
      return; 
    this.value.clear(paramInt);
    markAsDirty(paramInt);
    if (paramInt == this.minIndex) {
      this.minIndex++;
      while (this.minIndex <= this.maxIndex && !this.value.get(this.minIndex))
        this.minIndex++; 
    } 
    if (paramInt == this.maxIndex) {
      this.maxIndex--;
      while (this.minIndex <= this.maxIndex && !this.value.get(this.maxIndex))
        this.maxIndex--; 
    } 
    if (isSelectionEmpty()) {
      this.minIndex = Integer.MAX_VALUE;
      this.maxIndex = -1;
    } 
  }
  
  public void setLeadAnchorNotificationEnabled(boolean paramBoolean) { this.leadAnchorNotificationEnabled = paramBoolean; }
  
  public boolean isLeadAnchorNotificationEnabled() { return this.leadAnchorNotificationEnabled; }
  
  private void updateLeadAnchorIndices(int paramInt1, int paramInt2) {
    if (this.leadAnchorNotificationEnabled) {
      if (this.anchorIndex != paramInt1) {
        markAsDirty(this.anchorIndex);
        markAsDirty(paramInt1);
      } 
      if (this.leadIndex != paramInt2) {
        markAsDirty(this.leadIndex);
        markAsDirty(paramInt2);
      } 
    } 
    this.anchorIndex = paramInt1;
    this.leadIndex = paramInt2;
  }
  
  private boolean contains(int paramInt1, int paramInt2, int paramInt3) { return (paramInt3 >= paramInt1 && paramInt3 <= paramInt2); }
  
  private void changeSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    for (int i = Math.min(paramInt3, paramInt1); i <= Math.max(paramInt4, paramInt2); i++) {
      boolean bool1 = contains(paramInt1, paramInt2, i);
      boolean bool2 = contains(paramInt3, paramInt4, i);
      if (bool2 && bool1)
        if (paramBoolean) {
          bool1 = false;
        } else {
          bool2 = false;
        }  
      if (bool2)
        set(i); 
      if (bool1)
        clear(i); 
    } 
    fireValueChanged();
  }
  
  private void changeSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { changeSelection(paramInt1, paramInt2, paramInt3, paramInt4, true); }
  
  public void clearSelection() { removeSelectionIntervalImpl(this.minIndex, this.maxIndex, false); }
  
  public void setSelectionInterval(int paramInt1, int paramInt2) {
    if (paramInt1 == -1 || paramInt2 == -1)
      return; 
    if (getSelectionMode() == 0)
      paramInt1 = paramInt2; 
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = this.minIndex;
    int j = this.maxIndex;
    int k = Math.min(paramInt1, paramInt2);
    int m = Math.max(paramInt1, paramInt2);
    changeSelection(i, j, k, m);
  }
  
  public void addSelectionInterval(int paramInt1, int paramInt2) {
    if (paramInt1 == -1 || paramInt2 == -1)
      return; 
    if (getSelectionMode() == 0) {
      setSelectionInterval(paramInt1, paramInt2);
      return;
    } 
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = Integer.MAX_VALUE;
    byte b = -1;
    int j = Math.min(paramInt1, paramInt2);
    int k = Math.max(paramInt1, paramInt2);
    if (getSelectionMode() == 1 && (k < this.minIndex - 1 || j > this.maxIndex + 1)) {
      setSelectionInterval(paramInt1, paramInt2);
      return;
    } 
    changeSelection(i, b, j, k);
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2) { removeSelectionIntervalImpl(paramInt1, paramInt2, true); }
  
  private void removeSelectionIntervalImpl(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramInt1 == -1 || paramInt2 == -1)
      return; 
    if (paramBoolean)
      updateLeadAnchorIndices(paramInt1, paramInt2); 
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = Integer.MAX_VALUE;
    byte b = -1;
    if (getSelectionMode() != 2 && i > this.minIndex && j < this.maxIndex)
      j = this.maxIndex; 
    changeSelection(i, j, k, b);
  }
  
  private void setState(int paramInt, boolean paramBoolean) {
    if (paramBoolean) {
      set(paramInt);
    } else {
      clear(paramInt);
    } 
  }
  
  public void insertIndexInterval(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramBoolean ? paramInt1 : (paramInt1 + 1);
    int j = i + paramInt2 - 1;
    int k;
    for (k = this.maxIndex; k >= i; k--)
      setState(k + paramInt2, this.value.get(k)); 
    k = (getSelectionMode() == 0) ? 0 : this.value.get(paramInt1);
    int m;
    for (m = i; m <= j; m++)
      setState(m, k); 
    m = this.leadIndex;
    if (m > paramInt1 || (paramBoolean && m == paramInt1))
      m = this.leadIndex + paramInt2; 
    int n = this.anchorIndex;
    if (n > paramInt1 || (paramBoolean && n == paramInt1))
      n = this.anchorIndex + paramInt2; 
    if (m != this.leadIndex || n != this.anchorIndex)
      updateLeadAnchorIndices(n, m); 
    fireValueChanged();
  }
  
  public void removeIndexInterval(int paramInt1, int paramInt2) {
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = j - i + 1;
    int m;
    for (m = i; m <= this.maxIndex; m++)
      setState(m, this.value.get(m + k)); 
    m = this.leadIndex;
    if (m != 0 || i != 0)
      if (m > j) {
        m = this.leadIndex - k;
      } else if (m >= i) {
        m = i - 1;
      }  
    int n = this.anchorIndex;
    if (n != 0 || i != 0)
      if (n > j) {
        n = this.anchorIndex - k;
      } else if (n >= i) {
        n = i - 1;
      }  
    if (m != this.leadIndex || n != this.anchorIndex)
      updateLeadAnchorIndices(n, m); 
    fireValueChanged();
  }
  
  public void setValueIsAdjusting(boolean paramBoolean) {
    if (paramBoolean != this.isAdjusting) {
      this.isAdjusting = paramBoolean;
      fireValueChanged(paramBoolean);
    } 
  }
  
  public String toString() {
    String str = (getValueIsAdjusting() ? "~" : "=") + this.value.toString();
    return getClass().getName() + " " + Integer.toString(hashCode()) + " " + str;
  }
  
  public Object clone() throws CloneNotSupportedException {
    DefaultListSelectionModel defaultListSelectionModel = (DefaultListSelectionModel)super.clone();
    defaultListSelectionModel.value = (BitSet)this.value.clone();
    defaultListSelectionModel.listenerList = new EventListenerList();
    return defaultListSelectionModel;
  }
  
  @Transient
  public int getAnchorSelectionIndex() { return this.anchorIndex; }
  
  @Transient
  public int getLeadSelectionIndex() { return this.leadIndex; }
  
  public void setAnchorSelectionIndex(int paramInt) {
    updateLeadAnchorIndices(paramInt, this.leadIndex);
    fireValueChanged();
  }
  
  public void moveLeadSelectionIndex(int paramInt) {
    if (paramInt == -1 && this.anchorIndex != -1)
      return; 
    updateLeadAnchorIndices(this.anchorIndex, paramInt);
    fireValueChanged();
  }
  
  public void setLeadSelectionIndex(int paramInt) {
    int i = this.anchorIndex;
    if (paramInt == -1) {
      if (i == -1) {
        updateLeadAnchorIndices(i, paramInt);
        fireValueChanged();
      } 
      return;
    } 
    if (i == -1)
      return; 
    if (this.leadIndex == -1)
      this.leadIndex = paramInt; 
    boolean bool = this.value.get(this.anchorIndex);
    if (getSelectionMode() == 0) {
      i = paramInt;
      bool = true;
    } 
    int j = Math.min(this.anchorIndex, this.leadIndex);
    int k = Math.max(this.anchorIndex, this.leadIndex);
    int m = Math.min(i, paramInt);
    int n = Math.max(i, paramInt);
    updateLeadAnchorIndices(i, paramInt);
    if (bool) {
      changeSelection(j, k, m, n);
    } else {
      changeSelection(m, n, j, k, false);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultListSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */