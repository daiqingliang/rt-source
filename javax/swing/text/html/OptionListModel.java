package javax.swing.text.html;

import java.io.Serializable;
import java.util.BitSet;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class OptionListModel<E> extends DefaultListModel<E> implements ListSelectionModel, Serializable {
  private static final int MIN = -1;
  
  private static final int MAX = 2147483647;
  
  private int selectionMode = 0;
  
  private int minIndex = Integer.MAX_VALUE;
  
  private int maxIndex = -1;
  
  private int anchorIndex = -1;
  
  private int leadIndex = -1;
  
  private int firstChangedIndex = Integer.MAX_VALUE;
  
  private int lastChangedIndex = -1;
  
  private boolean isAdjusting = false;
  
  private BitSet value = new BitSet(32);
  
  private BitSet initialValue = new BitSet(32);
  
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
  
  protected void fireValueChanged(boolean paramBoolean) { fireValueChanged(getMinSelectionIndex(), getMaxSelectionIndex(), paramBoolean); }
  
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
    if (this.lastChangedIndex == -1)
      return; 
    int i = this.firstChangedIndex;
    int j = this.lastChangedIndex;
    this.firstChangedIndex = Integer.MAX_VALUE;
    this.lastChangedIndex = -1;
    fireValueChanged(i, j);
  }
  
  private void markAsDirty(int paramInt) {
    this.firstChangedIndex = Math.min(this.firstChangedIndex, paramInt);
    this.lastChangedIndex = Math.max(this.lastChangedIndex, paramInt);
  }
  
  private void set(int paramInt) {
    if (this.value.get(paramInt))
      return; 
    this.value.set(paramInt);
    Option option = (Option)get(paramInt);
    option.setSelection(true);
    markAsDirty(paramInt);
    this.minIndex = Math.min(this.minIndex, paramInt);
    this.maxIndex = Math.max(this.maxIndex, paramInt);
  }
  
  private void clear(int paramInt) {
    if (!this.value.get(paramInt))
      return; 
    this.value.clear(paramInt);
    Option option = (Option)get(paramInt);
    option.setSelection(false);
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
        if (this.anchorIndex != -1)
          markAsDirty(this.anchorIndex); 
        markAsDirty(paramInt1);
      } 
      if (this.leadIndex != paramInt2) {
        if (this.leadIndex != -1)
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
  
  public void clearSelection() { removeSelectionInterval(this.minIndex, this.maxIndex); }
  
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
    if (getSelectionMode() != 2) {
      setSelectionInterval(paramInt1, paramInt2);
      return;
    } 
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = Integer.MAX_VALUE;
    byte b = -1;
    int j = Math.min(paramInt1, paramInt2);
    int k = Math.max(paramInt1, paramInt2);
    changeSelection(i, b, j, k);
  }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2) {
    if (paramInt1 == -1 || paramInt2 == -1)
      return; 
    updateLeadAnchorIndices(paramInt1, paramInt2);
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = Integer.MAX_VALUE;
    byte b = -1;
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
    for (int k = this.maxIndex; k >= i; k--)
      setState(k + paramInt2, this.value.get(k)); 
    boolean bool = this.value.get(paramInt1);
    for (int m = i; m <= j; m++)
      setState(m, bool); 
  }
  
  public void removeIndexInterval(int paramInt1, int paramInt2) {
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = j - i + 1;
    for (int m = i; m <= this.maxIndex; m++)
      setState(m, this.value.get(m + k)); 
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
    OptionListModel optionListModel = (OptionListModel)super.clone();
    optionListModel.value = (BitSet)this.value.clone();
    optionListModel.listenerList = new EventListenerList();
    return optionListModel;
  }
  
  public int getAnchorSelectionIndex() { return this.anchorIndex; }
  
  public int getLeadSelectionIndex() { return this.leadIndex; }
  
  public void setAnchorSelectionIndex(int paramInt) { this.anchorIndex = paramInt; }
  
  public void setLeadSelectionIndex(int paramInt) {
    int i = this.anchorIndex;
    if (getSelectionMode() == 0)
      i = paramInt; 
    int j = Math.min(this.anchorIndex, this.leadIndex);
    int k = Math.max(this.anchorIndex, this.leadIndex);
    int m = Math.min(i, paramInt);
    int n = Math.max(i, paramInt);
    if (this.value.get(this.anchorIndex)) {
      changeSelection(j, k, m, n);
    } else {
      changeSelection(m, n, j, k, false);
    } 
    this.anchorIndex = i;
    this.leadIndex = paramInt;
  }
  
  public void setInitialSelection(int paramInt) {
    if (this.initialValue.get(paramInt))
      return; 
    if (this.selectionMode == 0)
      this.initialValue.and(new BitSet()); 
    this.initialValue.set(paramInt);
  }
  
  public BitSet getInitialSelection() { return this.initialValue; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\OptionListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */