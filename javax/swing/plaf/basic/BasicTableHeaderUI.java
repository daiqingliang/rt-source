package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableHeaderUI extends TableHeaderUI {
  private static Cursor resizeCursor = Cursor.getPredefinedCursor(11);
  
  protected JTableHeader header;
  
  protected CellRendererPane rendererPane;
  
  protected MouseInputListener mouseInputListener;
  
  private int rolloverColumn = -1;
  
  private int selectedColumnIndex = 0;
  
  private static FocusListener focusListener = new FocusListener() {
      public void focusGained(FocusEvent param1FocusEvent) { repaintHeader(param1FocusEvent.getSource()); }
      
      public void focusLost(FocusEvent param1FocusEvent) { repaintHeader(param1FocusEvent.getSource()); }
      
      private void repaintHeader(Object param1Object) {
        if (param1Object instanceof JTableHeader) {
          JTableHeader jTableHeader = (JTableHeader)param1Object;
          BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(jTableHeader.getUI(), BasicTableHeaderUI.class);
          if (basicTableHeaderUI == null)
            return; 
          jTableHeader.repaint(jTableHeader.getHeaderRect(basicTableHeaderUI.getSelectedColumnIndex()));
        } 
      }
    };
  
  protected MouseInputListener createMouseInputListener() { return new MouseInputHandler(); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTableHeaderUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.header = (JTableHeader)paramJComponent;
    this.rendererPane = new CellRendererPane();
    this.header.add(this.rendererPane);
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.header, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
    LookAndFeel.installProperty(this.header, "opaque", Boolean.TRUE);
  }
  
  protected void installListeners() {
    this.mouseInputListener = createMouseInputListener();
    this.header.addMouseListener(this.mouseInputListener);
    this.header.addMouseMotionListener(this.mouseInputListener);
    this.header.addFocusListener(focusListener);
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = (InputMap)DefaultLookup.get(this.header, this, "TableHeader.ancestorInputMap");
    SwingUtilities.replaceUIInputMap(this.header, 1, inputMap);
    LazyActionMap.installLazyActionMap(this.header, BasicTableHeaderUI.class, "TableHeader.actionMap");
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    this.header.remove(this.rendererPane);
    this.rendererPane = null;
    this.header = null;
  }
  
  protected void uninstallDefaults() {}
  
  protected void uninstallListeners() {
    this.header.removeMouseListener(this.mouseInputListener);
    this.header.removeMouseMotionListener(this.mouseInputListener);
    this.mouseInputListener = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.header, 0, null);
    SwingUtilities.replaceUIActionMap(this.header, null);
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("toggleSortOrder"));
    paramLazyActionMap.put(new Actions("selectColumnToLeft"));
    paramLazyActionMap.put(new Actions("selectColumnToRight"));
    paramLazyActionMap.put(new Actions("moveColumnLeft"));
    paramLazyActionMap.put(new Actions("moveColumnRight"));
    paramLazyActionMap.put(new Actions("resizeLeft"));
    paramLazyActionMap.put(new Actions("resizeRight"));
    paramLazyActionMap.put(new Actions("focusTable"));
  }
  
  protected int getRolloverColumn() { return this.rolloverColumn; }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2) {}
  
  private void updateRolloverColumn(MouseEvent paramMouseEvent) {
    if (this.header.getDraggedColumn() == null && this.header.contains(paramMouseEvent.getPoint())) {
      int i = this.header.columnAtPoint(paramMouseEvent.getPoint());
      if (i != this.rolloverColumn) {
        int j = this.rolloverColumn;
        this.rolloverColumn = i;
        rolloverColumnUpdated(j, this.rolloverColumn);
      } 
    } 
  }
  
  private int selectNextColumn(boolean paramBoolean) {
    int i = getSelectedColumnIndex();
    if (i < this.header.getColumnModel().getColumnCount() - 1) {
      i++;
      if (paramBoolean)
        selectColumn(i); 
    } 
    return i;
  }
  
  private int selectPreviousColumn(boolean paramBoolean) {
    int i = getSelectedColumnIndex();
    if (i > 0) {
      i--;
      if (paramBoolean)
        selectColumn(i); 
    } 
    return i;
  }
  
  void selectColumn(int paramInt) { selectColumn(paramInt, true); }
  
  void selectColumn(int paramInt, boolean paramBoolean) {
    Rectangle rectangle = this.header.getHeaderRect(this.selectedColumnIndex);
    this.header.repaint(rectangle);
    this.selectedColumnIndex = paramInt;
    rectangle = this.header.getHeaderRect(paramInt);
    this.header.repaint(rectangle);
    if (paramBoolean)
      scrollToColumn(paramInt); 
  }
  
  private void scrollToColumn(int paramInt) {
    Container container;
    JTable jTable;
    if (this.header.getParent() == null || (container = this.header.getParent().getParent()) == null || !(container instanceof JScrollPane) || (jTable = this.header.getTable()) == null)
      return; 
    Rectangle rectangle1 = jTable.getVisibleRect();
    Rectangle rectangle2 = jTable.getCellRect(0, paramInt, true);
    rectangle1.x = rectangle2.x;
    rectangle1.width = rectangle2.width;
    jTable.scrollRectToVisible(rectangle1);
  }
  
  private int getSelectedColumnIndex() {
    int i = this.header.getColumnModel().getColumnCount();
    if (this.selectedColumnIndex >= i && i > 0)
      this.selectedColumnIndex = i - 1; 
    return this.selectedColumnIndex;
  }
  
  private static boolean canResize(TableColumn paramTableColumn, JTableHeader paramJTableHeader) { return (paramTableColumn != null && paramJTableHeader.getResizingAllowed() && paramTableColumn.getResizable()); }
  
  private int changeColumnWidth(TableColumn paramTableColumn, JTableHeader paramJTableHeader, int paramInt1, int paramInt2) {
    paramTableColumn.setWidth(paramInt2);
    Container container;
    JTable jTable;
    if (paramJTableHeader.getParent() == null || (container = paramJTableHeader.getParent().getParent()) == null || !(container instanceof JScrollPane) || (jTable = paramJTableHeader.getTable()) == null)
      return 0; 
    if (!container.getComponentOrientation().isLeftToRight() && !paramJTableHeader.getComponentOrientation().isLeftToRight()) {
      JViewport jViewport = ((JScrollPane)container).getViewport();
      int i = jViewport.getWidth();
      int j = paramInt2 - paramInt1;
      int k = jTable.getWidth() + j;
      Dimension dimension = jTable.getSize();
      dimension.width += j;
      jTable.setSize(dimension);
      if (k >= i && jTable.getAutoResizeMode() == 0) {
        Point point = jViewport.getViewPosition();
        point.x = Math.max(0, Math.min(k - i, point.x + j));
        jViewport.setViewPosition(point);
        return j;
      } 
    } 
    return 0;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = -1;
    TableColumnModel tableColumnModel = this.header.getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      TableColumn tableColumn = tableColumnModel.getColumn(b);
      Component component = getHeaderRenderer(b);
      Dimension dimension = component.getPreferredSize();
      int j = component.getBaseline(dimension.width, paramInt2);
      if (j >= 0)
        if (i == -1) {
          i = j;
        } else if (i != j) {
          i = -1;
          break;
        }  
    } 
    return i;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (this.header.getColumnModel().getColumnCount() <= 0)
      return; 
    boolean bool = this.header.getComponentOrientation().isLeftToRight();
    Rectangle rectangle1 = paramGraphics.getClipBounds();
    Point point1 = rectangle1.getLocation();
    Point point2 = new Point(rectangle1.x + rectangle1.width - 1, rectangle1.y);
    TableColumnModel tableColumnModel = this.header.getColumnModel();
    int i = this.header.columnAtPoint(bool ? point1 : point2);
    int j = this.header.columnAtPoint(bool ? point2 : point1);
    if (i == -1)
      i = 0; 
    if (j == -1)
      j = tableColumnModel.getColumnCount() - 1; 
    TableColumn tableColumn = this.header.getDraggedColumn();
    Rectangle rectangle2 = this.header.getHeaderRect(bool ? i : j);
    if (bool) {
      for (int k = i; k <= j; k++) {
        TableColumn tableColumn1 = tableColumnModel.getColumn(k);
        int m = tableColumn1.getWidth();
        rectangle2.width = m;
        if (tableColumn1 != tableColumn)
          paintCell(paramGraphics, rectangle2, k); 
        rectangle2.x += m;
      } 
    } else {
      for (int k = j; k >= i; k--) {
        TableColumn tableColumn1 = tableColumnModel.getColumn(k);
        int m = tableColumn1.getWidth();
        rectangle2.width = m;
        if (tableColumn1 != tableColumn)
          paintCell(paramGraphics, rectangle2, k); 
        rectangle2.x += m;
      } 
    } 
    if (tableColumn != null) {
      int k = viewIndexForColumn(tableColumn);
      Rectangle rectangle = this.header.getHeaderRect(k);
      paramGraphics.setColor(this.header.getParent().getBackground());
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      rectangle.x += this.header.getDraggedDistance();
      paramGraphics.setColor(this.header.getBackground());
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      paintCell(paramGraphics, rectangle, k);
    } 
    this.rendererPane.removeAll();
  }
  
  private Component getHeaderRenderer(int paramInt) {
    TableColumn tableColumn = this.header.getColumnModel().getColumn(paramInt);
    TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
    if (tableCellRenderer == null)
      tableCellRenderer = this.header.getDefaultRenderer(); 
    boolean bool = (!this.header.isPaintingForPrint() && paramInt == getSelectedColumnIndex() && this.header.hasFocus());
    return tableCellRenderer.getTableCellRendererComponent(this.header.getTable(), tableColumn.getHeaderValue(), false, bool, -1, paramInt);
  }
  
  private void paintCell(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    Component component = getHeaderRenderer(paramInt);
    this.rendererPane.paintComponent(paramGraphics, component, this.header, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, true);
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn) {
    TableColumnModel tableColumnModel = this.header.getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      if (tableColumnModel.getColumn(b) == paramTableColumn)
        return b; 
    } 
    return -1;
  }
  
  private int getHeaderHeight() {
    int i = 0;
    boolean bool = false;
    TableColumnModel tableColumnModel = this.header.getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      TableColumn tableColumn = tableColumnModel.getColumn(b);
      boolean bool1 = (tableColumn.getHeaderRenderer() == null) ? 1 : 0;
      if (!bool1 || !bool) {
        Component component = getHeaderRenderer(b);
        int j = (component.getPreferredSize()).height;
        i = Math.max(i, j);
        if (bool1 && j > 0) {
          Object object = tableColumn.getHeaderValue();
          if (object != null) {
            object = object.toString();
            if (object != null && !object.equals(""))
              bool = true; 
          } 
        } 
      } 
    } 
    return i;
  }
  
  private Dimension createHeaderSize(long paramLong) {
    if (paramLong > 2147483647L)
      paramLong = 2147483647L; 
    return new Dimension((int)paramLong, getHeaderHeight());
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.header.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getMinWidth();
    } 
    return createHeaderSize(l);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.header.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getPreferredWidth();
    } 
    return createHeaderSize(l);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.header.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getMaxWidth();
    } 
    return createHeaderSize(l);
  }
  
  private static class Actions extends UIAction {
    public static final String TOGGLE_SORT_ORDER = "toggleSortOrder";
    
    public static final String SELECT_COLUMN_TO_LEFT = "selectColumnToLeft";
    
    public static final String SELECT_COLUMN_TO_RIGHT = "selectColumnToRight";
    
    public static final String MOVE_COLUMN_LEFT = "moveColumnLeft";
    
    public static final String MOVE_COLUMN_RIGHT = "moveColumnRight";
    
    public static final String RESIZE_LEFT = "resizeLeft";
    
    public static final String RESIZE_RIGHT = "resizeRight";
    
    public static final String FOCUS_TABLE = "focusTable";
    
    public Actions(String param1String) { super(param1String); }
    
    public boolean isEnabled(Object param1Object) {
      if (param1Object instanceof JTableHeader) {
        JTableHeader jTableHeader = (JTableHeader)param1Object;
        TableColumnModel tableColumnModel = jTableHeader.getColumnModel();
        if (tableColumnModel.getColumnCount() <= 0)
          return false; 
        String str = getName();
        BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(jTableHeader.getUI(), BasicTableHeaderUI.class);
        if (basicTableHeaderUI != null) {
          if (str == "moveColumnLeft")
            return (jTableHeader.getReorderingAllowed() && maybeMoveColumn(true, jTableHeader, basicTableHeaderUI, false)); 
          if (str == "moveColumnRight")
            return (jTableHeader.getReorderingAllowed() && maybeMoveColumn(false, jTableHeader, basicTableHeaderUI, false)); 
          if (str == "resizeLeft" || str == "resizeRight")
            return BasicTableHeaderUI.canResize(tableColumnModel.getColumn(basicTableHeaderUI.getSelectedColumnIndex()), jTableHeader); 
          if (str == "focusTable")
            return (jTableHeader.getTable() != null); 
        } 
      } 
      return true;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTableHeader jTableHeader = (JTableHeader)param1ActionEvent.getSource();
      BasicTableHeaderUI basicTableHeaderUI = (BasicTableHeaderUI)BasicLookAndFeel.getUIOfType(jTableHeader.getUI(), BasicTableHeaderUI.class);
      if (basicTableHeaderUI == null)
        return; 
      String str = getName();
      if ("toggleSortOrder" == str) {
        JTable jTable = jTableHeader.getTable();
        RowSorter rowSorter = (jTable == null) ? null : jTable.getRowSorter();
        if (rowSorter != null) {
          int i = basicTableHeaderUI.getSelectedColumnIndex();
          i = jTable.convertColumnIndexToModel(i);
          rowSorter.toggleSortOrder(i);
        } 
      } else if ("selectColumnToLeft" == str) {
        if (jTableHeader.getComponentOrientation().isLeftToRight()) {
          basicTableHeaderUI.selectPreviousColumn(true);
        } else {
          basicTableHeaderUI.selectNextColumn(true);
        } 
      } else if ("selectColumnToRight" == str) {
        if (jTableHeader.getComponentOrientation().isLeftToRight()) {
          basicTableHeaderUI.selectNextColumn(true);
        } else {
          basicTableHeaderUI.selectPreviousColumn(true);
        } 
      } else if ("moveColumnLeft" == str) {
        moveColumn(true, jTableHeader, basicTableHeaderUI);
      } else if ("moveColumnRight" == str) {
        moveColumn(false, jTableHeader, basicTableHeaderUI);
      } else if ("resizeLeft" == str) {
        resize(true, jTableHeader, basicTableHeaderUI);
      } else if ("resizeRight" == str) {
        resize(false, jTableHeader, basicTableHeaderUI);
      } else if ("focusTable" == str) {
        JTable jTable = jTableHeader.getTable();
        if (jTable != null)
          jTable.requestFocusInWindow(); 
      } 
    }
    
    private void moveColumn(boolean param1Boolean, JTableHeader param1JTableHeader, BasicTableHeaderUI param1BasicTableHeaderUI) { maybeMoveColumn(param1Boolean, param1JTableHeader, param1BasicTableHeaderUI, true); }
    
    private boolean maybeMoveColumn(boolean param1Boolean1, JTableHeader param1JTableHeader, BasicTableHeaderUI param1BasicTableHeaderUI, boolean param1Boolean2) {
      int j;
      int i = param1BasicTableHeaderUI.getSelectedColumnIndex();
      if (param1JTableHeader.getComponentOrientation().isLeftToRight()) {
        j = param1Boolean1 ? param1BasicTableHeaderUI.selectPreviousColumn(param1Boolean2) : param1BasicTableHeaderUI.selectNextColumn(param1Boolean2);
      } else {
        j = param1Boolean1 ? param1BasicTableHeaderUI.selectNextColumn(param1Boolean2) : param1BasicTableHeaderUI.selectPreviousColumn(param1Boolean2);
      } 
      if (j != i)
        if (param1Boolean2) {
          param1JTableHeader.getColumnModel().moveColumn(i, j);
        } else {
          return true;
        }  
      return false;
    }
    
    private void resize(boolean param1Boolean, JTableHeader param1JTableHeader, BasicTableHeaderUI param1BasicTableHeaderUI) {
      int i = param1BasicTableHeaderUI.getSelectedColumnIndex();
      TableColumn tableColumn = param1JTableHeader.getColumnModel().getColumn(i);
      param1JTableHeader.setResizingColumn(tableColumn);
      int j = tableColumn.getWidth();
      int k = j;
      if (param1JTableHeader.getComponentOrientation().isLeftToRight()) {
        k += (param1Boolean ? -1 : 1);
      } else {
        k += (param1Boolean ? 1 : -1);
      } 
      param1BasicTableHeaderUI.changeColumnWidth(tableColumn, param1JTableHeader, j, k);
    }
  }
  
  public class MouseInputHandler implements MouseInputListener {
    private int mouseXOffset;
    
    private Cursor otherCursor = resizeCursor;
    
    public void mouseClicked(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      if (param1MouseEvent.getClickCount() % 2 == 1 && SwingUtilities.isLeftMouseButton(param1MouseEvent)) {
        JTable jTable = BasicTableHeaderUI.this.header.getTable();
        RowSorter rowSorter;
        if (jTable != null && (rowSorter = jTable.getRowSorter()) != null) {
          int i = BasicTableHeaderUI.this.header.columnAtPoint(param1MouseEvent.getPoint());
          if (i != -1) {
            i = jTable.convertColumnIndexToModel(i);
            rowSorter.toggleSortOrder(i);
          } 
        } 
      } 
    }
    
    private TableColumn getResizingColumn(Point param1Point) { return getResizingColumn(param1Point, BasicTableHeaderUI.this.header.columnAtPoint(param1Point)); }
    
    private TableColumn getResizingColumn(Point param1Point, int param1Int) {
      int j;
      if (param1Int == -1)
        return null; 
      Rectangle rectangle = BasicTableHeaderUI.this.header.getHeaderRect(param1Int);
      rectangle.grow(-3, 0);
      if (rectangle.contains(param1Point))
        return null; 
      int i = rectangle.x + rectangle.width / 2;
      if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
        j = (param1Point.x < i) ? (param1Int - 1) : param1Int;
      } else {
        j = (param1Point.x < i) ? param1Int : (param1Int - 1);
      } 
      return (j == -1) ? null : BasicTableHeaderUI.this.header.getColumnModel().getColumn(j);
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      BasicTableHeaderUI.this.header.setDraggedColumn(null);
      BasicTableHeaderUI.this.header.setResizingColumn(null);
      BasicTableHeaderUI.this.header.setDraggedDistance(0);
      Point point = param1MouseEvent.getPoint();
      TableColumnModel tableColumnModel = BasicTableHeaderUI.this.header.getColumnModel();
      int i = BasicTableHeaderUI.this.header.columnAtPoint(point);
      if (i != -1) {
        TableColumn tableColumn = getResizingColumn(point, i);
        if (BasicTableHeaderUI.canResize(tableColumn, BasicTableHeaderUI.this.header)) {
          BasicTableHeaderUI.this.header.setResizingColumn(tableColumn);
          if (BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight()) {
            this.mouseXOffset = point.x - tableColumn.getWidth();
          } else {
            this.mouseXOffset = point.x + tableColumn.getWidth();
          } 
        } else if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
          TableColumn tableColumn1 = tableColumnModel.getColumn(i);
          BasicTableHeaderUI.this.header.setDraggedColumn(tableColumn1);
          this.mouseXOffset = point.x;
        } 
      } 
      if (BasicTableHeaderUI.this.header.getReorderingAllowed()) {
        int j = BasicTableHeaderUI.this.rolloverColumn;
        BasicTableHeaderUI.this.rolloverColumn = -1;
        BasicTableHeaderUI.this.rolloverColumnUpdated(j, BasicTableHeaderUI.this.rolloverColumn);
      } 
    }
    
    private void swapCursor() {
      Cursor cursor = BasicTableHeaderUI.this.header.getCursor();
      BasicTableHeaderUI.this.header.setCursor(this.otherCursor);
      this.otherCursor = cursor;
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      if (BasicTableHeaderUI.canResize(getResizingColumn(param1MouseEvent.getPoint()), BasicTableHeaderUI.this.header) != ((BasicTableHeaderUI.this.header.getCursor() == resizeCursor)))
        swapCursor(); 
      BasicTableHeaderUI.this.updateRolloverColumn(param1MouseEvent);
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      int i = param1MouseEvent.getX();
      TableColumn tableColumn1 = BasicTableHeaderUI.this.header.getResizingColumn();
      TableColumn tableColumn2 = BasicTableHeaderUI.this.header.getDraggedColumn();
      boolean bool = BasicTableHeaderUI.this.header.getComponentOrientation().isLeftToRight();
      if (tableColumn1 != null) {
        int k;
        int j = tableColumn1.getWidth();
        if (bool) {
          k = i - this.mouseXOffset;
        } else {
          k = this.mouseXOffset - i;
        } 
        this.mouseXOffset += BasicTableHeaderUI.this.changeColumnWidth(tableColumn1, BasicTableHeaderUI.this.header, j, k);
      } else if (tableColumn2 != null) {
        TableColumnModel tableColumnModel = BasicTableHeaderUI.this.header.getColumnModel();
        int j = i - this.mouseXOffset;
        int k = (j < 0) ? -1 : 1;
        int m = BasicTableHeaderUI.this.viewIndexForColumn(tableColumn2);
        int n = m + (bool ? k : -k);
        if (0 <= n && n < tableColumnModel.getColumnCount()) {
          int i1 = tableColumnModel.getColumn(n).getWidth();
          if (Math.abs(j) > i1 / 2) {
            this.mouseXOffset += k * i1;
            BasicTableHeaderUI.this.header.setDraggedDistance(j - k * i1);
            int i2 = SwingUtilities2.convertColumnIndexToModel(BasicTableHeaderUI.this.header.getColumnModel(), BasicTableHeaderUI.this.getSelectedColumnIndex());
            tableColumnModel.moveColumn(m, n);
            BasicTableHeaderUI.this.selectColumn(SwingUtilities2.convertColumnIndexToView(BasicTableHeaderUI.this.header.getColumnModel(), i2), false);
            return;
          } 
        } 
        setDraggedDistance(j, m);
      } 
      BasicTableHeaderUI.this.updateRolloverColumn(param1MouseEvent);
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      setDraggedDistance(0, BasicTableHeaderUI.this.viewIndexForColumn(BasicTableHeaderUI.this.header.getDraggedColumn()));
      BasicTableHeaderUI.this.header.setResizingColumn(null);
      BasicTableHeaderUI.this.header.setDraggedColumn(null);
      BasicTableHeaderUI.this.updateRolloverColumn(param1MouseEvent);
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      BasicTableHeaderUI.this.updateRolloverColumn(param1MouseEvent);
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (!BasicTableHeaderUI.this.header.isEnabled())
        return; 
      int i = BasicTableHeaderUI.this.rolloverColumn;
      BasicTableHeaderUI.this.rolloverColumn = -1;
      BasicTableHeaderUI.this.rolloverColumnUpdated(i, BasicTableHeaderUI.this.rolloverColumn);
    }
    
    private void setDraggedDistance(int param1Int1, int param1Int2) {
      BasicTableHeaderUI.this.header.setDraggedDistance(param1Int1);
      if (param1Int2 != -1)
        BasicTableHeaderUI.this.header.getColumnModel().moveColumn(param1Int2, param1Int2); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTableHeaderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */