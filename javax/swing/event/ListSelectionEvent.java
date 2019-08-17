package javax.swing.event;

import java.util.EventObject;

public class ListSelectionEvent extends EventObject {
  private int firstIndex;
  
  private int lastIndex;
  
  private boolean isAdjusting;
  
  public ListSelectionEvent(Object paramObject, int paramInt1, int paramInt2, boolean paramBoolean) {
    super(paramObject);
    this.firstIndex = paramInt1;
    this.lastIndex = paramInt2;
    this.isAdjusting = paramBoolean;
  }
  
  public int getFirstIndex() { return this.firstIndex; }
  
  public int getLastIndex() { return this.lastIndex; }
  
  public boolean getValueIsAdjusting() { return this.isAdjusting; }
  
  public String toString() {
    String str = " source=" + getSource() + " firstIndex= " + this.firstIndex + " lastIndex= " + this.lastIndex + " isAdjusting= " + this.isAdjusting + " ";
    return getClass().getName() + "[" + str + "]";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\ListSelectionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */