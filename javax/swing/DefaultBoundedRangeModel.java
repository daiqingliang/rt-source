package javax.swing;

import java.io.Serializable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultBoundedRangeModel implements BoundedRangeModel, Serializable {
  protected ChangeEvent changeEvent = null;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  private int value = 0;
  
  private int extent = 0;
  
  private int min = 0;
  
  private int max = 100;
  
  private boolean isAdjusting = false;
  
  public DefaultBoundedRangeModel() {}
  
  public DefaultBoundedRangeModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt4 >= paramInt3 && paramInt1 >= paramInt3 && paramInt1 + paramInt2 >= paramInt1 && paramInt1 + paramInt2 <= paramInt4) {
      this.value = paramInt1;
      this.extent = paramInt2;
      this.min = paramInt3;
      this.max = paramInt4;
    } else {
      throw new IllegalArgumentException("invalid range properties");
    } 
  }
  
  public int getValue() { return this.value; }
  
  public int getExtent() { return this.extent; }
  
  public int getMinimum() { return this.min; }
  
  public int getMaximum() { return this.max; }
  
  public void setValue(int paramInt) {
    paramInt = Math.min(paramInt, Integer.MAX_VALUE - this.extent);
    int i = Math.max(paramInt, this.min);
    if (i + this.extent > this.max)
      i = this.max - this.extent; 
    setRangeProperties(i, this.extent, this.min, this.max, this.isAdjusting);
  }
  
  public void setExtent(int paramInt) {
    int i = Math.max(0, paramInt);
    if (this.value + i > this.max)
      i = this.max - this.value; 
    setRangeProperties(this.value, i, this.min, this.max, this.isAdjusting);
  }
  
  public void setMinimum(int paramInt) {
    int i = Math.max(paramInt, this.max);
    int j = Math.max(paramInt, this.value);
    int k = Math.min(i - j, this.extent);
    setRangeProperties(j, k, paramInt, i, this.isAdjusting);
  }
  
  public void setMaximum(int paramInt) {
    int i = Math.min(paramInt, this.min);
    int j = Math.min(paramInt - i, this.extent);
    int k = Math.min(paramInt - j, this.value);
    setRangeProperties(k, j, i, paramInt, this.isAdjusting);
  }
  
  public void setValueIsAdjusting(boolean paramBoolean) { setRangeProperties(this.value, this.extent, this.min, this.max, paramBoolean); }
  
  public boolean getValueIsAdjusting() { return this.isAdjusting; }
  
  public void setRangeProperties(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
    if (paramInt3 > paramInt4)
      paramInt3 = paramInt4; 
    if (paramInt1 > paramInt4)
      paramInt4 = paramInt1; 
    if (paramInt1 < paramInt3)
      paramInt3 = paramInt1; 
    if (paramInt2 + paramInt1 > paramInt4)
      paramInt2 = paramInt4 - paramInt1; 
    if (paramInt2 < 0)
      paramInt2 = 0; 
    boolean bool = (paramInt1 != this.value || paramInt2 != this.extent || paramInt3 != this.min || paramInt4 != this.max || paramBoolean != this.isAdjusting) ? 1 : 0;
    if (bool) {
      this.value = paramInt1;
      this.extent = paramInt2;
      this.min = paramInt3;
      this.max = paramInt4;
      this.isAdjusting = paramBoolean;
      fireStateChanged();
    } 
  }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public String toString() {
    String str = "value=" + getValue() + ", extent=" + getExtent() + ", min=" + getMinimum() + ", max=" + getMaximum() + ", adj=" + getValueIsAdjusting();
    return getClass().getName() + "[" + str + "]";
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultBoundedRangeModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */