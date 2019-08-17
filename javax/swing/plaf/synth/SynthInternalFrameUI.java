package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class SynthInternalFrameUI extends BasicInternalFrameUI implements SynthUI, PropertyChangeListener {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthInternalFrameUI((JInternalFrame)paramJComponent); }
  
  protected SynthInternalFrameUI(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  public void installDefaults() {
    this.frame.setLayout(this.internalFrameLayout = createLayoutManager());
    updateStyle(this.frame);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.frame.addPropertyChangeListener(this);
  }
  
  protected void uninstallComponents() {
    if (this.frame.getComponentPopupMenu() instanceof javax.swing.plaf.UIResource)
      this.frame.setComponentPopupMenu(null); 
    super.uninstallComponents();
  }
  
  protected void uninstallListeners() {
    this.frame.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      Icon icon = this.frame.getFrameIcon();
      if (icon == null || icon instanceof javax.swing.plaf.UIResource)
        this.frame.setFrameIcon(synthContext.getStyle().getIcon(synthContext, "InternalFrame.icon")); 
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.frame, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    if (this.frame.getLayout() == this.internalFrameLayout)
      this.frame.setLayout(null); 
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame) {
    this.titlePane = new SynthInternalFrameTitlePane(paramJInternalFrame);
    this.titlePane.setName("InternalFrame.northPane");
    return this.titlePane;
  }
  
  protected ComponentListener createComponentListener() { return UIManager.getBoolean("InternalFrame.useTaskBar") ? new BasicInternalFrameUI.ComponentHandler() {
        public void componentResized(ComponentEvent param1ComponentEvent) {
          if (SynthInternalFrameUI.this.frame != null && SynthInternalFrameUI.this.frame.isMaximum()) {
            JDesktopPane jDesktopPane = (JDesktopPane)param1ComponentEvent.getSource();
            for (Component component : jDesktopPane.getComponents()) {
              if (component instanceof SynthDesktopPaneUI.TaskBar) {
                SynthInternalFrameUI.this.frame.setBounds(0, 0, jDesktopPane.getWidth(), jDesktopPane.getHeight() - component.getHeight());
                SynthInternalFrameUI.this.frame.revalidate();
                break;
              } 
            } 
          } 
          JInternalFrame jInternalFrame = SynthInternalFrameUI.this.frame;
          SynthInternalFrameUI.this.frame = null;
          super.componentResized(param1ComponentEvent);
          SynthInternalFrameUI.this.frame = jInternalFrame;
        }
      } : super.createComponentListener(); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintInternalFrameBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintInternalFrameBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    SynthStyle synthStyle = this.style;
    JInternalFrame jInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
    String str = paramPropertyChangeEvent.getPropertyName();
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle(jInternalFrame); 
    if (this.style == synthStyle && (str == "maximum" || str == "selected")) {
      SynthContext synthContext = getContext(jInternalFrame, 1);
      this.style.uninstallDefaults(synthContext);
      this.style.installDefaults(synthContext, this);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */