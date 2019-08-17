package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import sun.swing.SwingUtilities2;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class WindowsTableHeaderUI extends BasicTableHeaderUI {
  private TableCellRenderer originalHeaderRenderer;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsTableHeaderUI(); }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    if (XPStyle.getXP() != null) {
      this.originalHeaderRenderer = this.header.getDefaultRenderer();
      if (this.originalHeaderRenderer instanceof UIResource)
        this.header.setDefaultRenderer(new XPDefaultRenderer()); 
    } 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    if (this.header.getDefaultRenderer() instanceof XPDefaultRenderer)
      this.header.setDefaultRenderer(this.originalHeaderRenderer); 
    super.uninstallUI(paramJComponent);
  }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2) {
    if (XPStyle.getXP() != null) {
      this.header.repaint(this.header.getHeaderRect(paramInt1));
      this.header.repaint(this.header.getHeaderRect(paramInt2));
    } 
  }
  
  private static class IconBorder implements Border, UIResource {
    private final Icon icon;
    
    private final int top;
    
    private final int left;
    
    private final int bottom;
    
    private final int right;
    
    public IconBorder(Icon param1Icon, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.icon = param1Icon;
      this.top = param1Int1;
      this.left = param1Int2;
      this.bottom = param1Int3;
      this.right = param1Int4;
    }
    
    public Insets getBorderInsets(Component param1Component) { return new Insets(this.icon.getIconHeight() + this.top, this.left, this.bottom, this.right); }
    
    public boolean isBorderOpaque() { return false; }
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { this.icon.paintIcon(param1Component, param1Graphics, param1Int1 + this.left + (param1Int3 - this.left - this.right - this.icon.getIconWidth()) / 2, param1Int2 + this.top); }
  }
  
  private class XPDefaultRenderer extends DefaultTableCellHeaderRenderer {
    XPStyle.Skin skin;
    
    boolean isSelected;
    
    boolean hasFocus;
    
    boolean hasRollover;
    
    int column;
    
    XPDefaultRenderer() { setHorizontalAlignment(10); }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
      this.isSelected = param1Boolean1;
      this.hasFocus = param1Boolean2;
      this.column = param1Int2;
      this.hasRollover = (param1Int2 == WindowsTableHeaderUI.this.getRolloverColumn());
      if (this.skin == null) {
        XPStyle xPStyle = XPStyle.getXP();
        this.skin = (xPStyle != null) ? xPStyle.getSkin(WindowsTableHeaderUI.this.header, TMSchema.Part.HP_HEADERITEM) : null;
      } 
      Insets insets = (this.skin != null) ? this.skin.getContentMargin() : null;
      EmptyBorder emptyBorder = null;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      if (insets != null) {
        i = insets.top;
        j = insets.left;
        k = insets.bottom;
        m = insets.right;
      } 
      j += 5;
      k += 4;
      m += 5;
      Icon icon;
      if (WindowsLookAndFeel.isOnVista() && (icon = getIcon() instanceof UIResource || icon == null)) {
        i++;
        setIcon(null);
        icon = null;
        SortOrder sortOrder = getColumnSortOrder(param1JTable, param1Int2);
        if (sortOrder != null)
          switch (WindowsTableHeaderUI.null.$SwitchMap$javax$swing$SortOrder[sortOrder.ordinal()]) {
            case 1:
              icon = UIManager.getIcon("Table.ascendingSortIcon");
              break;
            case 2:
              icon = UIManager.getIcon("Table.descendingSortIcon");
              break;
          }  
        if (icon != null) {
          k = icon.getIconHeight();
          emptyBorder = new WindowsTableHeaderUI.IconBorder(icon, i, j, k, m);
        } else {
          icon = UIManager.getIcon("Table.ascendingSortIcon");
          int n = (icon != null) ? icon.getIconHeight() : 0;
          if (n != 0)
            k = n; 
          EmptyBorder emptyBorder1 = new EmptyBorder(n + i, j, k, m);
        } 
      } else {
        i += 3;
        emptyBorder = new EmptyBorder(i, j, k, m);
      } 
      setBorder(emptyBorder);
      return this;
    }
    
    public void paint(Graphics param1Graphics) {
      Dimension dimension = getSize();
      TMSchema.State state = TMSchema.State.NORMAL;
      TableColumn tableColumn = WindowsTableHeaderUI.this.header.getDraggedColumn();
      if (tableColumn != null && this.column == SwingUtilities2.convertColumnIndexToView(WindowsTableHeaderUI.this.header.getColumnModel(), tableColumn.getModelIndex())) {
        state = TMSchema.State.PRESSED;
      } else if (this.isSelected || this.hasFocus || this.hasRollover) {
        state = TMSchema.State.HOT;
      } 
      if (WindowsLookAndFeel.isOnVista()) {
        SortOrder sortOrder = getColumnSortOrder(WindowsTableHeaderUI.this.header.getTable(), this.column);
        if (sortOrder != null)
          switch (WindowsTableHeaderUI.null.$SwitchMap$javax$swing$SortOrder[sortOrder.ordinal()]) {
            case 1:
            case 2:
              switch (WindowsTableHeaderUI.null.$SwitchMap$com$sun$java$swing$plaf$windows$TMSchema$State[state.ordinal()]) {
                case 1:
                  state = TMSchema.State.SORTEDNORMAL;
                  break;
                case 2:
                  state = TMSchema.State.SORTEDPRESSED;
                  break;
                case 3:
                  state = TMSchema.State.SORTEDHOT;
                  break;
              } 
              break;
          }  
      } 
      this.skin.paintSkin(param1Graphics, 0, 0, dimension.width - 1, dimension.height - 1, state);
      super.paint(param1Graphics);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTableHeaderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */