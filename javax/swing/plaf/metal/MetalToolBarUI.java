package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class MetalToolBarUI extends BasicToolBarUI {
  private static List<WeakReference<JComponent>> components = new ArrayList();
  
  protected ContainerListener contListener;
  
  protected PropertyChangeListener rolloverListener;
  
  private static Border nonRolloverBorder;
  
  private JMenuBar lastMenuBar;
  
  static void register(JComponent paramJComponent) {
    if (paramJComponent == null)
      throw new NullPointerException("JComponent must be non-null"); 
    components.add(new WeakReference(paramJComponent));
  }
  
  static void unregister(JComponent paramJComponent) {
    for (int i = components.size() - 1; i >= 0; i--) {
      JComponent jComponent = (JComponent)((WeakReference)components.get(i)).get();
      if (jComponent == paramJComponent || jComponent == null)
        components.remove(i); 
    } 
  }
  
  static Object findRegisteredComponentOfType(JComponent paramJComponent, Class paramClass) {
    JRootPane jRootPane = SwingUtilities.getRootPane(paramJComponent);
    if (jRootPane != null)
      for (int i = components.size() - 1; i >= 0; i--) {
        Object object = ((WeakReference)components.get(i)).get();
        if (object == null) {
          components.remove(i);
        } else if (paramClass.isInstance(object) && SwingUtilities.getRootPane((Component)object) == jRootPane) {
          return object;
        } 
      }  
    return null;
  }
  
  static boolean doesMenuBarBorderToolBar(JMenuBar paramJMenuBar) {
    JToolBar jToolBar = (JToolBar)findRegisteredComponentOfType(paramJMenuBar, JToolBar.class);
    if (jToolBar != null && jToolBar.getOrientation() == 0) {
      JRootPane jRootPane = SwingUtilities.getRootPane(paramJMenuBar);
      Point point = new Point(0, 0);
      point = SwingUtilities.convertPoint(paramJMenuBar, point, jRootPane);
      int i = point.x;
      int j = point.y;
      point.x = point.y = 0;
      point = SwingUtilities.convertPoint(jToolBar, point, jRootPane);
      return (point.x == i && j + paramJMenuBar.getHeight() == point.y && paramJMenuBar.getWidth() == jToolBar.getWidth());
    } 
    return false;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalToolBarUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    register(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    nonRolloverBorder = null;
    unregister(paramJComponent);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.contListener = createContainerListener();
    if (this.contListener != null)
      this.toolBar.addContainerListener(this.contListener); 
    this.rolloverListener = createRolloverListener();
    if (this.rolloverListener != null)
      this.toolBar.addPropertyChangeListener(this.rolloverListener); 
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    if (this.contListener != null)
      this.toolBar.removeContainerListener(this.contListener); 
    this.rolloverListener = createRolloverListener();
    if (this.rolloverListener != null)
      this.toolBar.removePropertyChangeListener(this.rolloverListener); 
  }
  
  protected Border createRolloverBorder() { return super.createRolloverBorder(); }
  
  protected Border createNonRolloverBorder() { return super.createNonRolloverBorder(); }
  
  private Border createNonRolloverToggleBorder() { return createNonRolloverBorder(); }
  
  protected void setBorderToNonRollover(Component paramComponent) {
    if (paramComponent instanceof JToggleButton && !(paramComponent instanceof javax.swing.JCheckBox)) {
      JToggleButton jToggleButton = (JToggleButton)paramComponent;
      Border border = jToggleButton.getBorder();
      super.setBorderToNonRollover(paramComponent);
      if (border instanceof javax.swing.plaf.UIResource) {
        if (nonRolloverBorder == null)
          nonRolloverBorder = createNonRolloverToggleBorder(); 
        jToggleButton.setBorder(nonRolloverBorder);
      } 
    } else {
      super.setBorderToNonRollover(paramComponent);
    } 
  }
  
  protected ContainerListener createContainerListener() { return null; }
  
  protected PropertyChangeListener createRolloverListener() { return null; }
  
  protected MouseInputListener createDockingListener() { return new MetalDockingListener(this.toolBar); }
  
  protected void setDragOffset(Point paramPoint) {
    if (!GraphicsEnvironment.isHeadless()) {
      if (this.dragWindow == null)
        this.dragWindow = createDragWindow(this.toolBar); 
      this.dragWindow.setOffset(paramPoint);
    } 
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    if (paramGraphics == null)
      throw new NullPointerException("graphics must be non-null"); 
    if (paramJComponent.isOpaque() && paramJComponent.getBackground() instanceof javax.swing.plaf.UIResource && ((JToolBar)paramJComponent).getOrientation() == 0 && UIManager.get("MenuBar.gradient") != null) {
      JRootPane jRootPane = SwingUtilities.getRootPane(paramJComponent);
      JMenuBar jMenuBar = (JMenuBar)findRegisteredComponentOfType(paramJComponent, JMenuBar.class);
      if (jMenuBar != null && jMenuBar.isOpaque() && jMenuBar.getBackground() instanceof javax.swing.plaf.UIResource) {
        Point point = new Point(0, 0);
        point = SwingUtilities.convertPoint(paramJComponent, point, jRootPane);
        int i = point.x;
        int j = point.y;
        point.x = point.y = 0;
        point = SwingUtilities.convertPoint(jMenuBar, point, jRootPane);
        if (point.x == i && j == point.y + jMenuBar.getHeight() && jMenuBar.getWidth() == paramJComponent.getWidth() && MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, -jMenuBar.getHeight(), paramJComponent.getWidth(), paramJComponent.getHeight() + jMenuBar.getHeight(), true)) {
          setLastMenuBar(jMenuBar);
          paint(paramGraphics, paramJComponent);
          return;
        } 
      } 
      if (MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true)) {
        setLastMenuBar(null);
        paint(paramGraphics, paramJComponent);
        return;
      } 
    } 
    setLastMenuBar(null);
    super.update(paramGraphics, paramJComponent);
  }
  
  private void setLastMenuBar(JMenuBar paramJMenuBar) {
    if (MetalLookAndFeel.usingOcean() && this.lastMenuBar != paramJMenuBar) {
      if (this.lastMenuBar != null)
        this.lastMenuBar.repaint(); 
      if (paramJMenuBar != null)
        paramJMenuBar.repaint(); 
      this.lastMenuBar = paramJMenuBar;
    } 
  }
  
  protected class MetalContainerListener extends BasicToolBarUI.ToolBarContListener {
    protected MetalContainerListener() { super(MetalToolBarUI.this); }
  }
  
  protected class MetalDockingListener extends BasicToolBarUI.DockingListener {
    private boolean pressedInBumps = false;
    
    public MetalDockingListener(JToolBar param1JToolBar) { super(MetalToolBarUI.this, param1JToolBar); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      super.mousePressed(param1MouseEvent);
      if (!this.toolBar.isEnabled())
        return; 
      this.pressedInBumps = false;
      Rectangle rectangle = new Rectangle();
      if (this.toolBar.getOrientation() == 0) {
        byte b = MetalUtils.isLeftToRight(this.toolBar) ? 0 : ((this.toolBar.getSize()).width - 14);
        rectangle.setBounds(b, 0, 14, (this.toolBar.getSize()).height);
      } else {
        rectangle.setBounds(0, 0, (this.toolBar.getSize()).width, 14);
      } 
      if (rectangle.contains(param1MouseEvent.getPoint())) {
        this.pressedInBumps = true;
        Point point = param1MouseEvent.getPoint();
        if (!MetalUtils.isLeftToRight(this.toolBar))
          point.x -= (this.toolBar.getSize()).width - (this.toolBar.getPreferredSize()).width; 
        MetalToolBarUI.this.setDragOffset(point);
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (this.pressedInBumps)
        super.mouseDragged(param1MouseEvent); 
    }
  }
  
  protected class MetalRolloverListener extends BasicToolBarUI.PropertyListener {
    protected MetalRolloverListener() { super(MetalToolBarUI.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */