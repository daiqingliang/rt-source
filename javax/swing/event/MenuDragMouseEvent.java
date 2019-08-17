package javax.swing.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class MenuDragMouseEvent extends MouseEvent {
  private MenuElement[] path;
  
  private MenuSelectionManager manager;
  
  public MenuDragMouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean);
    this.path = paramArrayOfMenuElement;
    this.manager = paramMenuSelectionManager;
  }
  
  public MenuDragMouseEvent(Component paramComponent, int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {
    super(paramComponent, paramInt1, paramLong, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean, 0);
    this.path = paramArrayOfMenuElement;
    this.manager = paramMenuSelectionManager;
  }
  
  public MenuElement[] getPath() { return this.path; }
  
  public MenuSelectionManager getMenuSelectionManager() { return this.manager; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\MenuDragMouseEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */