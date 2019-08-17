package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class SynthDesktopPaneUI extends BasicDesktopPaneUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private TaskBar taskBar;
  
  private DesktopManager oldDesktopManager;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthDesktopPaneUI(); }
  
  protected void installListeners() {
    super.installListeners();
    this.desktop.addPropertyChangeListener(this);
    if (this.taskBar != null) {
      this.desktop.addComponentListener(this.taskBar);
      this.desktop.addContainerListener(this.taskBar);
    } 
  }
  
  protected void installDefaults() {
    updateStyle(this.desktop);
    if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
      this.taskBar = new TaskBar();
      for (Component component : this.desktop.getComponents()) {
        JInternalFrame.JDesktopIcon jDesktopIcon;
        if (component instanceof JInternalFrame.JDesktopIcon) {
          jDesktopIcon = (JInternalFrame.JDesktopIcon)component;
        } else if (component instanceof JInternalFrame) {
          jDesktopIcon = ((JInternalFrame)component).getDesktopIcon();
        } else {
          continue;
        } 
        if (jDesktopIcon.getParent() == this.desktop)
          this.desktop.remove(jDesktopIcon); 
        if (jDesktopIcon.getParent() != this.taskBar) {
          this.taskBar.add(jDesktopIcon);
          jDesktopIcon.getInternalFrame().addComponentListener(this.taskBar);
        } 
        continue;
      } 
      this.taskBar.setBackground(this.desktop.getBackground());
      this.desktop.add(this.taskBar, Integer.valueOf(JLayeredPane.PALETTE_LAYER.intValue() + 1));
      if (this.desktop.isShowing())
        this.taskBar.adjustSize(); 
    } 
  }
  
  private void updateStyle(JDesktopPane paramJDesktopPane) {
    SynthStyle synthStyle = this.style;
    SynthContext synthContext = getContext(paramJDesktopPane, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (synthStyle != null) {
      uninstallKeyboardActions();
      installKeyboardActions();
    } 
    synthContext.dispose();
  }
  
  protected void uninstallListeners() {
    if (this.taskBar != null) {
      this.desktop.removeComponentListener(this.taskBar);
      this.desktop.removeContainerListener(this.taskBar);
    } 
    this.desktop.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.desktop, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    if (this.taskBar != null) {
      for (Component component : this.taskBar.getComponents()) {
        JInternalFrame.JDesktopIcon jDesktopIcon = (JInternalFrame.JDesktopIcon)component;
        this.taskBar.remove(jDesktopIcon);
        jDesktopIcon.setPreferredSize(null);
        JInternalFrame jInternalFrame = jDesktopIcon.getInternalFrame();
        if (jInternalFrame.isIcon())
          this.desktop.add(jDesktopIcon); 
        jInternalFrame.removeComponentListener(this.taskBar);
      } 
      this.desktop.remove(this.taskBar);
      this.taskBar = null;
    } 
  }
  
  protected void installDesktopManager() {
    if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
      this.desktopManager = this.oldDesktopManager = this.desktop.getDesktopManager();
      if (!(this.desktopManager instanceof SynthDesktopManager)) {
        this.desktopManager = new SynthDesktopManager();
        this.desktop.setDesktopManager(this.desktopManager);
      } 
    } else {
      super.installDesktopManager();
    } 
  }
  
  protected void uninstallDesktopManager() {
    if (this.oldDesktopManager != null && !(this.oldDesktopManager instanceof UIResource)) {
      this.desktopManager = this.desktop.getDesktopManager();
      if (this.desktopManager == null || this.desktopManager instanceof UIResource)
        this.desktop.setDesktopManager(this.oldDesktopManager); 
    } 
    this.oldDesktopManager = null;
    super.uninstallDesktopManager();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintDesktopPaneBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintDesktopPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JDesktopPane)paramPropertyChangeEvent.getSource()); 
    if (paramPropertyChangeEvent.getPropertyName() == "ancestor" && this.taskBar != null)
      this.taskBar.adjustSize(); 
  }
  
  class SynthDesktopManager extends DefaultDesktopManager implements UIResource {
    public void maximizeFrame(JInternalFrame param1JInternalFrame) {
      if (param1JInternalFrame.isIcon()) {
        try {
          param1JInternalFrame.setIcon(false);
        } catch (PropertyVetoException propertyVetoException) {}
      } else {
        param1JInternalFrame.setNormalBounds(param1JInternalFrame.getBounds());
        Container container = param1JInternalFrame.getParent();
        setBoundsForFrame(param1JInternalFrame, 0, 0, container.getWidth(), container.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight());
      } 
      try {
        param1JInternalFrame.setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {}
    }
    
    public void iconifyFrame(JInternalFrame param1JInternalFrame) {
      Container container = param1JInternalFrame.getParent();
      JDesktopPane jDesktopPane = param1JInternalFrame.getDesktopPane();
      boolean bool = param1JInternalFrame.isSelected();
      if (container == null)
        return; 
      JInternalFrame.JDesktopIcon jDesktopIcon = param1JInternalFrame.getDesktopIcon();
      if (!param1JInternalFrame.isMaximum())
        param1JInternalFrame.setNormalBounds(param1JInternalFrame.getBounds()); 
      container.remove(param1JInternalFrame);
      container.repaint(param1JInternalFrame.getX(), param1JInternalFrame.getY(), param1JInternalFrame.getWidth(), param1JInternalFrame.getHeight());
      try {
        param1JInternalFrame.setSelected(false);
      } catch (PropertyVetoException propertyVetoException) {}
      if (bool)
        for (Component component : container.getComponents()) {
          if (component instanceof JInternalFrame) {
            try {
              ((JInternalFrame)component).setSelected(true);
            } catch (PropertyVetoException propertyVetoException) {}
            ((JInternalFrame)component).moveToFront();
            return;
          } 
        }  
    }
    
    public void deiconifyFrame(JInternalFrame param1JInternalFrame) {
      JInternalFrame.JDesktopIcon jDesktopIcon = param1JInternalFrame.getDesktopIcon();
      Container container = jDesktopIcon.getParent();
      if (container != null) {
        container = container.getParent();
        if (container != null) {
          container.add(param1JInternalFrame);
          if (param1JInternalFrame.isMaximum()) {
            int i = container.getWidth();
            int j = container.getHeight() - SynthDesktopPaneUI.this.taskBar.getHeight();
            if (param1JInternalFrame.getWidth() != i || param1JInternalFrame.getHeight() != j)
              setBoundsForFrame(param1JInternalFrame, 0, 0, i, j); 
          } 
          if (param1JInternalFrame.isSelected()) {
            param1JInternalFrame.moveToFront();
          } else {
            try {
              param1JInternalFrame.setSelected(true);
            } catch (PropertyVetoException propertyVetoException) {}
          } 
        } 
      } 
    }
    
    protected void removeIconFor(JInternalFrame param1JInternalFrame) {
      super.removeIconFor(param1JInternalFrame);
      SynthDesktopPaneUI.this.taskBar.validate();
    }
    
    public void setBoundsForFrame(JComponent param1JComponent, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      super.setBoundsForFrame(param1JComponent, param1Int1, param1Int2, param1Int3, param1Int4);
      if (SynthDesktopPaneUI.this.taskBar != null && param1Int2 >= SynthDesktopPaneUI.this.taskBar.getY())
        param1JComponent.setLocation(param1JComponent.getX(), SynthDesktopPaneUI.this.taskBar.getY() - (param1JComponent.getInsets()).top); 
    }
  }
  
  static class TaskBar extends JPanel implements ComponentListener, ContainerListener {
    TaskBar() {
      setOpaque(true);
      setLayout(new FlowLayout(0, 0, 0) {
            public void layoutContainer(Container param2Container) {
              Component[] arrayOfComponent = param2Container.getComponents();
              int i = arrayOfComponent.length;
              if (i > 0) {
                int j = 0;
                for (Component component : arrayOfComponent) {
                  component.setPreferredSize(null);
                  Dimension dimension = component.getPreferredSize();
                  if (dimension.width > j)
                    j = dimension.width; 
                } 
                Insets insets = param2Container.getInsets();
                int k = param2Container.getWidth() - insets.left - insets.right;
                int m = Math.min(j, Math.max(10, k / i));
                for (Component component : arrayOfComponent) {
                  Dimension dimension = component.getPreferredSize();
                  component.setPreferredSize(new Dimension(m, dimension.height));
                } 
              } 
              super.layoutContainer(param2Container);
            }
          });
      setBorder(new BevelBorder(0) {
            protected void paintRaisedBevel(Component param2Component, Graphics param2Graphics, int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
              Color color = param2Graphics.getColor();
              param2Graphics.translate(param2Int1, param2Int2);
              param2Graphics.setColor(getHighlightOuterColor(param2Component));
              param2Graphics.drawLine(0, 0, 0, param2Int4 - 2);
              param2Graphics.drawLine(1, 0, param2Int3 - 2, 0);
              param2Graphics.setColor(getShadowOuterColor(param2Component));
              param2Graphics.drawLine(0, param2Int4 - 1, param2Int3 - 1, param2Int4 - 1);
              param2Graphics.drawLine(param2Int3 - 1, 0, param2Int3 - 1, param2Int4 - 2);
              param2Graphics.translate(-param2Int1, -param2Int2);
              param2Graphics.setColor(color);
            }
          });
    }
    
    void adjustSize() {
      JDesktopPane jDesktopPane = (JDesktopPane)getParent();
      if (jDesktopPane != null) {
        int i = (getPreferredSize()).height;
        Insets insets = getInsets();
        if (i == insets.top + insets.bottom)
          if (getHeight() <= i) {
            i += 21;
          } else {
            i = getHeight();
          }  
        setBounds(0, jDesktopPane.getHeight() - i, jDesktopPane.getWidth(), i);
        revalidate();
        repaint();
      } 
    }
    
    public void componentResized(ComponentEvent param1ComponentEvent) {
      if (param1ComponentEvent.getSource() instanceof JDesktopPane)
        adjustSize(); 
    }
    
    public void componentMoved(ComponentEvent param1ComponentEvent) {}
    
    public void componentShown(ComponentEvent param1ComponentEvent) {
      if (param1ComponentEvent.getSource() instanceof JInternalFrame)
        adjustSize(); 
    }
    
    public void componentHidden(ComponentEvent param1ComponentEvent) {
      if (param1ComponentEvent.getSource() instanceof JInternalFrame) {
        ((JInternalFrame)param1ComponentEvent.getSource()).getDesktopIcon().setVisible(false);
        revalidate();
      } 
    }
    
    public void componentAdded(ContainerEvent param1ContainerEvent) {
      if (param1ContainerEvent.getChild() instanceof JInternalFrame) {
        JDesktopPane jDesktopPane = (JDesktopPane)param1ContainerEvent.getSource();
        JInternalFrame jInternalFrame = (JInternalFrame)param1ContainerEvent.getChild();
        JInternalFrame.JDesktopIcon jDesktopIcon = jInternalFrame.getDesktopIcon();
        for (Component component : getComponents()) {
          if (component == jDesktopIcon)
            return; 
        } 
        add(jDesktopIcon);
        jInternalFrame.addComponentListener(this);
        if (getComponentCount() == 1)
          adjustSize(); 
      } 
    }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) {
      if (param1ContainerEvent.getChild() instanceof JInternalFrame) {
        JInternalFrame jInternalFrame = (JInternalFrame)param1ContainerEvent.getChild();
        if (!jInternalFrame.isIcon()) {
          remove(jInternalFrame.getDesktopIcon());
          jInternalFrame.removeComponentListener(this);
          revalidate();
          repaint();
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */