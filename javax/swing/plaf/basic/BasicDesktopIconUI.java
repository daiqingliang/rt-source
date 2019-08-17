package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;

public class BasicDesktopIconUI extends DesktopIconUI {
  protected JInternalFrame.JDesktopIcon desktopIcon;
  
  protected JInternalFrame frame;
  
  protected JComponent iconPane;
  
  MouseInputListener mouseInputListener;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicDesktopIconUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.desktopIcon = (JInternalFrame.JDesktopIcon)paramJComponent;
    this.frame = this.desktopIcon.getInternalFrame();
    installDefaults();
    installComponents();
    JInternalFrame jInternalFrame = this.desktopIcon.getInternalFrame();
    if (jInternalFrame.isIcon() && jInternalFrame.getParent() == null) {
      JDesktopPane jDesktopPane = this.desktopIcon.getDesktopPane();
      if (jDesktopPane != null) {
        DesktopManager desktopManager = jDesktopPane.getDesktopManager();
        if (desktopManager instanceof javax.swing.DefaultDesktopManager)
          desktopManager.iconifyFrame(jInternalFrame); 
      } 
    } 
    installListeners();
    JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer(this.frame));
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallComponents();
    JInternalFrame jInternalFrame = this.desktopIcon.getInternalFrame();
    if (jInternalFrame.isIcon()) {
      JDesktopPane jDesktopPane = this.desktopIcon.getDesktopPane();
      if (jDesktopPane != null) {
        DesktopManager desktopManager = jDesktopPane.getDesktopManager();
        if (desktopManager instanceof javax.swing.DefaultDesktopManager) {
          jInternalFrame.putClientProperty("wasIconOnce", null);
          this.desktopIcon.setLocation(-2147483648, 0);
        } 
      } 
    } 
    uninstallListeners();
    this.frame = null;
    this.desktopIcon = null;
  }
  
  protected void installComponents() {
    this.iconPane = new BasicInternalFrameTitlePane(this.frame);
    this.desktopIcon.setLayout(new BorderLayout());
    this.desktopIcon.add(this.iconPane, "Center");
  }
  
  protected void uninstallComponents() {
    this.desktopIcon.remove(this.iconPane);
    this.desktopIcon.setLayout(null);
    this.iconPane = null;
  }
  
  protected void installListeners() {
    this.mouseInputListener = createMouseInputListener();
    this.desktopIcon.addMouseMotionListener(this.mouseInputListener);
    this.desktopIcon.addMouseListener(this.mouseInputListener);
  }
  
  protected void uninstallListeners() {
    this.desktopIcon.removeMouseMotionListener(this.mouseInputListener);
    this.desktopIcon.removeMouseListener(this.mouseInputListener);
    this.mouseInputListener = null;
  }
  
  protected void installDefaults() {
    LookAndFeel.installBorder(this.desktopIcon, "DesktopIcon.border");
    LookAndFeel.installProperty(this.desktopIcon, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults() { LookAndFeel.uninstallBorder(this.desktopIcon); }
  
  protected MouseInputListener createMouseInputListener() { return new MouseInputHandler(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return this.desktopIcon.getLayout().preferredLayoutSize(this.desktopIcon); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = new Dimension(this.iconPane.getMinimumSize());
    Border border = this.frame.getBorder();
    if (border != null)
      dimension.height += (border.getBorderInsets(this.frame)).bottom + (border.getBorderInsets(this.frame)).top; 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return this.iconPane.getMaximumSize(); }
  
  public Insets getInsets(JComponent paramJComponent) {
    JInternalFrame jInternalFrame = this.desktopIcon.getInternalFrame();
    Border border = jInternalFrame.getBorder();
    return (border != null) ? border.getBorderInsets(jInternalFrame) : new Insets(0, 0, 0, 0);
  }
  
  public void deiconize() {
    try {
      this.frame.setIcon(false);
    } catch (PropertyVetoException propertyVetoException) {}
  }
  
  public class MouseInputHandler extends MouseInputAdapter {
    int _x;
    
    int _y;
    
    int __x;
    
    int __y;
    
    Rectangle startingBounds;
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      this._x = 0;
      this._y = 0;
      this.__x = 0;
      this.__y = 0;
      this.startingBounds = null;
      JDesktopPane jDesktopPane;
      if ((jDesktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
        DesktopManager desktopManager = jDesktopPane.getDesktopManager();
        desktopManager.endDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
      } 
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      Point point = SwingUtilities.convertPoint((Component)param1MouseEvent.getSource(), param1MouseEvent.getX(), param1MouseEvent.getY(), null);
      this.__x = param1MouseEvent.getX();
      this.__y = param1MouseEvent.getY();
      this._x = point.x;
      this._y = point.y;
      this.startingBounds = BasicDesktopIconUI.this.desktopIcon.getBounds();
      JDesktopPane jDesktopPane;
      if ((jDesktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
        DesktopManager desktopManager = jDesktopPane.getDesktopManager();
        desktopManager.beginDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
      } 
      try {
        BasicDesktopIconUI.this.frame.setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {}
      if (BasicDesktopIconUI.this.desktopIcon.getParent() instanceof JLayeredPane)
        ((JLayeredPane)BasicDesktopIconUI.this.desktopIcon.getParent()).moveToFront(BasicDesktopIconUI.this.desktopIcon); 
      if (param1MouseEvent.getClickCount() > 1 && BasicDesktopIconUI.this.frame.isIconifiable() && BasicDesktopIconUI.this.frame.isIcon())
        BasicDesktopIconUI.this.deiconize(); 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      Point point = SwingUtilities.convertPoint((Component)param1MouseEvent.getSource(), param1MouseEvent.getX(), param1MouseEvent.getY(), null);
      Insets insets = BasicDesktopIconUI.this.desktopIcon.getInsets();
      int k = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getWidth();
      int m = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getHeight();
      if (this.startingBounds == null)
        return; 
      int i = this.startingBounds.x - this._x - point.x;
      int j = this.startingBounds.y - this._y - point.y;
      if (i + insets.left <= -this.__x)
        i = -this.__x - insets.left; 
      if (j + insets.top <= -this.__y)
        j = -this.__y - insets.top; 
      if (i + this.__x + insets.right > k)
        i = k - this.__x - insets.right; 
      if (j + this.__y + insets.bottom > m)
        j = m - this.__y - insets.bottom; 
      JDesktopPane jDesktopPane;
      if ((jDesktopPane = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
        DesktopManager desktopManager = jDesktopPane.getDesktopManager();
        desktopManager.dragFrame(BasicDesktopIconUI.this.desktopIcon, i, j);
      } else {
        moveAndRepaint(BasicDesktopIconUI.this.desktopIcon, i, j, BasicDesktopIconUI.this.desktopIcon.getWidth(), BasicDesktopIconUI.this.desktopIcon.getHeight());
      } 
    }
    
    public void moveAndRepaint(JComponent param1JComponent, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = param1JComponent.getBounds();
      param1JComponent.setBounds(param1Int1, param1Int2, param1Int3, param1Int4);
      SwingUtilities.computeUnion(param1Int1, param1Int2, param1Int3, param1Int4, rectangle);
      param1JComponent.getParent().repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */