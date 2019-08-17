package javax.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MenuSelectionManager {
  private Vector<MenuElement> selection = new Vector();
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  private static final StringBuilder MENU_SELECTION_MANAGER_KEY = new StringBuilder("javax.swing.MenuSelectionManager");
  
  protected ChangeEvent changeEvent = null;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  public static MenuSelectionManager defaultManager() {
    synchronized (MENU_SELECTION_MANAGER_KEY) {
      AppContext appContext = AppContext.getAppContext();
      MenuSelectionManager menuSelectionManager = (MenuSelectionManager)appContext.get(MENU_SELECTION_MANAGER_KEY);
      if (menuSelectionManager == null) {
        menuSelectionManager = new MenuSelectionManager();
        appContext.put(MENU_SELECTION_MANAGER_KEY, menuSelectionManager);
        Object object = appContext.get(SwingUtilities2.MENU_SELECTION_MANAGER_LISTENER_KEY);
        if (object != null && object instanceof ChangeListener)
          menuSelectionManager.addChangeListener((ChangeListener)object); 
      } 
      return menuSelectionManager;
    } 
  }
  
  public void setSelectedPath(MenuElement[] paramArrayOfMenuElement) {
    int k = this.selection.size();
    int m = 0;
    if (paramArrayOfMenuElement == null)
      paramArrayOfMenuElement = new MenuElement[0]; 
    int i = 0;
    int j = paramArrayOfMenuElement.length;
    while (i < j && i < k && this.selection.elementAt(i) == paramArrayOfMenuElement[i]) {
      m++;
      i++;
    } 
    for (i = k - 1; i >= m; i--) {
      MenuElement menuElement = (MenuElement)this.selection.elementAt(i);
      this.selection.removeElementAt(i);
      menuElement.menuSelectionChanged(false);
    } 
    i = m;
    j = paramArrayOfMenuElement.length;
    while (i < j) {
      if (paramArrayOfMenuElement[i] != null) {
        this.selection.addElement(paramArrayOfMenuElement[i]);
        paramArrayOfMenuElement[i].menuSelectionChanged(true);
      } 
      i++;
    } 
    fireStateChanged();
  }
  
  public MenuElement[] getSelectedPath() {
    MenuElement[] arrayOfMenuElement = new MenuElement[this.selection.size()];
    byte b = 0;
    int i = this.selection.size();
    while (b < i) {
      arrayOfMenuElement[b] = (MenuElement)this.selection.elementAt(b);
      b++;
    } 
    return arrayOfMenuElement;
  }
  
  public void clearSelectedPath() {
    if (this.selection.size() > 0)
      setSelectedPath(null); 
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
  
  public void processMouseEvent(MouseEvent paramMouseEvent) {
    Point point = paramMouseEvent.getPoint();
    Component component = paramMouseEvent.getComponent();
    if (component != null && !component.isShowing())
      return; 
    int n = paramMouseEvent.getID();
    int i1 = paramMouseEvent.getModifiers();
    if ((n == 504 || n == 505) && (i1 & 0x1C) != 0)
      return; 
    if (component != null)
      SwingUtilities.convertPointToScreen(point, component); 
    int i = point.x;
    int j = point.y;
    Vector vector = (Vector)this.selection.clone();
    int m = vector.size();
    boolean bool = false;
    for (int k = m - 1; k >= 0 && !bool; k--) {
      MenuElement menuElement = (MenuElement)vector.elementAt(k);
      MenuElement[] arrayOfMenuElement1 = menuElement.getSubElements();
      MenuElement[] arrayOfMenuElement2 = null;
      byte b = 0;
      int i2 = arrayOfMenuElement1.length;
      while (b < i2 && !bool) {
        if (arrayOfMenuElement1[b] != null) {
          Component component1 = arrayOfMenuElement1[b].getComponent();
          if (component1.isShowing()) {
            int i4;
            int i3;
            if (component1 instanceof JComponent) {
              i3 = component1.getWidth();
              i4 = component1.getHeight();
            } else {
              Rectangle rectangle = component1.getBounds();
              i3 = rectangle.width;
              i4 = rectangle.height;
            } 
            point.x = i;
            point.y = j;
            SwingUtilities.convertPointFromScreen(point, component1);
            if (point.x >= 0 && point.x < i3 && point.y >= 0 && point.y < i4) {
              if (arrayOfMenuElement2 == null) {
                arrayOfMenuElement2 = new MenuElement[k + 2];
                for (byte b1 = 0; b1 <= k; b1++)
                  arrayOfMenuElement2[b1] = (MenuElement)vector.elementAt(b1); 
              } 
              arrayOfMenuElement2[k + 1] = arrayOfMenuElement1[b];
              MenuElement[] arrayOfMenuElement = getSelectedPath();
              if (arrayOfMenuElement[arrayOfMenuElement.length - true] != arrayOfMenuElement2[k + true] && (arrayOfMenuElement.length < 2 || arrayOfMenuElement[arrayOfMenuElement.length - 2] != arrayOfMenuElement2[k + true])) {
                Component component2 = arrayOfMenuElement[arrayOfMenuElement.length - 1].getComponent();
                MouseEvent mouseEvent1 = new MouseEvent(component2, 505, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
                AWTAccessor.MouseEventAccessor mouseEventAccessor1 = AWTAccessor.getMouseEventAccessor();
                mouseEventAccessor1.setCausedByTouchEvent(mouseEvent1, mouseEventAccessor1.isCausedByTouchEvent(paramMouseEvent));
                arrayOfMenuElement[arrayOfMenuElement.length - 1].processMouseEvent(mouseEvent1, arrayOfMenuElement2, this);
                MouseEvent mouseEvent2 = new MouseEvent(component1, 504, paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
                mouseEventAccessor1.setCausedByTouchEvent(mouseEvent2, mouseEventAccessor1.isCausedByTouchEvent(paramMouseEvent));
                arrayOfMenuElement1[b].processMouseEvent(mouseEvent2, arrayOfMenuElement2, this);
              } 
              MouseEvent mouseEvent = new MouseEvent(component1, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
              AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
              mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
              arrayOfMenuElement1[b].processMouseEvent(mouseEvent, arrayOfMenuElement2, this);
              bool = true;
              paramMouseEvent.consume();
            } 
          } 
        } 
        b++;
      } 
    } 
  }
  
  private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement) { printMenuElementArray(paramArrayOfMenuElement, false); }
  
  private void printMenuElementArray(MenuElement[] paramArrayOfMenuElement, boolean paramBoolean) {
    System.out.println("Path is(");
    byte b = 0;
    int i = paramArrayOfMenuElement.length;
    while (b < i) {
      for (byte b1 = 0; b1 <= b; b1++)
        System.out.print("  "); 
      MenuElement menuElement = paramArrayOfMenuElement[b];
      if (menuElement instanceof JMenuItem) {
        System.out.println(((JMenuItem)menuElement).getText() + ", ");
      } else if (menuElement instanceof JMenuBar) {
        System.out.println("JMenuBar, ");
      } else if (menuElement instanceof JPopupMenu) {
        System.out.println("JPopupMenu, ");
      } else if (menuElement == null) {
        System.out.println("NULL , ");
      } else {
        System.out.println("" + menuElement + ", ");
      } 
      b++;
    } 
    System.out.println(")");
    if (paramBoolean == true)
      Thread.dumpStack(); 
  }
  
  public Component componentForPoint(Component paramComponent, Point paramPoint) {
    Point point = paramPoint;
    SwingUtilities.convertPointToScreen(point, paramComponent);
    int i = point.x;
    int j = point.y;
    Vector vector = (Vector)this.selection.clone();
    int m = vector.size();
    for (int k = m - 1; k >= 0; k--) {
      MenuElement menuElement = (MenuElement)vector.elementAt(k);
      MenuElement[] arrayOfMenuElement = menuElement.getSubElements();
      byte b = 0;
      int n = arrayOfMenuElement.length;
      while (b < n) {
        if (arrayOfMenuElement[b] != null) {
          Component component = arrayOfMenuElement[b].getComponent();
          if (component.isShowing()) {
            int i2;
            int i1;
            if (component instanceof JComponent) {
              i1 = component.getWidth();
              i2 = component.getHeight();
            } else {
              Rectangle rectangle = component.getBounds();
              i1 = rectangle.width;
              i2 = rectangle.height;
            } 
            point.x = i;
            point.y = j;
            SwingUtilities.convertPointFromScreen(point, component);
            if (point.x >= 0 && point.x < i1 && point.y >= 0 && point.y < i2)
              return component; 
          } 
        } 
        b++;
      } 
    } 
    return null;
  }
  
  public void processKeyEvent(KeyEvent paramKeyEvent) {
    MenuElement[] arrayOfMenuElement1 = new MenuElement[0];
    arrayOfMenuElement1 = (MenuElement[])this.selection.toArray(arrayOfMenuElement1);
    int i = arrayOfMenuElement1.length;
    if (i < 1)
      return; 
    for (int j = i - 1; j >= 0; j--) {
      MenuElement menuElement = arrayOfMenuElement1[j];
      MenuElement[] arrayOfMenuElement4 = menuElement.getSubElements();
      MenuElement[] arrayOfMenuElement3 = null;
      for (byte b = 0; b < arrayOfMenuElement4.length; b++) {
        if (arrayOfMenuElement4[b] != null && arrayOfMenuElement4[b].getComponent().isShowing() && arrayOfMenuElement4[b].getComponent().isEnabled()) {
          if (arrayOfMenuElement3 == null) {
            arrayOfMenuElement3 = new MenuElement[j + 2];
            System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement3, 0, j + 1);
          } 
          arrayOfMenuElement3[j + 1] = arrayOfMenuElement4[b];
          arrayOfMenuElement4[b].processKeyEvent(paramKeyEvent, arrayOfMenuElement3, this);
          if (paramKeyEvent.isConsumed())
            return; 
        } 
      } 
    } 
    MenuElement[] arrayOfMenuElement2 = new MenuElement[1];
    arrayOfMenuElement2[0] = arrayOfMenuElement1[0];
    arrayOfMenuElement2[0].processKeyEvent(paramKeyEvent, arrayOfMenuElement2, this);
    if (paramKeyEvent.isConsumed())
      return; 
  }
  
  public boolean isComponentPartOfCurrentMenu(Component paramComponent) {
    if (this.selection.size() > 0) {
      MenuElement menuElement = (MenuElement)this.selection.elementAt(0);
      return isComponentPartOfCurrentMenu(menuElement, paramComponent);
    } 
    return false;
  }
  
  private boolean isComponentPartOfCurrentMenu(MenuElement paramMenuElement, Component paramComponent) {
    if (paramMenuElement == null)
      return false; 
    if (paramMenuElement.getComponent() == paramComponent)
      return true; 
    MenuElement[] arrayOfMenuElement = paramMenuElement.getSubElements();
    byte b = 0;
    int i = arrayOfMenuElement.length;
    while (b < i) {
      if (isComponentPartOfCurrentMenu(arrayOfMenuElement[b], paramComponent))
        return true; 
      b++;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\MenuSelectionManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */