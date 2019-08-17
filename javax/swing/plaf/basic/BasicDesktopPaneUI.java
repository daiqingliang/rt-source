package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SortingFocusTraversalPolicy;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopPaneUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicDesktopPaneUI extends DesktopPaneUI {
  private static final Actions SHARED_ACTION = new Actions();
  
  private Handler handler;
  
  private PropertyChangeListener pcl;
  
  protected JDesktopPane desktop;
  
  protected DesktopManager desktopManager;
  
  @Deprecated
  protected KeyStroke minimizeKey;
  
  @Deprecated
  protected KeyStroke maximizeKey;
  
  @Deprecated
  protected KeyStroke closeKey;
  
  @Deprecated
  protected KeyStroke navigateKey;
  
  @Deprecated
  protected KeyStroke navigateKey2;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicDesktopPaneUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.desktop = (JDesktopPane)paramJComponent;
    installDefaults();
    installDesktopManager();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDesktopManager();
    uninstallDefaults();
    this.desktop = null;
    this.handler = null;
  }
  
  protected void installDefaults() {
    if (this.desktop.getBackground() == null || this.desktop.getBackground() instanceof UIResource)
      this.desktop.setBackground(UIManager.getColor("Desktop.background")); 
    LookAndFeel.installProperty(this.desktop, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults() {}
  
  protected void installListeners() {
    this.pcl = createPropertyChangeListener();
    this.desktop.addPropertyChangeListener(this.pcl);
  }
  
  protected void uninstallListeners() {
    this.desktop.removePropertyChangeListener(this.pcl);
    this.pcl = null;
  }
  
  protected void installDesktopManager() {
    this.desktopManager = this.desktop.getDesktopManager();
    if (this.desktopManager == null) {
      this.desktopManager = new BasicDesktopManager(null);
      this.desktop.setDesktopManager(this.desktopManager);
    } 
  }
  
  protected void uninstallDesktopManager() {
    if (this.desktop.getDesktopManager() instanceof UIResource)
      this.desktop.setDesktopManager(null); 
    this.desktopManager = null;
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(2);
    if (inputMap != null)
      SwingUtilities.replaceUIInputMap(this.desktop, 2, inputMap); 
    inputMap = getInputMap(1);
    if (inputMap != null)
      SwingUtilities.replaceUIInputMap(this.desktop, 1, inputMap); 
    LazyActionMap.installLazyActionMap(this.desktop, BasicDesktopPaneUI.class, "DesktopPane.actionMap");
    registerKeyboardActions();
  }
  
  protected void registerKeyboardActions() {}
  
  protected void unregisterKeyboardActions() {}
  
  InputMap getInputMap(int paramInt) { return (paramInt == 2) ? createInputMap(paramInt) : ((paramInt == 1) ? (InputMap)DefaultLookup.get(this.desktop, this, "Desktop.ancestorInputMap") : null); }
  
  InputMap createInputMap(int paramInt) {
    if (paramInt == 2) {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(this.desktop, this, "Desktop.windowBindings");
      if (arrayOfObject != null)
        return LookAndFeel.makeComponentInputMap(this.desktop, arrayOfObject); 
    } 
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions(RESTORE));
    paramLazyActionMap.put(new Actions(CLOSE));
    paramLazyActionMap.put(new Actions(MOVE));
    paramLazyActionMap.put(new Actions(RESIZE));
    paramLazyActionMap.put(new Actions(LEFT));
    paramLazyActionMap.put(new Actions(SHRINK_LEFT));
    paramLazyActionMap.put(new Actions(RIGHT));
    paramLazyActionMap.put(new Actions(SHRINK_RIGHT));
    paramLazyActionMap.put(new Actions(UP));
    paramLazyActionMap.put(new Actions(SHRINK_UP));
    paramLazyActionMap.put(new Actions(DOWN));
    paramLazyActionMap.put(new Actions(SHRINK_DOWN));
    paramLazyActionMap.put(new Actions(ESCAPE));
    paramLazyActionMap.put(new Actions(MINIMIZE));
    paramLazyActionMap.put(new Actions(MAXIMIZE));
    paramLazyActionMap.put(new Actions(NEXT_FRAME));
    paramLazyActionMap.put(new Actions(PREVIOUS_FRAME));
    paramLazyActionMap.put(new Actions(NAVIGATE_NEXT));
    paramLazyActionMap.put(new Actions(NAVIGATE_PREVIOUS));
  }
  
  protected void uninstallKeyboardActions() {
    unregisterKeyboardActions();
    SwingUtilities.replaceUIInputMap(this.desktop, 2, null);
    SwingUtilities.replaceUIInputMap(this.desktop, 1, null);
    SwingUtilities.replaceUIActionMap(this.desktop, null);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return null; }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return new Dimension(0, 0); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(2147483647, 2147483647); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  private static class Actions extends UIAction {
    private static String CLOSE = "close";
    
    private static String ESCAPE = "escape";
    
    private static String MAXIMIZE = "maximize";
    
    private static String MINIMIZE = "minimize";
    
    private static String MOVE = "move";
    
    private static String RESIZE = "resize";
    
    private static String RESTORE = "restore";
    
    private static String LEFT = "left";
    
    private static String RIGHT = "right";
    
    private static String UP = "up";
    
    private static String DOWN = "down";
    
    private static String SHRINK_LEFT = "shrinkLeft";
    
    private static String SHRINK_RIGHT = "shrinkRight";
    
    private static String SHRINK_UP = "shrinkUp";
    
    private static String SHRINK_DOWN = "shrinkDown";
    
    private static String NEXT_FRAME = "selectNextFrame";
    
    private static String PREVIOUS_FRAME = "selectPreviousFrame";
    
    private static String NAVIGATE_NEXT = "navigateNext";
    
    private static String NAVIGATE_PREVIOUS = "navigatePrevious";
    
    private final int MOVE_RESIZE_INCREMENT = 10;
    
    private static boolean moving = false;
    
    private static boolean resizing = false;
    
    private static JInternalFrame sourceFrame = null;
    
    private static Component focusOwner = null;
    
    Actions() { super(null); }
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      String str = getName();
      if (CLOSE == str || MAXIMIZE == str || MINIMIZE == str || RESTORE == str) {
        setState(jDesktopPane, str);
      } else if (ESCAPE == str) {
        if (sourceFrame == jDesktopPane.getSelectedFrame() && focusOwner != null)
          focusOwner.requestFocus(); 
        moving = false;
        resizing = false;
        sourceFrame = null;
        focusOwner = null;
      } else if (MOVE == str || RESIZE == str) {
        sourceFrame = jDesktopPane.getSelectedFrame();
        if (sourceFrame == null)
          return; 
        moving = (str == MOVE);
        resizing = (str == RESIZE);
        focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (!SwingUtilities.isDescendingFrom(focusOwner, sourceFrame))
          focusOwner = null; 
        sourceFrame.requestFocus();
      } else if (LEFT == str || RIGHT == str || UP == str || DOWN == str || SHRINK_RIGHT == str || SHRINK_LEFT == str || SHRINK_UP == str || SHRINK_DOWN == str) {
        JInternalFrame jInternalFrame = jDesktopPane.getSelectedFrame();
        if (sourceFrame == null || jInternalFrame != sourceFrame || KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() != sourceFrame)
          return; 
        Insets insets = UIManager.getInsets("Desktop.minOnScreenInsets");
        Dimension dimension1 = jInternalFrame.getSize();
        Dimension dimension2 = jInternalFrame.getMinimumSize();
        int i = jDesktopPane.getWidth();
        int j = jDesktopPane.getHeight();
        Point point = jInternalFrame.getLocation();
        if (LEFT == str) {
          if (moving) {
            jInternalFrame.setLocation((point.x + dimension1.width - 10 < insets.right) ? (-dimension1.width + insets.right) : (point.x - 10), point.y);
          } else if (resizing) {
            jInternalFrame.setLocation(point.x - 10, point.y);
            jInternalFrame.setSize(dimension1.width + 10, dimension1.height);
          } 
        } else if (RIGHT == str) {
          if (moving) {
            jInternalFrame.setLocation((point.x + 10 > i - insets.left) ? (i - insets.left) : (point.x + 10), point.y);
          } else if (resizing) {
            jInternalFrame.setSize(dimension1.width + 10, dimension1.height);
          } 
        } else if (UP == str) {
          if (moving) {
            jInternalFrame.setLocation(point.x, (point.y + dimension1.height - 10 < insets.bottom) ? (-dimension1.height + insets.bottom) : (point.y - 10));
          } else if (resizing) {
            jInternalFrame.setLocation(point.x, point.y - 10);
            jInternalFrame.setSize(dimension1.width, dimension1.height + 10);
          } 
        } else if (DOWN == str) {
          if (moving) {
            jInternalFrame.setLocation(point.x, (point.y + 10 > j - insets.top) ? (j - insets.top) : (point.y + 10));
          } else if (resizing) {
            jInternalFrame.setSize(dimension1.width, dimension1.height + 10);
          } 
        } else if (SHRINK_LEFT == str && resizing) {
          int k;
          if (dimension2.width < dimension1.width - 10) {
            k = 10;
          } else {
            k = dimension1.width - dimension2.width;
          } 
          if (point.x + dimension1.width - k < insets.left)
            k = point.x + dimension1.width - insets.left; 
          jInternalFrame.setSize(dimension1.width - k, dimension1.height);
        } else if (SHRINK_RIGHT == str && resizing) {
          int k;
          if (dimension2.width < dimension1.width - 10) {
            k = 10;
          } else {
            k = dimension1.width - dimension2.width;
          } 
          if (point.x + k > i - insets.right)
            k = i - insets.right - point.x; 
          jInternalFrame.setLocation(point.x + k, point.y);
          jInternalFrame.setSize(dimension1.width - k, dimension1.height);
        } else if (SHRINK_UP == str && resizing) {
          int k;
          if (dimension2.height < dimension1.height - 10) {
            k = 10;
          } else {
            k = dimension1.height - dimension2.height;
          } 
          if (point.y + dimension1.height - k < insets.bottom)
            k = point.y + dimension1.height - insets.bottom; 
          jInternalFrame.setSize(dimension1.width, dimension1.height - k);
        } else if (SHRINK_DOWN == str && resizing) {
          int k;
          if (dimension2.height < dimension1.height - 10) {
            k = 10;
          } else {
            k = dimension1.height - dimension2.height;
          } 
          if (point.y + k > j - insets.top)
            k = j - insets.top - point.y; 
          jInternalFrame.setLocation(point.x, point.y + k);
          jInternalFrame.setSize(dimension1.width, dimension1.height - k);
        } 
      } else if (NEXT_FRAME == str || PREVIOUS_FRAME == str) {
        jDesktopPane.selectFrame((str == NEXT_FRAME));
      } else if (NAVIGATE_NEXT == str || NAVIGATE_PREVIOUS == str) {
        boolean bool = true;
        if (NAVIGATE_PREVIOUS == str)
          bool = false; 
        Container container = jDesktopPane.getFocusCycleRootAncestor();
        if (container != null) {
          FocusTraversalPolicy focusTraversalPolicy = container.getFocusTraversalPolicy();
          if (focusTraversalPolicy != null && focusTraversalPolicy instanceof SortingFocusTraversalPolicy) {
            sortingFocusTraversalPolicy = (SortingFocusTraversalPolicy)focusTraversalPolicy;
            bool1 = sortingFocusTraversalPolicy.getImplicitDownCycleTraversal();
            try {
              sortingFocusTraversalPolicy.setImplicitDownCycleTraversal(false);
              if (bool) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(jDesktopPane);
              } else {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(jDesktopPane);
              } 
            } finally {
              sortingFocusTraversalPolicy.setImplicitDownCycleTraversal(bool1);
            } 
          } 
        } 
      } 
    }
    
    private void setState(JDesktopPane param1JDesktopPane, String param1String) {
      if (param1String == CLOSE) {
        JInternalFrame jInternalFrame = param1JDesktopPane.getSelectedFrame();
        if (jInternalFrame == null)
          return; 
        jInternalFrame.doDefaultCloseAction();
      } else if (param1String == MAXIMIZE) {
        JInternalFrame jInternalFrame = param1JDesktopPane.getSelectedFrame();
        if (jInternalFrame == null)
          return; 
        if (!jInternalFrame.isMaximum())
          if (jInternalFrame.isIcon()) {
            try {
              jInternalFrame.setIcon(false);
              jInternalFrame.setMaximum(true);
            } catch (PropertyVetoException propertyVetoException) {}
          } else {
            try {
              jInternalFrame.setMaximum(true);
            } catch (PropertyVetoException propertyVetoException) {}
          }  
      } else if (param1String == MINIMIZE) {
        JInternalFrame jInternalFrame = param1JDesktopPane.getSelectedFrame();
        if (jInternalFrame == null)
          return; 
        if (!jInternalFrame.isIcon())
          try {
            jInternalFrame.setIcon(true);
          } catch (PropertyVetoException propertyVetoException) {} 
      } else if (param1String == RESTORE) {
        JInternalFrame jInternalFrame = param1JDesktopPane.getSelectedFrame();
        if (jInternalFrame == null)
          return; 
        try {
          if (jInternalFrame.isIcon()) {
            jInternalFrame.setIcon(false);
          } else if (jInternalFrame.isMaximum()) {
            jInternalFrame.setMaximum(false);
          } 
          jInternalFrame.setSelected(true);
        } catch (PropertyVetoException propertyVetoException) {}
      } 
    }
    
    public boolean isEnabled(Object param1Object) {
      if (param1Object instanceof JDesktopPane) {
        JDesktopPane jDesktopPane = (JDesktopPane)param1Object;
        String str = getName();
        if (str == NEXT_FRAME || str == PREVIOUS_FRAME)
          return true; 
        JInternalFrame jInternalFrame = jDesktopPane.getSelectedFrame();
        return (jInternalFrame == null) ? false : ((str == CLOSE) ? jInternalFrame.isClosable() : ((str == MINIMIZE) ? jInternalFrame.isIconifiable() : ((str == MAXIMIZE) ? jInternalFrame.isMaximizable() : 1)));
      } 
      return false;
    }
  }
  
  private class BasicDesktopManager extends DefaultDesktopManager implements UIResource {
    private BasicDesktopManager() {}
  }
  
  protected class CloseAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      SHARED_ACTION.setState(jDesktopPane, CLOSE);
    }
    
    public boolean isEnabled() {
      JInternalFrame jInternalFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
      return (jInternalFrame != null) ? jInternalFrame.isClosable() : 0;
    }
  }
  
  private class Handler implements PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("desktopManager" == str)
        BasicDesktopPaneUI.this.installDesktopManager(); 
    }
  }
  
  protected class MaximizeAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      SHARED_ACTION.setState(jDesktopPane, MAXIMIZE);
    }
    
    public boolean isEnabled() {
      JInternalFrame jInternalFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
      return (jInternalFrame != null) ? jInternalFrame.isMaximizable() : 0;
    }
  }
  
  protected class MinimizeAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      SHARED_ACTION.setState(jDesktopPane, MINIMIZE);
    }
    
    public boolean isEnabled() {
      JInternalFrame jInternalFrame = BasicDesktopPaneUI.this.desktop.getSelectedFrame();
      return (jInternalFrame != null) ? jInternalFrame.isIconifiable() : 0;
    }
  }
  
  protected class NavigateAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      jDesktopPane.selectFrame(true);
    }
    
    public boolean isEnabled() { return true; }
  }
  
  protected class OpenAction extends AbstractAction {
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JDesktopPane jDesktopPane = (JDesktopPane)param1ActionEvent.getSource();
      SHARED_ACTION.setState(jDesktopPane, RESTORE);
    }
    
    public boolean isEnabled() { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */