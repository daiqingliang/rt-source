package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTableUI extends TableUI {
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("Table.baselineComponent");
  
  protected JTable table;
  
  protected CellRendererPane rendererPane;
  
  protected KeyListener keyListener;
  
  protected FocusListener focusListener;
  
  protected MouseInputListener mouseInputListener;
  
  private Handler handler;
  
  private boolean isFileList = false;
  
  private static final TransferHandler defaultTransferHandler = new TableTransferHandler();
  
  private boolean pointOutsidePrefSize(int paramInt1, int paramInt2, Point paramPoint) { return !this.isFileList ? false : SwingUtilities2.pointOutsidePrefSize(this.table, paramInt1, paramInt2, paramPoint); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected KeyListener createKeyListener() { return null; }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  protected MouseInputListener createMouseInputListener() { return getHandler(); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTableUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.table = (JTable)paramJComponent;
    this.rendererPane = new CellRendererPane();
    this.table.add(this.rendererPane);
    installDefaults();
    installDefaults2();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.table, "Table.background", "Table.foreground", "Table.font");
    LookAndFeel.installProperty(this.table, "opaque", Boolean.TRUE);
    Color color1 = this.table.getSelectionBackground();
    if (color1 == null || color1 instanceof UIResource) {
      color1 = UIManager.getColor("Table.selectionBackground");
      this.table.setSelectionBackground((color1 != null) ? color1 : UIManager.getColor("textHighlight"));
    } 
    Color color2 = this.table.getSelectionForeground();
    if (color2 == null || color2 instanceof UIResource) {
      color2 = UIManager.getColor("Table.selectionForeground");
      this.table.setSelectionForeground((color2 != null) ? color2 : UIManager.getColor("textHighlightText"));
    } 
    Color color3 = this.table.getGridColor();
    if (color3 == null || color3 instanceof UIResource) {
      color3 = UIManager.getColor("Table.gridColor");
      this.table.setGridColor((color3 != null) ? color3 : Color.GRAY);
    } 
    Container container = SwingUtilities.getUnwrappedParent(this.table);
    if (container != null) {
      container = container.getParent();
      if (container != null && container instanceof JScrollPane)
        LookAndFeel.installBorder((JScrollPane)container, "Table.scrollPaneBorder"); 
    } 
    this.isFileList = Boolean.TRUE.equals(this.table.getClientProperty("Table.isFileList"));
  }
  
  private void installDefaults2() {
    TransferHandler transferHandler = this.table.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource) {
      this.table.setTransferHandler(defaultTransferHandler);
      if (this.table.getDropTarget() instanceof UIResource)
        this.table.setDropTarget(null); 
    } 
  }
  
  protected void installListeners() {
    this.focusListener = createFocusListener();
    this.keyListener = createKeyListener();
    this.mouseInputListener = createMouseInputListener();
    this.table.addFocusListener(this.focusListener);
    this.table.addKeyListener(this.keyListener);
    this.table.addMouseListener(this.mouseInputListener);
    this.table.addMouseMotionListener(this.mouseInputListener);
    this.table.addPropertyChangeListener(getHandler());
    if (this.isFileList)
      this.table.getSelectionModel().addListSelectionListener(getHandler()); 
  }
  
  protected void installKeyboardActions() {
    LazyActionMap.installLazyActionMap(this.table, BasicTableUI.class, "Table.actionMap");
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.table, 1, inputMap);
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 1) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap");
      InputMap inputMap2;
      if (this.table.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.table, this, "Table.ancestorInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("selectNextColumn", 1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectNextColumnChangeLead", 1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumn", -1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumnChangeLead", -1, 0, false, false));
    paramLazyActionMap.put(new Actions("selectNextRow", 0, 1, false, false));
    paramLazyActionMap.put(new Actions("selectNextRowChangeLead", 0, 1, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousRow", 0, -1, false, false));
    paramLazyActionMap.put(new Actions("selectPreviousRowChangeLead", 0, -1, false, false));
    paramLazyActionMap.put(new Actions("selectNextColumnExtendSelection", 1, 0, true, false));
    paramLazyActionMap.put(new Actions("selectPreviousColumnExtendSelection", -1, 0, true, false));
    paramLazyActionMap.put(new Actions("selectNextRowExtendSelection", 0, 1, true, false));
    paramLazyActionMap.put(new Actions("selectPreviousRowExtendSelection", 0, -1, true, false));
    paramLazyActionMap.put(new Actions("scrollUpChangeSelection", false, false, true, false));
    paramLazyActionMap.put(new Actions("scrollDownChangeSelection", false, true, true, false));
    paramLazyActionMap.put(new Actions("selectFirstColumn", false, false, false, true));
    paramLazyActionMap.put(new Actions("selectLastColumn", false, true, false, true));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection", true, false, true, false));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection", true, true, true, false));
    paramLazyActionMap.put(new Actions("selectFirstColumnExtendSelection", true, false, false, true));
    paramLazyActionMap.put(new Actions("selectLastColumnExtendSelection", true, true, false, true));
    paramLazyActionMap.put(new Actions("selectFirstRow", false, false, true, true));
    paramLazyActionMap.put(new Actions("selectLastRow", false, true, true, true));
    paramLazyActionMap.put(new Actions("selectFirstRowExtendSelection", true, false, true, true));
    paramLazyActionMap.put(new Actions("selectLastRowExtendSelection", true, true, true, true));
    paramLazyActionMap.put(new Actions("selectNextColumnCell", 1, 0, false, true));
    paramLazyActionMap.put(new Actions("selectPreviousColumnCell", -1, 0, false, true));
    paramLazyActionMap.put(new Actions("selectNextRowCell", 0, 1, false, true));
    paramLazyActionMap.put(new Actions("selectPreviousRowCell", 0, -1, false, true));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("startEditing"));
    paramLazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
    paramLazyActionMap.put(new Actions("scrollLeftChangeSelection", false, false, false, false));
    paramLazyActionMap.put(new Actions("scrollRightChangeSelection", false, true, false, false));
    paramLazyActionMap.put(new Actions("scrollLeftExtendSelection", true, false, false, false));
    paramLazyActionMap.put(new Actions("scrollRightExtendSelection", true, true, false, false));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(new Actions("focusHeader"));
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    this.table.remove(this.rendererPane);
    this.rendererPane = null;
    this.table = null;
  }
  
  protected void uninstallDefaults() {
    if (this.table.getTransferHandler() instanceof UIResource)
      this.table.setTransferHandler(null); 
  }
  
  protected void uninstallListeners() {
    this.table.removeFocusListener(this.focusListener);
    this.table.removeKeyListener(this.keyListener);
    this.table.removeMouseListener(this.mouseInputListener);
    this.table.removeMouseMotionListener(this.mouseInputListener);
    this.table.removePropertyChangeListener(getHandler());
    if (this.isFileList)
      this.table.getSelectionModel().removeListSelectionListener(getHandler()); 
    this.focusListener = null;
    this.keyListener = null;
    this.mouseInputListener = null;
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.table, 1, null);
    SwingUtilities.replaceUIActionMap(this.table, null);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    Component component = (Component)uIDefaults.get(BASELINE_COMPONENT_KEY);
    if (component == null) {
      DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
      component = defaultTableCellRenderer.getTableCellRendererComponent(this.table, "a", false, false, -1, -1);
      uIDefaults.put(BASELINE_COMPONENT_KEY, component);
    } 
    component.setFont(this.table.getFont());
    int i = this.table.getRowMargin();
    return component.getBaseline(2147483647, this.table.getRowHeight() - i) + i / 2;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  private Dimension createTableSize(long paramLong) {
    int i = 0;
    int j = this.table.getRowCount();
    if (j > 0 && this.table.getColumnCount() > 0) {
      Rectangle rectangle = this.table.getCellRect(j - 1, 0, true);
      i = rectangle.y + rectangle.height;
    } 
    long l = Math.abs(paramLong);
    if (l > 2147483647L)
      l = 2147483647L; 
    return new Dimension((int)l, i);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.table.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getMinWidth();
    } 
    return createTableSize(l);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.table.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getPreferredWidth();
    } 
    return createTableSize(l);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    long l = 0L;
    Enumeration enumeration = this.table.getColumnModel().getColumns();
    while (enumeration.hasMoreElements()) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      l += tableColumn.getMaxWidth();
    } 
    return createTableSize(l);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Rectangle rectangle1 = paramGraphics.getClipBounds();
    Rectangle rectangle2 = this.table.getBounds();
    rectangle2.x = rectangle2.y = 0;
    if (this.table.getRowCount() <= 0 || this.table.getColumnCount() <= 0 || !rectangle2.intersects(rectangle1)) {
      paintDropLines(paramGraphics);
      return;
    } 
    boolean bool = this.table.getComponentOrientation().isLeftToRight();
    Point point1 = rectangle1.getLocation();
    Point point2 = new Point(rectangle1.x + rectangle1.width - 1, rectangle1.y + rectangle1.height - 1);
    int i = this.table.rowAtPoint(point1);
    int j = this.table.rowAtPoint(point2);
    if (i == -1)
      i = 0; 
    if (j == -1)
      j = this.table.getRowCount() - 1; 
    int k = this.table.columnAtPoint(bool ? point1 : point2);
    int m = this.table.columnAtPoint(bool ? point2 : point1);
    if (k == -1)
      k = 0; 
    if (m == -1)
      m = this.table.getColumnCount() - 1; 
    paintGrid(paramGraphics, i, j, k, m);
    paintCells(paramGraphics, i, j, k, m);
    paintDropLines(paramGraphics);
  }
  
  private void paintDropLines(Graphics paramGraphics) {
    JTable.DropLocation dropLocation = this.table.getDropLocation();
    if (dropLocation == null)
      return; 
    Color color1 = UIManager.getColor("Table.dropLineColor");
    Color color2 = UIManager.getColor("Table.dropLineShortColor");
    if (color1 == null && color2 == null)
      return; 
    Rectangle rectangle = getHDropLineRect(dropLocation);
    if (rectangle != null) {
      int i = rectangle.x;
      int j = rectangle.width;
      if (color1 != null) {
        extendRect(rectangle, true);
        paramGraphics.setColor(color1);
        paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (!dropLocation.isInsertColumn() && color2 != null) {
        paramGraphics.setColor(color2);
        paramGraphics.fillRect(i, rectangle.y, j, rectangle.height);
      } 
    } 
    rectangle = getVDropLineRect(dropLocation);
    if (rectangle != null) {
      int i = rectangle.y;
      int j = rectangle.height;
      if (color1 != null) {
        extendRect(rectangle, false);
        paramGraphics.setColor(color1);
        paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (!dropLocation.isInsertRow() && color2 != null) {
        paramGraphics.setColor(color2);
        paramGraphics.fillRect(rectangle.x, i, rectangle.width, j);
      } 
    } 
  }
  
  private Rectangle getHDropLineRect(JTable.DropLocation paramDropLocation) {
    if (!paramDropLocation.isInsertRow())
      return null; 
    int i = paramDropLocation.getRow();
    int j = paramDropLocation.getColumn();
    if (j >= this.table.getColumnCount())
      j--; 
    Rectangle rectangle = this.table.getCellRect(i, j, true);
    if (i >= this.table.getRowCount()) {
      Rectangle rectangle1 = this.table.getCellRect(--i, j, true);
      rectangle1.y += rectangle1.height;
    } 
    if (rectangle.y == 0) {
      rectangle.y = -1;
    } else {
      rectangle.y -= 2;
    } 
    rectangle.height = 3;
    return rectangle;
  }
  
  private Rectangle getVDropLineRect(JTable.DropLocation paramDropLocation) {
    if (!paramDropLocation.isInsertColumn())
      return null; 
    boolean bool = this.table.getComponentOrientation().isLeftToRight();
    int i = paramDropLocation.getColumn();
    Rectangle rectangle = this.table.getCellRect(paramDropLocation.getRow(), i, true);
    if (i >= this.table.getColumnCount()) {
      rectangle = this.table.getCellRect(paramDropLocation.getRow(), --i, true);
      if (bool)
        rectangle.x += rectangle.width; 
    } else if (!bool) {
      rectangle.x += rectangle.width;
    } 
    if (rectangle.x == 0) {
      rectangle.x = -1;
    } else {
      rectangle.x -= 2;
    } 
    rectangle.width = 3;
    return rectangle;
  }
  
  private Rectangle extendRect(Rectangle paramRectangle, boolean paramBoolean) {
    if (paramRectangle == null)
      return paramRectangle; 
    if (paramBoolean) {
      paramRectangle.x = 0;
      paramRectangle.width = this.table.getWidth();
    } else {
      paramRectangle.y = 0;
      if (this.table.getRowCount() != 0) {
        Rectangle rectangle = this.table.getCellRect(this.table.getRowCount() - 1, 0, true);
        paramRectangle.height = rectangle.y + rectangle.height;
      } else {
        paramRectangle.height = this.table.getHeight();
      } 
    } 
    return paramRectangle;
  }
  
  private void paintGrid(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.setColor(this.table.getGridColor());
    Rectangle rectangle1 = this.table.getCellRect(paramInt1, paramInt3, true);
    Rectangle rectangle2 = this.table.getCellRect(paramInt2, paramInt4, true);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    if (this.table.getShowHorizontalLines()) {
      int i = rectangle3.x + rectangle3.width;
      int j = rectangle3.y;
      for (int k = paramInt1; k <= paramInt2; k++) {
        j += this.table.getRowHeight(k);
        paramGraphics.drawLine(rectangle3.x, j - 1, i - 1, j - 1);
      } 
    } 
    if (this.table.getShowVerticalLines()) {
      TableColumnModel tableColumnModel = this.table.getColumnModel();
      int i = rectangle3.y + rectangle3.height;
      if (this.table.getComponentOrientation().isLeftToRight()) {
        int j = rectangle3.x;
        for (int k = paramInt3; k <= paramInt4; k++) {
          int m = tableColumnModel.getColumn(k).getWidth();
          j += m;
          paramGraphics.drawLine(j - 1, 0, j - 1, i - 1);
        } 
      } else {
        int j = rectangle3.x;
        for (int k = paramInt4; k >= paramInt3; k--) {
          int m = tableColumnModel.getColumn(k).getWidth();
          j += m;
          paramGraphics.drawLine(j - 1, 0, j - 1, i - 1);
        } 
      } 
    } 
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn) {
    TableColumnModel tableColumnModel = this.table.getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      if (tableColumnModel.getColumn(b) == paramTableColumn)
        return b; 
    } 
    return -1;
  }
  
  private void paintCells(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    JTableHeader jTableHeader = this.table.getTableHeader();
    TableColumn tableColumn = (jTableHeader == null) ? null : jTableHeader.getDraggedColumn();
    TableColumnModel tableColumnModel = this.table.getColumnModel();
    int i = tableColumnModel.getColumnMargin();
    if (this.table.getComponentOrientation().isLeftToRight()) {
      for (int j = paramInt1; j <= paramInt2; j++) {
        Rectangle rectangle = this.table.getCellRect(j, paramInt3, false);
        for (int k = paramInt3; k <= paramInt4; k++) {
          TableColumn tableColumn1 = tableColumnModel.getColumn(k);
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          if (tableColumn1 != tableColumn)
            paintCell(paramGraphics, rectangle, j, k); 
          rectangle.x += m;
        } 
      } 
    } else {
      for (int j = paramInt1; j <= paramInt2; j++) {
        Rectangle rectangle = this.table.getCellRect(j, paramInt3, false);
        TableColumn tableColumn1 = tableColumnModel.getColumn(paramInt3);
        if (tableColumn1 != tableColumn) {
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          paintCell(paramGraphics, rectangle, j, paramInt3);
        } 
        for (int k = paramInt3 + 1; k <= paramInt4; k++) {
          tableColumn1 = tableColumnModel.getColumn(k);
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          rectangle.x -= m;
          if (tableColumn1 != tableColumn)
            paintCell(paramGraphics, rectangle, j, k); 
        } 
      } 
    } 
    if (tableColumn != null)
      paintDraggedArea(paramGraphics, paramInt1, paramInt2, tableColumn, jTableHeader.getDraggedDistance()); 
    this.rendererPane.removeAll();
  }
  
  private void paintDraggedArea(Graphics paramGraphics, int paramInt1, int paramInt2, TableColumn paramTableColumn, int paramInt3) {
    int i = viewIndexForColumn(paramTableColumn);
    Rectangle rectangle1 = this.table.getCellRect(paramInt1, i, true);
    Rectangle rectangle2 = this.table.getCellRect(paramInt2, i, true);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    paramGraphics.setColor(this.table.getParent().getBackground());
    paramGraphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
    rectangle3.x += paramInt3;
    paramGraphics.setColor(this.table.getBackground());
    paramGraphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
    if (this.table.getShowVerticalLines()) {
      paramGraphics.setColor(this.table.getGridColor());
      int k = rectangle3.x;
      int m = rectangle3.y;
      int n = k + rectangle3.width - 1;
      int i1 = m + rectangle3.height - 1;
      paramGraphics.drawLine(k - 1, m, k - 1, i1);
      paramGraphics.drawLine(n, m, n, i1);
    } 
    for (int j = paramInt1; j <= paramInt2; j++) {
      Rectangle rectangle = this.table.getCellRect(j, i, false);
      rectangle.x += paramInt3;
      paintCell(paramGraphics, rectangle, j, i);
      if (this.table.getShowHorizontalLines()) {
        paramGraphics.setColor(this.table.getGridColor());
        Rectangle rectangle4 = this.table.getCellRect(j, i, true);
        rectangle4.x += paramInt3;
        int k = rectangle4.x;
        int m = rectangle4.y;
        int n = k + rectangle4.width - 1;
        int i1 = m + rectangle4.height - 1;
        paramGraphics.drawLine(k, i1, n, i1);
      } 
    } 
  }
  
  private void paintCell(Graphics paramGraphics, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (this.table.isEditing() && this.table.getEditingRow() == paramInt1 && this.table.getEditingColumn() == paramInt2) {
      Component component = this.table.getEditorComponent();
      component.setBounds(paramRectangle);
      component.validate();
    } else {
      TableCellRenderer tableCellRenderer = this.table.getCellRenderer(paramInt1, paramInt2);
      Component component = this.table.prepareRenderer(tableCellRenderer, paramInt1, paramInt2);
      this.rendererPane.paintComponent(paramGraphics, component, this.table, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, true);
    } 
  }
  
  private static int getAdjustedLead(JTable paramJTable, boolean paramBoolean, ListSelectionModel paramListSelectionModel) {
    int i = paramListSelectionModel.getLeadSelectionIndex();
    int j = paramBoolean ? paramJTable.getRowCount() : paramJTable.getColumnCount();
    return (i < j) ? i : -1;
  }
  
  private static int getAdjustedLead(JTable paramJTable, boolean paramBoolean) { return paramBoolean ? getAdjustedLead(paramJTable, paramBoolean, paramJTable.getSelectionModel()) : getAdjustedLead(paramJTable, paramBoolean, paramJTable.getColumnModel().getSelectionModel()); }
  
  private static class Actions extends UIAction {
    private static final String CANCEL_EDITING = "cancel";
    
    private static final String SELECT_ALL = "selectAll";
    
    private static final String CLEAR_SELECTION = "clearSelection";
    
    private static final String START_EDITING = "startEditing";
    
    private static final String NEXT_ROW = "selectNextRow";
    
    private static final String NEXT_ROW_CELL = "selectNextRowCell";
    
    private static final String NEXT_ROW_EXTEND_SELECTION = "selectNextRowExtendSelection";
    
    private static final String NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
    
    private static final String PREVIOUS_ROW = "selectPreviousRow";
    
    private static final String PREVIOUS_ROW_CELL = "selectPreviousRowCell";
    
    private static final String PREVIOUS_ROW_EXTEND_SELECTION = "selectPreviousRowExtendSelection";
    
    private static final String PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
    
    private static final String NEXT_COLUMN = "selectNextColumn";
    
    private static final String NEXT_COLUMN_CELL = "selectNextColumnCell";
    
    private static final String NEXT_COLUMN_EXTEND_SELECTION = "selectNextColumnExtendSelection";
    
    private static final String NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
    
    private static final String PREVIOUS_COLUMN = "selectPreviousColumn";
    
    private static final String PREVIOUS_COLUMN_CELL = "selectPreviousColumnCell";
    
    private static final String PREVIOUS_COLUMN_EXTEND_SELECTION = "selectPreviousColumnExtendSelection";
    
    private static final String PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
    
    private static final String SCROLL_LEFT_CHANGE_SELECTION = "scrollLeftChangeSelection";
    
    private static final String SCROLL_LEFT_EXTEND_SELECTION = "scrollLeftExtendSelection";
    
    private static final String SCROLL_RIGHT_CHANGE_SELECTION = "scrollRightChangeSelection";
    
    private static final String SCROLL_RIGHT_EXTEND_SELECTION = "scrollRightExtendSelection";
    
    private static final String SCROLL_UP_CHANGE_SELECTION = "scrollUpChangeSelection";
    
    private static final String SCROLL_UP_EXTEND_SELECTION = "scrollUpExtendSelection";
    
    private static final String SCROLL_DOWN_CHANGE_SELECTION = "scrollDownChangeSelection";
    
    private static final String SCROLL_DOWN_EXTEND_SELECTION = "scrollDownExtendSelection";
    
    private static final String FIRST_COLUMN = "selectFirstColumn";
    
    private static final String FIRST_COLUMN_EXTEND_SELECTION = "selectFirstColumnExtendSelection";
    
    private static final String LAST_COLUMN = "selectLastColumn";
    
    private static final String LAST_COLUMN_EXTEND_SELECTION = "selectLastColumnExtendSelection";
    
    private static final String FIRST_ROW = "selectFirstRow";
    
    private static final String FIRST_ROW_EXTEND_SELECTION = "selectFirstRowExtendSelection";
    
    private static final String LAST_ROW = "selectLastRow";
    
    private static final String LAST_ROW_EXTEND_SELECTION = "selectLastRowExtendSelection";
    
    private static final String ADD_TO_SELECTION = "addToSelection";
    
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    
    private static final String EXTEND_TO = "extendTo";
    
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    
    private static final String FOCUS_HEADER = "focusHeader";
    
    protected int dx;
    
    protected int dy;
    
    protected boolean extend;
    
    protected boolean inSelection;
    
    protected boolean forwards;
    
    protected boolean vertically;
    
    protected boolean toLimit;
    
    protected int leadRow;
    
    protected int leadColumn;
    
    Actions(String param1String) { super(param1String); }
    
    Actions(String param1String, int param1Int1, int param1Int2, boolean param1Boolean1, boolean param1Boolean2) {
      super(param1String);
      if (param1Boolean2) {
        this.inSelection = true;
        param1Int1 = sign(param1Int1);
        param1Int2 = sign(param1Int2);
        assert (param1Int1 == 0 || param1Int2 == 0) && (param1Int1 != 0 || param1Int2 != 0);
      } 
      this.dx = param1Int1;
      this.dy = param1Int2;
      this.extend = param1Boolean1;
    }
    
    Actions(String param1String, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, boolean param1Boolean4) {
      this(param1String, 0, 0, param1Boolean1, false);
      this.forwards = param1Boolean2;
      this.vertically = param1Boolean3;
      this.toLimit = param1Boolean4;
    }
    
    private static int clipToRange(int param1Int1, int param1Int2, int param1Int3) { return Math.min(Math.max(param1Int1, param1Int2), param1Int3 - 1); }
    
    private void moveWithinTableRange(JTable param1JTable, int param1Int1, int param1Int2) {
      this.leadRow = clipToRange(this.leadRow + param1Int2, 0, param1JTable.getRowCount());
      this.leadColumn = clipToRange(this.leadColumn + param1Int1, 0, param1JTable.getColumnCount());
    }
    
    private static int sign(int param1Int) { return (param1Int < 0) ? -1 : ((param1Int == 0) ? 0 : 1); }
    
    private boolean moveWithinSelectedRange(JTable param1JTable, int param1Int1, int param1Int2, ListSelectionModel param1ListSelectionModel1, ListSelectionModel param1ListSelectionModel2) {
      boolean bool3;
      int m;
      int k;
      int j;
      int i;
      boolean bool;
      boolean bool1 = param1JTable.getRowSelectionAllowed();
      boolean bool2 = param1JTable.getColumnSelectionAllowed();
      if (bool1 && bool2) {
        bool = param1JTable.getSelectedRowCount() * param1JTable.getSelectedColumnCount();
        i = param1ListSelectionModel2.getMinSelectionIndex();
        j = param1ListSelectionModel2.getMaxSelectionIndex();
        k = param1ListSelectionModel1.getMinSelectionIndex();
        m = param1ListSelectionModel1.getMaxSelectionIndex();
      } else if (bool1) {
        bool = param1JTable.getSelectedRowCount();
        i = 0;
        j = param1JTable.getColumnCount() - 1;
        k = param1ListSelectionModel1.getMinSelectionIndex();
        m = param1ListSelectionModel1.getMaxSelectionIndex();
      } else if (bool2) {
        bool = param1JTable.getSelectedColumnCount();
        i = param1ListSelectionModel2.getMinSelectionIndex();
        j = param1ListSelectionModel2.getMaxSelectionIndex();
        k = 0;
        m = param1JTable.getRowCount() - 1;
      } else {
        bool = false;
        i = j = k = m = 0;
      } 
      if (!bool || (bool == true && param1JTable.isCellSelected(this.leadRow, this.leadColumn))) {
        bool3 = false;
        j = param1JTable.getColumnCount() - 1;
        m = param1JTable.getRowCount() - 1;
        i = Math.min(0, j);
        k = Math.min(0, m);
      } else {
        bool3 = true;
      } 
      if (param1Int2 == 1 && this.leadColumn == -1) {
        this.leadColumn = i;
        this.leadRow = -1;
      } else if (param1Int1 == 1 && this.leadRow == -1) {
        this.leadRow = k;
        this.leadColumn = -1;
      } else if (param1Int2 == -1 && this.leadColumn == -1) {
        this.leadColumn = j;
        this.leadRow = m + 1;
      } else if (param1Int1 == -1 && this.leadRow == -1) {
        this.leadRow = m;
        this.leadColumn = j + 1;
      } 
      this.leadRow = Math.min(Math.max(this.leadRow, k - 1), m + 1);
      this.leadColumn = Math.min(Math.max(this.leadColumn, i - 1), j + 1);
      do {
        calcNextPos(param1Int1, i, j, param1Int2, k, m);
      } while (bool3 && !param1JTable.isCellSelected(this.leadRow, this.leadColumn));
      return bool3;
    }
    
    private void calcNextPos(int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      if (param1Int1 != 0) {
        this.leadColumn += param1Int1;
        if (this.leadColumn > param1Int3) {
          this.leadColumn = param1Int2;
          this.leadRow++;
          if (this.leadRow > param1Int6)
            this.leadRow = param1Int5; 
        } else if (this.leadColumn < param1Int2) {
          this.leadColumn = param1Int3;
          this.leadRow--;
          if (this.leadRow < param1Int5)
            this.leadRow = param1Int6; 
        } 
      } else {
        this.leadRow += param1Int4;
        if (this.leadRow > param1Int6) {
          this.leadRow = param1Int5;
          this.leadColumn++;
          if (this.leadColumn > param1Int3)
            this.leadColumn = param1Int2; 
        } else if (this.leadRow < param1Int5) {
          this.leadRow = param1Int6;
          this.leadColumn--;
          if (this.leadColumn < param1Int2)
            this.leadColumn = param1Int3; 
        } 
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      JTable jTable = (JTable)param1ActionEvent.getSource();
      ListSelectionModel listSelectionModel1 = jTable.getSelectionModel();
      this.leadRow = BasicTableUI.getAdjustedLead(jTable, true, listSelectionModel1);
      ListSelectionModel listSelectionModel2 = jTable.getColumnModel().getSelectionModel();
      this.leadColumn = BasicTableUI.getAdjustedLead(jTable, false, listSelectionModel2);
      if (str == "scrollLeftChangeSelection" || str == "scrollLeftExtendSelection" || str == "scrollRightChangeSelection" || str == "scrollRightExtendSelection" || str == "scrollUpChangeSelection" || str == "scrollUpExtendSelection" || str == "scrollDownChangeSelection" || str == "scrollDownExtendSelection" || str == "selectFirstColumn" || str == "selectFirstColumnExtendSelection" || str == "selectFirstRow" || str == "selectFirstRowExtendSelection" || str == "selectLastColumn" || str == "selectLastColumnExtendSelection" || str == "selectLastRow" || str == "selectLastRowExtendSelection")
        if (this.toLimit) {
          if (this.vertically) {
            int i = jTable.getRowCount();
            this.dx = 0;
            this.dy = this.forwards ? i : -i;
          } else {
            int i = jTable.getColumnCount();
            this.dx = this.forwards ? i : -i;
            this.dy = 0;
          } 
        } else {
          if (!(SwingUtilities.getUnwrappedParent(jTable).getParent() instanceof JScrollPane))
            return; 
          Dimension dimension = jTable.getParent().getSize();
          if (this.vertically) {
            Rectangle rectangle = jTable.getCellRect(this.leadRow, 0, true);
            if (this.forwards) {
              rectangle.y += Math.max(dimension.height, rectangle.height);
            } else {
              rectangle.y -= dimension.height;
            } 
            this.dx = 0;
            int i = jTable.rowAtPoint(rectangle.getLocation());
            if (i == -1 && this.forwards)
              i = jTable.getRowCount(); 
            this.dy = i - this.leadRow;
          } else {
            Rectangle rectangle = jTable.getCellRect(0, this.leadColumn, true);
            if (this.forwards) {
              rectangle.x += Math.max(dimension.width, rectangle.width);
            } else {
              rectangle.x -= dimension.width;
            } 
            int i = jTable.columnAtPoint(rectangle.getLocation());
            if (i == -1) {
              boolean bool = jTable.getComponentOrientation().isLeftToRight();
              i = this.forwards ? (bool ? jTable.getColumnCount() : 0) : (bool ? 0 : jTable.getColumnCount());
            } 
            this.dx = i - this.leadColumn;
            this.dy = 0;
          } 
        }  
      if (str == "selectNextRow" || str == "selectNextRowCell" || str == "selectNextRowExtendSelection" || str == "selectNextRowChangeLead" || str == "selectNextColumn" || str == "selectNextColumnCell" || str == "selectNextColumnExtendSelection" || str == "selectNextColumnChangeLead" || str == "selectPreviousRow" || str == "selectPreviousRowCell" || str == "selectPreviousRowExtendSelection" || str == "selectPreviousRowChangeLead" || str == "selectPreviousColumn" || str == "selectPreviousColumnCell" || str == "selectPreviousColumnExtendSelection" || str == "selectPreviousColumnChangeLead" || str == "scrollLeftChangeSelection" || str == "scrollLeftExtendSelection" || str == "scrollRightChangeSelection" || str == "scrollRightExtendSelection" || str == "scrollUpChangeSelection" || str == "scrollUpExtendSelection" || str == "scrollDownChangeSelection" || str == "scrollDownExtendSelection" || str == "selectFirstColumn" || str == "selectFirstColumnExtendSelection" || str == "selectFirstRow" || str == "selectFirstRowExtendSelection" || str == "selectLastColumn" || str == "selectLastColumnExtendSelection" || str == "selectLastRow" || str == "selectLastRowExtendSelection") {
        if (jTable.isEditing() && !jTable.getCellEditor().stopCellEditing())
          return; 
        boolean bool = false;
        if (str == "selectNextRowChangeLead" || str == "selectPreviousRowChangeLead") {
          bool = (listSelectionModel1.getSelectionMode() == 2) ? 1 : 0;
        } else if (str == "selectNextColumnChangeLead" || str == "selectPreviousColumnChangeLead") {
          bool = (listSelectionModel2.getSelectionMode() == 2) ? 1 : 0;
        } 
        if (bool) {
          moveWithinTableRange(jTable, this.dx, this.dy);
          if (this.dy != 0) {
            ((DefaultListSelectionModel)listSelectionModel1).moveLeadSelectionIndex(this.leadRow);
            if (BasicTableUI.getAdjustedLead(jTable, false, listSelectionModel2) == -1 && jTable.getColumnCount() > 0)
              ((DefaultListSelectionModel)listSelectionModel2).moveLeadSelectionIndex(0); 
          } else {
            ((DefaultListSelectionModel)listSelectionModel2).moveLeadSelectionIndex(this.leadColumn);
            if (BasicTableUI.getAdjustedLead(jTable, true, listSelectionModel1) == -1 && jTable.getRowCount() > 0)
              ((DefaultListSelectionModel)listSelectionModel1).moveLeadSelectionIndex(0); 
          } 
          Rectangle rectangle = jTable.getCellRect(this.leadRow, this.leadColumn, false);
          if (rectangle != null)
            jTable.scrollRectToVisible(rectangle); 
        } else if (!this.inSelection) {
          moveWithinTableRange(jTable, this.dx, this.dy);
          jTable.changeSelection(this.leadRow, this.leadColumn, false, this.extend);
        } else {
          if (jTable.getRowCount() <= 0 || jTable.getColumnCount() <= 0)
            return; 
          if (moveWithinSelectedRange(jTable, this.dx, this.dy, listSelectionModel1, listSelectionModel2)) {
            if (listSelectionModel1.isSelectedIndex(this.leadRow)) {
              listSelectionModel1.addSelectionInterval(this.leadRow, this.leadRow);
            } else {
              listSelectionModel1.removeSelectionInterval(this.leadRow, this.leadRow);
            } 
            if (listSelectionModel2.isSelectedIndex(this.leadColumn)) {
              listSelectionModel2.addSelectionInterval(this.leadColumn, this.leadColumn);
            } else {
              listSelectionModel2.removeSelectionInterval(this.leadColumn, this.leadColumn);
            } 
            Rectangle rectangle = jTable.getCellRect(this.leadRow, this.leadColumn, false);
            if (rectangle != null)
              jTable.scrollRectToVisible(rectangle); 
          } else {
            jTable.changeSelection(this.leadRow, this.leadColumn, false, false);
          } 
        } 
      } else if (str == "cancel") {
        jTable.removeEditor();
      } else if (str == "selectAll") {
        jTable.selectAll();
      } else if (str == "clearSelection") {
        jTable.clearSelection();
      } else if (str == "startEditing") {
        if (!jTable.hasFocus()) {
          TableCellEditor tableCellEditor = jTable.getCellEditor();
          if (tableCellEditor != null && !tableCellEditor.stopCellEditing())
            return; 
          jTable.requestFocus();
          return;
        } 
        jTable.editCellAt(this.leadRow, this.leadColumn, param1ActionEvent);
        Component component = jTable.getEditorComponent();
        if (component != null)
          component.requestFocus(); 
      } else if (str == "addToSelection") {
        if (!jTable.isCellSelected(this.leadRow, this.leadColumn)) {
          int i = listSelectionModel1.getAnchorSelectionIndex();
          int j = listSelectionModel2.getAnchorSelectionIndex();
          listSelectionModel1.setValueIsAdjusting(true);
          listSelectionModel2.setValueIsAdjusting(true);
          jTable.changeSelection(this.leadRow, this.leadColumn, true, false);
          listSelectionModel1.setAnchorSelectionIndex(i);
          listSelectionModel2.setAnchorSelectionIndex(j);
          listSelectionModel1.setValueIsAdjusting(false);
          listSelectionModel2.setValueIsAdjusting(false);
        } 
      } else if (str == "toggleAndAnchor") {
        jTable.changeSelection(this.leadRow, this.leadColumn, true, false);
      } else if (str == "extendTo") {
        jTable.changeSelection(this.leadRow, this.leadColumn, false, true);
      } else if (str == "moveSelectionTo") {
        jTable.changeSelection(this.leadRow, this.leadColumn, false, false);
      } else if (str == "focusHeader") {
        JTableHeader jTableHeader = jTable.getTableHeader();
        if (jTableHeader != null) {
          int i = jTable.getSelectedColumn();
          if (i >= 0) {
            TableHeaderUI tableHeaderUI = jTableHeader.getUI();
            if (tableHeaderUI instanceof BasicTableHeaderUI)
              ((BasicTableHeaderUI)tableHeaderUI).selectColumn(i); 
          } 
          jTableHeader.requestFocusInWindow();
        } 
      } 
    }
    
    public boolean isEnabled(Object param1Object) {
      String str = getName();
      if (param1Object instanceof JTable && Boolean.TRUE.equals(((JTable)param1Object).getClientProperty("Table.isFileList")) && (str == "selectNextColumn" || str == "selectNextColumnCell" || str == "selectNextColumnExtendSelection" || str == "selectNextColumnChangeLead" || str == "selectPreviousColumn" || str == "selectPreviousColumnCell" || str == "selectPreviousColumnExtendSelection" || str == "selectPreviousColumnChangeLead" || str == "scrollLeftChangeSelection" || str == "scrollLeftExtendSelection" || str == "scrollRightChangeSelection" || str == "scrollRightExtendSelection" || str == "selectFirstColumn" || str == "selectFirstColumnExtendSelection" || str == "selectLastColumn" || str == "selectLastColumnExtendSelection" || str == "selectNextRowCell" || str == "selectPreviousRowCell"))
        return false; 
      if (str == "cancel" && param1Object instanceof JTable)
        return ((JTable)param1Object).isEditing(); 
      if (str == "selectNextRowChangeLead" || str == "selectPreviousRowChangeLead")
        return (param1Object != null && ((JTable)param1Object).getSelectionModel() instanceof DefaultListSelectionModel); 
      if (str == "selectNextColumnChangeLead" || str == "selectPreviousColumnChangeLead")
        return (param1Object != null && ((JTable)param1Object).getColumnModel().getSelectionModel() instanceof DefaultListSelectionModel); 
      if (str == "addToSelection" && param1Object instanceof JTable) {
        JTable jTable = (JTable)param1Object;
        int i = BasicTableUI.getAdjustedLead(jTable, true);
        int j = BasicTableUI.getAdjustedLead(jTable, false);
        return (!jTable.isEditing() && !jTable.isCellSelected(i, j));
      } 
      if (str == "focusHeader" && param1Object instanceof JTable) {
        JTable jTable = (JTable)param1Object;
        return (jTable.getTableHeader() != null);
      } 
      return true;
    }
  }
  
  public class FocusHandler implements FocusListener {
    public void focusGained(FocusEvent param1FocusEvent) { BasicTableUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicTableUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements FocusListener, MouseInputListener, PropertyChangeListener, ListSelectionListener, ActionListener, DragRecognitionSupport.BeforeDrag {
    private Component dispatchComponent;
    
    private int pressedRow;
    
    private int pressedCol;
    
    private MouseEvent pressedEvent;
    
    private boolean dragPressDidSelection;
    
    private boolean dragStarted;
    
    private boolean shouldStartTimer;
    
    private boolean outsidePrefSize;
    
    private Timer timer = null;
    
    private Handler() {}
    
    private void repaintLeadCell() {
      int i = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, true);
      int j = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, false);
      if (i < 0 || j < 0)
        return; 
      Rectangle rectangle = BasicTableUI.this.table.getCellRect(i, j, false);
      BasicTableUI.this.table.repaint(rectangle);
    }
    
    public void focusGained(FocusEvent param1FocusEvent) { repaintLeadCell(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { repaintLeadCell(); }
    
    public void keyPressed(KeyEvent param1KeyEvent) {}
    
    public void keyReleased(KeyEvent param1KeyEvent) {}
    
    public void keyTyped(KeyEvent param1KeyEvent) {
      KeyStroke keyStroke = KeyStroke.getKeyStroke(param1KeyEvent.getKeyChar(), param1KeyEvent.getModifiers());
      InputMap inputMap = BasicTableUI.this.table.getInputMap(0);
      if (inputMap != null && inputMap.get(keyStroke) != null)
        return; 
      inputMap = BasicTableUI.this.table.getInputMap(1);
      if (inputMap != null && inputMap.get(keyStroke) != null)
        return; 
      keyStroke = KeyStroke.getKeyStrokeForEvent(param1KeyEvent);
      if (param1KeyEvent.getKeyChar() == '\r')
        return; 
      int i = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, true);
      int j = BasicTableUI.getAdjustedLead(BasicTableUI.this.table, false);
      if (i != -1 && j != -1 && !BasicTableUI.this.table.isEditing() && !BasicTableUI.this.table.editCellAt(i, j))
        return; 
      Component component = BasicTableUI.this.table.getEditorComponent();
      if (BasicTableUI.this.table.isEditing() && component != null && component instanceof JComponent) {
        JComponent jComponent = (JComponent)component;
        inputMap = jComponent.getInputMap(0);
        Object object = (inputMap != null) ? inputMap.get(keyStroke) : null;
        if (object == null) {
          inputMap = jComponent.getInputMap(1);
          object = (inputMap != null) ? inputMap.get(keyStroke) : null;
        } 
        if (object != null) {
          ActionMap actionMap = jComponent.getActionMap();
          Action action = (actionMap != null) ? actionMap.get(object) : null;
          if (action != null && SwingUtilities.notifyAction(action, keyStroke, param1KeyEvent, jComponent, param1KeyEvent.getModifiers()))
            param1KeyEvent.consume(); 
        } 
      } 
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    private void setDispatchComponent(MouseEvent param1MouseEvent) {
      Component component = BasicTableUI.this.table.getEditorComponent();
      Point point1 = param1MouseEvent.getPoint();
      Point point2 = SwingUtilities.convertPoint(BasicTableUI.this.table, point1, component);
      this.dispatchComponent = SwingUtilities.getDeepestComponentAt(component, point2.x, point2.y);
      SwingUtilities2.setSkipClickCount(this.dispatchComponent, param1MouseEvent.getClickCount() - 1);
    }
    
    private boolean repostEvent(MouseEvent param1MouseEvent) {
      if (this.dispatchComponent == null || !BasicTableUI.this.table.isEditing())
        return false; 
      MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(BasicTableUI.this.table, param1MouseEvent, this.dispatchComponent);
      this.dispatchComponent.dispatchEvent(mouseEvent);
      return true;
    }
    
    private void setValueIsAdjusting(boolean param1Boolean) {
      BasicTableUI.this.table.getSelectionModel().setValueIsAdjusting(param1Boolean);
      BasicTableUI.this.table.getColumnModel().getSelectionModel().setValueIsAdjusting(param1Boolean);
    }
    
    private boolean canStartDrag() { return (this.pressedRow == -1 || this.pressedCol == -1) ? false : (BasicTableUI.this.isFileList ? (!this.outsidePrefSize) : ((BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0 && BasicTableUI.this.table.getColumnModel().getSelectionModel().getSelectionMode() == 0) ? true : BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol))); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTableUI.this.table))
        return; 
      if (BasicTableUI.this.table.isEditing() && !BasicTableUI.this.table.getCellEditor().stopCellEditing()) {
        Component component = BasicTableUI.this.table.getEditorComponent();
        if (component != null && !component.hasFocus())
          SwingUtilities2.compositeRequestFocus(component); 
        return;
      } 
      Point point = param1MouseEvent.getPoint();
      this.pressedRow = BasicTableUI.this.table.rowAtPoint(point);
      this.pressedCol = BasicTableUI.this.table.columnAtPoint(point);
      this.outsidePrefSize = BasicTableUI.this.pointOutsidePrefSize(this.pressedRow, this.pressedCol, point);
      if (BasicTableUI.this.isFileList)
        this.shouldStartTimer = (BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol) && !param1MouseEvent.isShiftDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent) && !this.outsidePrefSize); 
      if (BasicTableUI.this.table.getDragEnabled()) {
        mousePressedDND(param1MouseEvent);
      } else {
        SwingUtilities2.adjustFocus(BasicTableUI.this.table);
        if (!BasicTableUI.this.isFileList)
          setValueIsAdjusting(true); 
        adjustSelection(param1MouseEvent);
      } 
    }
    
    private void mousePressedDND(MouseEvent param1MouseEvent) {
      this.pressedEvent = param1MouseEvent;
      boolean bool = true;
      this.dragStarted = false;
      if (canStartDrag() && DragRecognitionSupport.mousePressed(param1MouseEvent)) {
        this.dragPressDidSelection = false;
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent) && BasicTableUI.this.isFileList)
          return; 
        if (!param1MouseEvent.isShiftDown() && BasicTableUI.this.table.isCellSelected(this.pressedRow, this.pressedCol)) {
          BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
          BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
          return;
        } 
        this.dragPressDidSelection = true;
        bool = false;
      } else if (!BasicTableUI.this.isFileList) {
        setValueIsAdjusting(true);
      } 
      if (bool)
        SwingUtilities2.adjustFocus(BasicTableUI.this.table); 
      adjustSelection(param1MouseEvent);
    }
    
    private void adjustSelection(MouseEvent param1MouseEvent) {
      if (this.outsidePrefSize) {
        if (param1MouseEvent.getID() == 501 && (!param1MouseEvent.isShiftDown() || BasicTableUI.this.table.getSelectionModel().getSelectionMode() == 0)) {
          BasicTableUI.this.table.clearSelection();
          TableCellEditor tableCellEditor1 = BasicTableUI.this.table.getCellEditor();
          if (tableCellEditor1 != null)
            tableCellEditor1.stopCellEditing(); 
        } 
        return;
      } 
      if (this.pressedCol == -1 || this.pressedRow == -1)
        return; 
      boolean bool = BasicTableUI.this.table.getDragEnabled();
      if (!bool && !BasicTableUI.this.isFileList && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, param1MouseEvent)) {
        setDispatchComponent(param1MouseEvent);
        repostEvent(param1MouseEvent);
      } 
      TableCellEditor tableCellEditor = BasicTableUI.this.table.getCellEditor();
      if (bool || tableCellEditor == null || tableCellEditor.shouldSelectCell(param1MouseEvent))
        BasicTableUI.this.table.changeSelection(this.pressedRow, this.pressedCol, BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent), param1MouseEvent.isShiftDown()); 
    }
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
      if (this.timer != null) {
        this.timer.stop();
        this.timer = null;
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, null);
      Component component = BasicTableUI.this.table.getEditorComponent();
      if (component != null && !component.hasFocus())
        SwingUtilities2.compositeRequestFocus(component); 
    }
    
    private void maybeStartTimer() {
      if (!this.shouldStartTimer)
        return; 
      if (this.timer == null) {
        this.timer = new Timer(1200, this);
        this.timer.setRepeats(false);
      } 
      this.timer.start();
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTableUI.this.table))
        return; 
      if (BasicTableUI.this.table.getDragEnabled()) {
        mouseReleasedDND(param1MouseEvent);
      } else if (BasicTableUI.this.isFileList) {
        maybeStartTimer();
      } 
      this.pressedEvent = null;
      repostEvent(param1MouseEvent);
      this.dispatchComponent = null;
      setValueIsAdjusting(false);
    }
    
    private void mouseReleasedDND(MouseEvent param1MouseEvent) {
      MouseEvent mouseEvent = DragRecognitionSupport.mouseReleased(param1MouseEvent);
      if (mouseEvent != null) {
        SwingUtilities2.adjustFocus(BasicTableUI.this.table);
        if (!this.dragPressDidSelection)
          adjustSelection(mouseEvent); 
      } 
      if (!this.dragStarted) {
        if (BasicTableUI.this.isFileList) {
          maybeStartTimer();
          return;
        } 
        Point point = param1MouseEvent.getPoint();
        if (this.pressedEvent != null && BasicTableUI.this.table.rowAtPoint(point) == this.pressedRow && BasicTableUI.this.table.columnAtPoint(point) == this.pressedCol && BasicTableUI.this.table.editCellAt(this.pressedRow, this.pressedCol, this.pressedEvent)) {
          setDispatchComponent(this.pressedEvent);
          repostEvent(this.pressedEvent);
          TableCellEditor tableCellEditor = BasicTableUI.this.table.getCellEditor();
          if (tableCellEditor != null)
            tableCellEditor.shouldSelectCell(this.pressedEvent); 
        } 
      } 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void dragStarting(MouseEvent param1MouseEvent) {
      this.dragStarted = true;
      if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent) && BasicTableUI.this.isFileList) {
        BasicTableUI.this.table.getSelectionModel().addSelectionInterval(this.pressedRow, this.pressedRow);
        BasicTableUI.this.table.getColumnModel().getSelectionModel().addSelectionInterval(this.pressedCol, this.pressedCol);
      } 
      this.pressedEvent = null;
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicTableUI.this.table))
        return; 
      if (BasicTableUI.this.table.getDragEnabled() && (DragRecognitionSupport.mouseDragged(param1MouseEvent, this) || this.dragStarted))
        return; 
      repostEvent(param1MouseEvent);
      if (BasicTableUI.this.isFileList || BasicTableUI.this.table.isEditing())
        return; 
      Point point = param1MouseEvent.getPoint();
      int i = BasicTableUI.this.table.rowAtPoint(point);
      int j = BasicTableUI.this.table.columnAtPoint(point);
      if (j == -1 || i == -1)
        return; 
      BasicTableUI.this.table.changeSelection(i, j, BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent), true);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("componentOrientation" == str) {
        InputMap inputMap = BasicTableUI.this.getInputMap(1);
        SwingUtilities.replaceUIInputMap(BasicTableUI.this.table, 1, inputMap);
        JTableHeader jTableHeader = BasicTableUI.this.table.getTableHeader();
        if (jTableHeader != null)
          jTableHeader.setComponentOrientation((ComponentOrientation)param1PropertyChangeEvent.getNewValue()); 
      } else if ("dropLocation" == str) {
        JTable.DropLocation dropLocation = (JTable.DropLocation)param1PropertyChangeEvent.getOldValue();
        repaintDropLocation(dropLocation);
        repaintDropLocation(BasicTableUI.this.table.getDropLocation());
      } else if ("Table.isFileList" == str) {
        BasicTableUI.this.isFileList = Boolean.TRUE.equals(BasicTableUI.this.table.getClientProperty("Table.isFileList"));
        BasicTableUI.this.table.revalidate();
        BasicTableUI.this.table.repaint();
        if (BasicTableUI.this.isFileList) {
          BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
        } else {
          BasicTableUI.this.table.getSelectionModel().removeListSelectionListener(BasicTableUI.this.getHandler());
          this.timer = null;
        } 
      } else if ("selectionModel" == str && BasicTableUI.this.isFileList) {
        ListSelectionModel listSelectionModel = (ListSelectionModel)param1PropertyChangeEvent.getOldValue();
        listSelectionModel.removeListSelectionListener(BasicTableUI.this.getHandler());
        BasicTableUI.this.table.getSelectionModel().addListSelectionListener(BasicTableUI.this.getHandler());
      } 
    }
    
    private void repaintDropLocation(JTable.DropLocation param1DropLocation) {
      if (param1DropLocation == null)
        return; 
      if (!param1DropLocation.isInsertRow() && !param1DropLocation.isInsertColumn()) {
        Rectangle rectangle = BasicTableUI.this.table.getCellRect(param1DropLocation.getRow(), param1DropLocation.getColumn(), false);
        if (rectangle != null)
          BasicTableUI.this.table.repaint(rectangle); 
        return;
      } 
      if (param1DropLocation.isInsertRow()) {
        Rectangle rectangle = BasicTableUI.this.extendRect(BasicTableUI.this.getHDropLineRect(param1DropLocation), true);
        if (rectangle != null)
          BasicTableUI.this.table.repaint(rectangle); 
      } 
      if (param1DropLocation.isInsertColumn()) {
        Rectangle rectangle = BasicTableUI.this.extendRect(BasicTableUI.this.getVDropLineRect(param1DropLocation), false);
        if (rectangle != null)
          BasicTableUI.this.table.repaint(rectangle); 
      } 
    }
  }
  
  public class KeyHandler implements KeyListener {
    public void keyPressed(KeyEvent param1KeyEvent) { BasicTableUI.this.getHandler().keyPressed(param1KeyEvent); }
    
    public void keyReleased(KeyEvent param1KeyEvent) { BasicTableUI.this.getHandler().keyReleased(param1KeyEvent); }
    
    public void keyTyped(KeyEvent param1KeyEvent) { BasicTableUI.this.getHandler().keyTyped(param1KeyEvent); }
  }
  
  public class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mousePressed(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseReleased(param1MouseEvent); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseMoved(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicTableUI.this.getHandler().mouseDragged(param1MouseEvent); }
  }
  
  static class TableTransferHandler extends TransferHandler implements UIResource {
    protected Transferable createTransferable(JComponent param1JComponent) {
      if (param1JComponent instanceof JTable) {
        int[] arrayOfInt2;
        int[] arrayOfInt1;
        JTable jTable = (JTable)param1JComponent;
        if (!jTable.getRowSelectionAllowed() && !jTable.getColumnSelectionAllowed())
          return null; 
        if (!jTable.getRowSelectionAllowed()) {
          int i = jTable.getRowCount();
          arrayOfInt1 = new int[i];
          for (byte b1 = 0; b1 < i; b1++)
            arrayOfInt1[b1] = b1; 
        } else {
          arrayOfInt1 = jTable.getSelectedRows();
        } 
        if (!jTable.getColumnSelectionAllowed()) {
          int i = jTable.getColumnCount();
          arrayOfInt2 = new int[i];
          for (byte b1 = 0; b1 < i; b1++)
            arrayOfInt2[b1] = b1; 
        } else {
          arrayOfInt2 = jTable.getSelectedColumns();
        } 
        if (arrayOfInt1 == null || arrayOfInt2 == null || arrayOfInt1.length == 0 || arrayOfInt2.length == 0)
          return null; 
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("<html>\n<body>\n<table>\n");
        for (byte b = 0; b < arrayOfInt1.length; b++) {
          stringBuffer2.append("<tr>\n");
          for (byte b1 = 0; b1 < arrayOfInt2.length; b1++) {
            Object object = jTable.getValueAt(arrayOfInt1[b], arrayOfInt2[b1]);
            String str = (object == null) ? "" : object.toString();
            stringBuffer1.append(str + "\t");
            stringBuffer2.append("  <td>" + str + "</td>\n");
          } 
          stringBuffer1.deleteCharAt(stringBuffer1.length() - 1).append("\n");
          stringBuffer2.append("</tr>\n");
        } 
        stringBuffer1.deleteCharAt(stringBuffer1.length() - 1);
        stringBuffer2.append("</table>\n</body>\n</html>");
        return new BasicTransferable(stringBuffer1.toString(), stringBuffer2.toString());
      } 
      return null;
    }
    
    public int getSourceActions(JComponent param1JComponent) { return 1; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTableUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */