package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.AccessController;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleStateSet;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.text.View;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.reflect.misc.ReflectUtil;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class SwingUtilities implements SwingConstants {
  private static boolean canAccessEventQueue = false;
  
  private static boolean eventQueueTested = false;
  
  private static boolean suppressDropSupport;
  
  private static boolean checkedSuppressDropSupport;
  
  private static final Object sharedOwnerFrameKey = new StringBuffer("SwingUtilities.sharedOwnerFrame");
  
  private static boolean getSuppressDropTarget() {
    if (!checkedSuppressDropSupport) {
      suppressDropSupport = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("suppressSwingDropSupport"))).booleanValue();
      checkedSuppressDropSupport = true;
    } 
    return suppressDropSupport;
  }
  
  static void installSwingDropTargetAsNecessary(Component paramComponent, TransferHandler paramTransferHandler) {
    if (!getSuppressDropTarget()) {
      DropTarget dropTarget = paramComponent.getDropTarget();
      if (dropTarget == null || dropTarget instanceof javax.swing.plaf.UIResource)
        if (paramTransferHandler == null) {
          paramComponent.setDropTarget(null);
        } else if (!GraphicsEnvironment.isHeadless()) {
          paramComponent.setDropTarget(new TransferHandler.SwingDropTarget(paramComponent));
        }  
    } 
  }
  
  public static final boolean isRectangleContainingRectangle(Rectangle paramRectangle1, Rectangle paramRectangle2) { return (paramRectangle2.x >= paramRectangle1.x && paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width && paramRectangle2.y >= paramRectangle1.y && paramRectangle2.y + paramRectangle2.height <= paramRectangle1.y + paramRectangle1.height); }
  
  public static Rectangle getLocalBounds(Component paramComponent) {
    Rectangle rectangle = new Rectangle(paramComponent.getBounds());
    rectangle.x = rectangle.y = 0;
    return rectangle;
  }
  
  public static Window getWindowAncestor(Component paramComponent) {
    for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
      if (container instanceof Window)
        return (Window)container; 
    } 
    return null;
  }
  
  static Point convertScreenLocationToParent(Container paramContainer, int paramInt1, int paramInt2) {
    for (Container container = paramContainer; container != null; container = container.getParent()) {
      if (container instanceof Window) {
        Point point = new Point(paramInt1, paramInt2);
        convertPointFromScreen(point, paramContainer);
        return point;
      } 
    } 
    throw new Error("convertScreenLocationToParent: no window ancestor");
  }
  
  public static Point convertPoint(Component paramComponent1, Point paramPoint, Component paramComponent2) {
    if (paramComponent1 == null && paramComponent2 == null)
      return paramPoint; 
    if (paramComponent1 == null) {
      paramComponent1 = getWindowAncestor(paramComponent2);
      if (paramComponent1 == null)
        throw new Error("Source component not connected to component tree hierarchy"); 
    } 
    Point point = new Point(paramPoint);
    convertPointToScreen(point, paramComponent1);
    if (paramComponent2 == null) {
      paramComponent2 = getWindowAncestor(paramComponent1);
      if (paramComponent2 == null)
        throw new Error("Destination component not connected to component tree hierarchy"); 
    } 
    convertPointFromScreen(point, paramComponent2);
    return point;
  }
  
  public static Point convertPoint(Component paramComponent1, int paramInt1, int paramInt2, Component paramComponent2) {
    Point point = new Point(paramInt1, paramInt2);
    return convertPoint(paramComponent1, point, paramComponent2);
  }
  
  public static Rectangle convertRectangle(Component paramComponent1, Rectangle paramRectangle, Component paramComponent2) {
    Point point = new Point(paramRectangle.x, paramRectangle.y);
    point = convertPoint(paramComponent1, point, paramComponent2);
    return new Rectangle(point.x, point.y, paramRectangle.width, paramRectangle.height);
  }
  
  public static Container getAncestorOfClass(Class<?> paramClass, Component paramComponent) {
    if (paramComponent == null || paramClass == null)
      return null; 
    Container container;
    for (container = paramComponent.getParent(); container != null && !paramClass.isInstance(container); container = container.getParent());
    return container;
  }
  
  public static Container getAncestorNamed(String paramString, Component paramComponent) {
    if (paramComponent == null || paramString == null)
      return null; 
    Container container;
    for (container = paramComponent.getParent(); container != null && !paramString.equals(container.getName()); container = container.getParent());
    return container;
  }
  
  public static Component getDeepestComponentAt(Component paramComponent, int paramInt1, int paramInt2) {
    if (!paramComponent.contains(paramInt1, paramInt2))
      return null; 
    if (paramComponent instanceof Container) {
      Component[] arrayOfComponent = ((Container)paramComponent).getComponents();
      for (Component component : arrayOfComponent) {
        if (component != null && component.isVisible()) {
          Point point = component.getLocation();
          if (component instanceof Container) {
            component = getDeepestComponentAt(component, paramInt1 - point.x, paramInt2 - point.y);
          } else {
            component = component.getComponentAt(paramInt1 - point.x, paramInt2 - point.y);
          } 
          if (component != null && component.isVisible())
            return component; 
        } 
      } 
    } 
    return paramComponent;
  }
  
  public static MouseEvent convertMouseEvent(Component paramComponent1, MouseEvent paramMouseEvent, Component paramComponent2) {
    MouseEvent mouseEvent;
    Component component;
    Point point = convertPoint(paramComponent1, new Point(paramMouseEvent.getX(), paramMouseEvent.getY()), paramComponent2);
    if (paramComponent2 != null) {
      component = paramComponent2;
    } else {
      component = paramComponent1;
    } 
    if (paramMouseEvent instanceof MouseWheelEvent) {
      MouseWheelEvent mouseWheelEvent = (MouseWheelEvent)paramMouseEvent;
      mouseEvent = new MouseWheelEvent(component, mouseWheelEvent.getID(), mouseWheelEvent.getWhen(), mouseWheelEvent.getModifiers() | mouseWheelEvent.getModifiersEx(), point.x, point.y, mouseWheelEvent.getXOnScreen(), mouseWheelEvent.getYOnScreen(), mouseWheelEvent.getClickCount(), mouseWheelEvent.isPopupTrigger(), mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation());
    } else if (paramMouseEvent instanceof MenuDragMouseEvent) {
      MenuDragMouseEvent menuDragMouseEvent = (MenuDragMouseEvent)paramMouseEvent;
      mouseEvent = new MenuDragMouseEvent(component, menuDragMouseEvent.getID(), menuDragMouseEvent.getWhen(), menuDragMouseEvent.getModifiers() | menuDragMouseEvent.getModifiersEx(), point.x, point.y, menuDragMouseEvent.getXOnScreen(), menuDragMouseEvent.getYOnScreen(), menuDragMouseEvent.getClickCount(), menuDragMouseEvent.isPopupTrigger(), menuDragMouseEvent.getPath(), menuDragMouseEvent.getMenuSelectionManager());
    } else {
      mouseEvent = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers() | paramMouseEvent.getModifiersEx(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), paramMouseEvent.getButton());
      AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
      mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
    } 
    return mouseEvent;
  }
  
  public static void convertPointToScreen(Point paramPoint, Component paramComponent) {
    do {
      int j;
      int i;
      if (paramComponent instanceof JComponent) {
        i = paramComponent.getX();
        j = paramComponent.getY();
      } else if (paramComponent instanceof java.applet.Applet || paramComponent instanceof Window) {
        try {
          Point point = paramComponent.getLocationOnScreen();
          i = point.x;
          j = point.y;
        } catch (IllegalComponentStateException illegalComponentStateException) {
          i = paramComponent.getX();
          j = paramComponent.getY();
        } 
      } else {
        i = paramComponent.getX();
        j = paramComponent.getY();
      } 
      paramPoint.x += i;
      paramPoint.y += j;
      if (paramComponent instanceof Window || paramComponent instanceof java.applet.Applet)
        break; 
      paramComponent = paramComponent.getParent();
    } while (paramComponent != null);
  }
  
  public static void convertPointFromScreen(Point paramPoint, Component paramComponent) {
    do {
      int j;
      int i;
      if (paramComponent instanceof JComponent) {
        i = paramComponent.getX();
        j = paramComponent.getY();
      } else if (paramComponent instanceof java.applet.Applet || paramComponent instanceof Window) {
        try {
          Point point = paramComponent.getLocationOnScreen();
          i = point.x;
          j = point.y;
        } catch (IllegalComponentStateException illegalComponentStateException) {
          i = paramComponent.getX();
          j = paramComponent.getY();
        } 
      } else {
        i = paramComponent.getX();
        j = paramComponent.getY();
      } 
      paramPoint.x -= i;
      paramPoint.y -= j;
      if (paramComponent instanceof Window || paramComponent instanceof java.applet.Applet)
        break; 
      paramComponent = paramComponent.getParent();
    } while (paramComponent != null);
  }
  
  public static Window windowForComponent(Component paramComponent) { return getWindowAncestor(paramComponent); }
  
  public static boolean isDescendingFrom(Component paramComponent1, Component paramComponent2) {
    if (paramComponent1 == paramComponent2)
      return true; 
    for (Container container = paramComponent1.getParent(); container != null; container = container.getParent()) {
      if (container == paramComponent2)
        return true; 
    } 
    return false;
  }
  
  public static Rectangle computeIntersection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle) {
    int i = (paramInt1 > paramRectangle.x) ? paramInt1 : paramRectangle.x;
    int j = (paramInt1 + paramInt3 < paramRectangle.x + paramRectangle.width) ? (paramInt1 + paramInt3) : (paramRectangle.x + paramRectangle.width);
    int k = (paramInt2 > paramRectangle.y) ? paramInt2 : paramRectangle.y;
    int m = (paramInt2 + paramInt4 < paramRectangle.y + paramRectangle.height) ? (paramInt2 + paramInt4) : (paramRectangle.y + paramRectangle.height);
    paramRectangle.x = i;
    paramRectangle.y = k;
    paramRectangle.width = j - i;
    paramRectangle.height = m - k;
    if (paramRectangle.width < 0 || paramRectangle.height < 0)
      paramRectangle.x = paramRectangle.y = paramRectangle.width = paramRectangle.height = 0; 
    return paramRectangle;
  }
  
  public static Rectangle computeUnion(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle) {
    int i = (paramInt1 < paramRectangle.x) ? paramInt1 : paramRectangle.x;
    int j = (paramInt1 + paramInt3 > paramRectangle.x + paramRectangle.width) ? (paramInt1 + paramInt3) : (paramRectangle.x + paramRectangle.width);
    int k = (paramInt2 < paramRectangle.y) ? paramInt2 : paramRectangle.y;
    int m = (paramInt2 + paramInt4 > paramRectangle.y + paramRectangle.height) ? (paramInt2 + paramInt4) : (paramRectangle.y + paramRectangle.height);
    paramRectangle.x = i;
    paramRectangle.y = k;
    paramRectangle.width = j - i;
    paramRectangle.height = m - k;
    return paramRectangle;
  }
  
  public static Rectangle[] computeDifference(Rectangle paramRectangle1, Rectangle paramRectangle2) {
    if (paramRectangle2 == null || !paramRectangle1.intersects(paramRectangle2) || isRectangleContainingRectangle(paramRectangle2, paramRectangle1))
      return new Rectangle[0]; 
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = null;
    Rectangle rectangle3 = null;
    Rectangle rectangle4 = null;
    Rectangle rectangle5 = null;
    byte b = 0;
    if (isRectangleContainingRectangle(paramRectangle1, paramRectangle2)) {
      rectangle1.x = paramRectangle1.x;
      rectangle1.y = paramRectangle1.y;
      rectangle1.width = paramRectangle2.x - paramRectangle1.x;
      rectangle1.height = paramRectangle1.height;
      if (rectangle1.width > 0 && rectangle1.height > 0) {
        rectangle2 = new Rectangle(rectangle1);
        b++;
      } 
      rectangle1.x = paramRectangle2.x;
      rectangle1.y = paramRectangle1.y;
      rectangle1.width = paramRectangle2.width;
      rectangle1.height = paramRectangle2.y - paramRectangle1.y;
      if (rectangle1.width > 0 && rectangle1.height > 0) {
        rectangle3 = new Rectangle(rectangle1);
        b++;
      } 
      rectangle1.x = paramRectangle2.x;
      paramRectangle2.y += paramRectangle2.height;
      rectangle1.width = paramRectangle2.width;
      rectangle1.height = paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height;
      if (rectangle1.width > 0 && rectangle1.height > 0) {
        rectangle4 = new Rectangle(rectangle1);
        b++;
      } 
      paramRectangle2.x += paramRectangle2.width;
      rectangle1.y = paramRectangle1.y;
      rectangle1.width = paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width;
      rectangle1.height = paramRectangle1.height;
      if (rectangle1.width > 0 && rectangle1.height > 0) {
        rectangle5 = new Rectangle(rectangle1);
        b++;
      } 
    } else if (paramRectangle2.x <= paramRectangle1.x && paramRectangle2.y <= paramRectangle1.y) {
      if (paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width) {
        rectangle1.x = paramRectangle1.x;
        paramRectangle2.y += paramRectangle2.height;
        rectangle1.width = paramRectangle1.width;
        rectangle1.height = paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height;
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = rectangle1;
          b++;
        } 
      } else if (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = rectangle1;
          b++;
        } 
      } else {
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle2.y + paramRectangle2.height - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } 
    } else if (paramRectangle2.x <= paramRectangle1.x && paramRectangle2.y + paramRectangle2.height >= paramRectangle1.y + paramRectangle1.height) {
      if (paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = rectangle1;
          b++;
        } 
      } else {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle2.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } 
    } else if (paramRectangle2.x <= paramRectangle1.x) {
      if (paramRectangle2.x + paramRectangle2.width >= paramRectangle1.x + paramRectangle1.width) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } else {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle2.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle4 = new Rectangle(rectangle1);
          b++;
        } 
      } 
    } else if (paramRectangle2.x <= paramRectangle1.x + paramRectangle1.width && paramRectangle2.x + paramRectangle2.width > paramRectangle1.x + paramRectangle1.width) {
      if (paramRectangle2.y <= paramRectangle1.y && paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = rectangle1;
          b++;
        } 
      } else if (paramRectangle2.y <= paramRectangle1.y) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle2.y + paramRectangle2.height - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } else if (paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } else {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle1.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y, paramRectangle2.x - paramRectangle1.x, paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle1.x, paramRectangle2.y + paramRectangle2.height, paramRectangle1.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle4 = new Rectangle(rectangle1);
          b++;
        } 
      } 
    } else if (paramRectangle2.x >= paramRectangle1.x && paramRectangle2.x + paramRectangle2.width <= paramRectangle1.x + paramRectangle1.width) {
      if (paramRectangle2.y <= paramRectangle1.y && paramRectangle2.y + paramRectangle2.height > paramRectangle1.y + paramRectangle1.height) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
      } else if (paramRectangle2.y <= paramRectangle1.y) {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x, paramRectangle2.y + paramRectangle2.height, paramRectangle2.width, paramRectangle1.y + paramRectangle1.height - paramRectangle2.y + paramRectangle2.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle4 = new Rectangle(rectangle1);
          b++;
        } 
      } else {
        rectangle1.setBounds(paramRectangle1.x, paramRectangle1.y, paramRectangle2.x - paramRectangle1.x, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle2 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x, paramRectangle1.y, paramRectangle2.width, paramRectangle2.y - paramRectangle1.y);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle3 = new Rectangle(rectangle1);
          b++;
        } 
        rectangle1.setBounds(paramRectangle2.x + paramRectangle2.width, paramRectangle1.y, paramRectangle1.x + paramRectangle1.width - paramRectangle2.x + paramRectangle2.width, paramRectangle1.height);
        if (rectangle1.width > 0 && rectangle1.height > 0) {
          rectangle4 = new Rectangle(rectangle1);
          b++;
        } 
      } 
    } 
    Rectangle[] arrayOfRectangle = new Rectangle[b];
    b = 0;
    if (rectangle2 != null)
      arrayOfRectangle[b++] = rectangle2; 
    if (rectangle3 != null)
      arrayOfRectangle[b++] = rectangle3; 
    if (rectangle4 != null)
      arrayOfRectangle[b++] = rectangle4; 
    if (rectangle5 != null)
      arrayOfRectangle[b++] = rectangle5; 
    return arrayOfRectangle;
  }
  
  public static boolean isLeftMouseButton(MouseEvent paramMouseEvent) { return ((paramMouseEvent.getModifiersEx() & 0x400) != 0 || paramMouseEvent.getButton() == 1); }
  
  public static boolean isMiddleMouseButton(MouseEvent paramMouseEvent) { return ((paramMouseEvent.getModifiersEx() & 0x800) != 0 || paramMouseEvent.getButton() == 2); }
  
  public static boolean isRightMouseButton(MouseEvent paramMouseEvent) { return ((paramMouseEvent.getModifiersEx() & 0x1000) != 0 || paramMouseEvent.getButton() == 3); }
  
  public static int computeStringWidth(FontMetrics paramFontMetrics, String paramString) { return SwingUtilities2.stringWidth(null, paramFontMetrics, paramString); }
  
  public static String layoutCompoundLabel(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5) {
    boolean bool = true;
    int i = paramInt2;
    int j = paramInt4;
    if (paramJComponent != null && !paramJComponent.getComponentOrientation().isLeftToRight())
      bool = false; 
    switch (paramInt2) {
      case 10:
        i = bool ? 2 : 4;
        break;
      case 11:
        i = bool ? 4 : 2;
        break;
    } 
    switch (paramInt4) {
      case 10:
        j = bool ? 2 : 4;
        break;
      case 11:
        j = bool ? 4 : 2;
        break;
    } 
    return layoutCompoundLabelImpl(paramJComponent, paramFontMetrics, paramString, paramIcon, paramInt1, i, paramInt3, j, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
  }
  
  public static String layoutCompoundLabel(FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5) { return layoutCompoundLabelImpl(null, paramFontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5); }
  
  private static String layoutCompoundLabelImpl(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5) {
    int i4;
    int i3;
    int k;
    if (paramIcon != null) {
      paramRectangle2.width = paramIcon.getIconWidth();
      paramRectangle2.height = paramIcon.getIconHeight();
    } else {
      paramRectangle2.width = paramRectangle2.height = 0;
    } 
    boolean bool = (paramString == null || paramString.equals("")) ? 1 : 0;
    int i = 0;
    int j = 0;
    if (bool) {
      paramRectangle3.width = paramRectangle3.height = 0;
      paramString = "";
      k = 0;
    } else {
      int i5;
      k = (paramIcon == null) ? 0 : paramInt5;
      if (paramInt4 == 0) {
        i5 = paramRectangle1.width;
      } else {
        i5 = paramRectangle1.width - paramRectangle2.width + k;
      } 
      View view = (paramJComponent != null) ? (View)paramJComponent.getClientProperty("html") : null;
      if (view != null) {
        paramRectangle3.width = Math.min(i5, (int)view.getPreferredSpan(0));
        paramRectangle3.height = (int)view.getPreferredSpan(1);
      } else {
        paramRectangle3.width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
        i = SwingUtilities2.getLeftSideBearing(paramJComponent, paramFontMetrics, paramString);
        if (i < 0)
          paramRectangle3.width -= i; 
        if (paramRectangle3.width > i5) {
          paramString = SwingUtilities2.clipString(paramJComponent, paramFontMetrics, paramString, i5);
          paramRectangle3.width = SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString);
        } 
        paramRectangle3.height = paramFontMetrics.getHeight();
      } 
    } 
    if (paramInt3 == 1) {
      if (paramInt4 != 0) {
        paramRectangle3.y = 0;
      } else {
        paramRectangle3.y = -(paramRectangle3.height + k);
      } 
    } else if (paramInt3 == 0) {
      paramRectangle3.y = paramRectangle2.height / 2 - paramRectangle3.height / 2;
    } else if (paramInt4 != 0) {
      paramRectangle3.y = paramRectangle2.height - paramRectangle3.height;
    } else {
      paramRectangle3.y = paramRectangle2.height + k;
    } 
    if (paramInt4 == 2) {
      paramRectangle3.x = -(paramRectangle3.width + k);
    } else if (paramInt4 == 0) {
      paramRectangle3.x = paramRectangle2.width / 2 - paramRectangle3.width / 2;
    } else {
      paramRectangle3.x = paramRectangle2.width + k;
    } 
    int m = Math.min(paramRectangle2.x, paramRectangle3.x);
    int n = Math.max(paramRectangle2.x + paramRectangle2.width, paramRectangle3.x + paramRectangle3.width) - m;
    int i1 = Math.min(paramRectangle2.y, paramRectangle3.y);
    int i2 = Math.max(paramRectangle2.y + paramRectangle2.height, paramRectangle3.y + paramRectangle3.height) - i1;
    if (paramInt1 == 1) {
      i4 = paramRectangle1.y - i1;
    } else if (paramInt1 == 0) {
      i4 = paramRectangle1.y + paramRectangle1.height / 2 - i1 + i2 / 2;
    } else {
      i4 = paramRectangle1.y + paramRectangle1.height - i1 + i2;
    } 
    if (paramInt2 == 2) {
      i3 = paramRectangle1.x - m;
    } else if (paramInt2 == 4) {
      i3 = paramRectangle1.x + paramRectangle1.width - m + n;
    } else {
      i3 = paramRectangle1.x + paramRectangle1.width / 2 - m + n / 2;
    } 
    paramRectangle3.x += i3;
    paramRectangle3.y += i4;
    paramRectangle2.x += i3;
    paramRectangle2.y += i4;
    if (i < 0) {
      paramRectangle3.x -= i;
      paramRectangle3.width += i;
    } 
    if (j)
      paramRectangle3.width -= j; 
    return paramString;
  }
  
  public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { getCellRendererPane(paramComponent, paramContainer).paintComponent(paramGraphics, paramComponent, paramContainer, paramInt1, paramInt2, paramInt3, paramInt4, false); }
  
  public static void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, Rectangle paramRectangle) { paintComponent(paramGraphics, paramComponent, paramContainer, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height); }
  
  private static CellRendererPane getCellRendererPane(Component paramComponent, Container paramContainer) {
    Container container = paramComponent.getParent();
    if (container instanceof CellRendererPane) {
      if (container.getParent() != paramContainer)
        paramContainer.add(container); 
    } else {
      container = new CellRendererPane();
      container.add(paramComponent);
      paramContainer.add(container);
    } 
    return (CellRendererPane)container;
  }
  
  public static void updateComponentTreeUI(Component paramComponent) {
    updateComponentTreeUI0(paramComponent);
    paramComponent.invalidate();
    paramComponent.validate();
    paramComponent.repaint();
  }
  
  private static void updateComponentTreeUI0(Component paramComponent) {
    if (paramComponent instanceof JComponent) {
      JComponent jComponent = (JComponent)paramComponent;
      jComponent.updateUI();
      JPopupMenu jPopupMenu = jComponent.getComponentPopupMenu();
      if (jPopupMenu != null)
        updateComponentTreeUI(jPopupMenu); 
    } 
    Component[] arrayOfComponent = null;
    if (paramComponent instanceof JMenu) {
      arrayOfComponent = ((JMenu)paramComponent).getMenuComponents();
    } else if (paramComponent instanceof Container) {
      arrayOfComponent = ((Container)paramComponent).getComponents();
    } 
    if (arrayOfComponent != null)
      for (Component component : arrayOfComponent)
        updateComponentTreeUI0(component);  
  }
  
  public static void invokeLater(Runnable paramRunnable) { EventQueue.invokeLater(paramRunnable); }
  
  public static void invokeAndWait(Runnable paramRunnable) { EventQueue.invokeAndWait(paramRunnable); }
  
  public static boolean isEventDispatchThread() { return EventQueue.isDispatchThread(); }
  
  public static int getAccessibleIndexInParent(Component paramComponent) { return paramComponent.getAccessibleContext().getAccessibleIndexInParent(); }
  
  public static Accessible getAccessibleAt(Component paramComponent, Point paramPoint) {
    if (paramComponent instanceof Container)
      return paramComponent.getAccessibleContext().getAccessibleComponent().getAccessibleAt(paramPoint); 
    if (paramComponent instanceof Accessible) {
      Accessible accessible = (Accessible)paramComponent;
      if (accessible != null) {
        AccessibleContext accessibleContext = accessible.getAccessibleContext();
        if (accessibleContext != null) {
          int i = accessibleContext.getAccessibleChildrenCount();
          for (byte b = 0; b < i; b++) {
            accessible = accessibleContext.getAccessibleChild(b);
            if (accessible != null) {
              accessibleContext = accessible.getAccessibleContext();
              if (accessibleContext != null) {
                AccessibleComponent accessibleComponent = accessibleContext.getAccessibleComponent();
                if (accessibleComponent != null && accessibleComponent.isShowing()) {
                  Point point1 = accessibleComponent.getLocation();
                  Point point2 = new Point(paramPoint.x - point1.x, paramPoint.y - point1.y);
                  if (accessibleComponent.contains(point2))
                    return accessible; 
                } 
              } 
            } 
          } 
        } 
      } 
      return (Accessible)paramComponent;
    } 
    return null;
  }
  
  public static AccessibleStateSet getAccessibleStateSet(Component paramComponent) { return paramComponent.getAccessibleContext().getAccessibleStateSet(); }
  
  public static int getAccessibleChildrenCount(Component paramComponent) { return paramComponent.getAccessibleContext().getAccessibleChildrenCount(); }
  
  public static Accessible getAccessibleChild(Component paramComponent, int paramInt) { return paramComponent.getAccessibleContext().getAccessibleChild(paramInt); }
  
  @Deprecated
  public static Component findFocusOwner(Component paramComponent) {
    Component component1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    for (Component component2 = component1; component2 != null; component2 = (component2 instanceof Window) ? null : component2.getParent()) {
      if (component2 == paramComponent)
        return component1; 
    } 
    return null;
  }
  
  public static JRootPane getRootPane(Component paramComponent) {
    if (paramComponent instanceof RootPaneContainer)
      return ((RootPaneContainer)paramComponent).getRootPane(); 
    while (paramComponent != null) {
      if (paramComponent instanceof JRootPane)
        return (JRootPane)paramComponent; 
      paramComponent = paramComponent.getParent();
    } 
    return null;
  }
  
  public static Component getRoot(Component paramComponent) {
    Component component1 = null;
    for (Component component2 = paramComponent; component2 != null; component2 = component2.getParent()) {
      if (component2 instanceof Window)
        return component2; 
      if (component2 instanceof java.applet.Applet)
        component1 = component2; 
    } 
    return component1;
  }
  
  static JComponent getPaintingOrigin(JComponent paramJComponent) {
    JComponent jComponent = paramJComponent;
    Container container;
    while (container = jComponent.getParent() instanceof JComponent) {
      JComponent jComponent1 = (JComponent)container;
      if (jComponent1.isPaintingOrigin())
        return jComponent1; 
    } 
    return null;
  }
  
  public static boolean processKeyBindings(KeyEvent paramKeyEvent) {
    if (paramKeyEvent != null) {
      if (paramKeyEvent.isConsumed())
        return false; 
      Component component = paramKeyEvent.getComponent();
      boolean bool = (paramKeyEvent.getID() == 401);
      if (!isValidKeyEventForKeyBindings(paramKeyEvent))
        return false; 
      while (component != null) {
        if (component instanceof JComponent)
          return ((JComponent)component).processKeyBindings(paramKeyEvent, bool); 
        if (component instanceof java.applet.Applet || component instanceof Window)
          return JComponent.processKeyBindingsForAllComponents(paramKeyEvent, (Container)component, bool); 
        component = component.getParent();
      } 
    } 
    return false;
  }
  
  static boolean isValidKeyEventForKeyBindings(KeyEvent paramKeyEvent) { return true; }
  
  public static boolean notifyAction(Action paramAction, KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, Object paramObject, int paramInt) {
    String str;
    boolean bool;
    if (paramAction == null)
      return false; 
    if (paramAction instanceof UIAction) {
      if (!((UIAction)paramAction).isEnabled(paramObject))
        return false; 
    } else if (!paramAction.isEnabled()) {
      return false;
    } 
    Object object = paramAction.getValue("ActionCommandKey");
    if (object == null && paramAction instanceof JComponent.ActionStandin) {
      bool = true;
    } else {
      bool = false;
    } 
    if (object != null) {
      str = object.toString();
    } else if (!bool && paramKeyEvent.getKeyChar() != Character.MAX_VALUE) {
      str = String.valueOf(paramKeyEvent.getKeyChar());
    } else {
      str = null;
    } 
    paramAction.actionPerformed(new ActionEvent(paramObject, 1001, str, paramKeyEvent.getWhen(), paramInt));
    return true;
  }
  
  public static void replaceUIInputMap(JComponent paramJComponent, int paramInt, InputMap paramInputMap) {
    for (InputMap inputMap = paramJComponent.getInputMap(paramInt, (paramInputMap != null)); inputMap != null; inputMap = inputMap1) {
      InputMap inputMap1 = inputMap.getParent();
      if (inputMap1 == null || inputMap1 instanceof javax.swing.plaf.UIResource) {
        inputMap.setParent(paramInputMap);
        return;
      } 
    } 
  }
  
  public static void replaceUIActionMap(JComponent paramJComponent, ActionMap paramActionMap) {
    for (ActionMap actionMap = paramJComponent.getActionMap((paramActionMap != null)); actionMap != null; actionMap = actionMap1) {
      ActionMap actionMap1 = actionMap.getParent();
      if (actionMap1 == null || actionMap1 instanceof javax.swing.plaf.UIResource) {
        actionMap.setParent(paramActionMap);
        return;
      } 
    } 
  }
  
  public static InputMap getUIInputMap(JComponent paramJComponent, int paramInt) {
    for (InputMap inputMap = paramJComponent.getInputMap(paramInt, false); inputMap != null; inputMap = inputMap1) {
      InputMap inputMap1 = inputMap.getParent();
      if (inputMap1 instanceof javax.swing.plaf.UIResource)
        return inputMap1; 
    } 
    return null;
  }
  
  public static ActionMap getUIActionMap(JComponent paramJComponent) {
    for (ActionMap actionMap = paramJComponent.getActionMap(false); actionMap != null; actionMap = actionMap1) {
      ActionMap actionMap1 = actionMap.getParent();
      if (actionMap1 instanceof javax.swing.plaf.UIResource)
        return actionMap1; 
    } 
    return null;
  }
  
  static Frame getSharedOwnerFrame() throws HeadlessException {
    Frame frame = (Frame)appContextGet(sharedOwnerFrameKey);
    if (frame == null) {
      frame = new SharedOwnerFrame();
      appContextPut(sharedOwnerFrameKey, frame);
    } 
    return frame;
  }
  
  static WindowListener getSharedOwnerFrameShutdownListener() throws HeadlessException {
    Frame frame = getSharedOwnerFrame();
    return (WindowListener)frame;
  }
  
  static Object appContextGet(Object paramObject) { return AppContext.getAppContext().get(paramObject); }
  
  static void appContextPut(Object paramObject1, Object paramObject2) { AppContext.getAppContext().put(paramObject1, paramObject2); }
  
  static void appContextRemove(Object paramObject) { AppContext.getAppContext().remove(paramObject); }
  
  static Class<?> loadSystemClass(String paramString) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    return Class.forName(paramString, true, Thread.currentThread().getContextClassLoader());
  }
  
  static boolean isLeftToRight(Component paramComponent) { return paramComponent.getComponentOrientation().isLeftToRight(); }
  
  private SwingUtilities() { throw new Error("SwingUtilities is just a container for static methods"); }
  
  static boolean doesIconReferenceImage(Icon paramIcon, Image paramImage) {
    Image image = (paramIcon != null && paramIcon instanceof ImageIcon) ? ((ImageIcon)paramIcon).getImage() : null;
    return (image == paramImage);
  }
  
  static int findDisplayedMnemonicIndex(String paramString, int paramInt) {
    if (paramString == null || paramInt == 0)
      return -1; 
    char c1 = Character.toUpperCase((char)paramInt);
    char c2 = Character.toLowerCase((char)paramInt);
    int i = paramString.indexOf(c1);
    int j = paramString.indexOf(c2);
    return (i == -1) ? j : ((j == -1) ? i : ((j < i) ? j : i));
  }
  
  public static Rectangle calculateInnerArea(JComponent paramJComponent, Rectangle paramRectangle) {
    if (paramJComponent == null)
      return null; 
    Rectangle rectangle = paramRectangle;
    Insets insets = paramJComponent.getInsets();
    if (rectangle == null)
      rectangle = new Rectangle(); 
    rectangle.x = insets.left;
    rectangle.y = insets.top;
    rectangle.width = paramJComponent.getWidth() - insets.left - insets.right;
    rectangle.height = paramJComponent.getHeight() - insets.top - insets.bottom;
    return rectangle;
  }
  
  static void updateRendererOrEditorUI(Object paramObject) {
    if (paramObject == null)
      return; 
    Component component = null;
    if (paramObject instanceof Component)
      component = (Component)paramObject; 
    if (paramObject instanceof DefaultCellEditor)
      component = ((DefaultCellEditor)paramObject).getComponent(); 
    if (component != null)
      updateComponentTreeUI(component); 
  }
  
  public static Container getUnwrappedParent(Component paramComponent) {
    Container container;
    for (container = paramComponent.getParent(); container instanceof JLayer; container = container.getParent());
    return container;
  }
  
  public static Component getUnwrappedView(JViewport paramJViewport) {
    Component component;
    for (component = paramJViewport.getView(); component instanceof JLayer; component = ((JLayer)component).getView());
    return component;
  }
  
  static Container getValidateRoot(Container paramContainer, boolean paramBoolean) {
    Container container = null;
    while (paramContainer != null) {
      if (!paramContainer.isDisplayable() || paramContainer instanceof CellRendererPane)
        return null; 
      if (paramContainer.isValidateRoot()) {
        container = paramContainer;
        break;
      } 
      paramContainer = paramContainer.getParent();
    } 
    if (container == null)
      return null; 
    while (paramContainer != null) {
      if (!paramContainer.isDisplayable() || (paramBoolean && !paramContainer.isVisible()))
        return null; 
      if (paramContainer instanceof Window || paramContainer instanceof java.applet.Applet)
        return container; 
      paramContainer = paramContainer.getParent();
    } 
    return null;
  }
  
  static class SharedOwnerFrame extends Frame implements WindowListener {
    public void addNotify() {
      super.addNotify();
      installListeners();
    }
    
    void installListeners() {
      Window[] arrayOfWindow = getOwnedWindows();
      for (Window window : arrayOfWindow) {
        if (window != null) {
          window.removeWindowListener(this);
          window.addWindowListener(this);
        } 
      } 
    }
    
    public void windowClosed(WindowEvent param1WindowEvent) {
      synchronized (getTreeLock()) {
        Window[] arrayOfWindow = getOwnedWindows();
        for (Window window : arrayOfWindow) {
          if (window != null) {
            if (window.isDisplayable())
              return; 
            window.removeWindowListener(this);
          } 
        } 
        dispose();
      } 
    }
    
    public void windowOpened(WindowEvent param1WindowEvent) {}
    
    public void windowClosing(WindowEvent param1WindowEvent) {}
    
    public void windowIconified(WindowEvent param1WindowEvent) {}
    
    public void windowDeiconified(WindowEvent param1WindowEvent) {}
    
    public void windowActivated(WindowEvent param1WindowEvent) {}
    
    public void windowDeactivated(WindowEvent param1WindowEvent) {}
    
    public void show() {}
    
    public void dispose() {
      try {
        getToolkit().getSystemEventQueue();
        super.dispose();
      } catch (Exception exception) {}
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SwingUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */