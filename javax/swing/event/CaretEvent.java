package javax.swing.event;

import java.util.EventObject;

public abstract class CaretEvent extends EventObject {
  public CaretEvent(Object paramObject) { super(paramObject); }
  
  public abstract int getDot();
  
  public abstract int getMark();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\CaretEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */