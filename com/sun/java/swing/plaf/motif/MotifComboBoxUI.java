package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MotifComboBoxUI extends BasicComboBoxUI implements Serializable {
  Icon arrowIcon;
  
  static final int HORIZ_MARGIN = 3;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifComboBoxUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    this.arrowIcon = new MotifComboBoxArrowIcon(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
    Runnable runnable = new Runnable() {
        public void run() {
          if (MotifComboBoxUI.this.motifGetEditor() != null)
            MotifComboBoxUI.this.motifGetEditor().setBackground(UIManager.getColor("text")); 
        }
      };
    SwingUtilities.invokeLater(runnable);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    if (!this.isMinimumSizeDirty)
      return new Dimension(this.cachedMinimumSize); 
    Insets insets = getInsets();
    Dimension dimension = getDisplaySize();
    dimension.height += insets.top + insets.bottom;
    int i = iconAreaWidth();
    dimension.width += insets.left + insets.right + i;
    this.cachedMinimumSize.setSize(dimension.width, dimension.height);
    this.isMinimumSizeDirty = false;
    return dimension;
  }
  
  protected ComboPopup createPopup() { return new MotifComboPopup(this.comboBox); }
  
  protected void installComponents() {
    if (this.comboBox.isEditable())
      addEditor(); 
    this.comboBox.add(this.currentValuePane);
  }
  
  protected void uninstallComponents() {
    removeEditor();
    this.comboBox.removeAll();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    boolean bool = this.comboBox.hasFocus();
    if (this.comboBox.isEnabled()) {
      paramGraphics.setColor(this.comboBox.getBackground());
    } else {
      paramGraphics.setColor(UIManager.getColor("ComboBox.disabledBackground"));
    } 
    paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    if (!this.comboBox.isEditable()) {
      Rectangle rectangle1 = rectangleForCurrentValue();
      paintCurrentValue(paramGraphics, rectangle1, bool);
    } 
    Rectangle rectangle = rectangleForArrowIcon();
    this.arrowIcon.paintIcon(paramJComponent, paramGraphics, rectangle.x, rectangle.y);
    if (!this.comboBox.isEditable()) {
      Insets insets;
      Border border = this.comboBox.getBorder();
      if (border != null) {
        insets = border.getBorderInsets(this.comboBox);
      } else {
        insets = new Insets(0, 0, 0, 0);
      } 
      if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
        rectangle.x -= 5;
      } else {
        rectangle.x += rectangle.width + 3 + 1;
      } 
      rectangle.y = insets.top;
      rectangle.width = 1;
      rectangle.height = (this.comboBox.getBounds()).height - insets.bottom - insets.top;
      paramGraphics.setColor(UIManager.getColor("controlShadow"));
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      rectangle.x++;
      paramGraphics.setColor(UIManager.getColor("controlHighlight"));
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
    Component component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
    component.setFont(this.comboBox.getFont());
    if (this.comboBox.isEnabled()) {
      component.setForeground(this.comboBox.getForeground());
      component.setBackground(this.comboBox.getBackground());
    } else {
      component.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      component.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
    } 
    Dimension dimension = component.getPreferredSize();
    this.currentValuePane.paintComponent(paramGraphics, component, this.comboBox, paramRectangle.x, paramRectangle.y, paramRectangle.width, dimension.height);
  }
  
  protected Rectangle rectangleForArrowIcon() {
    Insets insets;
    Rectangle rectangle = this.comboBox.getBounds();
    Border border = this.comboBox.getBorder();
    if (border != null) {
      insets = border.getBorderInsets(this.comboBox);
    } else {
      insets = new Insets(0, 0, 0, 0);
    } 
    rectangle.x = insets.left;
    rectangle.y = insets.top;
    rectangle.width -= insets.left + insets.right;
    rectangle.height -= insets.top + insets.bottom;
    if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
      rectangle.x = rectangle.x + rectangle.width - 3 - this.arrowIcon.getIconWidth();
    } else {
      rectangle.x += 3;
    } 
    rectangle.y += (rectangle.height - this.arrowIcon.getIconHeight()) / 2;
    rectangle.width = this.arrowIcon.getIconWidth();
    rectangle.height = this.arrowIcon.getIconHeight();
    return rectangle;
  }
  
  protected Rectangle rectangleForCurrentValue() {
    int i = this.comboBox.getWidth();
    int j = this.comboBox.getHeight();
    Insets insets = getInsets();
    return MotifGraphicsUtils.isLeftToRight(this.comboBox) ? new Rectangle(insets.left, insets.top, i - insets.left + insets.right - iconAreaWidth(), j - insets.top + insets.bottom) : new Rectangle(insets.left + iconAreaWidth(), insets.top, i - insets.left + insets.right - iconAreaWidth(), j - insets.top + insets.bottom);
  }
  
  public int iconAreaWidth() { return this.comboBox.isEditable() ? (this.arrowIcon.getIconWidth() + 6) : (this.arrowIcon.getIconWidth() + 9 + 2); }
  
  public void configureEditor() {
    super.configureEditor();
    this.editor.setBackground(UIManager.getColor("text"));
  }
  
  protected LayoutManager createLayoutManager() { return new ComboBoxLayoutManager(); }
  
  private Component motifGetEditor() { return this.editor; }
  
  protected PropertyChangeListener createPropertyChangeListener() { return new MotifPropertyChangeListener(null); }
  
  public class ComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
    public ComboBoxLayoutManager() { super(MotifComboBoxUI.this); }
    
    public void layoutContainer(Container param1Container) {
      if (MotifComboBoxUI.this.motifGetEditor() != null) {
        Rectangle rectangle = MotifComboBoxUI.this.rectangleForCurrentValue();
        rectangle.x++;
        rectangle.y++;
        rectangle.width--;
        rectangle.height -= 2;
        MotifComboBoxUI.this.motifGetEditor().setBounds(rectangle);
      } 
    }
  }
  
  static class MotifComboBoxArrowIcon implements Icon, Serializable {
    private Color lightShadow;
    
    private Color darkShadow;
    
    private Color fill;
    
    public MotifComboBoxArrowIcon(Color param1Color1, Color param1Color2, Color param1Color3) {
      this.lightShadow = param1Color1;
      this.darkShadow = param1Color2;
      this.fill = param1Color3;
    }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      int i = getIconWidth();
      int j = getIconHeight();
      param1Graphics.setColor(this.lightShadow);
      param1Graphics.drawLine(param1Int1, param1Int2, param1Int1 + i - 1, param1Int2);
      param1Graphics.drawLine(param1Int1, param1Int2 + 1, param1Int1 + i - 3, param1Int2 + 1);
      param1Graphics.setColor(this.darkShadow);
      param1Graphics.drawLine(param1Int1 + i - 2, param1Int2 + 1, param1Int1 + i - 1, param1Int2 + 1);
      int k = param1Int1 + 1;
      int m = param1Int2 + 2;
      int n = i - 6;
      while (m + 1 < param1Int2 + j) {
        param1Graphics.setColor(this.lightShadow);
        param1Graphics.drawLine(k, m, k + 1, m);
        param1Graphics.drawLine(k, m + 1, k + 1, m + 1);
        if (n > 0) {
          param1Graphics.setColor(this.fill);
          param1Graphics.drawLine(k + 2, m, k + 1 + n, m);
          param1Graphics.drawLine(k + 2, m + 1, k + 1 + n, m + 1);
        } 
        param1Graphics.setColor(this.darkShadow);
        param1Graphics.drawLine(k + n + 2, m, k + n + 3, m);
        param1Graphics.drawLine(k + n + 2, m + 1, k + n + 3, m + 1);
        k++;
        n -= 2;
        m += 2;
      } 
      param1Graphics.setColor(this.darkShadow);
      param1Graphics.drawLine(param1Int1 + i / 2, param1Int2 + j - 1, param1Int1 + i / 2, param1Int2 + j - 1);
    }
    
    public int getIconWidth() { return 11; }
    
    public int getIconHeight() { return 11; }
  }
  
  protected class MotifComboPopup extends BasicComboPopup {
    public MotifComboPopup(JComboBox param1JComboBox) { super(param1JComboBox); }
    
    public MouseMotionListener createListMouseMotionListener() { return new MouseMotionAdapter() {
        
        }; }
    
    public KeyListener createKeyListener() { return super.createKeyListener(); }
    
    protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
      protected InvocationKeyHandler() { super(MotifComboBoxUI.MotifComboPopup.this); }
    }
  }
  
  private class MotifPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
    private MotifPropertyChangeListener() { super(MotifComboBoxUI.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      super.propertyChange(param1PropertyChangeEvent);
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "enabled" && MotifComboBoxUI.this.comboBox.isEnabled()) {
        Component component = MotifComboBoxUI.this.motifGetEditor();
        if (component != null)
          component.setBackground(UIManager.getColor("text")); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */