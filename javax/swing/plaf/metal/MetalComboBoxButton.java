package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class MetalComboBoxButton extends JButton {
  protected JComboBox comboBox;
  
  protected JList listBox;
  
  protected CellRendererPane rendererPane;
  
  protected Icon comboIcon;
  
  protected boolean iconOnly = false;
  
  public final JComboBox getComboBox() { return this.comboBox; }
  
  public final void setComboBox(JComboBox paramJComboBox) { this.comboBox = paramJComboBox; }
  
  public final Icon getComboIcon() { return this.comboIcon; }
  
  public final void setComboIcon(Icon paramIcon) { this.comboIcon = paramIcon; }
  
  public final boolean isIconOnly() { return this.iconOnly; }
  
  public final void setIconOnly(boolean paramBoolean) { this.iconOnly = paramBoolean; }
  
  MetalComboBoxButton() {
    super("");
    DefaultButtonModel defaultButtonModel = new DefaultButtonModel() {
        public void setArmed(boolean param1Boolean) { super.setArmed(isPressed() ? true : param1Boolean); }
      };
    setModel(defaultButtonModel);
  }
  
  public MetalComboBoxButton(JComboBox paramJComboBox, Icon paramIcon, CellRendererPane paramCellRendererPane, JList paramJList) {
    this();
    this.comboBox = paramJComboBox;
    this.comboIcon = paramIcon;
    this.rendererPane = paramCellRendererPane;
    this.listBox = paramJList;
    setEnabled(this.comboBox.isEnabled());
  }
  
  public MetalComboBoxButton(JComboBox paramJComboBox, Icon paramIcon, boolean paramBoolean, CellRendererPane paramCellRendererPane, JList paramJList) {
    this(paramJComboBox, paramIcon, paramCellRendererPane, paramJList);
    this.iconOnly = paramBoolean;
  }
  
  public boolean isFocusTraversable() { return false; }
  
  public void setEnabled(boolean paramBoolean) {
    super.setEnabled(paramBoolean);
    if (paramBoolean) {
      setBackground(this.comboBox.getBackground());
      setForeground(this.comboBox.getForeground());
    } else {
      setBackground(UIManager.getColor("ComboBox.disabledBackground"));
      setForeground(UIManager.getColor("ComboBox.disabledForeground"));
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    boolean bool = MetalUtils.isLeftToRight(this.comboBox);
    super.paintComponent(paramGraphics);
    Insets insets = getInsets();
    int i = getWidth() - insets.left + insets.right;
    int j = getHeight() - insets.top + insets.bottom;
    if (j <= 0 || i <= 0)
      return; 
    int k = insets.left;
    int m = insets.top;
    int n = k + i - 1;
    int i1 = m + j - 1;
    int i2 = 0;
    int i3 = bool ? n : k;
    if (this.comboIcon != null) {
      i2 = this.comboIcon.getIconWidth();
      int i4 = this.comboIcon.getIconHeight();
      int i5 = 0;
      if (this.iconOnly) {
        i3 = getWidth() / 2 - i2 / 2;
        i5 = getHeight() / 2 - i4 / 2;
      } else {
        if (bool) {
          i3 = k + i - 1 - i2;
        } else {
          i3 = k;
        } 
        i5 = m + (i1 - m) / 2 - i4 / 2;
      } 
      this.comboIcon.paintIcon(this, paramGraphics, i3, i5);
      if (this.comboBox.hasFocus() && (!MetalLookAndFeel.usingOcean() || this.comboBox.isEditable())) {
        paramGraphics.setColor(MetalLookAndFeel.getFocusColor());
        paramGraphics.drawRect(k - 1, m - 1, i + 3, j + 1);
      } 
    } 
    if (MetalLookAndFeel.usingOcean())
      return; 
    if (!this.iconOnly && this.comboBox != null) {
      ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
      boolean bool1 = getModel().isPressed();
      Component component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, bool1, false);
      component.setFont(this.rendererPane.getFont());
      if (this.model.isArmed() && this.model.isPressed()) {
        if (isOpaque())
          component.setBackground(UIManager.getColor("Button.select")); 
        component.setForeground(this.comboBox.getForeground());
      } else if (!this.comboBox.isEnabled()) {
        if (isOpaque())
          component.setBackground(UIManager.getColor("ComboBox.disabledBackground")); 
        component.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      } else {
        component.setForeground(this.comboBox.getForeground());
        component.setBackground(this.comboBox.getBackground());
      } 
      int i4 = i - insets.right + i2;
      boolean bool2 = false;
      if (component instanceof javax.swing.JPanel)
        bool2 = true; 
      if (bool) {
        this.rendererPane.paintComponent(paramGraphics, component, this, k, m, i4, j, bool2);
      } else {
        this.rendererPane.paintComponent(paramGraphics, component, this, k + i2, m, i4, j, bool2);
      } 
    } 
  }
  
  public Dimension getMinimumSize() {
    Dimension dimension = new Dimension();
    Insets insets = getInsets();
    dimension.width = insets.left + getComboIcon().getIconWidth() + insets.right;
    dimension.height = insets.bottom + getComboIcon().getIconHeight() + insets.top;
    return dimension;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalComboBoxButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */