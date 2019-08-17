package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.swing.DefaultLookup;

public class DefaultTreeCellRenderer extends JLabel implements TreeCellRenderer {
  private JTree tree;
  
  protected boolean selected;
  
  protected boolean hasFocus;
  
  private boolean drawsFocusBorderAroundIcon;
  
  private boolean drawDashedFocusIndicator;
  
  private Color treeBGColor;
  
  private Color focusBGColor;
  
  protected Icon closedIcon;
  
  protected Icon leafIcon;
  
  protected Icon openIcon;
  
  protected Color textSelectionColor;
  
  protected Color textNonSelectionColor;
  
  protected Color backgroundSelectionColor;
  
  protected Color backgroundNonSelectionColor;
  
  protected Color borderSelectionColor;
  
  private boolean isDropCell;
  
  private boolean fillBackground;
  
  private boolean inited = true;
  
  public void updateUI() {
    super.updateUI();
    if (!this.inited || getLeafIcon() instanceof javax.swing.plaf.UIResource)
      setLeafIcon(DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon")); 
    if (!this.inited || getClosedIcon() instanceof javax.swing.plaf.UIResource)
      setClosedIcon(DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon")); 
    if (!this.inited || getOpenIcon() instanceof UIManager)
      setOpenIcon(DefaultLookup.getIcon(this, this.ui, "Tree.openIcon")); 
    if (!this.inited || getTextSelectionColor() instanceof javax.swing.plaf.UIResource)
      setTextSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionForeground")); 
    if (!this.inited || getTextNonSelectionColor() instanceof javax.swing.plaf.UIResource)
      setTextNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textForeground")); 
    if (!this.inited || getBackgroundSelectionColor() instanceof javax.swing.plaf.UIResource)
      setBackgroundSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBackground")); 
    if (!this.inited || getBackgroundNonSelectionColor() instanceof javax.swing.plaf.UIResource)
      setBackgroundNonSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.textBackground")); 
    if (!this.inited || getBorderSelectionColor() instanceof javax.swing.plaf.UIResource)
      setBorderSelectionColor(DefaultLookup.getColor(this, this.ui, "Tree.selectionBorderColor")); 
    this.drawsFocusBorderAroundIcon = DefaultLookup.getBoolean(this, this.ui, "Tree.drawsFocusBorderAroundIcon", false);
    this.drawDashedFocusIndicator = DefaultLookup.getBoolean(this, this.ui, "Tree.drawDashedFocusIndicator", false);
    this.fillBackground = DefaultLookup.getBoolean(this, this.ui, "Tree.rendererFillBackground", true);
    Insets insets = DefaultLookup.getInsets(this, this.ui, "Tree.rendererMargins");
    if (insets != null)
      setBorder(new EmptyBorder(insets.top, insets.left, insets.bottom, insets.right)); 
    setName("Tree.cellRenderer");
  }
  
  public Icon getDefaultOpenIcon() { return DefaultLookup.getIcon(this, this.ui, "Tree.openIcon"); }
  
  public Icon getDefaultClosedIcon() { return DefaultLookup.getIcon(this, this.ui, "Tree.closedIcon"); }
  
  public Icon getDefaultLeafIcon() { return DefaultLookup.getIcon(this, this.ui, "Tree.leafIcon"); }
  
  public void setOpenIcon(Icon paramIcon) { this.openIcon = paramIcon; }
  
  public Icon getOpenIcon() { return this.openIcon; }
  
  public void setClosedIcon(Icon paramIcon) { this.closedIcon = paramIcon; }
  
  public Icon getClosedIcon() { return this.closedIcon; }
  
  public void setLeafIcon(Icon paramIcon) { this.leafIcon = paramIcon; }
  
  public Icon getLeafIcon() { return this.leafIcon; }
  
  public void setTextSelectionColor(Color paramColor) { this.textSelectionColor = paramColor; }
  
  public Color getTextSelectionColor() { return this.textSelectionColor; }
  
  public void setTextNonSelectionColor(Color paramColor) { this.textNonSelectionColor = paramColor; }
  
  public Color getTextNonSelectionColor() { return this.textNonSelectionColor; }
  
  public void setBackgroundSelectionColor(Color paramColor) { this.backgroundSelectionColor = paramColor; }
  
  public Color getBackgroundSelectionColor() { return this.backgroundSelectionColor; }
  
  public void setBackgroundNonSelectionColor(Color paramColor) { this.backgroundNonSelectionColor = paramColor; }
  
  public Color getBackgroundNonSelectionColor() { return this.backgroundNonSelectionColor; }
  
  public void setBorderSelectionColor(Color paramColor) { this.borderSelectionColor = paramColor; }
  
  public Color getBorderSelectionColor() { return this.borderSelectionColor; }
  
  public void setFont(Font paramFont) {
    if (paramFont instanceof javax.swing.plaf.FontUIResource)
      paramFont = null; 
    super.setFont(paramFont);
  }
  
  public Font getFont() {
    Font font = super.getFont();
    if (font == null && this.tree != null)
      font = this.tree.getFont(); 
    return font;
  }
  
  public void setBackground(Color paramColor) {
    if (paramColor instanceof javax.swing.plaf.ColorUIResource)
      paramColor = null; 
    super.setBackground(paramColor);
  }
  
  public Component getTreeCellRendererComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4) {
    String str = paramJTree.convertValueToText(paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4);
    this.tree = paramJTree;
    this.hasFocus = paramBoolean4;
    setText(str);
    Color color = null;
    this.isDropCell = false;
    JTree.DropLocation dropLocation = paramJTree.getDropLocation();
    if (dropLocation != null && dropLocation.getChildIndex() == -1 && paramJTree.getRowForPath(dropLocation.getPath()) == paramInt) {
      Color color1 = DefaultLookup.getColor(this, this.ui, "Tree.dropCellForeground");
      if (color1 != null) {
        color = color1;
      } else {
        color = getTextSelectionColor();
      } 
      this.isDropCell = true;
    } else if (paramBoolean1) {
      color = getTextSelectionColor();
    } else {
      color = getTextNonSelectionColor();
    } 
    setForeground(color);
    Icon icon = null;
    if (paramBoolean3) {
      icon = getLeafIcon();
    } else if (paramBoolean2) {
      icon = getOpenIcon();
    } else {
      icon = getClosedIcon();
    } 
    if (!paramJTree.isEnabled()) {
      setEnabled(false);
      LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
      Icon icon1 = lookAndFeel.getDisabledIcon(paramJTree, icon);
      if (icon1 != null)
        icon = icon1; 
      setDisabledIcon(icon);
    } else {
      setEnabled(true);
      setIcon(icon);
    } 
    setComponentOrientation(paramJTree.getComponentOrientation());
    this.selected = paramBoolean1;
    return this;
  }
  
  public void paint(Graphics paramGraphics) {
    Color color;
    if (this.isDropCell) {
      color = DefaultLookup.getColor(this, this.ui, "Tree.dropCellBackground");
      if (color == null)
        color = getBackgroundSelectionColor(); 
    } else if (this.selected) {
      color = getBackgroundSelectionColor();
    } else {
      color = getBackgroundNonSelectionColor();
      if (color == null)
        color = getBackground(); 
    } 
    int i = -1;
    if (color != null && this.fillBackground) {
      i = getLabelStart();
      paramGraphics.setColor(color);
      if (getComponentOrientation().isLeftToRight()) {
        paramGraphics.fillRect(i, 0, getWidth() - i, getHeight());
      } else {
        paramGraphics.fillRect(0, 0, getWidth() - i, getHeight());
      } 
    } 
    if (this.hasFocus) {
      if (this.drawsFocusBorderAroundIcon) {
        i = 0;
      } else if (i == -1) {
        i = getLabelStart();
      } 
      if (getComponentOrientation().isLeftToRight()) {
        paintFocus(paramGraphics, i, 0, getWidth() - i, getHeight(), color);
      } else {
        paintFocus(paramGraphics, 0, 0, getWidth() - i, getHeight(), color);
      } 
    } 
    super.paint(paramGraphics);
  }
  
  private void paintFocus(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor) {
    Color color = getBorderSelectionColor();
    if (color != null && (this.selected || !this.drawDashedFocusIndicator)) {
      paramGraphics.setColor(color);
      paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
    } 
    if (this.drawDashedFocusIndicator && paramColor != null) {
      if (this.treeBGColor != paramColor) {
        this.treeBGColor = paramColor;
        this.focusBGColor = new Color(paramColor.getRGB() ^ 0xFFFFFFFF);
      } 
      paramGraphics.setColor(this.focusBGColor);
      BasicGraphicsUtils.drawDashedRect(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } 
  }
  
  private int getLabelStart() {
    Icon icon = getIcon();
    return (icon != null && getText() != null) ? (icon.getIconWidth() + Math.max(0, getIconTextGap() - 1)) : 0;
  }
  
  public Dimension getPreferredSize() {
    Dimension dimension = super.getPreferredSize();
    if (dimension != null)
      dimension = new Dimension(dimension.width + 3, dimension.height); 
    return dimension;
  }
  
  public void validate() {}
  
  public void invalidate() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
  public void repaint() {}
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramString == "text" || ((paramString == "font" || paramString == "foreground") && paramObject1 != paramObject2 && getClientProperty("html") != null))
      super.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2) {}
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2) {}
  
  public void firePropertyChange(String paramString, short paramShort1, short paramShort2) {}
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2) {}
  
  public void firePropertyChange(String paramString, long paramLong1, long paramLong2) {}
  
  public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2) {}
  
  public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2) {}
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\DefaultTreeCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */