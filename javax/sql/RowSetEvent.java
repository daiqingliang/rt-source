package javax.sql;

import java.util.EventObject;

public class RowSetEvent extends EventObject {
  static final long serialVersionUID = -1875450876546332005L;
  
  public RowSetEvent(RowSet paramRowSet) { super(paramRowSet); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\RowSetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */