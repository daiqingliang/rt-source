package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MetalComboBoxUI extends BasicComboBoxUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalComboBoxUI(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (MetalLookAndFeel.usingOcean())
      super.paint(paramGraphics, paramJComponent); 
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    if (MetalLookAndFeel.usingOcean()) {
      paramRectangle.x += 2;
      paramRectangle.width -= 3;
      if (this.arrowButton != null) {
        Insets insets = this.arrowButton.getInsets();
        paramRectangle.y += insets.top;
        paramRectangle.height -= insets.top + insets.bottom;
      } else {
        paramRectangle.y += 2;
        paramRectangle.height -= 4;
      } 
      super.paintCurrentValue(paramGraphics, paramRectangle, paramBoolean);
    } else if (paramGraphics == null || paramRectangle == null) {
      throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
    } 
  }
  
  public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    if (MetalLookAndFeel.usingOcean()) {
      paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
      paramGraphics.drawRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height - 1);
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawRect(paramRectangle.x + 1, paramRectangle.y + 1, paramRectangle.width - 2, paramRectangle.height - 3);
      if (paramBoolean && !isPopupVisible(this.comboBox) && this.arrowButton != null) {
        paramGraphics.setColor(this.listBox.getSelectionBackground());
        Insets insets = this.arrowButton.getInsets();
        if (insets.top > 2)
          paramGraphics.fillRect(paramRectangle.x + 2, paramRectangle.y + 2, paramRectangle.width - 3, insets.top - 2); 
        if (insets.bottom > 2)
          paramGraphics.fillRect(paramRectangle.x + 2, paramRectangle.y + paramRectangle.height - insets.bottom, paramRectangle.width - 3, insets.bottom - 2); 
      } 
    } else if (paramGraphics == null || paramRectangle == null) {
      throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
    } 
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    int i;
    if (MetalLookAndFeel.usingOcean() && paramInt2 >= 4) {
      paramInt2 -= 4;
      i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
      if (i >= 0)
        i += 2; 
    } else {
      i = super.getBaseline(paramJComponent, paramInt1, paramInt2);
    } 
    return i;
  }
  
  protected ComboBoxEditor createEditor() { return new MetalComboBoxEditor.UIResource(); }
  
  protected ComboPopup createPopup() { return super.createPopup(); }
  
  protected JButton createArrowButton() {
    boolean bool = (this.comboBox.isEditable() || MetalLookAndFeel.usingOcean());
    MetalComboBoxButton metalComboBoxButton = new MetalComboBoxButton(this.comboBox, new MetalComboBoxIcon(), bool, this.currentValuePane, this.listBox);
    metalComboBoxButton.setMargin(new Insets(0, 1, 1, 3));
    if (MetalLookAndFeel.usingOcean())
      metalComboBoxButton.putClientProperty(MetalBorders.NO_BUTTON_ROLLOVER, Boolean.TRUE); 
    updateButtonForOcean(metalComboBoxButton);
    return metalComboBoxButton;
  }
  
  private void updateButtonForOcean(JButton paramJButton) {
    if (MetalLookAndFeel.usingOcean())
      paramJButton.setFocusPainted(this.comboBox.isEditable()); 
  }
  
  public PropertyChangeListener createPropertyChangeListener() { return new MetalPropertyChangeListener(); }
  
  @Deprecated
  protected void editablePropertyChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected LayoutManager createLayoutManager() { return new MetalComboBoxLayoutManager(); }
  
  public void layoutComboBox(Container paramContainer, MetalComboBoxLayoutManager paramMetalComboBoxLayoutManager) {
    if (this.comboBox.isEditable() && !MetalLookAndFeel.usingOcean()) {
      paramMetalComboBoxLayoutManager.superLayout(paramContainer);
      return;
    } 
    if (this.arrowButton != null)
      if (MetalLookAndFeel.usingOcean()) {
        Insets insets = this.comboBox.getInsets();
        int i = (this.arrowButton.getMinimumSize()).width;
        this.arrowButton.setBounds(MetalUtils.isLeftToRight(this.comboBox) ? (this.comboBox.getWidth() - insets.right - i) : insets.left, insets.top, i, this.comboBox.getHeight() - insets.top - insets.bottom);
      } else {
        Insets insets = this.comboBox.getInsets();
        int i = this.comboBox.getWidth();
        int j = this.comboBox.getHeight();
        this.arrowButton.setBounds(insets.left, insets.top, i - insets.left + insets.right, j - insets.top + insets.bottom);
      }  
    if (this.editor != null && MetalLookAndFeel.usingOcean()) {
      Rectangle rectangle = rectangleForCurrentValue();
      this.editor.setBounds(rectangle);
    } 
  }
  
  @Deprecated
  protected void removeListeners() {
    if (this.propertyChangeListener != null)
      this.comboBox.removePropertyChangeListener(this.propertyChangeListener); 
  }
  
  public void configureEditor() { super.configureEditor(); }
  
  public void unconfigureEditor() { super.unconfigureEditor(); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    if (!this.isMinimumSizeDirty)
      return new Dimension(this.cachedMinimumSize); 
    Dimension dimension = null;
    if (!this.comboBox.isEditable() && this.arrowButton != null) {
      Insets insets1 = this.arrowButton.getInsets();
      Insets insets2 = this.comboBox.getInsets();
      dimension = getDisplaySize();
      dimension.width += insets2.left + insets2.right;
      dimension.width += insets1.right;
      dimension.width += (this.arrowButton.getMinimumSize()).width;
      dimension.height += insets2.top + insets2.bottom;
      dimension.height += insets1.top + insets1.bottom;
    } else if (this.comboBox.isEditable() && this.arrowButton != null && this.editor != null) {
      dimension = super.getMinimumSize(paramJComponent);
      Insets insets = this.arrowButton.getMargin();
      dimension.height += insets.top + insets.bottom;
      dimension.width += insets.left + insets.right;
    } else {
      dimension = super.getMinimumSize(paramJComponent);
    } 
    this.cachedMinimumSize.setSize(dimension.width, dimension.height);
    this.isMinimumSizeDirty = false;
    return new Dimension(this.cachedMinimumSize);
  }
  
  public class MetalComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
    public MetalComboBoxLayoutManager() { super(MetalComboBoxUI.this); }
    
    public void layoutContainer(Container param1Container) { MetalComboBoxUI.this.layoutComboBox(param1Container, this); }
    
    public void superLayout(Container param1Container) { super.layoutContainer(param1Container); }
  }
  
  @Deprecated
  public class MetalComboPopup extends BasicComboPopup {
    public MetalComboPopup(JComboBox param1JComboBox) { super(param1JComboBox); }
    
    public void delegateFocus(MouseEvent param1MouseEvent) { super.delegateFocus(param1MouseEvent); }
  }
  
  public class MetalPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
    public MetalPropertyChangeListener() { super(MetalComboBoxUI.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      super.propertyChange(param1PropertyChangeEvent);
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "editable") {
        if (MetalComboBoxUI.this.arrowButton instanceof MetalComboBoxButton) {
          MetalComboBoxButton metalComboBoxButton = (MetalComboBoxButton)MetalComboBoxUI.this.arrowButton;
          metalComboBoxButton.setIconOnly((MetalComboBoxUI.this.comboBox.isEditable() || MetalLookAndFeel.usingOcean()));
        } 
        MetalComboBoxUI.this.comboBox.repaint();
        MetalComboBoxUI.this.updateButtonForOcean(MetalComboBoxUI.this.arrowButton);
      } else if (str == "background") {
        Color color = (Color)param1PropertyChangeEvent.getNewValue();
        MetalComboBoxUI.this.arrowButton.setBackground(color);
        MetalComboBoxUI.this.listBox.setBackground(color);
      } else if (str == "foreground") {
        Color color = (Color)param1PropertyChangeEvent.getNewValue();
        MetalComboBoxUI.this.arrowButton.setForeground(color);
        MetalComboBoxUI.this.listBox.setForeground(color);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */