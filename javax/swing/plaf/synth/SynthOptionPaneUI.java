package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import sun.swing.DefaultLookup;

public class SynthOptionPaneUI extends BasicOptionPaneUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthOptionPaneUI(); }
  
  protected void installDefaults() { updateStyle(this.optionPane); }
  
  protected void installListeners() {
    super.installListeners();
    this.optionPane.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.minimumSize = (Dimension)this.style.get(synthContext, "OptionPane.minimumSize");
      if (this.minimumSize == null)
        this.minimumSize = new Dimension(262, 90); 
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.optionPane, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.optionPane.removePropertyChangeListener(this);
  }
  
  protected void installComponents() {
    this.optionPane.add(createMessageArea());
    Container container = createSeparator();
    if (container != null) {
      this.optionPane.add(container);
      SynthContext synthContext = getContext(this.optionPane, 1);
      this.optionPane.add(Box.createVerticalStrut(synthContext.getStyle().getInt(synthContext, "OptionPane.separatorPadding", 6)));
      synthContext.dispose();
    } 
    this.optionPane.add(createButtonArea());
    this.optionPane.applyComponentOrientation(this.optionPane.getComponentOrientation());
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintOptionPaneBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintOptionPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JOptionPane)paramPropertyChangeEvent.getSource()); 
  }
  
  protected boolean getSizeButtonsToSameWidth() { return DefaultLookup.getBoolean(this.optionPane, this, "OptionPane.sameSizeButtons", true); }
  
  protected Container createMessageArea() {
    JPanel jPanel1 = new JPanel();
    jPanel1.setName("OptionPane.messageArea");
    jPanel1.setLayout(new BorderLayout());
    JPanel jPanel2 = new JPanel(new GridBagLayout());
    JPanel jPanel3 = new JPanel(new BorderLayout());
    jPanel2.setName("OptionPane.body");
    jPanel3.setName("OptionPane.realBody");
    if (getIcon() != null) {
      JPanel jPanel = new JPanel();
      jPanel.setName("OptionPane.separator");
      jPanel.setPreferredSize(new Dimension(15, 1));
      jPanel3.add(jPanel, "Before");
    } 
    jPanel3.add(jPanel2, "Center");
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.gridheight = 1;
    SynthContext synthContext = getContext(this.optionPane, 1);
    gridBagConstraints.anchor = synthContext.getStyle().getInt(synthContext, "OptionPane.messageAnchor", 10);
    synthContext.dispose();
    gridBagConstraints.insets = new Insets(0, 0, 3, 0);
    addMessageComponents(jPanel2, gridBagConstraints, getMessage(), getMaxCharactersPerLineCount(), false);
    jPanel1.add(jPanel3, "Center");
    addIcon(jPanel1);
    return jPanel1;
  }
  
  protected Container createSeparator() {
    JSeparator jSeparator = new JSeparator(0);
    jSeparator.setName("OptionPane.separator");
    return jSeparator;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */