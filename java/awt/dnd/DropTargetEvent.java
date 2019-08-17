package java.awt.dnd;

import java.util.EventObject;

public class DropTargetEvent extends EventObject {
  private static final long serialVersionUID = 2821229066521922993L;
  
  protected DropTargetContext context;
  
  public DropTargetEvent(DropTargetContext paramDropTargetContext) {
    super(paramDropTargetContext.getDropTarget());
    this.context = paramDropTargetContext;
  }
  
  public DropTargetContext getDropTargetContext() { return this.context; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DropTargetEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */