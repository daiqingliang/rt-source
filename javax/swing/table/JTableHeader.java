package javax.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.TableHeaderUI;
import sun.awt.AWTAccessor;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class JTableHeader extends JComponent implements TableColumnModelListener, Accessible {
  private static final String uiClassID = "TableHeaderUI";
  
  protected JTable table;
  
  protected TableColumnModel columnModel;
  
  protected boolean reorderingAllowed;
  
  protected boolean resizingAllowed;
  
  protected boolean updateTableInRealTime;
  
  protected TableColumn resizingColumn;
  
  protected TableColumn draggedColumn;
  
  protected int draggedDistance;
  
  private TableCellRenderer defaultRenderer;
  
  public JTableHeader() { this(null); }
  
  public JTableHeader(TableColumnModel paramTableColumnModel) {
    if (paramTableColumnModel == null)
      paramTableColumnModel = createDefaultColumnModel(); 
    setColumnModel(paramTableColumnModel);
    initializeLocalVars();
    updateUI();
  }
  
  public void setTable(JTable paramJTable) {
    JTable jTable = this.table;
    this.table = paramJTable;
    firePropertyChange("table", jTable, paramJTable);
  }
  
  public JTable getTable() { return this.table; }
  
  public void setReorderingAllowed(boolean paramBoolean) {
    boolean bool = this.reorderingAllowed;
    this.reorderingAllowed = paramBoolean;
    firePropertyChange("reorderingAllowed", bool, paramBoolean);
  }
  
  public boolean getReorderingAllowed() { return this.reorderingAllowed; }
  
  public void setResizingAllowed(boolean paramBoolean) {
    boolean bool = this.resizingAllowed;
    this.resizingAllowed = paramBoolean;
    firePropertyChange("resizingAllowed", bool, paramBoolean);
  }
  
  public boolean getResizingAllowed() { return this.resizingAllowed; }
  
  public TableColumn getDraggedColumn() { return this.draggedColumn; }
  
  public int getDraggedDistance() { return this.draggedDistance; }
  
  public TableColumn getResizingColumn() { return this.resizingColumn; }
  
  public void setUpdateTableInRealTime(boolean paramBoolean) { this.updateTableInRealTime = paramBoolean; }
  
  public boolean getUpdateTableInRealTime() { return this.updateTableInRealTime; }
  
  public void setDefaultRenderer(TableCellRenderer paramTableCellRenderer) { this.defaultRenderer = paramTableCellRenderer; }
  
  @Transient
  public TableCellRenderer getDefaultRenderer() { return this.defaultRenderer; }
  
  public int columnAtPoint(Point paramPoint) {
    int i = paramPoint.x;
    if (!getComponentOrientation().isLeftToRight())
      i = getWidthInRightToLeft() - i - 1; 
    return getColumnModel().getColumnIndexAtX(i);
  }
  
  public Rectangle getHeaderRect(int paramInt) {
    Rectangle rectangle = new Rectangle();
    TableColumnModel tableColumnModel = getColumnModel();
    rectangle.height = getHeight();
    if (paramInt < 0) {
      if (!getComponentOrientation().isLeftToRight())
        rectangle.x = getWidthInRightToLeft(); 
    } else if (paramInt >= tableColumnModel.getColumnCount()) {
      if (getComponentOrientation().isLeftToRight())
        rectangle.x = getWidth(); 
    } else {
      for (byte b = 0; b < paramInt; b++)
        rectangle.x += tableColumnModel.getColumn(b).getWidth(); 
      if (!getComponentOrientation().isLeftToRight())
        rectangle.x = getWidthInRightToLeft() - rectangle.x - tableColumnModel.getColumn(paramInt).getWidth(); 
      rectangle.width = tableColumnModel.getColumn(paramInt).getWidth();
    } 
    return rectangle;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    String str = null;
    Point point = paramMouseEvent.getPoint();
    int i;
    if ((i = columnAtPoint(point)) != -1) {
      TableColumn tableColumn = this.columnModel.getColumn(i);
      TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
      if (tableCellRenderer == null)
        tableCellRenderer = this.defaultRenderer; 
      Component component = tableCellRenderer.getTableCellRendererComponent(getTable(), tableColumn.getHeaderValue(), false, false, -1, i);
      if (component instanceof JComponent) {
        Rectangle rectangle = getHeaderRect(i);
        point.translate(-rectangle.x, -rectangle.y);
        MouseEvent mouseEvent = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
        AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
        mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
        str = ((JComponent)component).getToolTipText(mouseEvent);
      } 
    } 
    if (str == null)
      str = getToolTipText(); 
    return str;
  }
  
  public TableHeaderUI getUI() { return (TableHeaderUI)this.ui; }
  
  public void setUI(TableHeaderUI paramTableHeaderUI) {
    if (this.ui != paramTableHeaderUI) {
      setUI(paramTableHeaderUI);
      repaint();
    } 
  }
  
  public void updateUI() {
    setUI((TableHeaderUI)UIManager.getUI(this));
    TableCellRenderer tableCellRenderer = getDefaultRenderer();
    if (tableCellRenderer instanceof Component)
      SwingUtilities.updateComponentTreeUI((Component)tableCellRenderer); 
  }
  
  public String getUIClassID() { return "TableHeaderUI"; }
  
  public void setColumnModel(TableColumnModel paramTableColumnModel) {
    if (paramTableColumnModel == null)
      throw new IllegalArgumentException("Cannot set a null ColumnModel"); 
    TableColumnModel tableColumnModel = this.columnModel;
    if (paramTableColumnModel != tableColumnModel) {
      if (tableColumnModel != null)
        tableColumnModel.removeColumnModelListener(this); 
      this.columnModel = paramTableColumnModel;
      paramTableColumnModel.addColumnModelListener(this);
      firePropertyChange("columnModel", tableColumnModel, paramTableColumnModel);
      resizeAndRepaint();
    } 
  }
  
  public TableColumnModel getColumnModel() { return this.columnModel; }
  
  public void columnAdded(TableColumnModelEvent paramTableColumnModelEvent) { resizeAndRepaint(); }
  
  public void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent) { resizeAndRepaint(); }
  
  public void columnMoved(TableColumnModelEvent paramTableColumnModelEvent) { repaint(); }
  
  public void columnMarginChanged(ChangeEvent paramChangeEvent) { resizeAndRepaint(); }
  
  public void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent) {}
  
  protected TableColumnModel createDefaultColumnModel() { return new DefaultTableColumnModel(); }
  
  protected TableCellRenderer createDefaultRenderer() { return new DefaultTableCellHeaderRenderer(); }
  
  protected void initializeLocalVars() {
    setOpaque(true);
    this.table = null;
    this.reorderingAllowed = true;
    this.resizingAllowed = true;
    this.draggedColumn = null;
    this.draggedDistance = 0;
    this.resizingColumn = null;
    this.updateTableInRealTime = true;
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(this);
    setDefaultRenderer(createDefaultRenderer());
  }
  
  public void resizeAndRepaint() {
    revalidate();
    repaint();
  }
  
  public void setDraggedColumn(TableColumn paramTableColumn) { this.draggedColumn = paramTableColumn; }
  
  public void setDraggedDistance(int paramInt) { this.draggedDistance = paramInt; }
  
  public void setResizingColumn(TableColumn paramTableColumn) { this.resizingColumn = paramTableColumn; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.ui != null && getUIClassID().equals("TableHeaderUI"))
      this.ui.installUI(this); 
  }
  
  private int getWidthInRightToLeft() { return (this.table != null && this.table.getAutoResizeMode() != 0) ? this.table.getWidth() : getWidth(); }
  
  protected String paramString() {
    String str1 = this.reorderingAllowed ? "true" : "false";
    String str2 = this.resizingAllowed ? "true" : "false";
    String str3 = this.updateTableInRealTime ? "true" : "false";
    return super.paramString() + ",draggedDistance=" + this.draggedDistance + ",reorderingAllowed=" + str1 + ",resizingAllowed=" + str2 + ",updateTableInRealTime=" + str3;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTableHeader(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJTableHeader extends JComponent.AccessibleJComponent {
    protected AccessibleJTableHeader() { super(JTableHeader.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.PANEL; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      int i;
      if ((i = JTableHeader.this.columnAtPoint(param1Point)) != -1) {
        TableColumn tableColumn = JTableHeader.this.columnModel.getColumn(i);
        TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
        if (tableCellRenderer == null)
          if (JTableHeader.this.defaultRenderer != null) {
            tableCellRenderer = JTableHeader.this.defaultRenderer;
          } else {
            return null;
          }  
        Component component = tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), tableColumn.getHeaderValue(), false, false, -1, i);
        return new AccessibleJTableHeaderEntry(i, JTableHeader.this, JTableHeader.this.table);
      } 
      return null;
    }
    
    public int getAccessibleChildrenCount() { return JTableHeader.this.columnModel.getColumnCount(); }
    
    public Accessible getAccessibleChild(int param1Int) {
      if (param1Int < 0 || param1Int >= getAccessibleChildrenCount())
        return null; 
      TableColumn tableColumn = JTableHeader.this.columnModel.getColumn(param1Int);
      TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
      if (tableCellRenderer == null)
        if (JTableHeader.this.defaultRenderer != null) {
          tableCellRenderer = JTableHeader.this.defaultRenderer;
        } else {
          return null;
        }  
      Component component = tableCellRenderer.getTableCellRendererComponent(JTableHeader.this.getTable(), tableColumn.getHeaderValue(), false, false, -1, param1Int);
      return new AccessibleJTableHeaderEntry(param1Int, JTableHeader.this, JTableHeader.this.table);
    }
    
    protected class AccessibleJTableHeaderEntry extends AccessibleContext implements Accessible, AccessibleComponent {
      private JTableHeader parent;
      
      private int column;
      
      private JTable table;
      
      public AccessibleJTableHeaderEntry(int param2Int, JTableHeader param2JTableHeader, JTable param2JTable) {
        this.parent = param2JTableHeader;
        this.column = param2Int;
        this.table = param2JTable;
        setAccessibleParent(this.parent);
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      private AccessibleContext getCurrentAccessibleContext() {
        TableColumnModel tableColumnModel = this.table.getColumnModel();
        if (tableColumnModel != null) {
          if (this.column < 0 || this.column >= tableColumnModel.getColumnCount())
            return null; 
          TableColumn tableColumn = tableColumnModel.getColumn(this.column);
          TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
          if (tableCellRenderer == null)
            if (JTableHeader.AccessibleJTableHeader.this.this$0.defaultRenderer != null) {
              tableCellRenderer = JTableHeader.AccessibleJTableHeader.this.this$0.defaultRenderer;
            } else {
              return null;
            }  
          Component component = tableCellRenderer.getTableCellRendererComponent(JTableHeader.AccessibleJTableHeader.this.this$0.getTable(), tableColumn.getHeaderValue(), false, false, -1, this.column);
          if (component instanceof Accessible)
            return ((Accessible)component).getAccessibleContext(); 
        } 
        return null;
      }
      
      private Component getCurrentComponent() {
        TableColumnModel tableColumnModel = this.table.getColumnModel();
        if (tableColumnModel != null) {
          if (this.column < 0 || this.column >= tableColumnModel.getColumnCount())
            return null; 
          TableColumn tableColumn = tableColumnModel.getColumn(this.column);
          TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
          if (tableCellRenderer == null)
            if (JTableHeader.AccessibleJTableHeader.this.this$0.defaultRenderer != null) {
              tableCellRenderer = JTableHeader.AccessibleJTableHeader.this.this$0.defaultRenderer;
            } else {
              return null;
            }  
          return tableCellRenderer.getTableCellRendererComponent(JTableHeader.AccessibleJTableHeader.this.this$0.getTable(), tableColumn.getHeaderValue(), false, false, -1, this.column);
        } 
        return null;
      }
      
      public String getAccessibleName() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          String str1 = accessibleContext.getAccessibleName();
          if (str1 != null && str1 != "")
            return str1; 
        } 
        if (this.accessibleName != null && this.accessibleName != "")
          return this.accessibleName; 
        String str = (String)JTableHeader.AccessibleJTableHeader.this.this$0.getClientProperty("AccessibleName");
        return (str != null) ? str : this.table.getColumnName(this.column);
      }
      
      public void setAccessibleName(String param2String) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.setAccessibleName(param2String);
        } else {
          super.setAccessibleName(param2String);
        } 
      }
      
      public String getAccessibleDescription() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getAccessibleDescription() : super.getAccessibleDescription();
      }
      
      public void setAccessibleDescription(String param2String) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.setAccessibleDescription(param2String);
        } else {
          super.setAccessibleDescription(param2String);
        } 
      }
      
      public AccessibleRole getAccessibleRole() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getAccessibleRole() : AccessibleRole.COLUMN_HEADER;
      }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          AccessibleStateSet accessibleStateSet = accessibleContext.getAccessibleStateSet();
          if (isShowing())
            accessibleStateSet.add(AccessibleState.SHOWING); 
          return accessibleStateSet;
        } 
        return new AccessibleStateSet();
      }
      
      public int getAccessibleIndexInParent() { return this.column; }
      
      public int getAccessibleChildrenCount() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getAccessibleChildrenCount() : 0;
      }
      
      public Accessible getAccessibleChild(int param2Int) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          Accessible accessible = accessibleContext.getAccessibleChild(param2Int);
          accessibleContext.setAccessibleParent(this);
          return accessible;
        } 
        return null;
      }
      
      public Locale getLocale() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext != null) ? accessibleContext.getLocale() : null;
      }
      
      public void addPropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.addPropertyChangeListener(param2PropertyChangeListener);
        } else {
          super.addPropertyChangeListener(param2PropertyChangeListener);
        } 
      }
      
      public void removePropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          accessibleContext.removePropertyChangeListener(param2PropertyChangeListener);
        } else {
          super.removePropertyChangeListener(param2PropertyChangeListener);
        } 
      }
      
      public AccessibleAction getAccessibleAction() { return getCurrentAccessibleContext().getAccessibleAction(); }
      
      public AccessibleComponent getAccessibleComponent() { return this; }
      
      public AccessibleSelection getAccessibleSelection() { return getCurrentAccessibleContext().getAccessibleSelection(); }
      
      public AccessibleText getAccessibleText() { return getCurrentAccessibleContext().getAccessibleText(); }
      
      public AccessibleValue getAccessibleValue() { return getCurrentAccessibleContext().getAccessibleValue(); }
      
      public Color getBackground() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getBackground(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getBackground() : null;
      }
      
      public void setBackground(Color param2Color) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setBackground(param2Color);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setBackground(param2Color); 
        } 
      }
      
      public Color getForeground() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getForeground(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getForeground() : null;
      }
      
      public void setForeground(Color param2Color) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setForeground(param2Color);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setForeground(param2Color); 
        } 
      }
      
      public Cursor getCursor() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getCursor(); 
        Component component = getCurrentComponent();
        if (component != null)
          return component.getCursor(); 
        Accessible accessible = getAccessibleParent();
        return (accessible instanceof AccessibleComponent) ? ((AccessibleComponent)accessible).getCursor() : null;
      }
      
      public void setCursor(Cursor param2Cursor) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setCursor(param2Cursor);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setCursor(param2Cursor); 
        } 
      }
      
      public Font getFont() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getFont(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getFont() : null;
      }
      
      public void setFont(Font param2Font) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setFont(param2Font);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setFont(param2Font); 
        } 
      }
      
      public FontMetrics getFontMetrics(Font param2Font) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).getFontMetrics(param2Font); 
        Component component = getCurrentComponent();
        return (component != null) ? component.getFontMetrics(param2Font) : null;
      }
      
      public boolean isEnabled() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).isEnabled(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isEnabled() : 0;
      }
      
      public void setEnabled(boolean param2Boolean) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setEnabled(param2Boolean);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setEnabled(param2Boolean); 
        } 
      }
      
      public boolean isVisible() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).isVisible(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isVisible() : 0;
      }
      
      public void setVisible(boolean param2Boolean) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setVisible(param2Boolean);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setVisible(param2Boolean); 
        } 
      }
      
      public boolean isShowing() { return (isVisible() && JTableHeader.AccessibleJTableHeader.this.this$0.isShowing()); }
      
      public boolean contains(Point param2Point) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          Rectangle rectangle = ((AccessibleComponent)accessibleContext).getBounds();
          return rectangle.contains(param2Point);
        } 
        Component component = getCurrentComponent();
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          return rectangle.contains(param2Point);
        } 
        return getBounds().contains(param2Point);
      }
      
      public Point getLocationOnScreen() {
        if (this.parent != null) {
          Point point1 = this.parent.getLocationOnScreen();
          Point point2 = getLocation();
          point2.translate(point1.x, point1.y);
          return point2;
        } 
        return null;
      }
      
      public Point getLocation() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          Rectangle rectangle = ((AccessibleComponent)accessibleContext).getBounds();
          return rectangle.getLocation();
        } 
        Component component = getCurrentComponent();
        if (component != null) {
          Rectangle rectangle = component.getBounds();
          return rectangle.getLocation();
        } 
        return getBounds().getLocation();
      }
      
      public void setLocation(Point param2Point) {}
      
      public Rectangle getBounds() {
        Rectangle rectangle = this.table.getCellRect(-1, this.column, false);
        rectangle.y = 0;
        return rectangle;
      }
      
      public void setBounds(Rectangle param2Rectangle) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setBounds(param2Rectangle);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setBounds(param2Rectangle); 
        } 
      }
      
      public Dimension getSize() { return getBounds().getSize(); }
      
      public void setSize(Dimension param2Dimension) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).setSize(param2Dimension);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.setSize(param2Dimension); 
        } 
      }
      
      public Accessible getAccessibleAt(Point param2Point) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        return (accessibleContext instanceof AccessibleComponent) ? ((AccessibleComponent)accessibleContext).getAccessibleAt(param2Point) : null;
      }
      
      public boolean isFocusTraversable() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext).isFocusTraversable(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isFocusTraversable() : 0;
      }
      
      public void requestFocus() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).requestFocus();
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.requestFocus(); 
        } 
      }
      
      public void addFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).addFocusListener(param2FocusListener);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.addFocusListener(param2FocusListener); 
        } 
      }
      
      public void removeFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext).removeFocusListener(param2FocusListener);
        } else {
          Component component = getCurrentComponent();
          if (component != null)
            component.removeFocusListener(param2FocusListener); 
        } 
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\JTableHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */