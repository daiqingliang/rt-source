package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class DefaultTableCellRenderer extends JLabel implements TableCellRenderer, Serializable {
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
  
  private Color unselectedForeground;
  
  private Color unselectedBackground;
  
  public DefaultTableCellRenderer() {
    setOpaque(true);
    setBorder(getNoFocusBorder());
    setName("Table.cellRenderer");
  }
  
  private Border getNoFocusBorder() {
    Border border = DefaultLookup.getBorder(this, this.ui, "Table.cellNoFocusBorder");
    return (System.getSecurityManager() != null) ? ((border != null) ? border : SAFE_NO_FOCUS_BORDER) : ((border != null && (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) ? border : noFocusBorder);
  }
  
  public void setForeground(Color paramColor) {
    super.setForeground(paramColor);
    this.unselectedForeground = paramColor;
  }
  
  public void setBackground(Color paramColor) {
    super.setBackground(paramColor);
    this.unselectedBackground = paramColor;
  }
  
  public void updateUI() {
    super.updateUI();
    setForeground(null);
    setBackground(null);
  }
  
  public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    if (paramJTable == null)
      return this; 
    Color color1 = null;
    Color color2 = null;
    JTable.DropLocation dropLocation = paramJTable.getDropLocation();
    if (dropLocation != null && !dropLocation.isInsertRow() && !dropLocation.isInsertColumn() && dropLocation.getRow() == paramInt1 && dropLocation.getColumn() == paramInt2) {
      color1 = DefaultLookup.getColor(this, this.ui, "Table.dropCellForeground");
      color2 = DefaultLookup.getColor(this, this.ui, "Table.dropCellBackground");
      paramBoolean1 = true;
    } 
    if (paramBoolean1) {
      super.setForeground((color1 == null) ? paramJTable.getSelectionForeground() : color1);
      super.setBackground((color2 == null) ? paramJTable.getSelectionBackground() : color2);
    } else {
      Color color = (this.unselectedBackground != null) ? this.unselectedBackground : paramJTable.getBackground();
      if (color == null || color instanceof UIResource) {
        Color color3 = DefaultLookup.getColor(this, this.ui, "Table.alternateRowColor");
        if (color3 != null && paramInt1 % 2 != 0)
          color = color3; 
      } 
      super.setForeground((this.unselectedForeground != null) ? this.unselectedForeground : paramJTable.getForeground());
      super.setBackground(color);
    } 
    setFont(paramJTable.getFont());
    if (paramBoolean2) {
      Border border = null;
      if (paramBoolean1)
        border = DefaultLookup.getBorder(this, this.ui, "Table.focusSelectedCellHighlightBorder"); 
      if (border == null)
        border = DefaultLookup.getBorder(this, this.ui, "Table.focusCellHighlightBorder"); 
      setBorder(border);
      if (!paramBoolean1 && paramJTable.isCellEditable(paramInt1, paramInt2)) {
        Color color = DefaultLookup.getColor(this, this.ui, "Table.focusCellForeground");
        if (color != null)
          super.setForeground(color); 
        color = DefaultLookup.getColor(this, this.ui, "Table.focusCellBackground");
        if (color != null)
          super.setBackground(color); 
      } 
    } else {
      setBorder(getNoFocusBorder());
    } 
    setValue(paramObject);
    return this;
  }
  
  public boolean isOpaque() {
    Color color = getBackground();
    Container container = getParent();
    if (container != null)
      container = container.getParent(); 
    boolean bool = (color != null && container != null && color.equals(container.getBackground()) && container.isOpaque()) ? 1 : 0;
    return (!bool && super.isOpaque());
  }
  
  public void invalidate() {}
  
  public void validate() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
  public void repaint() {}
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (paramString == "text" || paramString == "labelFor" || paramString == "displayedMnemonic" || ((paramString == "font" || paramString == "foreground") && paramObject1 != paramObject2 && getClientProperty("html") != null))
      super.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {}
  
  protected void setValue(Object paramObject) { setText((paramObject == null) ? "" : paramObject.toString()); }
  
  public static class UIResource extends DefaultTableCellRenderer implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\DefaultTableCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */