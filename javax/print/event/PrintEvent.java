package javax.print.event;

import java.util.EventObject;

public class PrintEvent extends EventObject {
  private static final long serialVersionUID = 2286914924430763847L;
  
  public PrintEvent(Object paramObject) { super(paramObject); }
  
  public String toString() { return "PrintEvent on " + getSource().toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\event\PrintEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */