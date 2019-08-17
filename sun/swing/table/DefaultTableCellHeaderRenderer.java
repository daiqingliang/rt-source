package sun.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import sun.swing.DefaultLookup;

public class DefaultTableCellHeaderRenderer extends DefaultTableCellRenderer implements UIResource {
  private boolean horizontalTextPositionSet;
  
  private Icon sortArrow;
  
  private EmptyIcon emptyIcon = new EmptyIcon(null);
  
  public DefaultTableCellHeaderRenderer() { setHorizontalAlignment(0); }
  
  public void setHorizontalTextPosition(int paramInt) {
    this.horizontalTextPositionSet = true;
    super.setHorizontalTextPosition(paramInt);
  }
  
  public Component getTableCellRendererComponent(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    Icon icon = null;
    boolean bool = false;
    if (paramJTable != null) {
      JTableHeader jTableHeader = paramJTable.getTableHeader();
      if (jTableHeader != null) {
        Color color1 = null;
        Color color2 = null;
        if (paramBoolean2) {
          color1 = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellForeground");
          color2 = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellBackground");
        } 
        if (color1 == null)
          color1 = jTableHeader.getForeground(); 
        if (color2 == null)
          color2 = jTableHeader.getBackground(); 
        setForeground(color1);
        setBackground(color2);
        setFont(jTableHeader.getFont());
        bool = jTableHeader.isPaintingForPrint();
      } 
      if (!bool && paramJTable.getRowSorter() != null) {
        if (!this.horizontalTextPositionSet)
          setHorizontalTextPosition(10); 
        SortOrder sortOrder = getColumnSortOrder(paramJTable, paramInt2);
        if (sortOrder != null)
          switch (sortOrder) {
            case ASCENDING:
              icon = DefaultLookup.getIcon(this, this.ui, "Table.ascendingSortIcon");
              break;
            case DESCENDING:
              icon = DefaultLookup.getIcon(this, this.ui, "Table.descendingSortIcon");
              break;
            case UNSORTED:
              icon = DefaultLookup.getIcon(this, this.ui, "Table.naturalSortIcon");
              break;
          }  
      } 
    } 
    setText((paramObject == null) ? "" : paramObject.toString());
    setIcon(icon);
    this.sortArrow = icon;
    Border border = null;
    if (paramBoolean2)
      border = DefaultLookup.getBorder(this, this.ui, "TableHeader.focusCellBorder"); 
    if (border == null)
      border = DefaultLookup.getBorder(this, this.ui, "TableHeader.cellBorder"); 
    setBorder(border);
    return this;
  }
  
  public static SortOrder getColumnSortOrder(JTable paramJTable, int paramInt) {
    SortOrder sortOrder = null;
    if (paramJTable == null || paramJTable.getRowSorter() == null)
      return sortOrder; 
    List list = paramJTable.getRowSorter().getSortKeys();
    if (list.size() > 0 && ((RowSorter.SortKey)list.get(0)).getColumn() == paramJTable.convertColumnIndexToModel(paramInt))
      sortOrder = ((RowSorter.SortKey)list.get(0)).getSortOrder(); 
    return sortOrder;
  }
  
  public void paintComponent(Graphics paramGraphics) {
    boolean bool = DefaultLookup.getBoolean(this, this.ui, "TableHeader.rightAlignSortArrow", false);
    if (bool && this.sortArrow != null) {
      this.emptyIcon.width = this.sortArrow.getIconWidth();
      this.emptyIcon.height = this.sortArrow.getIconHeight();
      setIcon(this.emptyIcon);
      super.paintComponent(paramGraphics);
      Point point = computeIconPosition(paramGraphics);
      this.sortArrow.paintIcon(this, paramGraphics, point.x, point.y);
    } else {
      super.paintComponent(paramGraphics);
    } 
  }
  
  private Point computeIconPosition(Graphics paramGraphics) {
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = new Rectangle();
    Insets insets = getInsets();
    rectangle1.x = insets.left;
    rectangle1.y = insets.top;
    rectangle1.width = getWidth() - insets.left + insets.right;
    rectangle1.height = getHeight() - insets.top + insets.bottom;
    SwingUtilities.layoutCompoundLabel(this, fontMetrics, getText(), this.sortArrow, getVerticalAlignment(), getHorizontalAlignment(), getVerticalTextPosition(), getHorizontalTextPosition(), rectangle1, rectangle3, rectangle2, getIconTextGap());
    int i = getWidth() - insets.right - this.sortArrow.getIconWidth();
    int j = rectangle3.y;
    return new Point(i, j);
  }
  
  private class EmptyIcon implements Icon, Serializable {
    int width = 0;
    
    int height = 0;
    
    private EmptyIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return this.width; }
    
    public int getIconHeight() { return this.height; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\table\DefaultTableCellHeaderRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */