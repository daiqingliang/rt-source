package javax.swing.event;

import java.util.EventListener;

public interface CellEditorListener extends EventListener {
  void editingStopped(ChangeEvent paramChangeEvent);
  
  void editingCanceled(ChangeEvent paramChangeEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\CellEditorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */