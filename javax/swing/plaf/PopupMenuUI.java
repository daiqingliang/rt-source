package javax.swing.plaf;

import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.Popup;
import javax.swing.PopupFactory;

public abstract class PopupMenuUI extends ComponentUI {
  public boolean isPopupTrigger(MouseEvent paramMouseEvent) { return paramMouseEvent.isPopupTrigger(); }
  
  public Popup getPopup(JPopupMenu paramJPopupMenu, int paramInt1, int paramInt2) {
    PopupFactory popupFactory = PopupFactory.getSharedInstance();
    return popupFactory.getPopup(paramJPopupMenu.getInvoker(), paramJPopupMenu, paramInt1, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\PopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */