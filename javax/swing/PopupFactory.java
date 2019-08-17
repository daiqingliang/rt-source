package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.awt.EmbeddedFrame;
import sun.awt.OSInfo;

public class PopupFactory {
  private static final Object SharedInstanceKey = new StringBuffer("PopupFactory.SharedInstanceKey");
  
  private static final int MAX_CACHE_SIZE = 5;
  
  static final int LIGHT_WEIGHT_POPUP = 0;
  
  static final int MEDIUM_WEIGHT_POPUP = 1;
  
  static final int HEAVY_WEIGHT_POPUP = 2;
  
  private int popupType = 0;
  
  public static void setSharedInstance(PopupFactory paramPopupFactory) {
    if (paramPopupFactory == null)
      throw new IllegalArgumentException("PopupFactory can not be null"); 
    SwingUtilities.appContextPut(SharedInstanceKey, paramPopupFactory);
  }
  
  public static PopupFactory getSharedInstance() {
    PopupFactory popupFactory = (PopupFactory)SwingUtilities.appContextGet(SharedInstanceKey);
    if (popupFactory == null)
      (popupFactory = new PopupFactory()).setSharedInstance(popupFactory); 
    return popupFactory;
  }
  
  void setPopupType(int paramInt) { this.popupType = paramInt; }
  
  int getPopupType() { return this.popupType; }
  
  public Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) throws IllegalArgumentException {
    if (paramComponent2 == null)
      throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents"); 
    int i = getPopupType(paramComponent1, paramComponent2, paramInt1, paramInt2);
    Popup popup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, i);
    if (popup == null)
      popup = getPopup(paramComponent1, paramComponent2, paramInt1, paramInt2, 2); 
    return popup;
  }
  
  private int getPopupType(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) {
    int i = getPopupType();
    if (paramComponent1 == null || invokerInHeavyWeightPopup(paramComponent1)) {
      i = 2;
    } else if (i == 0 && !(paramComponent2 instanceof JToolTip) && !(paramComponent2 instanceof JPopupMenu)) {
      i = 1;
    } 
    for (Component component = paramComponent1; component != null; component = component.getParent()) {
      if (component instanceof JComponent && ((JComponent)component).getClientProperty(ClientPropertyKey.PopupFactory_FORCE_HEAVYWEIGHT_POPUP) == Boolean.TRUE) {
        i = 2;
        break;
      } 
    } 
    return i;
  }
  
  private Popup getPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2, int paramInt3) {
    Popup popup;
    if (GraphicsEnvironment.isHeadless())
      return getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2); 
    switch (paramInt3) {
      case 0:
        return getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
      case 1:
        return getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
      case 2:
        popup = getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2);
        if (AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.MACOSX && paramComponent1 != null && EmbeddedFrame.getAppletIfAncestorOf(paramComponent1) != null)
          ((HeavyWeightPopup)popup).setCacheEnabled(false); 
        return popup;
    } 
    return null;
  }
  
  private Popup getHeadlessPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) throws IllegalArgumentException { return HeadlessPopup.getHeadlessPopup(paramComponent1, paramComponent2, paramInt1, paramInt2); }
  
  private Popup getLightWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) throws IllegalArgumentException { return LightWeightPopup.getLightWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2); }
  
  private Popup getMediumWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) throws IllegalArgumentException { return MediumWeightPopup.getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2); }
  
  private Popup getHeavyWeightPopup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2) throws IllegalArgumentException { return GraphicsEnvironment.isHeadless() ? getMediumWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2) : HeavyWeightPopup.getHeavyWeightPopup(paramComponent1, paramComponent2, paramInt1, paramInt2); }
  
  private boolean invokerInHeavyWeightPopup(Component paramComponent) {
    if (paramComponent != null)
      for (Container container = paramComponent.getParent(); container != null; container = container.getParent()) {
        if (container instanceof Popup.HeavyWeightWindow)
          return true; 
      }  
    return false;
  }
  
  private static class ContainerPopup extends Popup {
    Component owner;
    
    int x;
    
    int y;
    
    private ContainerPopup() {}
    
    public void hide() {
      Component component = getComponent();
      if (component != null) {
        Container container = component.getParent();
        if (container != null) {
          Rectangle rectangle = component.getBounds();
          container.remove(component);
          container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
      } 
      this.owner = null;
    }
    
    public void pack() {
      Component component = getComponent();
      if (component != null)
        component.setSize(component.getPreferredSize()); 
    }
    
    void reset(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) {
      if (param1Component1 instanceof JFrame || param1Component1 instanceof JDialog || param1Component1 instanceof JWindow)
        param1Component1 = ((RootPaneContainer)param1Component1).getLayeredPane(); 
      super.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      this.x = param1Int1;
      this.y = param1Int2;
      this.owner = param1Component1;
    }
    
    boolean overlappedByOwnedWindow() {
      Component component = getComponent();
      if (this.owner != null && component != null) {
        Window window = SwingUtilities.getWindowAncestor(this.owner);
        if (window == null)
          return false; 
        Window[] arrayOfWindow = window.getOwnedWindows();
        if (arrayOfWindow != null) {
          Rectangle rectangle = component.getBounds();
          for (Window window1 : arrayOfWindow) {
            if (window1.isVisible() && rectangle.intersects(window1.getBounds()))
              return true; 
          } 
        } 
      } 
      return false;
    }
    
    boolean fitsOnScreen() {
      boolean bool = false;
      Component component = getComponent();
      if (this.owner != null && component != null) {
        int i = component.getWidth();
        int j = component.getHeight();
        Container container = (Container)SwingUtilities.getRoot(this.owner);
        if (container instanceof JFrame || container instanceof JDialog || container instanceof JWindow) {
          Rectangle rectangle = container.getBounds();
          Insets insets = container.getInsets();
          rectangle.x += insets.left;
          rectangle.y += insets.top;
          rectangle.width -= insets.left + insets.right;
          rectangle.height -= insets.top + insets.bottom;
          if (JPopupMenu.canPopupOverlapTaskBar()) {
            GraphicsConfiguration graphicsConfiguration = container.getGraphicsConfiguration();
            Rectangle rectangle1 = getContainerPopupArea(graphicsConfiguration);
            bool = rectangle.intersection(rectangle1).contains(this.x, this.y, i, j);
          } else {
            bool = rectangle.contains(this.x, this.y, i, j);
          } 
        } else if (container instanceof JApplet) {
          Rectangle rectangle = container.getBounds();
          Point point = container.getLocationOnScreen();
          rectangle.x = point.x;
          rectangle.y = point.y;
          bool = rectangle.contains(this.x, this.y, i, j);
        } 
      } 
      return bool;
    }
    
    Rectangle getContainerPopupArea(GraphicsConfiguration param1GraphicsConfiguration) {
      Insets insets;
      Rectangle rectangle;
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (param1GraphicsConfiguration != null) {
        rectangle = param1GraphicsConfiguration.getBounds();
        insets = toolkit.getScreenInsets(param1GraphicsConfiguration);
      } else {
        rectangle = new Rectangle(toolkit.getScreenSize());
        insets = new Insets(0, 0, 0, 0);
      } 
      rectangle.x += insets.left;
      rectangle.y += insets.top;
      rectangle.width -= insets.left + insets.right;
      rectangle.height -= insets.top + insets.bottom;
      return rectangle;
    }
  }
  
  private static class HeadlessPopup extends ContainerPopup {
    private HeadlessPopup() { super(null); }
    
    static Popup getHeadlessPopup(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) throws IllegalArgumentException {
      HeadlessPopup headlessPopup = new HeadlessPopup();
      headlessPopup.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      return headlessPopup;
    }
    
    Component createComponent(Component param1Component) { return new Panel(new BorderLayout()); }
    
    public void show() {}
    
    public void hide() {}
  }
  
  private static class HeavyWeightPopup extends Popup {
    private static final Object heavyWeightPopupCacheKey = new StringBuffer("PopupFactory.heavyWeightPopupCache");
    
    static Popup getHeavyWeightPopup(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) throws IllegalArgumentException {
      Window window = (param1Component1 != null) ? SwingUtilities.getWindowAncestor(param1Component1) : null;
      HeavyWeightPopup heavyWeightPopup = null;
      if (window != null)
        heavyWeightPopup = getRecycledHeavyWeightPopup(window); 
      boolean bool = false;
      if (param1Component2 != null && param1Component2.isFocusable() && param1Component2 instanceof JPopupMenu) {
        JPopupMenu jPopupMenu = (JPopupMenu)param1Component2;
        Component[] arrayOfComponent = jPopupMenu.getComponents();
        for (Component component : arrayOfComponent) {
          if (!(component instanceof MenuElement) && !(component instanceof JSeparator)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (heavyWeightPopup == null || ((JWindow)heavyWeightPopup.getComponent()).getFocusableWindowState() != bool) {
        if (heavyWeightPopup != null)
          heavyWeightPopup._dispose(); 
        heavyWeightPopup = new HeavyWeightPopup();
      } 
      heavyWeightPopup.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      if (bool) {
        JWindow jWindow = (JWindow)heavyWeightPopup.getComponent();
        jWindow.setFocusableWindowState(true);
        jWindow.setName("###focusableSwingPopup###");
      } 
      return heavyWeightPopup;
    }
    
    private static HeavyWeightPopup getRecycledHeavyWeightPopup(Window param1Window) {
      synchronized (HeavyWeightPopup.class) {
        List list;
        Map map = getHeavyWeightPopupCache();
        if (map.containsKey(param1Window)) {
          list = (List)map.get(param1Window);
        } else {
          return null;
        } 
        if (list.size() > 0) {
          HeavyWeightPopup heavyWeightPopup = (HeavyWeightPopup)list.get(0);
          list.remove(0);
          return heavyWeightPopup;
        } 
        return null;
      } 
    }
    
    private static Map<Window, List<HeavyWeightPopup>> getHeavyWeightPopupCache() {
      synchronized (HeavyWeightPopup.class) {
        Map map = (Map)SwingUtilities.appContextGet(heavyWeightPopupCacheKey);
        if (map == null) {
          map = new HashMap(2);
          SwingUtilities.appContextPut(heavyWeightPopupCacheKey, map);
        } 
        return map;
      } 
    }
    
    private static void recycleHeavyWeightPopup(HeavyWeightPopup param1HeavyWeightPopup) {
      synchronized (HeavyWeightPopup.class) {
        ArrayList arrayList;
        Window window = SwingUtilities.getWindowAncestor(param1HeavyWeightPopup.getComponent());
        Map map = getHeavyWeightPopupCache();
        if (window instanceof Popup.DefaultFrame || !window.isVisible()) {
          param1HeavyWeightPopup._dispose();
          return;
        } 
        if (map.containsKey(window)) {
          arrayList = (List)map.get(window);
        } else {
          arrayList = new ArrayList();
          map.put(window, arrayList);
          final Window w = window;
          window1.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent param2WindowEvent) {
                  List list;
                  synchronized (PopupFactory.HeavyWeightPopup.class) {
                    Map map = PopupFactory.HeavyWeightPopup.getHeavyWeightPopupCache();
                    list = (List)map.remove(w);
                  } 
                  if (list != null)
                    for (int i = list.size() - 1; i >= 0; i--)
                      ((PopupFactory.HeavyWeightPopup)list.get(i))._dispose();  
                }
              });
        } 
        if (arrayList.size() < 5) {
          arrayList.add(param1HeavyWeightPopup);
        } else {
          param1HeavyWeightPopup._dispose();
        } 
      } 
    }
    
    void setCacheEnabled(boolean param1Boolean) { this.isCacheEnabled = param1Boolean; }
    
    public void hide() {
      super.hide();
      if (this.isCacheEnabled) {
        recycleHeavyWeightPopup(this);
      } else {
        _dispose();
      } 
    }
    
    void dispose() {}
    
    void _dispose() { super.dispose(); }
  }
  
  private static class LightWeightPopup extends ContainerPopup {
    private static final Object lightWeightPopupCacheKey = new StringBuffer("PopupFactory.lightPopupCache");
    
    private LightWeightPopup() { super(null); }
    
    static Popup getLightWeightPopup(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) throws IllegalArgumentException {
      LightWeightPopup lightWeightPopup = getRecycledLightWeightPopup();
      if (lightWeightPopup == null)
        lightWeightPopup = new LightWeightPopup(); 
      lightWeightPopup.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      if (!lightWeightPopup.fitsOnScreen() || lightWeightPopup.overlappedByOwnedWindow()) {
        lightWeightPopup.hide();
        return null;
      } 
      return lightWeightPopup;
    }
    
    private static List<LightWeightPopup> getLightWeightPopupCache() {
      List list = (List)SwingUtilities.appContextGet(lightWeightPopupCacheKey);
      if (list == null) {
        list = new ArrayList();
        SwingUtilities.appContextPut(lightWeightPopupCacheKey, list);
      } 
      return list;
    }
    
    private static void recycleLightWeightPopup(LightWeightPopup param1LightWeightPopup) {
      synchronized (LightWeightPopup.class) {
        List list = getLightWeightPopupCache();
        if (list.size() < 5)
          list.add(param1LightWeightPopup); 
      } 
    }
    
    private static LightWeightPopup getRecycledLightWeightPopup() {
      synchronized (LightWeightPopup.class) {
        List list = getLightWeightPopupCache();
        if (list.size() > 0) {
          LightWeightPopup lightWeightPopup = (LightWeightPopup)list.get(0);
          list.remove(0);
          return lightWeightPopup;
        } 
        return null;
      } 
    }
    
    public void hide() {
      super.hide();
      Container container = (Container)getComponent();
      container.removeAll();
      recycleLightWeightPopup(this);
    }
    
    public void show() {
      Container container1 = null;
      if (this.owner != null)
        container1 = (this.owner instanceof Container) ? (Container)this.owner : this.owner.getParent(); 
      for (Container container2 = container1; container2 != null; container2 = container2.getParent()) {
        if (container2 instanceof JRootPane) {
          if (!(container2.getParent() instanceof JInternalFrame))
            container1 = ((JRootPane)container2).getLayeredPane(); 
        } else {
          if (container2 instanceof Window) {
            if (container1 == null)
              container1 = container2; 
            break;
          } 
          if (container2 instanceof JApplet)
            break; 
        } 
      } 
      Point point = SwingUtilities.convertScreenLocationToParent(container1, this.x, this.y);
      Component component = getComponent();
      component.setLocation(point.x, point.y);
      if (container1 instanceof JLayeredPane) {
        container1.add(component, JLayeredPane.POPUP_LAYER, 0);
      } else {
        container1.add(component);
      } 
    }
    
    Component createComponent(Component param1Component) {
      JPanel jPanel = new JPanel(new BorderLayout(), true);
      jPanel.setOpaque(true);
      return jPanel;
    }
    
    void reset(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) {
      super.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      JComponent jComponent = (JComponent)getComponent();
      jComponent.setOpaque(param1Component2.isOpaque());
      jComponent.setLocation(param1Int1, param1Int2);
      jComponent.add(param1Component2, "Center");
      param1Component2.invalidate();
      pack();
    }
  }
  
  private static class MediumWeightPopup extends ContainerPopup {
    private static final Object mediumWeightPopupCacheKey = new StringBuffer("PopupFactory.mediumPopupCache");
    
    private JRootPane rootPane;
    
    private MediumWeightPopup() { super(null); }
    
    static Popup getMediumWeightPopup(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) throws IllegalArgumentException {
      MediumWeightPopup mediumWeightPopup = getRecycledMediumWeightPopup();
      if (mediumWeightPopup == null)
        mediumWeightPopup = new MediumWeightPopup(); 
      mediumWeightPopup.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      if (!mediumWeightPopup.fitsOnScreen() || mediumWeightPopup.overlappedByOwnedWindow()) {
        mediumWeightPopup.hide();
        return null;
      } 
      return mediumWeightPopup;
    }
    
    private static List<MediumWeightPopup> getMediumWeightPopupCache() {
      List list = (List)SwingUtilities.appContextGet(mediumWeightPopupCacheKey);
      if (list == null) {
        list = new ArrayList();
        SwingUtilities.appContextPut(mediumWeightPopupCacheKey, list);
      } 
      return list;
    }
    
    private static void recycleMediumWeightPopup(MediumWeightPopup param1MediumWeightPopup) {
      synchronized (MediumWeightPopup.class) {
        List list = getMediumWeightPopupCache();
        if (list.size() < 5)
          list.add(param1MediumWeightPopup); 
      } 
    }
    
    private static MediumWeightPopup getRecycledMediumWeightPopup() {
      synchronized (MediumWeightPopup.class) {
        List list = getMediumWeightPopupCache();
        if (list.size() > 0) {
          MediumWeightPopup mediumWeightPopup = (MediumWeightPopup)list.get(0);
          list.remove(0);
          return mediumWeightPopup;
        } 
        return null;
      } 
    }
    
    public void hide() {
      super.hide();
      this.rootPane.getContentPane().removeAll();
      recycleMediumWeightPopup(this);
    }
    
    public void show() {
      Component component = getComponent();
      Container container = null;
      if (this.owner != null)
        container = this.owner.getParent(); 
      while (!(container instanceof Window) && !(container instanceof java.applet.Applet) && container != null)
        container = container.getParent(); 
      if (container instanceof RootPaneContainer) {
        container = ((RootPaneContainer)container).getLayeredPane();
        Point point = SwingUtilities.convertScreenLocationToParent(container, this.x, this.y);
        component.setVisible(false);
        component.setLocation(point.x, point.y);
        container.add(component, JLayeredPane.POPUP_LAYER, 0);
      } else {
        Point point = SwingUtilities.convertScreenLocationToParent(container, this.x, this.y);
        component.setLocation(point.x, point.y);
        component.setVisible(false);
        container.add(component);
      } 
      component.setVisible(true);
    }
    
    Component createComponent(Component param1Component) {
      MediumWeightComponent mediumWeightComponent = new MediumWeightComponent();
      this.rootPane = new JRootPane();
      this.rootPane.setOpaque(true);
      mediumWeightComponent.add(this.rootPane, "Center");
      return mediumWeightComponent;
    }
    
    void reset(Component param1Component1, Component param1Component2, int param1Int1, int param1Int2) {
      super.reset(param1Component1, param1Component2, param1Int1, param1Int2);
      Component component = getComponent();
      component.setLocation(param1Int1, param1Int2);
      this.rootPane.getContentPane().add(param1Component2, "Center");
      param1Component2.invalidate();
      component.validate();
      pack();
    }
    
    private static class MediumWeightComponent extends Panel implements SwingHeavyWeight {
      MediumWeightComponent() { super(new BorderLayout()); }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\PopupFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */