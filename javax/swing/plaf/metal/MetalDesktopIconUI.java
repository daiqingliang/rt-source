package javax.swing.plaf.metal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class MetalDesktopIconUI extends BasicDesktopIconUI {
  JButton button;
  
  JLabel label;
  
  TitleListener titleListener;
  
  private int width;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalDesktopIconUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    LookAndFeel.installColorsAndFont(this.desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground", "DesktopIcon.font");
    this.width = UIManager.getInt("DesktopIcon.width");
  }
  
  protected void installComponents() {
    this.frame = this.desktopIcon.getInternalFrame();
    Icon icon = this.frame.getFrameIcon();
    String str = this.frame.getTitle();
    this.button = new JButton(str, icon);
    this.button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent param1ActionEvent) { MetalDesktopIconUI.this.deiconize(); }
        });
    this.button.setFont(this.desktopIcon.getFont());
    this.button.setBackground(this.desktopIcon.getBackground());
    this.button.setForeground(this.desktopIcon.getForeground());
    int i = (this.button.getPreferredSize()).height;
    MetalBumps metalBumps = new MetalBumps(i / 3, i, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
    this.label = new JLabel(metalBumps);
    this.label.setBorder(new MatteBorder(0, 2, 0, 1, this.desktopIcon.getBackground()));
    this.desktopIcon.setLayout(new BorderLayout(2, 0));
    this.desktopIcon.add(this.button, "Center");
    this.desktopIcon.add(this.label, "West");
  }
  
  protected void uninstallComponents() {
    this.desktopIcon.setLayout(null);
    this.desktopIcon.remove(this.label);
    this.desktopIcon.remove(this.button);
    this.button = null;
    this.frame = null;
  }
  
  protected void installListeners() {
    super.installListeners();
    this.desktopIcon.getInternalFrame().addPropertyChangeListener(this.titleListener = new TitleListener());
  }
  
  protected void uninstallListeners() {
    this.desktopIcon.getInternalFrame().removePropertyChangeListener(this.titleListener);
    this.titleListener = null;
    super.uninstallListeners();
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return new Dimension(this.width, (this.desktopIcon.getLayout().minimumLayoutSize(this.desktopIcon)).height); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  class TitleListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getPropertyName().equals("title"))
        MetalDesktopIconUI.this.button.setText((String)param1PropertyChangeEvent.getNewValue()); 
      if (param1PropertyChangeEvent.getPropertyName().equals("frameIcon"))
        MetalDesktopIconUI.this.button.setIcon((Icon)param1PropertyChangeEvent.getNewValue()); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */