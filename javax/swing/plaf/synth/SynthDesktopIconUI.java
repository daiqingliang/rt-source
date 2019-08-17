package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class SynthDesktopIconUI extends BasicDesktopIconUI implements SynthUI, PropertyChangeListener {
  private SynthStyle style;
  
  private Handler handler = new Handler(null);
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthDesktopIconUI(); }
  
  protected void installComponents() {
    if (UIManager.getBoolean("InternalFrame.useTaskBar")) {
      this.iconPane = new JToggleButton(this.frame.getTitle(), this.frame.getFrameIcon()) {
          public String getToolTipText() { return getText(); }
          
          public JPopupMenu getComponentPopupMenu() { return SynthDesktopIconUI.this.frame.getComponentPopupMenu(); }
        };
      ToolTipManager.sharedInstance().registerComponent(this.iconPane);
      this.iconPane.setFont(this.desktopIcon.getFont());
      this.iconPane.setBackground(this.desktopIcon.getBackground());
      this.iconPane.setForeground(this.desktopIcon.getForeground());
    } else {
      this.iconPane = new SynthInternalFrameTitlePane(this.frame);
      this.iconPane.setName("InternalFrame.northPane");
    } 
    this.desktopIcon.setLayout(new BorderLayout());
    this.desktopIcon.add(this.iconPane, "Center");
  }
  
  protected void installListeners() {
    super.installListeners();
    this.desktopIcon.addPropertyChangeListener(this);
    if (this.iconPane instanceof JToggleButton) {
      this.frame.addPropertyChangeListener(this);
      ((JToggleButton)this.iconPane).addActionListener(this.handler);
    } 
  }
  
  protected void uninstallListeners() {
    if (this.iconPane instanceof JToggleButton) {
      ((JToggleButton)this.iconPane).removeActionListener(this.handler);
      this.frame.removePropertyChangeListener(this);
    } 
    this.desktopIcon.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  protected void installDefaults() { updateStyle(this.desktopIcon); }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.desktopIcon, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintDesktopIconBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintDesktopIconBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getSource() instanceof JInternalFrame.JDesktopIcon) {
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
        updateStyle((JInternalFrame.JDesktopIcon)paramPropertyChangeEvent.getSource()); 
    } else if (paramPropertyChangeEvent.getSource() instanceof JInternalFrame) {
      JInternalFrame jInternalFrame = (JInternalFrame)paramPropertyChangeEvent.getSource();
      if (this.iconPane instanceof JToggleButton) {
        JToggleButton jToggleButton = (JToggleButton)this.iconPane;
        String str = paramPropertyChangeEvent.getPropertyName();
        if (str == "title") {
          jToggleButton.setText((String)paramPropertyChangeEvent.getNewValue());
        } else if (str == "frameIcon") {
          jToggleButton.setIcon((Icon)paramPropertyChangeEvent.getNewValue());
        } else if (str == "icon" || str == "selected") {
          jToggleButton.setSelected((!jInternalFrame.isIcon() && jInternalFrame.isSelected()));
        } 
      } 
    } 
  }
  
  private final class Handler implements ActionListener {
    private Handler() {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (param1ActionEvent.getSource() instanceof JToggleButton) {
        JToggleButton jToggleButton = (JToggleButton)param1ActionEvent.getSource();
        try {
          boolean bool = jToggleButton.isSelected();
          if (!bool && !SynthDesktopIconUI.this.frame.isIconifiable()) {
            jToggleButton.setSelected(true);
          } else {
            SynthDesktopIconUI.this.frame.setIcon(!bool);
            if (bool)
              SynthDesktopIconUI.this.frame.setSelected(true); 
          } 
        } catch (PropertyVetoException propertyVetoException) {}
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */