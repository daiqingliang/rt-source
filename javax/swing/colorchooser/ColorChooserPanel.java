package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

final class ColorChooserPanel extends AbstractColorChooserPanel implements PropertyChangeListener {
  private static final int MASK = -16777216;
  
  private final ColorModel model;
  
  private final ColorPanel panel;
  
  private final DiagramComponent slider;
  
  private final DiagramComponent diagram;
  
  private final JFormattedTextField text;
  
  private final JLabel label;
  
  ColorChooserPanel(ColorModel paramColorModel) {
    this.model = paramColorModel;
    this.panel = new ColorPanel(this.model);
    this.slider = new DiagramComponent(this.panel, false);
    this.diagram = new DiagramComponent(this.panel, true);
    this.text = new JFormattedTextField();
    this.label = new JLabel(null, null, 4);
    ValueFormatter.init(6, true, this.text);
  }
  
  public void setEnabled(boolean paramBoolean) {
    super.setEnabled(paramBoolean);
    setEnabled(this, paramBoolean);
  }
  
  private static void setEnabled(Container paramContainer, boolean paramBoolean) {
    for (Component component : paramContainer.getComponents()) {
      component.setEnabled(paramBoolean);
      if (component instanceof Container)
        setEnabled((Container)component, paramBoolean); 
    } 
  }
  
  public void updateChooser() {
    Color color = getColorFromModel();
    if (color != null) {
      this.panel.setColor(color);
      this.text.setValue(Integer.valueOf(color.getRGB()));
      this.slider.repaint();
      this.diagram.repaint();
    } 
  }
  
  protected void buildChooser() {
    if (0 == getComponentCount()) {
      setLayout(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.weighty = 1.0D;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets.top = 10;
      gridBagConstraints.insets.right = 10;
      add(this.panel, gridBagConstraints);
      gridBagConstraints.gridwidth = 1;
      gridBagConstraints.weightx = 1.0D;
      gridBagConstraints.weighty = 0.0D;
      gridBagConstraints.anchor = 10;
      gridBagConstraints.insets.right = 5;
      gridBagConstraints.insets.bottom = 10;
      add(this.label, gridBagConstraints);
      gridBagConstraints.gridx = 4;
      gridBagConstraints.weightx = 0.0D;
      gridBagConstraints.insets.right = 10;
      add(this.text, gridBagConstraints);
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.ipadx = (this.text.getPreferredSize()).height;
      gridBagConstraints.ipady = (getPreferredSize()).height;
      add(this.slider, gridBagConstraints);
      gridBagConstraints.gridx = 1;
      gridBagConstraints.insets.left = 10;
      gridBagConstraints.ipadx = gridBagConstraints.ipady;
      add(this.diagram, gridBagConstraints);
      this.label.setLabelFor(this.text);
      this.text.addPropertyChangeListener("value", this);
      this.slider.setBorder(this.text.getBorder());
      this.diagram.setBorder(this.text.getBorder());
      setInheritsPopupMenu(this, true);
    } 
    String str = this.model.getText(this, "HexCode");
    boolean bool = (str != null);
    this.text.setVisible(bool);
    this.text.getAccessibleContext().setAccessibleDescription(str);
    this.label.setVisible(bool);
    if (bool) {
      this.label.setText(str);
      int i = this.model.getInteger(this, "HexCodeMnemonic");
      if (i > 0) {
        this.label.setDisplayedMnemonic(i);
        i = this.model.getInteger(this, "HexCodeMnemonicIndex");
        if (i >= 0)
          this.label.setDisplayedMnemonicIndex(i); 
      } 
    } 
    this.panel.buildPanel();
  }
  
  public String getDisplayName() { return this.model.getText(this, "Name"); }
  
  public int getMnemonic() { return this.model.getInteger(this, "Mnemonic"); }
  
  public int getDisplayedMnemonicIndex() { return this.model.getInteger(this, "DisplayedMnemonicIndex"); }
  
  public Icon getSmallDisplayIcon() { return null; }
  
  public Icon getLargeDisplayIcon() { return null; }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    ColorSelectionModel colorSelectionModel = getColorSelectionModel();
    if (colorSelectionModel != null) {
      Object object = paramPropertyChangeEvent.getNewValue();
      if (object instanceof Integer) {
        int i = 0xFF000000 & colorSelectionModel.getSelectedColor().getRGB() | ((Integer)object).intValue();
        colorSelectionModel.setSelectedColor(new Color(i, true));
      } 
    } 
    this.text.selectAll();
  }
  
  private static void setInheritsPopupMenu(JComponent paramJComponent, boolean paramBoolean) {
    paramJComponent.setInheritsPopupMenu(paramBoolean);
    for (Component component : paramJComponent.getComponents()) {
      if (component instanceof JComponent)
        setInheritsPopupMenu((JComponent)component, paramBoolean); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */