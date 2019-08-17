package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedTable;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleTableModelChange;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import sun.awt.AWTAccessor;
import sun.reflect.misc.ReflectUtil;
import sun.swing.PrintingStatus;
import sun.swing.SwingUtilities2;

public class JTable extends JComponent implements TableModelListener, Scrollable, TableColumnModelListener, ListSelectionListener, CellEditorListener, Accessible, RowSorterListener {
  private static final String uiClassID = "TableUI";
  
  public static final int AUTO_RESIZE_OFF = 0;
  
  public static final int AUTO_RESIZE_NEXT_COLUMN = 1;
  
  public static final int AUTO_RESIZE_SUBSEQUENT_COLUMNS = 2;
  
  public static final int AUTO_RESIZE_LAST_COLUMN = 3;
  
  public static final int AUTO_RESIZE_ALL_COLUMNS = 4;
  
  protected TableModel dataModel;
  
  protected TableColumnModel columnModel;
  
  protected ListSelectionModel selectionModel;
  
  protected JTableHeader tableHeader;
  
  protected int rowHeight;
  
  protected int rowMargin;
  
  protected Color gridColor;
  
  protected boolean showHorizontalLines;
  
  protected boolean showVerticalLines;
  
  protected int autoResizeMode;
  
  protected boolean autoCreateColumnsFromModel;
  
  protected Dimension preferredViewportSize;
  
  protected boolean rowSelectionAllowed;
  
  protected boolean cellSelectionEnabled;
  
  protected Component editorComp;
  
  protected TableCellEditor cellEditor;
  
  protected int editingColumn;
  
  protected int editingRow;
  
  protected Hashtable defaultRenderersByColumnClass;
  
  protected Hashtable defaultEditorsByColumnClass;
  
  protected Color selectionForeground;
  
  protected Color selectionBackground;
  
  private SizeSequence rowModel;
  
  private boolean dragEnabled;
  
  private boolean surrendersFocusOnKeystroke;
  
  private PropertyChangeListener editorRemover = null;
  
  private boolean columnSelectionAdjusting;
  
  private boolean rowSelectionAdjusting;
  
  private Throwable printError;
  
  private boolean isRowHeightSet;
  
  private boolean updateSelectionOnSort;
  
  private SortManager sortManager;
  
  private boolean ignoreSortChange;
  
  private boolean sorterChanged;
  
  private boolean autoCreateRowSorter;
  
  private boolean fillsViewportHeight;
  
  private DropMode dropMode = DropMode.USE_SELECTION;
  
  private DropLocation dropLocation;
  
  public JTable() { this(null, null, null); }
  
  public JTable(TableModel paramTableModel) { this(paramTableModel, null, null); }
  
  public JTable(TableModel paramTableModel, TableColumnModel paramTableColumnModel) { this(paramTableModel, paramTableColumnModel, null); }
  
  public JTable(TableModel paramTableModel, TableColumnModel paramTableColumnModel, ListSelectionModel paramListSelectionModel) {
    setLayout(null);
    setFocusTraversalKeys(0, JComponent.getManagingFocusForwardTraversalKeys());
    setFocusTraversalKeys(1, JComponent.getManagingFocusBackwardTraversalKeys());
    if (paramTableColumnModel == null) {
      paramTableColumnModel = createDefaultColumnModel();
      this.autoCreateColumnsFromModel = true;
    } 
    setColumnModel(paramTableColumnModel);
    if (paramListSelectionModel == null)
      paramListSelectionModel = createDefaultSelectionModel(); 
    setSelectionModel(paramListSelectionModel);
    if (paramTableModel == null)
      paramTableModel = createDefaultDataModel(); 
    setModel(paramTableModel);
    initializeLocalVars();
    updateUI();
  }
  
  public JTable(int paramInt1, int paramInt2) { this(new DefaultTableModel(paramInt1, paramInt2)); }
  
  public JTable(Vector paramVector1, Vector paramVector2) { this(new DefaultTableModel(paramVector1, paramVector2)); }
  
  public JTable(final Object[][] rowData, final Object[] columnNames) { this(new AbstractTableModel() {
          public String getColumnName(int param1Int) { return columnNames[param1Int].toString(); }
          
          public int getRowCount() { return rowData.length; }
          
          public int getColumnCount() { return columnNames.length; }
          
          public Object getValueAt(int param1Int1, int param1Int2) { return rowData[param1Int1][param1Int2]; }
          
          public boolean isCellEditable(int param1Int1, int param1Int2) { return true; }
          
          public void setValueAt(Object param1Object, int param1Int1, int param1Int2) {
            rowData[param1Int1][param1Int2] = param1Object;
            fireTableCellUpdated(param1Int1, param1Int2);
          }
        }); }
  
  public void addNotify() {
    super.addNotify();
    configureEnclosingScrollPane();
  }
  
  protected void configureEnclosingScrollPane() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      Container container1 = jViewport.getParent();
      if (container1 instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane)container1;
        JViewport jViewport1 = jScrollPane.getViewport();
        if (jViewport1 == null || SwingUtilities.getUnwrappedView(jViewport1) != this)
          return; 
        jScrollPane.setColumnHeaderView(getTableHeader());
        configureEnclosingScrollPaneUI();
      } 
    } 
  }
  
  private void configureEnclosingScrollPaneUI() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      Container container1 = jViewport.getParent();
      if (container1 instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane)container1;
        JViewport jViewport1 = jScrollPane.getViewport();
        if (jViewport1 == null || SwingUtilities.getUnwrappedView(jViewport1) != this)
          return; 
        Border border = jScrollPane.getBorder();
        if (border == null || border instanceof UIResource) {
          Border border1 = UIManager.getBorder("Table.scrollPaneBorder");
          if (border1 != null)
            jScrollPane.setBorder(border1); 
        } 
        Component component = jScrollPane.getCorner("UPPER_TRAILING_CORNER");
        if (component == null || component instanceof UIResource) {
          component = null;
          try {
            component = (Component)UIManager.get("Table.scrollPaneCornerComponent");
          } catch (Exception exception) {}
          jScrollPane.setCorner("UPPER_TRAILING_CORNER", component);
        } 
      } 
    } 
  }
  
  public void removeNotify() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
    this.editorRemover = null;
    unconfigureEnclosingScrollPane();
    super.removeNotify();
  }
  
  protected void unconfigureEnclosingScrollPane() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    if (container instanceof JViewport) {
      JViewport jViewport = (JViewport)container;
      Container container1 = jViewport.getParent();
      if (container1 instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane)container1;
        JViewport jViewport1 = jScrollPane.getViewport();
        if (jViewport1 == null || SwingUtilities.getUnwrappedView(jViewport1) != this)
          return; 
        jScrollPane.setColumnHeaderView(null);
        Component component = jScrollPane.getCorner("UPPER_TRAILING_CORNER");
        if (component instanceof UIResource)
          jScrollPane.setCorner("UPPER_TRAILING_CORNER", null); 
      } 
    } 
  }
  
  void setUIProperty(String paramString, Object paramObject) {
    if (paramString == "rowHeight") {
      if (!this.isRowHeightSet) {
        setRowHeight(((Number)paramObject).intValue());
        this.isRowHeightSet = false;
      } 
      return;
    } 
    super.setUIProperty(paramString, paramObject);
  }
  
  @Deprecated
  public static JScrollPane createScrollPaneForTable(JTable paramJTable) { return new JScrollPane(paramJTable); }
  
  public void setTableHeader(JTableHeader paramJTableHeader) {
    if (this.tableHeader != paramJTableHeader) {
      JTableHeader jTableHeader = this.tableHeader;
      if (jTableHeader != null)
        jTableHeader.setTable(null); 
      this.tableHeader = paramJTableHeader;
      if (paramJTableHeader != null)
        paramJTableHeader.setTable(this); 
      firePropertyChange("tableHeader", jTableHeader, paramJTableHeader);
    } 
  }
  
  public JTableHeader getTableHeader() { return this.tableHeader; }
  
  public void setRowHeight(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("New row height less than 1"); 
    int i = this.rowHeight;
    this.rowHeight = paramInt;
    this.rowModel = null;
    if (this.sortManager != null)
      this.sortManager.modelRowSizes = null; 
    this.isRowHeightSet = true;
    resizeAndRepaint();
    firePropertyChange("rowHeight", i, paramInt);
  }
  
  public int getRowHeight() { return this.rowHeight; }
  
  private SizeSequence getRowModel() {
    if (this.rowModel == null)
      this.rowModel = new SizeSequence(getRowCount(), getRowHeight()); 
    return this.rowModel;
  }
  
  public void setRowHeight(int paramInt1, int paramInt2) {
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("New row height less than 1"); 
    getRowModel().setSize(paramInt1, paramInt2);
    if (this.sortManager != null)
      this.sortManager.setViewRowHeight(paramInt1, paramInt2); 
    resizeAndRepaint();
  }
  
  public int getRowHeight(int paramInt) { return (this.rowModel == null) ? getRowHeight() : this.rowModel.getSize(paramInt); }
  
  public void setRowMargin(int paramInt) {
    int i = this.rowMargin;
    this.rowMargin = paramInt;
    resizeAndRepaint();
    firePropertyChange("rowMargin", i, paramInt);
  }
  
  public int getRowMargin() { return this.rowMargin; }
  
  public void setIntercellSpacing(Dimension paramDimension) {
    setRowMargin(paramDimension.height);
    getColumnModel().setColumnMargin(paramDimension.width);
    resizeAndRepaint();
  }
  
  public Dimension getIntercellSpacing() { return new Dimension(getColumnModel().getColumnMargin(), this.rowMargin); }
  
  public void setGridColor(Color paramColor) {
    if (paramColor == null)
      throw new IllegalArgumentException("New color is null"); 
    Color color = this.gridColor;
    this.gridColor = paramColor;
    firePropertyChange("gridColor", color, paramColor);
    repaint();
  }
  
  public Color getGridColor() { return this.gridColor; }
  
  public void setShowGrid(boolean paramBoolean) {
    setShowHorizontalLines(paramBoolean);
    setShowVerticalLines(paramBoolean);
    repaint();
  }
  
  public void setShowHorizontalLines(boolean paramBoolean) {
    boolean bool = this.showHorizontalLines;
    this.showHorizontalLines = paramBoolean;
    firePropertyChange("showHorizontalLines", bool, paramBoolean);
    repaint();
  }
  
  public void setShowVerticalLines(boolean paramBoolean) {
    boolean bool = this.showVerticalLines;
    this.showVerticalLines = paramBoolean;
    firePropertyChange("showVerticalLines", bool, paramBoolean);
    repaint();
  }
  
  public boolean getShowHorizontalLines() { return this.showHorizontalLines; }
  
  public boolean getShowVerticalLines() { return this.showVerticalLines; }
  
  public void setAutoResizeMode(int paramInt) {
    if (paramInt == 0 || paramInt == 1 || paramInt == 2 || paramInt == 3 || paramInt == 4) {
      int i = this.autoResizeMode;
      this.autoResizeMode = paramInt;
      resizeAndRepaint();
      if (this.tableHeader != null)
        this.tableHeader.resizeAndRepaint(); 
      firePropertyChange("autoResizeMode", i, this.autoResizeMode);
    } 
  }
  
  public int getAutoResizeMode() { return this.autoResizeMode; }
  
  public void setAutoCreateColumnsFromModel(boolean paramBoolean) {
    if (this.autoCreateColumnsFromModel != paramBoolean) {
      boolean bool = this.autoCreateColumnsFromModel;
      this.autoCreateColumnsFromModel = paramBoolean;
      if (paramBoolean)
        createDefaultColumnsFromModel(); 
      firePropertyChange("autoCreateColumnsFromModel", bool, paramBoolean);
    } 
  }
  
  public boolean getAutoCreateColumnsFromModel() { return this.autoCreateColumnsFromModel; }
  
  public void createDefaultColumnsFromModel() {
    TableModel tableModel = getModel();
    if (tableModel != null) {
      TableColumnModel tableColumnModel = getColumnModel();
      while (tableColumnModel.getColumnCount() > 0)
        tableColumnModel.removeColumn(tableColumnModel.getColumn(0)); 
      for (byte b = 0; b < tableModel.getColumnCount(); b++) {
        TableColumn tableColumn = new TableColumn(b);
        addColumn(tableColumn);
      } 
    } 
  }
  
  public void setDefaultRenderer(Class<?> paramClass, TableCellRenderer paramTableCellRenderer) {
    if (paramTableCellRenderer != null) {
      this.defaultRenderersByColumnClass.put(paramClass, paramTableCellRenderer);
    } else {
      this.defaultRenderersByColumnClass.remove(paramClass);
    } 
  }
  
  public TableCellRenderer getDefaultRenderer(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    Object object = this.defaultRenderersByColumnClass.get(paramClass);
    if (object != null)
      return (TableCellRenderer)object; 
    Class clazz = paramClass.getSuperclass();
    if (clazz == null && paramClass != Object.class)
      clazz = Object.class; 
    return getDefaultRenderer(clazz);
  }
  
  public void setDefaultEditor(Class<?> paramClass, TableCellEditor paramTableCellEditor) {
    if (paramTableCellEditor != null) {
      this.defaultEditorsByColumnClass.put(paramClass, paramTableCellEditor);
    } else {
      this.defaultEditorsByColumnClass.remove(paramClass);
    } 
  }
  
  public TableCellEditor getDefaultEditor(Class<?> paramClass) {
    if (paramClass == null)
      return null; 
    Object object = this.defaultEditorsByColumnClass.get(paramClass);
    return (object != null) ? (TableCellEditor)object : getDefaultEditor(paramClass.getSuperclass());
  }
  
  public void setDragEnabled(boolean paramBoolean) {
    if (paramBoolean && GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    this.dragEnabled = paramBoolean;
  }
  
  public boolean getDragEnabled() { return this.dragEnabled; }
  
  public final void setDropMode(DropMode paramDropMode) {
    if (paramDropMode != null)
      switch (paramDropMode) {
        case USE_SELECTION:
        case ON:
        case INSERT:
        case INSERT_ROWS:
        case INSERT_COLS:
        case ON_OR_INSERT:
        case ON_OR_INSERT_ROWS:
        case ON_OR_INSERT_COLS:
          this.dropMode = paramDropMode;
          return;
      }  
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for table");
  }
  
  public final DropMode getDropMode() { return this.dropMode; }
  
  DropLocation dropLocationForPoint(Point paramPoint) {
    DropLocation dropLocation1 = null;
    int i = rowAtPoint(paramPoint);
    int j = columnAtPoint(paramPoint);
    boolean bool = (Boolean.TRUE == getClientProperty("Table.isFileList") && SwingUtilities2.pointOutsidePrefSize(this, i, j, paramPoint)) ? 1 : 0;
    Rectangle rectangle = getCellRect(i, j, true);
    boolean bool1 = false;
    boolean bool2 = getComponentOrientation().isLeftToRight();
    switch (this.dropMode) {
      case USE_SELECTION:
      case ON:
        if (i == -1 || j == -1 || bool) {
          dropLocation1 = new DropLocation(paramPoint, -1, -1, false, false, null);
        } else {
          dropLocation1 = new DropLocation(paramPoint, i, j, false, false, null);
        } 
        return dropLocation1;
      case INSERT:
        if (i == -1 && j == -1) {
          dropLocation1 = new DropLocation(paramPoint, 0, 0, true, true, null);
        } else {
          SwingUtilities2.Section section = SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool2, true);
          if (i == -1) {
            if (section == SwingUtilities2.Section.LEADING) {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, true, null);
            } else if (section == SwingUtilities2.Section.TRAILING) {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j + 1, true, true, null);
            } else {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
            } 
          } else if (section == SwingUtilities2.Section.LEADING || section == SwingUtilities2.Section.TRAILING) {
            SwingUtilities2.Section section1 = SwingUtilities2.liesInVertical(rectangle, paramPoint, true);
            if (section1 == SwingUtilities2.Section.LEADING) {
              bool1 = true;
            } else if (section1 == SwingUtilities2.Section.TRAILING) {
              i++;
              bool1 = true;
            } 
            dropLocation1 = new DropLocation(paramPoint, i, (section == SwingUtilities2.Section.TRAILING) ? (j + 1) : j, bool1, true, null);
          } else {
            if (SwingUtilities2.liesInVertical(rectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING)
              i++; 
            dropLocation1 = new DropLocation(paramPoint, i, j, true, false, null);
          } 
        } 
        return dropLocation1;
      case INSERT_ROWS:
        if (i == -1 && j == -1) {
          dropLocation1 = new DropLocation(paramPoint, -1, -1, false, false, null);
        } else if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
        } else {
          if (SwingUtilities2.liesInVertical(rectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING)
            i++; 
          dropLocation1 = new DropLocation(paramPoint, i, j, true, false, null);
        } 
        return dropLocation1;
      case ON_OR_INSERT_ROWS:
        if (i == -1 && j == -1) {
          dropLocation1 = new DropLocation(paramPoint, -1, -1, false, false, null);
        } else if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
        } else {
          SwingUtilities2.Section section = SwingUtilities2.liesInVertical(rectangle, paramPoint, true);
          if (section == SwingUtilities2.Section.LEADING) {
            bool1 = true;
          } else if (section == SwingUtilities2.Section.TRAILING) {
            i++;
            bool1 = true;
          } 
          dropLocation1 = new DropLocation(paramPoint, i, j, bool1, false, null);
        } 
        return dropLocation1;
      case INSERT_COLS:
        if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, -1, -1, false, false, null);
        } else if (j == -1) {
          dropLocation1 = new DropLocation(paramPoint, getColumnCount(), j, false, true, null);
        } else {
          if (SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool2, false) == SwingUtilities2.Section.TRAILING)
            j++; 
          dropLocation1 = new DropLocation(paramPoint, i, j, false, true, null);
        } 
        return dropLocation1;
      case ON_OR_INSERT_COLS:
        if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, -1, -1, false, false, null);
        } else if (j == -1) {
          dropLocation1 = new DropLocation(paramPoint, i, getColumnCount(), false, true, null);
        } else {
          SwingUtilities2.Section section = SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool2, true);
          if (section == SwingUtilities2.Section.LEADING) {
            bool1 = true;
          } else if (section == SwingUtilities2.Section.TRAILING) {
            j++;
            bool1 = true;
          } 
          dropLocation1 = new DropLocation(paramPoint, i, j, false, bool1, null);
        } 
        return dropLocation1;
      case ON_OR_INSERT:
        if (i == -1 && j == -1) {
          dropLocation1 = new DropLocation(paramPoint, 0, 0, true, true, null);
        } else {
          SwingUtilities2.Section section = SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool2, true);
          if (i == -1) {
            if (section == SwingUtilities2.Section.LEADING) {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, true, null);
            } else if (section == SwingUtilities2.Section.TRAILING) {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j + 1, true, true, null);
            } else {
              dropLocation1 = new DropLocation(paramPoint, getRowCount(), j, true, false, null);
            } 
          } else {
            SwingUtilities2.Section section1 = SwingUtilities2.liesInVertical(rectangle, paramPoint, true);
            if (section1 == SwingUtilities2.Section.LEADING) {
              bool1 = true;
            } else if (section1 == SwingUtilities2.Section.TRAILING) {
              i++;
              bool1 = true;
            } 
            dropLocation1 = new DropLocation(paramPoint, i, (section == SwingUtilities2.Section.TRAILING) ? (j + 1) : j, bool1, (section != SwingUtilities2.Section.MIDDLE), null);
          } 
        } 
        return dropLocation1;
    } 
    assert false : "Unexpected drop mode";
    return dropLocation1;
  }
  
  Object setDropLocation(TransferHandler.DropLocation paramDropLocation, Object paramObject, boolean paramBoolean) {
    int[][] arrayOfInt = null;
    DropLocation dropLocation1 = (DropLocation)paramDropLocation;
    if (this.dropMode == DropMode.USE_SELECTION)
      if (dropLocation1 == null) {
        if (!paramBoolean && paramObject != null) {
          clearSelection();
          int[] arrayOfInt1 = (int[][])paramObject[0];
          int[] arrayOfInt2 = (int[][])paramObject[1];
          int[] arrayOfInt3 = (int[][])paramObject[2];
          for (int i : arrayOfInt1)
            addRowSelectionInterval(i, i); 
          for (int i : arrayOfInt2)
            addColumnSelectionInterval(i, i); 
          SwingUtilities2.setLeadAnchorWithoutSelection(getSelectionModel(), arrayOfInt3[1], arrayOfInt3[0]);
          SwingUtilities2.setLeadAnchorWithoutSelection(getColumnModel().getSelectionModel(), arrayOfInt3[3], arrayOfInt3[2]);
        } 
      } else {
        if (this.dropLocation == null) {
          arrayOfInt = new int[][] { getSelectedRows(), getSelectedColumns(), { getAdjustedIndex(getSelectionModel().getAnchorSelectionIndex(), true), getAdjustedIndex(getSelectionModel().getLeadSelectionIndex(), true), getAdjustedIndex(getColumnModel().getSelectionModel().getAnchorSelectionIndex(), false), getAdjustedIndex(getColumnModel().getSelectionModel().getLeadSelectionIndex(), false) } };
        } else {
          arrayOfInt = paramObject;
        } 
        if (dropLocation1.getRow() == -1) {
          clearSelectionAndLeadAnchor();
        } else {
          setRowSelectionInterval(dropLocation1.getRow(), dropLocation1.getRow());
          setColumnSelectionInterval(dropLocation1.getColumn(), dropLocation1.getColumn());
        } 
      }  
    DropLocation dropLocation2 = this.dropLocation;
    this.dropLocation = dropLocation1;
    firePropertyChange("dropLocation", dropLocation2, this.dropLocation);
    return arrayOfInt;
  }
  
  public final DropLocation getDropLocation() { return this.dropLocation; }
  
  public void setAutoCreateRowSorter(boolean paramBoolean) {
    boolean bool = this.autoCreateRowSorter;
    this.autoCreateRowSorter = paramBoolean;
    if (paramBoolean)
      setRowSorter(new TableRowSorter(getModel())); 
    firePropertyChange("autoCreateRowSorter", bool, paramBoolean);
  }
  
  public boolean getAutoCreateRowSorter() { return this.autoCreateRowSorter; }
  
  public void setUpdateSelectionOnSort(boolean paramBoolean) {
    if (this.updateSelectionOnSort != paramBoolean) {
      this.updateSelectionOnSort = paramBoolean;
      firePropertyChange("updateSelectionOnSort", !paramBoolean, paramBoolean);
    } 
  }
  
  public boolean getUpdateSelectionOnSort() { return this.updateSelectionOnSort; }
  
  public void setRowSorter(RowSorter<? extends TableModel> paramRowSorter) {
    RowSorter rowSorter = null;
    if (this.sortManager != null) {
      rowSorter = this.sortManager.sorter;
      this.sortManager.dispose();
      this.sortManager = null;
    } 
    this.rowModel = null;
    clearSelectionAndLeadAnchor();
    if (paramRowSorter != null)
      this.sortManager = new SortManager(paramRowSorter); 
    resizeAndRepaint();
    firePropertyChange("rowSorter", rowSorter, paramRowSorter);
    firePropertyChange("sorter", rowSorter, paramRowSorter);
  }
  
  public RowSorter<? extends TableModel> getRowSorter() { return (this.sortManager != null) ? this.sortManager.sorter : null; }
  
  public void setSelectionMode(int paramInt) {
    clearSelection();
    getSelectionModel().setSelectionMode(paramInt);
    getColumnModel().getSelectionModel().setSelectionMode(paramInt);
  }
  
  public void setRowSelectionAllowed(boolean paramBoolean) {
    boolean bool = this.rowSelectionAllowed;
    this.rowSelectionAllowed = paramBoolean;
    if (bool != paramBoolean)
      repaint(); 
    firePropertyChange("rowSelectionAllowed", bool, paramBoolean);
  }
  
  public boolean getRowSelectionAllowed() { return this.rowSelectionAllowed; }
  
  public void setColumnSelectionAllowed(boolean paramBoolean) {
    boolean bool = this.columnModel.getColumnSelectionAllowed();
    this.columnModel.setColumnSelectionAllowed(paramBoolean);
    if (bool != paramBoolean)
      repaint(); 
    firePropertyChange("columnSelectionAllowed", bool, paramBoolean);
  }
  
  public boolean getColumnSelectionAllowed() { return this.columnModel.getColumnSelectionAllowed(); }
  
  public void setCellSelectionEnabled(boolean paramBoolean) {
    setRowSelectionAllowed(paramBoolean);
    setColumnSelectionAllowed(paramBoolean);
    boolean bool = this.cellSelectionEnabled;
    this.cellSelectionEnabled = paramBoolean;
    firePropertyChange("cellSelectionEnabled", bool, paramBoolean);
  }
  
  public boolean getCellSelectionEnabled() { return (getRowSelectionAllowed() && getColumnSelectionAllowed()); }
  
  public void selectAll() {
    if (isEditing())
      removeEditor(); 
    if (getRowCount() > 0 && getColumnCount() > 0) {
      ListSelectionModel listSelectionModel = this.selectionModel;
      listSelectionModel.setValueIsAdjusting(true);
      int i = getAdjustedIndex(listSelectionModel.getLeadSelectionIndex(), true);
      int j = getAdjustedIndex(listSelectionModel.getAnchorSelectionIndex(), true);
      setRowSelectionInterval(0, getRowCount() - 1);
      SwingUtilities2.setLeadAnchorWithoutSelection(listSelectionModel, i, j);
      listSelectionModel.setValueIsAdjusting(false);
      listSelectionModel = this.columnModel.getSelectionModel();
      listSelectionModel.setValueIsAdjusting(true);
      i = getAdjustedIndex(listSelectionModel.getLeadSelectionIndex(), false);
      j = getAdjustedIndex(listSelectionModel.getAnchorSelectionIndex(), false);
      setColumnSelectionInterval(0, getColumnCount() - 1);
      SwingUtilities2.setLeadAnchorWithoutSelection(listSelectionModel, i, j);
      listSelectionModel.setValueIsAdjusting(false);
    } 
  }
  
  public void clearSelection() {
    this.selectionModel.clearSelection();
    this.columnModel.getSelectionModel().clearSelection();
  }
  
  private void clearSelectionAndLeadAnchor() {
    this.selectionModel.setValueIsAdjusting(true);
    this.columnModel.getSelectionModel().setValueIsAdjusting(true);
    clearSelection();
    this.selectionModel.setAnchorSelectionIndex(-1);
    this.selectionModel.setLeadSelectionIndex(-1);
    this.columnModel.getSelectionModel().setAnchorSelectionIndex(-1);
    this.columnModel.getSelectionModel().setLeadSelectionIndex(-1);
    this.selectionModel.setValueIsAdjusting(false);
    this.columnModel.getSelectionModel().setValueIsAdjusting(false);
  }
  
  private int getAdjustedIndex(int paramInt, boolean paramBoolean) {
    int i = paramBoolean ? getRowCount() : getColumnCount();
    return (paramInt < i) ? paramInt : -1;
  }
  
  private int boundRow(int paramInt) {
    if (paramInt < 0 || paramInt >= getRowCount())
      throw new IllegalArgumentException("Row index out of range"); 
    return paramInt;
  }
  
  private int boundColumn(int paramInt) {
    if (paramInt < 0 || paramInt >= getColumnCount())
      throw new IllegalArgumentException("Column index out of range"); 
    return paramInt;
  }
  
  public void setRowSelectionInterval(int paramInt1, int paramInt2) { this.selectionModel.setSelectionInterval(boundRow(paramInt1), boundRow(paramInt2)); }
  
  public void setColumnSelectionInterval(int paramInt1, int paramInt2) { this.columnModel.getSelectionModel().setSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2)); }
  
  public void addRowSelectionInterval(int paramInt1, int paramInt2) { this.selectionModel.addSelectionInterval(boundRow(paramInt1), boundRow(paramInt2)); }
  
  public void addColumnSelectionInterval(int paramInt1, int paramInt2) { this.columnModel.getSelectionModel().addSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2)); }
  
  public void removeRowSelectionInterval(int paramInt1, int paramInt2) { this.selectionModel.removeSelectionInterval(boundRow(paramInt1), boundRow(paramInt2)); }
  
  public void removeColumnSelectionInterval(int paramInt1, int paramInt2) { this.columnModel.getSelectionModel().removeSelectionInterval(boundColumn(paramInt1), boundColumn(paramInt2)); }
  
  public int getSelectedRow() { return this.selectionModel.getMinSelectionIndex(); }
  
  public int getSelectedColumn() { return this.columnModel.getSelectionModel().getMinSelectionIndex(); }
  
  public int[] getSelectedRows() {
    int i = this.selectionModel.getMinSelectionIndex();
    int j = this.selectionModel.getMaxSelectionIndex();
    if (i == -1 || j == -1)
      return new int[0]; 
    int[] arrayOfInt1 = new int[1 + j - i];
    byte b = 0;
    for (int k = i; k <= j; k++) {
      if (this.selectionModel.isSelectedIndex(k))
        arrayOfInt1[b++] = k; 
    } 
    int[] arrayOfInt2 = new int[b];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b);
    return arrayOfInt2;
  }
  
  public int[] getSelectedColumns() { return this.columnModel.getSelectedColumns(); }
  
  public int getSelectedRowCount() {
    int i = this.selectionModel.getMinSelectionIndex();
    int j = this.selectionModel.getMaxSelectionIndex();
    byte b = 0;
    for (int k = i; k <= j; k++) {
      if (this.selectionModel.isSelectedIndex(k))
        b++; 
    } 
    return b;
  }
  
  public int getSelectedColumnCount() { return this.columnModel.getSelectedColumnCount(); }
  
  public boolean isRowSelected(int paramInt) { return this.selectionModel.isSelectedIndex(paramInt); }
  
  public boolean isColumnSelected(int paramInt) { return this.columnModel.getSelectionModel().isSelectedIndex(paramInt); }
  
  public boolean isCellSelected(int paramInt1, int paramInt2) { return (!getRowSelectionAllowed() && !getColumnSelectionAllowed()) ? false : (((!getRowSelectionAllowed() || isRowSelected(paramInt1)) && (!getColumnSelectionAllowed() || isColumnSelected(paramInt2)))); }
  
  private void changeSelectionModel(ListSelectionModel paramListSelectionModel, int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, boolean paramBoolean4) {
    if (paramBoolean2) {
      if (paramBoolean1) {
        if (paramBoolean4) {
          paramListSelectionModel.addSelectionInterval(paramInt2, paramInt1);
        } else {
          paramListSelectionModel.removeSelectionInterval(paramInt2, paramInt1);
          if (Boolean.TRUE == getClientProperty("Table.isFileList")) {
            paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
            paramListSelectionModel.setAnchorSelectionIndex(paramInt2);
          } 
        } 
      } else {
        paramListSelectionModel.setSelectionInterval(paramInt2, paramInt1);
      } 
    } else if (paramBoolean1) {
      if (paramBoolean3) {
        paramListSelectionModel.removeSelectionInterval(paramInt1, paramInt1);
      } else {
        paramListSelectionModel.addSelectionInterval(paramInt1, paramInt1);
      } 
    } else {
      paramListSelectionModel.setSelectionInterval(paramInt1, paramInt1);
    } 
  }
  
  public void changeSelection(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    ListSelectionModel listSelectionModel1 = getSelectionModel();
    ListSelectionModel listSelectionModel2 = getColumnModel().getSelectionModel();
    int i = getAdjustedIndex(listSelectionModel1.getAnchorSelectionIndex(), true);
    int j = getAdjustedIndex(listSelectionModel2.getAnchorSelectionIndex(), false);
    boolean bool1 = true;
    if (i == -1) {
      if (getRowCount() > 0)
        i = 0; 
      bool1 = false;
    } 
    if (j == -1) {
      if (getColumnCount() > 0)
        j = 0; 
      bool1 = false;
    } 
    boolean bool2 = isCellSelected(paramInt1, paramInt2);
    bool1 = (bool1 && isCellSelected(i, j));
    changeSelectionModel(listSelectionModel2, paramInt2, paramBoolean1, paramBoolean2, bool2, j, bool1);
    changeSelectionModel(listSelectionModel1, paramInt1, paramBoolean1, paramBoolean2, bool2, i, bool1);
    if (getAutoscrolls()) {
      Rectangle rectangle = getCellRect(paramInt1, paramInt2, false);
      if (rectangle != null)
        scrollRectToVisible(rectangle); 
    } 
  }
  
  public Color getSelectionForeground() { return this.selectionForeground; }
  
  public void setSelectionForeground(Color paramColor) {
    Color color = this.selectionForeground;
    this.selectionForeground = paramColor;
    firePropertyChange("selectionForeground", color, paramColor);
    repaint();
  }
  
  public Color getSelectionBackground() { return this.selectionBackground; }
  
  public void setSelectionBackground(Color paramColor) {
    Color color = this.selectionBackground;
    this.selectionBackground = paramColor;
    firePropertyChange("selectionBackground", color, paramColor);
    repaint();
  }
  
  public TableColumn getColumn(Object paramObject) {
    TableColumnModel tableColumnModel = getColumnModel();
    int i = tableColumnModel.getColumnIndex(paramObject);
    return tableColumnModel.getColumn(i);
  }
  
  public int convertColumnIndexToModel(int paramInt) { return SwingUtilities2.convertColumnIndexToModel(getColumnModel(), paramInt); }
  
  public int convertColumnIndexToView(int paramInt) { return SwingUtilities2.convertColumnIndexToView(getColumnModel(), paramInt); }
  
  public int convertRowIndexToView(int paramInt) {
    RowSorter rowSorter = getRowSorter();
    return (rowSorter != null) ? rowSorter.convertRowIndexToView(paramInt) : paramInt;
  }
  
  public int convertRowIndexToModel(int paramInt) {
    RowSorter rowSorter = getRowSorter();
    return (rowSorter != null) ? rowSorter.convertRowIndexToModel(paramInt) : paramInt;
  }
  
  public int getRowCount() {
    RowSorter rowSorter = getRowSorter();
    return (rowSorter != null) ? rowSorter.getViewRowCount() : getModel().getRowCount();
  }
  
  public int getColumnCount() { return getColumnModel().getColumnCount(); }
  
  public String getColumnName(int paramInt) { return getModel().getColumnName(convertColumnIndexToModel(paramInt)); }
  
  public Class<?> getColumnClass(int paramInt) { return getModel().getColumnClass(convertColumnIndexToModel(paramInt)); }
  
  public Object getValueAt(int paramInt1, int paramInt2) { return getModel().getValueAt(convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2)); }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) { getModel().setValueAt(paramObject, convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2)); }
  
  public boolean isCellEditable(int paramInt1, int paramInt2) { return getModel().isCellEditable(convertRowIndexToModel(paramInt1), convertColumnIndexToModel(paramInt2)); }
  
  public void addColumn(TableColumn paramTableColumn) {
    if (paramTableColumn.getHeaderValue() == null) {
      int i = paramTableColumn.getModelIndex();
      String str = getModel().getColumnName(i);
      paramTableColumn.setHeaderValue(str);
    } 
    getColumnModel().addColumn(paramTableColumn);
  }
  
  public void removeColumn(TableColumn paramTableColumn) { getColumnModel().removeColumn(paramTableColumn); }
  
  public void moveColumn(int paramInt1, int paramInt2) { getColumnModel().moveColumn(paramInt1, paramInt2); }
  
  public int columnAtPoint(Point paramPoint) {
    int i = paramPoint.x;
    if (!getComponentOrientation().isLeftToRight())
      i = getWidth() - i - 1; 
    return getColumnModel().getColumnIndexAtX(i);
  }
  
  public int rowAtPoint(Point paramPoint) {
    int i = paramPoint.y;
    int j = (this.rowModel == null) ? (i / getRowHeight()) : this.rowModel.getIndex(i);
    return (j < 0) ? -1 : ((j >= getRowCount()) ? -1 : j);
  }
  
  public Rectangle getCellRect(int paramInt1, int paramInt2, boolean paramBoolean) {
    Rectangle rectangle = new Rectangle();
    boolean bool = true;
    if (paramInt1 < 0) {
      bool = false;
    } else if (paramInt1 >= getRowCount()) {
      rectangle.y = getHeight();
      bool = false;
    } else {
      rectangle.height = getRowHeight(paramInt1);
      rectangle.y = (this.rowModel == null) ? (paramInt1 * rectangle.height) : this.rowModel.getPosition(paramInt1);
    } 
    if (paramInt2 < 0) {
      if (!getComponentOrientation().isLeftToRight())
        rectangle.x = getWidth(); 
      bool = false;
    } else if (paramInt2 >= getColumnCount()) {
      if (getComponentOrientation().isLeftToRight())
        rectangle.x = getWidth(); 
      bool = false;
    } else {
      TableColumnModel tableColumnModel = getColumnModel();
      if (getComponentOrientation().isLeftToRight()) {
        for (byte b = 0; b < paramInt2; b++)
          rectangle.x += tableColumnModel.getColumn(b).getWidth(); 
      } else {
        for (int i = tableColumnModel.getColumnCount() - 1; i > paramInt2; i--)
          rectangle.x += tableColumnModel.getColumn(i).getWidth(); 
      } 
      rectangle.width = tableColumnModel.getColumn(paramInt2).getWidth();
    } 
    if (bool && !paramBoolean) {
      int i = Math.min(getRowMargin(), rectangle.height);
      int j = Math.min(getColumnModel().getColumnMargin(), rectangle.width);
      rectangle.setBounds(rectangle.x + j / 2, rectangle.y + i / 2, rectangle.width - j, rectangle.height - i);
    } 
    return rectangle;
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn) {
    TableColumnModel tableColumnModel = getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      if (tableColumnModel.getColumn(b) == paramTableColumn)
        return b; 
    } 
    return -1;
  }
  
  public void doLayout() {
    TableColumn tableColumn = getResizingColumn();
    if (tableColumn == null) {
      setWidthsFromPreferredWidths(false);
    } else {
      int i = viewIndexForColumn(tableColumn);
      int j = getWidth() - getColumnModel().getTotalColumnWidth();
      accommodateDelta(i, j);
      j = getWidth() - getColumnModel().getTotalColumnWidth();
      if (j != 0)
        tableColumn.setWidth(tableColumn.getWidth() + j); 
      setWidthsFromPreferredWidths(true);
    } 
    super.doLayout();
  }
  
  private TableColumn getResizingColumn() { return (this.tableHeader == null) ? null : this.tableHeader.getResizingColumn(); }
  
  @Deprecated
  public void sizeColumnsToFit(boolean paramBoolean) {
    int i = this.autoResizeMode;
    setAutoResizeMode(paramBoolean ? 3 : 4);
    sizeColumnsToFit(-1);
    setAutoResizeMode(i);
  }
  
  public void sizeColumnsToFit(int paramInt) {
    if (paramInt == -1) {
      setWidthsFromPreferredWidths(false);
    } else if (this.autoResizeMode == 0) {
      TableColumn tableColumn = getColumnModel().getColumn(paramInt);
      tableColumn.setPreferredWidth(tableColumn.getWidth());
    } else {
      int i = getWidth() - getColumnModel().getTotalColumnWidth();
      accommodateDelta(paramInt, i);
      setWidthsFromPreferredWidths(true);
    } 
  }
  
  private void setWidthsFromPreferredWidths(final boolean inverse) {
    int i = getWidth();
    int j = (getPreferredSize()).width;
    int k = !paramBoolean ? i : j;
    final TableColumnModel cm = this.columnModel;
    Resizable3 resizable3 = new Resizable3() {
        public int getElementCount() { return cm.getColumnCount(); }
        
        public int getLowerBoundAt(int param1Int) { return cm.getColumn(param1Int).getMinWidth(); }
        
        public int getUpperBoundAt(int param1Int) { return cm.getColumn(param1Int).getMaxWidth(); }
        
        public int getMidPointAt(int param1Int) { return !inverse ? cm.getColumn(param1Int).getPreferredWidth() : cm.getColumn(param1Int).getWidth(); }
        
        public void setSizeAt(int param1Int1, int param1Int2) {
          if (!inverse) {
            cm.getColumn(param1Int2).setWidth(param1Int1);
          } else {
            cm.getColumn(param1Int2).setPreferredWidth(param1Int1);
          } 
        }
      };
    adjustSizes(k, resizable3, paramBoolean);
  }
  
  private void accommodateDelta(int paramInt1, int paramInt2) {
    int k;
    int i = getColumnCount();
    int j = paramInt1;
    switch (this.autoResizeMode) {
      case 1:
        k = Math.min(++j + 1, i);
        break;
      case 2:
        j++;
        k = i;
        break;
      case 3:
        j = i - 1;
        k = j + 1;
        break;
      case 4:
        j = 0;
        k = i;
        break;
      default:
        return;
    } 
    final int start = j;
    final int end = k;
    final TableColumnModel cm = this.columnModel;
    Resizable3 resizable3 = new Resizable3() {
        public int getElementCount() { return end - start; }
        
        public int getLowerBoundAt(int param1Int) { return cm.getColumn(param1Int + start).getMinWidth(); }
        
        public int getUpperBoundAt(int param1Int) { return cm.getColumn(param1Int + start).getMaxWidth(); }
        
        public int getMidPointAt(int param1Int) { return cm.getColumn(param1Int + start).getWidth(); }
        
        public void setSizeAt(int param1Int1, int param1Int2) { cm.getColumn(param1Int2 + start).setWidth(param1Int1); }
      };
    int i1 = 0;
    for (int i2 = j; i2 < k; i2++) {
      TableColumn tableColumn = this.columnModel.getColumn(i2);
      int i3 = tableColumn.getWidth();
      i1 += i3;
    } 
    adjustSizes((i1 + paramInt2), resizable3, false);
  }
  
  private void adjustSizes(long paramLong, final Resizable3 r, boolean paramBoolean) {
    Resizable2 resizable2;
    int i = paramResizable3.getElementCount();
    long l = 0L;
    for (byte b = 0; b < i; b++)
      l += paramResizable3.getMidPointAt(b); 
    if (((paramLong < l) ? 1 : 0) == (!paramBoolean ? 1 : 0)) {
      resizable2 = new Resizable2() {
          public int getElementCount() { return r.getElementCount(); }
          
          public int getLowerBoundAt(int param1Int) { return r.getLowerBoundAt(param1Int); }
          
          public int getUpperBoundAt(int param1Int) { return r.getMidPointAt(param1Int); }
          
          public void setSizeAt(int param1Int1, int param1Int2) { r.setSizeAt(param1Int1, param1Int2); }
        };
    } else {
      resizable2 = new Resizable2() {
          public int getElementCount() { return r.getElementCount(); }
          
          public int getLowerBoundAt(int param1Int) { return r.getMidPointAt(param1Int); }
          
          public int getUpperBoundAt(int param1Int) { return r.getUpperBoundAt(param1Int); }
          
          public void setSizeAt(int param1Int1, int param1Int2) { r.setSizeAt(param1Int1, param1Int2); }
        };
    } 
    adjustSizes(paramLong, resizable2, !paramBoolean);
  }
  
  private void adjustSizes(long paramLong, Resizable2 paramResizable2, boolean paramBoolean) {
    long l1 = 0L;
    long l2 = 0L;
    byte b;
    for (b = 0; b < paramResizable2.getElementCount(); b++) {
      l1 += paramResizable2.getLowerBoundAt(b);
      l2 += paramResizable2.getUpperBoundAt(b);
    } 
    if (paramBoolean)
      paramLong = Math.min(Math.max(l1, paramLong), l2); 
    for (b = 0; b < paramResizable2.getElementCount(); b++) {
      int k;
      int i = paramResizable2.getLowerBoundAt(b);
      int j = paramResizable2.getUpperBoundAt(b);
      if (l1 == l2) {
        k = i;
      } else {
        double d = (paramLong - l1) / (l2 - l1);
        k = (int)Math.round(i + d * (j - i));
      } 
      paramResizable2.setSizeAt(k, b);
      paramLong -= k;
      l1 -= i;
      l2 -= j;
    } 
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    String str = null;
    Point point = paramMouseEvent.getPoint();
    int i = columnAtPoint(point);
    int j = rowAtPoint(point);
    if (i != -1 && j != -1) {
      TableCellRenderer tableCellRenderer = getCellRenderer(j, i);
      Component component = prepareRenderer(tableCellRenderer, j, i);
      if (component instanceof JComponent) {
        Rectangle rectangle = getCellRect(j, i, false);
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
  
  public void setSurrendersFocusOnKeystroke(boolean paramBoolean) { this.surrendersFocusOnKeystroke = paramBoolean; }
  
  public boolean getSurrendersFocusOnKeystroke() { return this.surrendersFocusOnKeystroke; }
  
  public boolean editCellAt(int paramInt1, int paramInt2) { return editCellAt(paramInt1, paramInt2, null); }
  
  public boolean editCellAt(int paramInt1, int paramInt2, EventObject paramEventObject) {
    if (this.cellEditor != null && !this.cellEditor.stopCellEditing())
      return false; 
    if (paramInt1 < 0 || paramInt1 >= getRowCount() || paramInt2 < 0 || paramInt2 >= getColumnCount())
      return false; 
    if (!isCellEditable(paramInt1, paramInt2))
      return false; 
    if (this.editorRemover == null) {
      KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      this.editorRemover = new CellEditorRemover(keyboardFocusManager);
      keyboardFocusManager.addPropertyChangeListener("permanentFocusOwner", this.editorRemover);
    } 
    TableCellEditor tableCellEditor = getCellEditor(paramInt1, paramInt2);
    if (tableCellEditor != null && tableCellEditor.isCellEditable(paramEventObject)) {
      this.editorComp = prepareEditor(tableCellEditor, paramInt1, paramInt2);
      if (this.editorComp == null) {
        removeEditor();
        return false;
      } 
      this.editorComp.setBounds(getCellRect(paramInt1, paramInt2, false));
      add(this.editorComp);
      this.editorComp.validate();
      this.editorComp.repaint();
      setCellEditor(tableCellEditor);
      setEditingRow(paramInt1);
      setEditingColumn(paramInt2);
      tableCellEditor.addCellEditorListener(this);
      return true;
    } 
    return false;
  }
  
  public boolean isEditing() { return (this.cellEditor != null); }
  
  public Component getEditorComponent() { return this.editorComp; }
  
  public int getEditingColumn() { return this.editingColumn; }
  
  public int getEditingRow() { return this.editingRow; }
  
  public TableUI getUI() { return (TableUI)this.ui; }
  
  public void setUI(TableUI paramTableUI) {
    if (this.ui != paramTableUI) {
      setUI(paramTableUI);
      repaint();
    } 
  }
  
  public void updateUI() {
    TableColumnModel tableColumnModel = getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      TableColumn tableColumn = tableColumnModel.getColumn(b);
      SwingUtilities.updateRendererOrEditorUI(tableColumn.getCellRenderer());
      SwingUtilities.updateRendererOrEditorUI(tableColumn.getCellEditor());
      SwingUtilities.updateRendererOrEditorUI(tableColumn.getHeaderRenderer());
    } 
    Enumeration enumeration1 = this.defaultRenderersByColumnClass.elements();
    while (enumeration1.hasMoreElements())
      SwingUtilities.updateRendererOrEditorUI(enumeration1.nextElement()); 
    Enumeration enumeration2 = this.defaultEditorsByColumnClass.elements();
    while (enumeration2.hasMoreElements())
      SwingUtilities.updateRendererOrEditorUI(enumeration2.nextElement()); 
    if (this.tableHeader != null && this.tableHeader.getParent() == null)
      this.tableHeader.updateUI(); 
    configureEnclosingScrollPaneUI();
    setUI((TableUI)UIManager.getUI(this));
  }
  
  public String getUIClassID() { return "TableUI"; }
  
  public void setModel(TableModel paramTableModel) {
    if (paramTableModel == null)
      throw new IllegalArgumentException("Cannot set a null TableModel"); 
    if (this.dataModel != paramTableModel) {
      TableModel tableModel = this.dataModel;
      if (tableModel != null)
        tableModel.removeTableModelListener(this); 
      this.dataModel = paramTableModel;
      paramTableModel.addTableModelListener(this);
      tableChanged(new TableModelEvent(paramTableModel, -1));
      firePropertyChange("model", tableModel, paramTableModel);
      if (getAutoCreateRowSorter())
        setRowSorter(new TableRowSorter(paramTableModel)); 
    } 
  }
  
  public TableModel getModel() { return this.dataModel; }
  
  public void setColumnModel(TableColumnModel paramTableColumnModel) {
    if (paramTableColumnModel == null)
      throw new IllegalArgumentException("Cannot set a null ColumnModel"); 
    TableColumnModel tableColumnModel = this.columnModel;
    if (paramTableColumnModel != tableColumnModel) {
      if (tableColumnModel != null)
        tableColumnModel.removeColumnModelListener(this); 
      this.columnModel = paramTableColumnModel;
      paramTableColumnModel.addColumnModelListener(this);
      if (this.tableHeader != null)
        this.tableHeader.setColumnModel(paramTableColumnModel); 
      firePropertyChange("columnModel", tableColumnModel, paramTableColumnModel);
      resizeAndRepaint();
    } 
  }
  
  public TableColumnModel getColumnModel() { return this.columnModel; }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel) {
    if (paramListSelectionModel == null)
      throw new IllegalArgumentException("Cannot set a null SelectionModel"); 
    ListSelectionModel listSelectionModel = this.selectionModel;
    if (paramListSelectionModel != listSelectionModel) {
      if (listSelectionModel != null)
        listSelectionModel.removeListSelectionListener(this); 
      this.selectionModel = paramListSelectionModel;
      paramListSelectionModel.addListSelectionListener(this);
      firePropertyChange("selectionModel", listSelectionModel, paramListSelectionModel);
      repaint();
    } 
  }
  
  public ListSelectionModel getSelectionModel() { return this.selectionModel; }
  
  public void sorterChanged(RowSorterEvent paramRowSorterEvent) {
    if (paramRowSorterEvent.getType() == RowSorterEvent.Type.SORT_ORDER_CHANGED) {
      JTableHeader jTableHeader = getTableHeader();
      if (jTableHeader != null)
        jTableHeader.repaint(); 
    } else if (paramRowSorterEvent.getType() == RowSorterEvent.Type.SORTED) {
      this.sorterChanged = true;
      if (!this.ignoreSortChange)
        sortedTableChanged(paramRowSorterEvent, null); 
    } 
  }
  
  private void sortedTableChanged(RowSorterEvent paramRowSorterEvent, TableModelEvent paramTableModelEvent) {
    int i = -1;
    ModelChange modelChange = (paramTableModelEvent != null) ? new ModelChange(paramTableModelEvent) : null;
    if ((modelChange == null || !modelChange.allRowsChanged) && this.editingRow != -1)
      i = convertRowIndexToModel(paramRowSorterEvent, this.editingRow); 
    this.sortManager.prepareForChange(paramRowSorterEvent, modelChange);
    if (paramTableModelEvent != null) {
      if (modelChange.type == 0)
        repaintSortedRows(modelChange); 
      notifySorter(modelChange);
      if (modelChange.type != 0)
        this.sorterChanged = true; 
    } else {
      this.sorterChanged = true;
    } 
    this.sortManager.processChange(paramRowSorterEvent, modelChange, this.sorterChanged);
    if (this.sorterChanged) {
      if (this.editingRow != -1) {
        byte b = (i == -1) ? -1 : convertRowIndexToView(i, modelChange);
        restoreSortingEditingRow(b);
      } 
      if (paramTableModelEvent == null || modelChange.type != 0)
        resizeAndRepaint(); 
    } 
    if (modelChange != null && modelChange.allRowsChanged) {
      clearSelectionAndLeadAnchor();
      resizeAndRepaint();
    } 
  }
  
  private void repaintSortedRows(ModelChange paramModelChange) {
    if (paramModelChange.startModelIndex > paramModelChange.endModelIndex || paramModelChange.startModelIndex + 10 < paramModelChange.endModelIndex) {
      repaint();
      return;
    } 
    int i = paramModelChange.event.getColumn();
    int j = i;
    if (j == -1) {
      j = 0;
    } else {
      j = convertColumnIndexToView(j);
      if (j == -1)
        return; 
    } 
    int k = paramModelChange.startModelIndex;
    while (k <= paramModelChange.endModelIndex) {
      int m = convertRowIndexToView(k++);
      if (m != -1) {
        Rectangle rectangle = getCellRect(m, j, false);
        int n = rectangle.x;
        int i1 = rectangle.width;
        if (i == -1) {
          n = 0;
          i1 = getWidth();
        } 
        repaint(n, rectangle.y, i1, rectangle.height);
      } 
    } 
  }
  
  private void restoreSortingSelection(int[] paramArrayOfInt, int paramInt, ModelChange paramModelChange) {
    int i;
    for (i = paramArrayOfInt.length - 1; i >= 0; i--)
      paramArrayOfInt[i] = convertRowIndexToView(paramArrayOfInt[i], paramModelChange); 
    paramInt = convertRowIndexToView(paramInt, paramModelChange);
    if (paramArrayOfInt.length == 0 || (paramArrayOfInt.length == 1 && paramArrayOfInt[0] == getSelectedRow()))
      return; 
    this.selectionModel.setValueIsAdjusting(true);
    this.selectionModel.clearSelection();
    for (i = paramArrayOfInt.length - 1; i >= 0; i--) {
      if (paramArrayOfInt[i] != -1)
        this.selectionModel.addSelectionInterval(paramArrayOfInt[i], paramArrayOfInt[i]); 
    } 
    SwingUtilities2.setLeadAnchorWithoutSelection(this.selectionModel, paramInt, paramInt);
    this.selectionModel.setValueIsAdjusting(false);
  }
  
  private void restoreSortingEditingRow(int paramInt) {
    if (paramInt == -1) {
      TableCellEditor tableCellEditor = getCellEditor();
      if (tableCellEditor != null) {
        tableCellEditor.cancelCellEditing();
        if (getCellEditor() != null)
          removeEditor(); 
      } 
    } else {
      this.editingRow = paramInt;
      repaint();
    } 
  }
  
  private void notifySorter(ModelChange paramModelChange) {
    try {
      this.ignoreSortChange = true;
      this.sorterChanged = false;
      switch (paramModelChange.type) {
        case 0:
          if (paramModelChange.event.getLastRow() == Integer.MAX_VALUE) {
            this.sortManager.sorter.allRowsChanged();
            break;
          } 
          if (paramModelChange.event.getColumn() == -1) {
            this.sortManager.sorter.rowsUpdated(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
            break;
          } 
          this.sortManager.sorter.rowsUpdated(paramModelChange.startModelIndex, paramModelChange.endModelIndex, paramModelChange.event.getColumn());
          break;
        case 1:
          this.sortManager.sorter.rowsInserted(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
          break;
        case -1:
          this.sortManager.sorter.rowsDeleted(paramModelChange.startModelIndex, paramModelChange.endModelIndex);
          break;
      } 
    } finally {
      this.ignoreSortChange = false;
    } 
  }
  
  private int convertRowIndexToView(int paramInt, ModelChange paramModelChange) {
    if (paramInt < 0)
      return -1; 
    if (paramModelChange != null && paramInt >= paramModelChange.startModelIndex) {
      if (paramModelChange.type == 1)
        return (paramInt + paramModelChange.length >= paramModelChange.modelRowCount) ? -1 : this.sortManager.sorter.convertRowIndexToView(paramInt + paramModelChange.length); 
      if (paramModelChange.type == -1)
        return (paramInt <= paramModelChange.endModelIndex) ? -1 : ((paramInt - paramModelChange.length >= paramModelChange.modelRowCount) ? -1 : this.sortManager.sorter.convertRowIndexToView(paramInt - paramModelChange.length)); 
    } 
    return (paramInt >= getModel().getRowCount()) ? -1 : this.sortManager.sorter.convertRowIndexToView(paramInt);
  }
  
  private int[] convertSelectionToModel(RowSorterEvent paramRowSorterEvent) {
    int[] arrayOfInt = getSelectedRows();
    for (int i = arrayOfInt.length - 1; i >= 0; i--)
      arrayOfInt[i] = convertRowIndexToModel(paramRowSorterEvent, arrayOfInt[i]); 
    return arrayOfInt;
  }
  
  private int convertRowIndexToModel(RowSorterEvent paramRowSorterEvent, int paramInt) { return (paramRowSorterEvent != null) ? ((paramRowSorterEvent.getPreviousRowCount() == 0) ? paramInt : paramRowSorterEvent.convertPreviousRowIndexToModel(paramInt)) : ((paramInt < 0 || paramInt >= getRowCount()) ? -1 : convertRowIndexToModel(paramInt)); }
  
  public void tableChanged(TableModelEvent paramTableModelEvent) {
    Rectangle rectangle;
    if (paramTableModelEvent == null || paramTableModelEvent.getFirstRow() == -1) {
      clearSelectionAndLeadAnchor();
      this.rowModel = null;
      if (this.sortManager != null) {
        try {
          this.ignoreSortChange = true;
          this.sortManager.sorter.modelStructureChanged();
        } finally {
          this.ignoreSortChange = false;
        } 
        this.sortManager.allChanged();
      } 
      if (getAutoCreateColumnsFromModel()) {
        createDefaultColumnsFromModel();
        return;
      } 
      resizeAndRepaint();
      return;
    } 
    if (this.sortManager != null) {
      sortedTableChanged(null, paramTableModelEvent);
      return;
    } 
    if (this.rowModel != null)
      repaint(); 
    if (paramTableModelEvent.getType() == 1) {
      tableRowsInserted(paramTableModelEvent);
      return;
    } 
    if (paramTableModelEvent.getType() == -1) {
      tableRowsDeleted(paramTableModelEvent);
      return;
    } 
    int i = paramTableModelEvent.getColumn();
    int j = paramTableModelEvent.getFirstRow();
    int k = paramTableModelEvent.getLastRow();
    if (i == -1) {
      rectangle = new Rectangle(0, j * getRowHeight(), getColumnModel().getTotalColumnWidth(), 0);
    } else {
      int m = convertColumnIndexToView(i);
      rectangle = getCellRect(j, m, false);
    } 
    if (k != Integer.MAX_VALUE) {
      rectangle.height = (k - j + 1) * getRowHeight();
      repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } else {
      clearSelectionAndLeadAnchor();
      resizeAndRepaint();
      this.rowModel = null;
    } 
  }
  
  private void tableRowsInserted(TableModelEvent paramTableModelEvent) {
    int i = paramTableModelEvent.getFirstRow();
    int j = paramTableModelEvent.getLastRow();
    if (i < 0)
      i = 0; 
    if (j < 0)
      j = getRowCount() - 1; 
    int k = j - i + 1;
    this.selectionModel.insertIndexInterval(i, k, true);
    if (this.rowModel != null)
      this.rowModel.insertEntries(i, k, getRowHeight()); 
    int m = getRowHeight();
    Rectangle rectangle = new Rectangle(0, i * m, getColumnModel().getTotalColumnWidth(), (getRowCount() - i) * m);
    revalidate();
    repaint(rectangle);
  }
  
  private void tableRowsDeleted(TableModelEvent paramTableModelEvent) {
    int i = paramTableModelEvent.getFirstRow();
    int j = paramTableModelEvent.getLastRow();
    if (i < 0)
      i = 0; 
    if (j < 0)
      j = getRowCount() - 1; 
    int k = j - i + 1;
    int m = getRowCount() + k;
    this.selectionModel.removeIndexInterval(i, j);
    if (this.rowModel != null)
      this.rowModel.removeEntries(i, k); 
    int n = getRowHeight();
    Rectangle rectangle = new Rectangle(0, i * n, getColumnModel().getTotalColumnWidth(), (m - i) * n);
    revalidate();
    repaint(rectangle);
  }
  
  public void columnAdded(TableColumnModelEvent paramTableColumnModelEvent) {
    if (isEditing())
      removeEditor(); 
    resizeAndRepaint();
  }
  
  public void columnRemoved(TableColumnModelEvent paramTableColumnModelEvent) {
    if (isEditing())
      removeEditor(); 
    resizeAndRepaint();
  }
  
  public void columnMoved(TableColumnModelEvent paramTableColumnModelEvent) {
    if (isEditing() && !getCellEditor().stopCellEditing())
      getCellEditor().cancelCellEditing(); 
    repaint();
  }
  
  public void columnMarginChanged(ChangeEvent paramChangeEvent) {
    if (isEditing() && !getCellEditor().stopCellEditing())
      getCellEditor().cancelCellEditing(); 
    TableColumn tableColumn = getResizingColumn();
    if (tableColumn != null && this.autoResizeMode == 0)
      tableColumn.setPreferredWidth(tableColumn.getWidth()); 
    resizeAndRepaint();
  }
  
  private int limit(int paramInt1, int paramInt2, int paramInt3) { return Math.min(paramInt3, Math.max(paramInt1, paramInt2)); }
  
  public void columnSelectionChanged(ListSelectionEvent paramListSelectionEvent) {
    boolean bool = paramListSelectionEvent.getValueIsAdjusting();
    if (this.columnSelectionAdjusting && !bool) {
      this.columnSelectionAdjusting = false;
      return;
    } 
    this.columnSelectionAdjusting = bool;
    if (getRowCount() <= 0 || getColumnCount() <= 0)
      return; 
    int i = limit(paramListSelectionEvent.getFirstIndex(), 0, getColumnCount() - 1);
    int j = limit(paramListSelectionEvent.getLastIndex(), 0, getColumnCount() - 1);
    int k = 0;
    int m = getRowCount() - 1;
    if (getRowSelectionAllowed()) {
      k = this.selectionModel.getMinSelectionIndex();
      m = this.selectionModel.getMaxSelectionIndex();
      int n = getAdjustedIndex(this.selectionModel.getLeadSelectionIndex(), true);
      if (k == -1 || m == -1) {
        if (n == -1)
          return; 
        k = m = n;
      } else if (n != -1) {
        k = Math.min(k, n);
        m = Math.max(m, n);
      } 
    } 
    Rectangle rectangle1 = getCellRect(k, i, false);
    Rectangle rectangle2 = getCellRect(m, j, false);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    repaint(rectangle3);
  }
  
  public void valueChanged(ListSelectionEvent paramListSelectionEvent) {
    if (this.sortManager != null)
      this.sortManager.viewSelectionChanged(paramListSelectionEvent); 
    boolean bool = paramListSelectionEvent.getValueIsAdjusting();
    if (this.rowSelectionAdjusting && !bool) {
      this.rowSelectionAdjusting = false;
      return;
    } 
    this.rowSelectionAdjusting = bool;
    if (getRowCount() <= 0 || getColumnCount() <= 0)
      return; 
    int i = limit(paramListSelectionEvent.getFirstIndex(), 0, getRowCount() - 1);
    int j = limit(paramListSelectionEvent.getLastIndex(), 0, getRowCount() - 1);
    Rectangle rectangle1 = getCellRect(i, 0, false);
    Rectangle rectangle2 = getCellRect(j, getColumnCount() - 1, false);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    repaint(rectangle3);
  }
  
  public void editingStopped(ChangeEvent paramChangeEvent) {
    TableCellEditor tableCellEditor = getCellEditor();
    if (tableCellEditor != null) {
      Object object = tableCellEditor.getCellEditorValue();
      setValueAt(object, this.editingRow, this.editingColumn);
      removeEditor();
    } 
  }
  
  public void editingCanceled(ChangeEvent paramChangeEvent) { removeEditor(); }
  
  public void setPreferredScrollableViewportSize(Dimension paramDimension) { this.preferredViewportSize = paramDimension; }
  
  public Dimension getPreferredScrollableViewportSize() { return this.preferredViewportSize; }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    int n;
    int i = getLeadingRow(paramRectangle);
    int j = getLeadingCol(paramRectangle);
    if (paramInt1 == 1 && i < 0)
      return getRowHeight(); 
    if (paramInt1 == 0 && j < 0)
      return 100; 
    Rectangle rectangle = getCellRect(i, j, true);
    int k = leadingEdge(paramRectangle, paramInt1);
    int m = leadingEdge(rectangle, paramInt1);
    if (paramInt1 == 1) {
      n = rectangle.height;
    } else {
      n = rectangle.width;
    } 
    if (k == m) {
      if (paramInt2 < 0) {
        int i3 = 0;
        if (paramInt1 == 1) {
          while (--i >= 0) {
            i3 = getRowHeight(i);
            if (i3 != 0)
              break; 
          } 
        } else {
          while (--j >= 0) {
            i3 = (getCellRect(i, j, true)).width;
            if (i3 != 0)
              break; 
          } 
        } 
        return i3;
      } 
      return n;
    } 
    int i1 = Math.abs(k - m);
    int i2 = n - i1;
    return (paramInt2 > 0) ? i2 : i1;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (getRowCount() == 0) {
      if (1 == paramInt1) {
        int i = getRowHeight();
        return (i > 0) ? Math.max(i, paramRectangle.height / i * i) : paramRectangle.height;
      } 
      return paramRectangle.width;
    } 
    if (null == this.rowModel && 1 == paramInt1) {
      int i = rowAtPoint(paramRectangle.getLocation());
      assert i != -1;
      int j = columnAtPoint(paramRectangle.getLocation());
      Rectangle rectangle = getCellRect(i, j, true);
      if (rectangle.y == paramRectangle.y) {
        int k = getRowHeight();
        assert k > 0;
        return Math.max(k, paramRectangle.height / k * k);
      } 
    } 
    return (paramInt2 < 0) ? getPreviousBlockIncrement(paramRectangle, paramInt1) : getNextBlockIncrement(paramRectangle, paramInt1);
  }
  
  private int getPreviousBlockIncrement(Rectangle paramRectangle, int paramInt) {
    int n;
    Point point;
    int k;
    int m = leadingEdge(paramRectangle, paramInt);
    boolean bool = getComponentOrientation().isLeftToRight();
    if (paramInt == 1) {
      k = m - paramRectangle.height;
      int i1 = paramRectangle.x + (bool ? 0 : paramRectangle.width);
      point = new Point(i1, k);
    } else if (bool) {
      k = m - paramRectangle.width;
      point = new Point(k, paramRectangle.y);
    } else {
      k = m + paramRectangle.width;
      point = new Point(k - 1, paramRectangle.y);
    } 
    int i = rowAtPoint(point);
    int j = columnAtPoint(point);
    if (((paramInt == 1) ? 1 : 0) & ((i < 0) ? 1 : 0)) {
      n = 0;
    } else if (((paramInt == 0) ? 1 : 0) & ((j < 0) ? 1 : 0)) {
      if (bool) {
        n = 0;
      } else {
        n = getWidth();
      } 
    } else {
      Rectangle rectangle = getCellRect(i, j, true);
      int i1 = leadingEdge(rectangle, paramInt);
      int i2 = trailingEdge(rectangle, paramInt);
      if ((paramInt == 1 || bool) && i2 >= m) {
        n = i1;
      } else if (paramInt == 0 && !bool && i2 <= m) {
        n = i1;
      } else if (k == i1) {
        n = i1;
      } else {
        n = i2;
      } 
    } 
    return Math.abs(m - n);
  }
  
  private int getNextBlockIncrement(Rectangle paramRectangle, int paramInt) {
    int n;
    boolean bool;
    int i = getTrailingRow(paramRectangle);
    int j = getTrailingCol(paramRectangle);
    int i1 = leadingEdge(paramRectangle, paramInt);
    if (paramInt == 1 && i < 0)
      return paramRectangle.height; 
    if (paramInt == 0 && j < 0)
      return paramRectangle.width; 
    Rectangle rectangle = getCellRect(i, j, true);
    int k = leadingEdge(rectangle, paramInt);
    int m = trailingEdge(rectangle, paramInt);
    if (paramInt == 1 || getComponentOrientation().isLeftToRight()) {
      bool = (k <= i1) ? 1 : 0;
    } else {
      bool = (k >= i1) ? 1 : 0;
    } 
    if (bool) {
      n = m;
    } else if (m == trailingEdge(paramRectangle, paramInt)) {
      n = m;
    } else {
      n = k;
    } 
    return Math.abs(n - i1);
  }
  
  private int getLeadingRow(Rectangle paramRectangle) {
    Point point;
    if (getComponentOrientation().isLeftToRight()) {
      point = new Point(paramRectangle.x, paramRectangle.y);
    } else {
      point = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
    } 
    return rowAtPoint(point);
  }
  
  private int getLeadingCol(Rectangle paramRectangle) {
    Point point;
    if (getComponentOrientation().isLeftToRight()) {
      point = new Point(paramRectangle.x, paramRectangle.y);
    } else {
      point = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
    } 
    return columnAtPoint(point);
  }
  
  private int getTrailingRow(Rectangle paramRectangle) {
    Point point;
    if (getComponentOrientation().isLeftToRight()) {
      point = new Point(paramRectangle.x, paramRectangle.y + paramRectangle.height - 1);
    } else {
      point = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y + paramRectangle.height - 1);
    } 
    return rowAtPoint(point);
  }
  
  private int getTrailingCol(Rectangle paramRectangle) {
    Point point;
    if (getComponentOrientation().isLeftToRight()) {
      point = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
    } else {
      point = new Point(paramRectangle.x, paramRectangle.y);
    } 
    return columnAtPoint(point);
  }
  
  private int leadingEdge(Rectangle paramRectangle, int paramInt) { return (paramInt == 1) ? paramRectangle.y : (getComponentOrientation().isLeftToRight() ? paramRectangle.x : (paramRectangle.x + paramRectangle.width)); }
  
  private int trailingEdge(Rectangle paramRectangle, int paramInt) { return (paramInt == 1) ? (paramRectangle.y + paramRectangle.height) : (getComponentOrientation().isLeftToRight() ? (paramRectangle.x + paramRectangle.width) : paramRectangle.x); }
  
  public boolean getScrollableTracksViewportWidth() { return (this.autoResizeMode != 0); }
  
  public boolean getScrollableTracksViewportHeight() {
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (getFillsViewportHeight() && container instanceof JViewport && container.getHeight() > (getPreferredSize()).height);
  }
  
  public void setFillsViewportHeight(boolean paramBoolean) {
    boolean bool = this.fillsViewportHeight;
    this.fillsViewportHeight = paramBoolean;
    resizeAndRepaint();
    firePropertyChange("fillsViewportHeight", bool, paramBoolean);
  }
  
  public boolean getFillsViewportHeight() { return this.fillsViewportHeight; }
  
  protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean) {
    boolean bool = super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean);
    if (!bool && paramInt == 1 && isFocusOwner() && !Boolean.FALSE.equals(getClientProperty("JTable.autoStartsEdit"))) {
      Component component = getEditorComponent();
      if (component == null) {
        if (paramKeyEvent == null || paramKeyEvent.getID() != 401)
          return false; 
        int i = paramKeyEvent.getKeyCode();
        if (i == 16 || i == 17 || i == 18)
          return false; 
        int j = getSelectionModel().getLeadSelectionIndex();
        int k = getColumnModel().getSelectionModel().getLeadSelectionIndex();
        if (j != -1 && k != -1 && !isEditing() && !editCellAt(j, k, paramKeyEvent))
          return false; 
        component = getEditorComponent();
        if (component == null)
          return false; 
      } 
      if (component instanceof JComponent) {
        bool = ((JComponent)component).processKeyBinding(paramKeyStroke, paramKeyEvent, 0, paramBoolean);
        if (getSurrendersFocusOnKeystroke())
          component.requestFocus(); 
      } 
    } 
    return bool;
  }
  
  protected void createDefaultRenderers() {
    this.defaultRenderersByColumnClass = new UIDefaults(8, 0.75F);
    this.defaultRenderersByColumnClass.put(Object.class, paramUIDefaults -> new DefaultTableCellRenderer.UIResource());
    this.defaultRenderersByColumnClass.put(Number.class, paramUIDefaults -> new NumberRenderer());
    this.defaultRenderersByColumnClass.put(Float.class, paramUIDefaults -> new DoubleRenderer());
    this.defaultRenderersByColumnClass.put(Double.class, paramUIDefaults -> new DoubleRenderer());
    this.defaultRenderersByColumnClass.put(java.util.Date.class, paramUIDefaults -> new DateRenderer());
    this.defaultRenderersByColumnClass.put(Icon.class, paramUIDefaults -> new IconRenderer());
    this.defaultRenderersByColumnClass.put(ImageIcon.class, paramUIDefaults -> new IconRenderer());
    this.defaultRenderersByColumnClass.put(Boolean.class, paramUIDefaults -> new BooleanRenderer());
  }
  
  protected void createDefaultEditors() {
    this.defaultEditorsByColumnClass = new UIDefaults(3, 0.75F);
    this.defaultEditorsByColumnClass.put(Object.class, paramUIDefaults -> new GenericEditor());
    this.defaultEditorsByColumnClass.put(Number.class, paramUIDefaults -> new NumberEditor());
    this.defaultEditorsByColumnClass.put(Boolean.class, paramUIDefaults -> new BooleanEditor());
  }
  
  protected void initializeLocalVars() {
    this.updateSelectionOnSort = true;
    setOpaque(true);
    createDefaultRenderers();
    createDefaultEditors();
    setTableHeader(createDefaultTableHeader());
    setShowGrid(true);
    setAutoResizeMode(2);
    setRowHeight(16);
    this.isRowHeightSet = false;
    setRowMargin(1);
    setRowSelectionAllowed(true);
    setCellEditor(null);
    setEditingColumn(-1);
    setEditingRow(-1);
    setSurrendersFocusOnKeystroke(false);
    setPreferredScrollableViewportSize(new Dimension(450, 400));
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(this);
    setAutoscrolls(true);
  }
  
  protected TableModel createDefaultDataModel() { return new DefaultTableModel(); }
  
  protected TableColumnModel createDefaultColumnModel() { return new DefaultTableColumnModel(); }
  
  protected ListSelectionModel createDefaultSelectionModel() { return new DefaultListSelectionModel(); }
  
  protected JTableHeader createDefaultTableHeader() { return new JTableHeader(this.columnModel); }
  
  protected void resizeAndRepaint() {
    revalidate();
    repaint();
  }
  
  public TableCellEditor getCellEditor() { return this.cellEditor; }
  
  public void setCellEditor(TableCellEditor paramTableCellEditor) {
    TableCellEditor tableCellEditor = this.cellEditor;
    this.cellEditor = paramTableCellEditor;
    firePropertyChange("tableCellEditor", tableCellEditor, paramTableCellEditor);
  }
  
  public void setEditingColumn(int paramInt) { this.editingColumn = paramInt; }
  
  public void setEditingRow(int paramInt) { this.editingRow = paramInt; }
  
  public TableCellRenderer getCellRenderer(int paramInt1, int paramInt2) {
    TableColumn tableColumn = getColumnModel().getColumn(paramInt2);
    TableCellRenderer tableCellRenderer = tableColumn.getCellRenderer();
    if (tableCellRenderer == null)
      tableCellRenderer = getDefaultRenderer(getColumnClass(paramInt2)); 
    return tableCellRenderer;
  }
  
  public Component prepareRenderer(TableCellRenderer paramTableCellRenderer, int paramInt1, int paramInt2) {
    Object object = getValueAt(paramInt1, paramInt2);
    boolean bool1 = false;
    boolean bool2 = false;
    if (!isPaintingForPrint()) {
      bool1 = isCellSelected(paramInt1, paramInt2);
      boolean bool3 = (this.selectionModel.getLeadSelectionIndex() == paramInt1) ? 1 : 0;
      boolean bool4 = (this.columnModel.getSelectionModel().getLeadSelectionIndex() == paramInt2) ? 1 : 0;
      bool2 = (bool3 && bool4 && isFocusOwner());
    } 
    return paramTableCellRenderer.getTableCellRendererComponent(this, object, bool1, bool2, paramInt1, paramInt2);
  }
  
  public TableCellEditor getCellEditor(int paramInt1, int paramInt2) {
    TableColumn tableColumn = getColumnModel().getColumn(paramInt2);
    TableCellEditor tableCellEditor = tableColumn.getCellEditor();
    if (tableCellEditor == null)
      tableCellEditor = getDefaultEditor(getColumnClass(paramInt2)); 
    return tableCellEditor;
  }
  
  public Component prepareEditor(TableCellEditor paramTableCellEditor, int paramInt1, int paramInt2) {
    Object object = getValueAt(paramInt1, paramInt2);
    boolean bool = isCellSelected(paramInt1, paramInt2);
    Component component = paramTableCellEditor.getTableCellEditorComponent(this, object, bool, paramInt1, paramInt2);
    if (component instanceof JComponent) {
      JComponent jComponent = (JComponent)component;
      if (jComponent.getNextFocusableComponent() == null)
        jComponent.setNextFocusableComponent(this); 
    } 
    return component;
  }
  
  public void removeEditor() {
    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("permanentFocusOwner", this.editorRemover);
    this.editorRemover = null;
    TableCellEditor tableCellEditor = getCellEditor();
    if (tableCellEditor != null) {
      tableCellEditor.removeCellEditorListener(this);
      if (this.editorComp != null) {
        Component component = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        boolean bool = (component != null) ? SwingUtilities.isDescendingFrom(component, this) : 0;
        remove(this.editorComp);
        if (bool)
          requestFocusInWindow(); 
      } 
      Rectangle rectangle = getCellRect(this.editingRow, this.editingColumn, false);
      setCellEditor(null);
      setEditingColumn(-1);
      setEditingRow(-1);
      this.editorComp = null;
      repaint(rectangle);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("TableUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.ui != null && getUIClassID().equals("TableUI"))
      this.ui.installUI(this); 
    createDefaultRenderers();
    createDefaultEditors();
    if (getToolTipText() == null)
      ToolTipManager.sharedInstance().registerComponent(this); 
  }
  
  void compWriteObjectNotify() {
    super.compWriteObjectNotify();
    if (getToolTipText() == null)
      ToolTipManager.sharedInstance().unregisterComponent(this); 
  }
  
  protected String paramString() {
    String str4;
    String str1 = (this.gridColor != null) ? this.gridColor.toString() : "";
    String str2 = this.showHorizontalLines ? "true" : "false";
    String str3 = this.showVerticalLines ? "true" : "false";
    if (this.autoResizeMode == 0) {
      str4 = "AUTO_RESIZE_OFF";
    } else if (this.autoResizeMode == 1) {
      str4 = "AUTO_RESIZE_NEXT_COLUMN";
    } else if (this.autoResizeMode == 2) {
      str4 = "AUTO_RESIZE_SUBSEQUENT_COLUMNS";
    } else if (this.autoResizeMode == 3) {
      str4 = "AUTO_RESIZE_LAST_COLUMN";
    } else if (this.autoResizeMode == 4) {
      str4 = "AUTO_RESIZE_ALL_COLUMNS";
    } else {
      str4 = "";
    } 
    String str5 = this.autoCreateColumnsFromModel ? "true" : "false";
    String str6 = (this.preferredViewportSize != null) ? this.preferredViewportSize.toString() : "";
    String str7 = this.rowSelectionAllowed ? "true" : "false";
    String str8 = this.cellSelectionEnabled ? "true" : "false";
    String str9 = (this.selectionForeground != null) ? this.selectionForeground.toString() : "";
    String str10 = (this.selectionBackground != null) ? this.selectionBackground.toString() : "";
    return super.paramString() + ",autoCreateColumnsFromModel=" + str5 + ",autoResizeMode=" + str4 + ",cellSelectionEnabled=" + str8 + ",editingColumn=" + this.editingColumn + ",editingRow=" + this.editingRow + ",gridColor=" + str1 + ",preferredViewportSize=" + str6 + ",rowHeight=" + this.rowHeight + ",rowMargin=" + this.rowMargin + ",rowSelectionAllowed=" + str7 + ",selectionBackground=" + str10 + ",selectionForeground=" + str9 + ",showHorizontalLines=" + str2 + ",showVerticalLines=" + str3;
  }
  
  public boolean print() { return print(PrintMode.FIT_WIDTH); }
  
  public boolean print(PrintMode paramPrintMode) throws PrinterException { return print(paramPrintMode, null, null); }
  
  public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) throws PrinterException {
    boolean bool = !GraphicsEnvironment.isHeadless();
    return print(paramPrintMode, paramMessageFormat1, paramMessageFormat2, bool, null, bool);
  }
  
  public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2) throws PrinterException, HeadlessException { return print(paramPrintMode, paramMessageFormat1, paramMessageFormat2, paramBoolean1, paramPrintRequestAttributeSet, paramBoolean2, null); }
  
  public boolean print(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2, boolean paramBoolean1, PrintRequestAttributeSet paramPrintRequestAttributeSet, boolean paramBoolean2, PrintService paramPrintService) throws PrinterException, HeadlessException {
    Throwable throwable;
    final PrintingStatus printingStatus;
    boolean bool = GraphicsEnvironment.isHeadless();
    if (bool) {
      if (paramBoolean1)
        throw new HeadlessException("Can't show print dialog."); 
      if (paramBoolean2)
        throw new HeadlessException("Can't run interactively."); 
    } 
    final PrinterJob job = PrinterJob.getPrinterJob();
    if (isEditing() && !getCellEditor().stopCellEditing())
      getCellEditor().cancelCellEditing(); 
    if (paramPrintRequestAttributeSet == null)
      paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet(); 
    Printable printable = getPrintable(paramPrintMode, paramMessageFormat1, paramMessageFormat2);
    if (paramBoolean2) {
      printable = new ThreadSafePrintable(printable);
      printingStatus = PrintingStatus.createPrintingStatus(this, printerJob);
      printable = printingStatus.createNotificationPrintable(printable);
    } else {
      printingStatus = null;
    } 
    printerJob.setPrintable(printable);
    if (paramPrintService != null)
      printerJob.setPrintService(paramPrintService); 
    if (paramBoolean1 && !printerJob.printDialog(paramPrintRequestAttributeSet))
      return false; 
    if (!paramBoolean2) {
      printerJob.print(paramPrintRequestAttributeSet);
      return true;
    } 
    this.printError = null;
    final Object lock = new Object();
    final PrintRequestAttributeSet copyAttr = paramPrintRequestAttributeSet;
    Runnable runnable = new Runnable() {
        public void run() {
          try {
            job.print(copyAttr);
          } catch (Throwable throwable) {
            synchronized (lock) {
              JTable.this.printError = throwable;
            } 
          } finally {
            printingStatus.dispose();
          } 
        }
      };
    Thread thread = new Thread(runnable);
    thread.start();
    printingStatus.showModal(true);
    synchronized (object) {
      throwable = this.printError;
      this.printError = null;
    } 
    if (throwable != null) {
      if (throwable instanceof java.awt.print.PrinterAbortException)
        return false; 
      if (throwable instanceof PrinterException)
        throw (PrinterException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new AssertionError(throwable);
    } 
    return true;
  }
  
  public Printable getPrintable(PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) { return new TablePrintable(this, paramPrintMode, paramMessageFormat1, paramMessageFormat2); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJTable(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJTable extends JComponent.AccessibleJComponent implements AccessibleSelection, ListSelectionListener, TableModelListener, TableColumnModelListener, CellEditorListener, PropertyChangeListener, AccessibleExtendedTable {
    int previousFocusedRow;
    
    int previousFocusedCol;
    
    private Accessible caption;
    
    private Accessible summary;
    
    private Accessible[] rowDescription;
    
    private Accessible[] columnDescription;
    
    protected AccessibleJTable() {
      super(JTable.this);
      this$0.addPropertyChangeListener(this);
      this$0.getSelectionModel().addListSelectionListener(this);
      TableColumnModel tableColumnModel = this$0.getColumnModel();
      tableColumnModel.addColumnModelListener(this);
      tableColumnModel.getSelectionModel().addListSelectionListener(this);
      this$0.getModel().addTableModelListener(this);
      this.previousFocusedRow = this$0.getSelectionModel().getLeadSelectionIndex();
      this.previousFocusedCol = this$0.getColumnModel().getSelectionModel().getLeadSelectionIndex();
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Object object1 = param1PropertyChangeEvent.getOldValue();
      Object object2 = param1PropertyChangeEvent.getNewValue();
      if (str.compareTo("model") == 0) {
        if (object1 != null && object1 instanceof TableModel)
          ((TableModel)object1).removeTableModelListener(this); 
        if (object2 != null && object2 instanceof TableModel)
          ((TableModel)object2).addTableModelListener(this); 
      } else if (str.compareTo("selectionModel") == 0) {
        Object object = param1PropertyChangeEvent.getSource();
        if (object == JTable.this) {
          if (object1 != null && object1 instanceof ListSelectionModel)
            ((ListSelectionModel)object1).removeListSelectionListener(this); 
          if (object2 != null && object2 instanceof ListSelectionModel)
            ((ListSelectionModel)object2).addListSelectionListener(this); 
        } else if (object == JTable.this.getColumnModel()) {
          if (object1 != null && object1 instanceof ListSelectionModel)
            ((ListSelectionModel)object1).removeListSelectionListener(this); 
          if (object2 != null && object2 instanceof ListSelectionModel)
            ((ListSelectionModel)object2).addListSelectionListener(this); 
        } 
      } else if (str.compareTo("columnModel") == 0) {
        if (object1 != null && object1 instanceof TableColumnModel) {
          TableColumnModel tableColumnModel = (TableColumnModel)object1;
          tableColumnModel.removeColumnModelListener(this);
          tableColumnModel.getSelectionModel().removeListSelectionListener(this);
        } 
        if (object2 != null && object2 instanceof TableColumnModel) {
          TableColumnModel tableColumnModel = (TableColumnModel)object2;
          tableColumnModel.addColumnModelListener(this);
          tableColumnModel.getSelectionModel().addListSelectionListener(this);
        } 
      } else if (str.compareTo("tableCellEditor") == 0) {
        if (object1 != null && object1 instanceof TableCellEditor)
          ((TableCellEditor)object1).removeCellEditorListener(this); 
        if (object2 != null && object2 instanceof TableCellEditor)
          ((TableCellEditor)object2).addCellEditorListener(this); 
      } 
    }
    
    public void tableChanged(TableModelEvent param1TableModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      if (param1TableModelEvent != null) {
        int i = param1TableModelEvent.getColumn();
        int j = param1TableModelEvent.getColumn();
        if (i == -1) {
          i = 0;
          j = JTable.this.getColumnCount() - 1;
        } 
        AccessibleJTableModelChange accessibleJTableModelChange = new AccessibleJTableModelChange(param1TableModelEvent.getType(), param1TableModelEvent.getFirstRow(), param1TableModelEvent.getLastRow(), i, j);
        firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange);
      } 
    }
    
    public void tableRowsInserted(TableModelEvent param1TableModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      int i = param1TableModelEvent.getColumn();
      int j = param1TableModelEvent.getColumn();
      if (i == -1) {
        i = 0;
        j = JTable.this.getColumnCount() - 1;
      } 
      AccessibleJTableModelChange accessibleJTableModelChange = new AccessibleJTableModelChange(param1TableModelEvent.getType(), param1TableModelEvent.getFirstRow(), param1TableModelEvent.getLastRow(), i, j);
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange);
    }
    
    public void tableRowsDeleted(TableModelEvent param1TableModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      int i = param1TableModelEvent.getColumn();
      int j = param1TableModelEvent.getColumn();
      if (i == -1) {
        i = 0;
        j = JTable.this.getColumnCount() - 1;
      } 
      AccessibleJTableModelChange accessibleJTableModelChange = new AccessibleJTableModelChange(param1TableModelEvent.getType(), param1TableModelEvent.getFirstRow(), param1TableModelEvent.getLastRow(), i, j);
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange);
    }
    
    public void columnAdded(TableColumnModelEvent param1TableColumnModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      byte b = 1;
      AccessibleJTableModelChange accessibleJTableModelChange = new AccessibleJTableModelChange(b, 0, 0, param1TableColumnModelEvent.getFromIndex(), param1TableColumnModelEvent.getToIndex());
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange);
    }
    
    public void columnRemoved(TableColumnModelEvent param1TableColumnModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      byte b = -1;
      AccessibleJTableModelChange accessibleJTableModelChange = new AccessibleJTableModelChange(b, 0, 0, param1TableColumnModelEvent.getFromIndex(), param1TableColumnModelEvent.getToIndex());
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange);
    }
    
    public void columnMoved(TableColumnModelEvent param1TableColumnModelEvent) {
      firePropertyChange("AccessibleVisibleData", null, null);
      byte b = -1;
      AccessibleJTableModelChange accessibleJTableModelChange1 = new AccessibleJTableModelChange(b, 0, 0, param1TableColumnModelEvent.getFromIndex(), param1TableColumnModelEvent.getFromIndex());
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange1);
      byte b1 = 1;
      AccessibleJTableModelChange accessibleJTableModelChange2 = new AccessibleJTableModelChange(b1, 0, 0, param1TableColumnModelEvent.getToIndex(), param1TableColumnModelEvent.getToIndex());
      firePropertyChange("accessibleTableModelChanged", null, accessibleJTableModelChange2);
    }
    
    public void columnMarginChanged(ChangeEvent param1ChangeEvent) { firePropertyChange("AccessibleVisibleData", null, null); }
    
    public void columnSelectionChanged(ListSelectionEvent param1ListSelectionEvent) {}
    
    public void editingStopped(ChangeEvent param1ChangeEvent) { firePropertyChange("AccessibleVisibleData", null, null); }
    
    public void editingCanceled(ChangeEvent param1ChangeEvent) {}
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
      firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      int i = JTable.this.getSelectionModel().getLeadSelectionIndex();
      int j = JTable.this.getColumnModel().getSelectionModel().getLeadSelectionIndex();
      if (i != this.previousFocusedRow || j != this.previousFocusedCol) {
        Accessible accessible1 = getAccessibleAt(this.previousFocusedRow, this.previousFocusedCol);
        Accessible accessible2 = getAccessibleAt(i, j);
        firePropertyChange("AccessibleActiveDescendant", accessible1, accessible2);
        this.previousFocusedRow = i;
        this.previousFocusedCol = j;
      } 
    }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.TABLE; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      int i = JTable.this.columnAtPoint(param1Point);
      int j = JTable.this.rowAtPoint(param1Point);
      if (i != -1 && j != -1) {
        TableColumn tableColumn = JTable.this.getColumnModel().getColumn(i);
        TableCellRenderer tableCellRenderer = tableColumn.getCellRenderer();
        if (tableCellRenderer == null) {
          Class clazz = JTable.this.getColumnClass(i);
          tableCellRenderer = JTable.this.getDefaultRenderer(clazz);
        } 
        Component component = tableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, j, i);
        return new AccessibleJTableCell(JTable.this, j, i, getAccessibleIndexAt(j, i));
      } 
      return null;
    }
    
    public int getAccessibleChildrenCount() { return JTable.this.getColumnCount() * JTable.this.getRowCount(); }
    
    public Accessible getAccessibleChild(int param1Int) {
      if (param1Int < 0 || param1Int >= getAccessibleChildrenCount())
        return null; 
      int i = getAccessibleColumnAtIndex(param1Int);
      int j = getAccessibleRowAtIndex(param1Int);
      TableColumn tableColumn = JTable.this.getColumnModel().getColumn(i);
      TableCellRenderer tableCellRenderer = tableColumn.getCellRenderer();
      if (tableCellRenderer == null) {
        Class clazz = JTable.this.getColumnClass(i);
        tableCellRenderer = JTable.this.getDefaultRenderer(clazz);
      } 
      Component component = tableCellRenderer.getTableCellRendererComponent(JTable.this, null, false, false, j, i);
      return new AccessibleJTableCell(JTable.this, j, i, getAccessibleIndexAt(j, i));
    }
    
    public int getAccessibleSelectionCount() {
      int i = JTable.this.getSelectedRowCount();
      int j = JTable.this.getSelectedColumnCount();
      return JTable.this.cellSelectionEnabled ? (i * j) : ((JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) ? (i * JTable.this.getColumnCount() + j * JTable.this.getRowCount() - i * j) : (JTable.this.getRowSelectionAllowed() ? (i * JTable.this.getColumnCount()) : (JTable.this.getColumnSelectionAllowed() ? (j * JTable.this.getRowCount()) : 0)));
    }
    
    public Accessible getAccessibleSelection(int param1Int) {
      if (param1Int < 0 || param1Int > getAccessibleSelectionCount())
        return null; 
      int i = JTable.this.getSelectedRowCount();
      int j = JTable.this.getSelectedColumnCount();
      int[] arrayOfInt1 = JTable.this.getSelectedRows();
      int[] arrayOfInt2 = JTable.this.getSelectedColumns();
      int k = JTable.this.getColumnCount();
      int m = JTable.this.getRowCount();
      if (JTable.this.cellSelectionEnabled) {
        int n = arrayOfInt1[param1Int / j];
        int i1 = arrayOfInt2[param1Int % j];
        return getAccessibleChild(n * k + i1);
      } 
      if (JTable.this.getRowSelectionAllowed() && JTable.this.getColumnSelectionAllowed()) {
        int n = param1Int;
        boolean bool = (arrayOfInt1[0] == 0) ? 0 : 1;
        byte b = 0;
        int i1 = -1;
        while (b < arrayOfInt1.length) {
          switch (bool) {
            case false:
              if (n < k) {
                int i3 = n % k;
                int i2 = arrayOfInt1[b];
                return getAccessibleChild(i2 * k + i3);
              } 
              n -= k;
              if (b + true == arrayOfInt1.length || arrayOfInt1[b] != arrayOfInt1[b + true] - 1) {
                bool = true;
                i1 = arrayOfInt1[b];
              } 
              b++;
            case true:
              if (n < j * (arrayOfInt1[b] - ((i1 == -1) ? 0 : (i1 + 1)))) {
                int i3 = arrayOfInt2[n % j];
                int i2 = ((b > 0) ? (arrayOfInt1[b - 1] + 1) : 0) + n / j;
                return getAccessibleChild(i2 * k + i3);
              } 
              n -= j * (arrayOfInt1[b] - ((i1 == -1) ? 0 : (i1 + 1)));
              bool = false;
          } 
        } 
        if (n < j * (m - ((i1 == -1) ? 0 : (i1 + 1)))) {
          int i3 = arrayOfInt2[n % j];
          int i2 = arrayOfInt1[b - 1] + n / j + 1;
          return getAccessibleChild(i2 * k + i3);
        } 
      } else {
        if (JTable.this.getRowSelectionAllowed()) {
          int i1 = param1Int % k;
          int n = arrayOfInt1[param1Int / k];
          return getAccessibleChild(n * k + i1);
        } 
        if (JTable.this.getColumnSelectionAllowed()) {
          int i1 = arrayOfInt2[param1Int % j];
          int n = param1Int / j;
          return getAccessibleChild(n * k + i1);
        } 
      } 
      return null;
    }
    
    public boolean isAccessibleChildSelected(int param1Int) {
      int i = getAccessibleColumnAtIndex(param1Int);
      int j = getAccessibleRowAtIndex(param1Int);
      return JTable.this.isCellSelected(j, i);
    }
    
    public void addAccessibleSelection(int param1Int) {
      int i = getAccessibleColumnAtIndex(param1Int);
      int j = getAccessibleRowAtIndex(param1Int);
      JTable.this.changeSelection(j, i, true, false);
    }
    
    public void removeAccessibleSelection(int param1Int) {
      if (JTable.this.cellSelectionEnabled) {
        int i = getAccessibleColumnAtIndex(param1Int);
        int j = getAccessibleRowAtIndex(param1Int);
        JTable.this.removeRowSelectionInterval(j, j);
        JTable.this.removeColumnSelectionInterval(i, i);
      } 
    }
    
    public void clearAccessibleSelection() { JTable.this.clearSelection(); }
    
    public void selectAllAccessibleSelection() {
      if (JTable.this.cellSelectionEnabled)
        JTable.this.selectAll(); 
    }
    
    public int getAccessibleRow(int param1Int) { return getAccessibleRowAtIndex(param1Int); }
    
    public int getAccessibleColumn(int param1Int) { return getAccessibleColumnAtIndex(param1Int); }
    
    public int getAccessibleIndex(int param1Int1, int param1Int2) { return getAccessibleIndexAt(param1Int1, param1Int2); }
    
    public AccessibleTable getAccessibleTable() { return this; }
    
    public Accessible getAccessibleCaption() { return this.caption; }
    
    public void setAccessibleCaption(Accessible param1Accessible) {
      Accessible accessible = this.caption;
      this.caption = param1Accessible;
      firePropertyChange("accessibleTableCaptionChanged", accessible, this.caption);
    }
    
    public Accessible getAccessibleSummary() { return this.summary; }
    
    public void setAccessibleSummary(Accessible param1Accessible) {
      Accessible accessible = this.summary;
      this.summary = param1Accessible;
      firePropertyChange("accessibleTableSummaryChanged", accessible, this.summary);
    }
    
    public int getAccessibleRowCount() { return JTable.this.getRowCount(); }
    
    public int getAccessibleColumnCount() { return JTable.this.getColumnCount(); }
    
    public Accessible getAccessibleAt(int param1Int1, int param1Int2) { return getAccessibleChild(param1Int1 * getAccessibleColumnCount() + param1Int2); }
    
    public int getAccessibleRowExtentAt(int param1Int1, int param1Int2) { return 1; }
    
    public int getAccessibleColumnExtentAt(int param1Int1, int param1Int2) { return 1; }
    
    public AccessibleTable getAccessibleRowHeader() { return null; }
    
    public void setAccessibleRowHeader(AccessibleTable param1AccessibleTable) {}
    
    public AccessibleTable getAccessibleColumnHeader() {
      JTableHeader jTableHeader = JTable.this.getTableHeader();
      return (jTableHeader == null) ? null : new AccessibleTableHeader(jTableHeader);
    }
    
    public void setAccessibleColumnHeader(AccessibleTable param1AccessibleTable) {}
    
    public Accessible getAccessibleRowDescription(int param1Int) {
      if (param1Int < 0 || param1Int >= getAccessibleRowCount())
        throw new IllegalArgumentException(Integer.toString(param1Int)); 
      return (this.rowDescription == null) ? null : this.rowDescription[param1Int];
    }
    
    public void setAccessibleRowDescription(int param1Int, Accessible param1Accessible) {
      if (param1Int < 0 || param1Int >= getAccessibleRowCount())
        throw new IllegalArgumentException(Integer.toString(param1Int)); 
      if (this.rowDescription == null) {
        int i = getAccessibleRowCount();
        this.rowDescription = new Accessible[i];
      } 
      this.rowDescription[param1Int] = param1Accessible;
    }
    
    public Accessible getAccessibleColumnDescription(int param1Int) {
      if (param1Int < 0 || param1Int >= getAccessibleColumnCount())
        throw new IllegalArgumentException(Integer.toString(param1Int)); 
      return (this.columnDescription == null) ? null : this.columnDescription[param1Int];
    }
    
    public void setAccessibleColumnDescription(int param1Int, Accessible param1Accessible) {
      if (param1Int < 0 || param1Int >= getAccessibleColumnCount())
        throw new IllegalArgumentException(Integer.toString(param1Int)); 
      if (this.columnDescription == null) {
        int i = getAccessibleColumnCount();
        this.columnDescription = new Accessible[i];
      } 
      this.columnDescription[param1Int] = param1Accessible;
    }
    
    public boolean isAccessibleSelected(int param1Int1, int param1Int2) { return JTable.this.isCellSelected(param1Int1, param1Int2); }
    
    public boolean isAccessibleRowSelected(int param1Int) { return JTable.this.isRowSelected(param1Int); }
    
    public boolean isAccessibleColumnSelected(int param1Int) { return JTable.this.isColumnSelected(param1Int); }
    
    public int[] getSelectedAccessibleRows() { return JTable.this.getSelectedRows(); }
    
    public int[] getSelectedAccessibleColumns() { return JTable.this.getSelectedColumns(); }
    
    public int getAccessibleRowAtIndex(int param1Int) {
      int i = getAccessibleColumnCount();
      return (i == 0) ? -1 : (param1Int / i);
    }
    
    public int getAccessibleColumnAtIndex(int param1Int) {
      int i = getAccessibleColumnCount();
      return (i == 0) ? -1 : (param1Int % i);
    }
    
    public int getAccessibleIndexAt(int param1Int1, int param1Int2) { return param1Int1 * getAccessibleColumnCount() + param1Int2; }
    
    protected class AccessibleJTableCell extends AccessibleContext implements Accessible, AccessibleComponent {
      private JTable parent;
      
      private int row;
      
      private int column;
      
      private int index;
      
      public AccessibleJTableCell(JTable param2JTable, int param2Int1, int param2Int2, int param2Int3) {
        this.parent = param2JTable;
        this.row = param2Int1;
        this.column = param2Int2;
        this.index = param2Int3;
        setAccessibleParent(this.parent);
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      protected AccessibleContext getCurrentAccessibleContext() {
        TableColumn tableColumn = JTable.AccessibleJTable.this.this$0.getColumnModel().getColumn(this.column);
        TableCellRenderer tableCellRenderer = tableColumn.getCellRenderer();
        if (tableCellRenderer == null) {
          Class clazz = JTable.AccessibleJTable.this.this$0.getColumnClass(this.column);
          tableCellRenderer = JTable.AccessibleJTable.this.this$0.getDefaultRenderer(clazz);
        } 
        Component component = tableCellRenderer.getTableCellRendererComponent(JTable.AccessibleJTable.this.this$0, JTable.AccessibleJTable.this.this$0.getValueAt(this.row, this.column), false, false, this.row, this.column);
        return (component instanceof Accessible) ? component.getAccessibleContext() : null;
      }
      
      protected Component getCurrentComponent() {
        TableColumn tableColumn = JTable.AccessibleJTable.this.this$0.getColumnModel().getColumn(this.column);
        TableCellRenderer tableCellRenderer = tableColumn.getCellRenderer();
        if (tableCellRenderer == null) {
          Class clazz = JTable.AccessibleJTable.this.this$0.getColumnClass(this.column);
          tableCellRenderer = JTable.AccessibleJTable.this.this$0.getDefaultRenderer(clazz);
        } 
        return tableCellRenderer.getTableCellRendererComponent(JTable.AccessibleJTable.this.this$0, null, false, false, this.row, this.column);
      }
      
      public String getAccessibleName() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          String str = accessibleContext.getAccessibleName();
          if (str != null && str != "")
            return str; 
        } 
        return (this.accessibleName != null && this.accessibleName != "") ? this.accessibleName : (String)JTable.AccessibleJTable.this.this$0.getClientProperty("AccessibleName");
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
        return (accessibleContext != null) ? accessibleContext.getAccessibleRole() : AccessibleRole.UNKNOWN;
      }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        AccessibleStateSet accessibleStateSet = null;
        if (accessibleContext != null)
          accessibleStateSet = accessibleContext.getAccessibleStateSet(); 
        if (accessibleStateSet == null)
          accessibleStateSet = new AccessibleStateSet(); 
        Rectangle rectangle1 = JTable.AccessibleJTable.this.this$0.getVisibleRect();
        Rectangle rectangle2 = JTable.AccessibleJTable.this.this$0.getCellRect(this.row, this.column, false);
        if (rectangle1.intersects(rectangle2)) {
          accessibleStateSet.add(AccessibleState.SHOWING);
        } else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
          accessibleStateSet.remove(AccessibleState.SHOWING);
        } 
        if (this.parent.isCellSelected(this.row, this.column)) {
          accessibleStateSet.add(AccessibleState.SELECTED);
        } else if (accessibleStateSet.contains(AccessibleState.SELECTED)) {
          accessibleStateSet.remove(AccessibleState.SELECTED);
        } 
        if (this.row == JTable.AccessibleJTable.this.this$0.getSelectedRow() && this.column == JTable.AccessibleJTable.this.this$0.getSelectedColumn())
          accessibleStateSet.add(AccessibleState.ACTIVE); 
        accessibleStateSet.add(AccessibleState.TRANSIENT);
        return accessibleStateSet;
      }
      
      public Accessible getAccessibleParent() { return this.parent; }
      
      public int getAccessibleIndexInParent() { return this.index; }
      
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
      
      public boolean isShowing() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return (accessibleContext.getAccessibleParent() != null) ? ((AccessibleComponent)accessibleContext).isShowing() : isVisible(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isShowing() : 0;
      }
      
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
        if (this.parent != null && this.parent.isShowing()) {
          Point point1 = this.parent.getLocationOnScreen();
          Point point2 = getLocation();
          point2.translate(point1.x, point1.y);
          return point2;
        } 
        return null;
      }
      
      public Point getLocation() {
        if (this.parent != null) {
          Rectangle rectangle = this.parent.getCellRect(this.row, this.column, false);
          if (rectangle != null)
            return rectangle.getLocation(); 
        } 
        return null;
      }
      
      public void setLocation(Point param2Point) {}
      
      public Rectangle getBounds() { return (this.parent != null) ? this.parent.getCellRect(this.row, this.column, false) : null; }
      
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
      
      public Dimension getSize() {
        if (this.parent != null) {
          Rectangle rectangle = this.parent.getCellRect(this.row, this.column, false);
          if (rectangle != null)
            return rectangle.getSize(); 
        } 
        return null;
      }
      
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
    
    private class AccessibleJTableHeaderCell extends AccessibleContext implements Accessible, AccessibleComponent {
      private int row;
      
      private int column;
      
      private JTableHeader parent;
      
      private Component rendererComponent;
      
      public AccessibleJTableHeaderCell(int param2Int1, int param2Int2, JTableHeader param2JTableHeader, Component param2Component) {
        this.row = param2Int1;
        this.column = param2Int2;
        this.parent = param2JTableHeader;
        this.rendererComponent = param2Component;
        setAccessibleParent(param2JTableHeader);
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      private AccessibleContext getCurrentAccessibleContext() { return this.rendererComponent.getAccessibleContext(); }
      
      private Component getCurrentComponent() { return this.rendererComponent; }
      
      public String getAccessibleName() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext != null) {
          String str = accessibleContext.getAccessibleName();
          if (str != null && str != "")
            return accessibleContext.getAccessibleName(); 
        } 
        return (this.accessibleName != null && this.accessibleName != "") ? this.accessibleName : null;
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
        return (accessibleContext != null) ? accessibleContext.getAccessibleRole() : AccessibleRole.UNKNOWN;
      }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        AccessibleStateSet accessibleStateSet = null;
        if (accessibleContext != null)
          accessibleStateSet = accessibleContext.getAccessibleStateSet(); 
        if (accessibleStateSet == null)
          accessibleStateSet = new AccessibleStateSet(); 
        Rectangle rectangle1 = JTable.AccessibleJTable.this.this$0.getVisibleRect();
        Rectangle rectangle2 = JTable.AccessibleJTable.this.this$0.getCellRect(this.row, this.column, false);
        if (rectangle1.intersects(rectangle2)) {
          accessibleStateSet.add(AccessibleState.SHOWING);
        } else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
          accessibleStateSet.remove(AccessibleState.SHOWING);
        } 
        if (JTable.AccessibleJTable.this.this$0.isCellSelected(this.row, this.column)) {
          accessibleStateSet.add(AccessibleState.SELECTED);
        } else if (accessibleStateSet.contains(AccessibleState.SELECTED)) {
          accessibleStateSet.remove(AccessibleState.SELECTED);
        } 
        if (this.row == JTable.AccessibleJTable.this.this$0.getSelectedRow() && this.column == JTable.AccessibleJTable.this.this$0.getSelectedColumn())
          accessibleStateSet.add(AccessibleState.ACTIVE); 
        accessibleStateSet.add(AccessibleState.TRANSIENT);
        return accessibleStateSet;
      }
      
      public Accessible getAccessibleParent() { return this.parent; }
      
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
      
      public boolean isShowing() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext instanceof AccessibleComponent)
          return (accessibleContext.getAccessibleParent() != null) ? ((AccessibleComponent)accessibleContext).isShowing() : isVisible(); 
        Component component = getCurrentComponent();
        return (component != null) ? component.isShowing() : 0;
      }
      
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
        if (this.parent != null && this.parent.isShowing()) {
          Point point1 = this.parent.getLocationOnScreen();
          Point point2 = getLocation();
          point2.translate(point1.x, point1.y);
          return point2;
        } 
        return null;
      }
      
      public Point getLocation() {
        if (this.parent != null) {
          Rectangle rectangle = this.parent.getHeaderRect(this.column);
          if (rectangle != null)
            return rectangle.getLocation(); 
        } 
        return null;
      }
      
      public void setLocation(Point param2Point) {}
      
      public Rectangle getBounds() { return (this.parent != null) ? this.parent.getHeaderRect(this.column) : null; }
      
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
      
      public Dimension getSize() {
        if (this.parent != null) {
          Rectangle rectangle = this.parent.getHeaderRect(this.column);
          if (rectangle != null)
            return rectangle.getSize(); 
        } 
        return null;
      }
      
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
    
    protected class AccessibleJTableModelChange implements AccessibleTableModelChange {
      protected int type;
      
      protected int firstRow;
      
      protected int lastRow;
      
      protected int firstColumn;
      
      protected int lastColumn;
      
      protected AccessibleJTableModelChange(int param2Int1, int param2Int2, int param2Int3, int param2Int4, int param2Int5) {
        this.type = param2Int1;
        this.firstRow = param2Int2;
        this.lastRow = param2Int3;
        this.firstColumn = param2Int4;
        this.lastColumn = param2Int5;
      }
      
      public int getType() { return this.type; }
      
      public int getFirstRow() { return this.firstRow; }
      
      public int getLastRow() { return this.lastRow; }
      
      public int getFirstColumn() { return this.firstColumn; }
      
      public int getLastColumn() { return this.lastColumn; }
    }
    
    private class AccessibleTableHeader implements AccessibleTable {
      private JTableHeader header;
      
      private TableColumnModel headerModel;
      
      AccessibleTableHeader(JTableHeader param2JTableHeader) {
        this.header = param2JTableHeader;
        this.headerModel = param2JTableHeader.getColumnModel();
      }
      
      public Accessible getAccessibleCaption() { return null; }
      
      public void setAccessibleCaption(Accessible param2Accessible) {}
      
      public Accessible getAccessibleSummary() { return null; }
      
      public void setAccessibleSummary(Accessible param2Accessible) {}
      
      public int getAccessibleRowCount() { return 1; }
      
      public int getAccessibleColumnCount() { return this.headerModel.getColumnCount(); }
      
      public Accessible getAccessibleAt(int param2Int1, int param2Int2) {
        TableColumn tableColumn = this.headerModel.getColumn(param2Int2);
        TableCellRenderer tableCellRenderer = tableColumn.getHeaderRenderer();
        if (tableCellRenderer == null)
          tableCellRenderer = this.header.getDefaultRenderer(); 
        Component component = tableCellRenderer.getTableCellRendererComponent(this.header.getTable(), tableColumn.getHeaderValue(), false, false, -1, param2Int2);
        return new JTable.AccessibleJTable.AccessibleJTableHeaderCell(JTable.AccessibleJTable.this, param2Int1, param2Int2, JTable.AccessibleJTable.this.this$0.getTableHeader(), component);
      }
      
      public int getAccessibleRowExtentAt(int param2Int1, int param2Int2) { return 1; }
      
      public int getAccessibleColumnExtentAt(int param2Int1, int param2Int2) { return 1; }
      
      public AccessibleTable getAccessibleRowHeader() { return null; }
      
      public void setAccessibleRowHeader(AccessibleTable param2AccessibleTable) {}
      
      public AccessibleTable getAccessibleColumnHeader() { return null; }
      
      public void setAccessibleColumnHeader(AccessibleTable param2AccessibleTable) {}
      
      public Accessible getAccessibleRowDescription(int param2Int) { return null; }
      
      public void setAccessibleRowDescription(int param2Int, Accessible param2Accessible) {}
      
      public Accessible getAccessibleColumnDescription(int param2Int) { return null; }
      
      public void setAccessibleColumnDescription(int param2Int, Accessible param2Accessible) {}
      
      public boolean isAccessibleSelected(int param2Int1, int param2Int2) { return false; }
      
      public boolean isAccessibleRowSelected(int param2Int) { return false; }
      
      public boolean isAccessibleColumnSelected(int param2Int) { return false; }
      
      public int[] getSelectedAccessibleRows() { return new int[0]; }
      
      public int[] getSelectedAccessibleColumns() { return new int[0]; }
    }
  }
  
  static class BooleanEditor extends DefaultCellEditor {
    public BooleanEditor() {
      super(new JCheckBox());
      JCheckBox jCheckBox = (JCheckBox)getComponent();
      jCheckBox.setHorizontalAlignment(0);
    }
  }
  
  static class BooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    
    public BooleanRenderer() {
      setHorizontalAlignment(0);
      setBorderPainted(true);
    }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      if (param1Boolean1) {
        setForeground(param1JTable.getSelectionForeground());
        setBackground(param1JTable.getSelectionBackground());
      } else {
        setForeground(param1JTable.getForeground());
        setBackground(param1JTable.getBackground());
      } 
      setSelected((param1Object != null && ((Boolean)param1Object).booleanValue()));
      if (param1Boolean2) {
        setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
      } else {
        setBorder(noFocusBorder);
      } 
      return this;
    }
  }
  
  class CellEditorRemover implements PropertyChangeListener {
    KeyboardFocusManager focusManager;
    
    public CellEditorRemover(KeyboardFocusManager param1KeyboardFocusManager) { this.focusManager = param1KeyboardFocusManager; }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (!JTable.this.isEditing() || JTable.this.getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE)
        return; 
      for (Component component = this.focusManager.getPermanentFocusOwner(); component != null; component = component.getParent()) {
        if (component == JTable.this)
          return; 
        if (component instanceof java.awt.Window || (component instanceof java.applet.Applet && component.getParent() == null)) {
          if (component == SwingUtilities.getRoot(JTable.this) && !JTable.this.getCellEditor().stopCellEditing())
            JTable.this.getCellEditor().cancelCellEditing(); 
          break;
        } 
      } 
    }
  }
  
  static class DateRenderer extends DefaultTableCellRenderer.UIResource {
    DateFormat formatter;
    
    public void setValue(Object param1Object) {
      if (this.formatter == null)
        this.formatter = DateFormat.getDateInstance(); 
      setText((param1Object == null) ? "" : this.formatter.format(param1Object));
    }
  }
  
  static class DoubleRenderer extends NumberRenderer {
    NumberFormat formatter;
    
    public void setValue(Object param1Object) {
      if (this.formatter == null)
        this.formatter = NumberFormat.getInstance(); 
      setText((param1Object == null) ? "" : this.formatter.format(param1Object));
    }
  }
  
  public static final class DropLocation extends TransferHandler.DropLocation {
    private final int row;
    
    private final int col;
    
    private final boolean isInsertRow;
    
    private final boolean isInsertCol;
    
    private DropLocation(Point param1Point, int param1Int1, int param1Int2, boolean param1Boolean1, boolean param1Boolean2) {
      super(param1Point);
      this.row = param1Int1;
      this.col = param1Int2;
      this.isInsertRow = param1Boolean1;
      this.isInsertCol = param1Boolean2;
    }
    
    public int getRow() { return this.row; }
    
    public int getColumn() { return this.col; }
    
    public boolean isInsertRow() { return this.isInsertRow; }
    
    public boolean isInsertColumn() { return this.isInsertCol; }
    
    public String toString() { return getClass().getName() + "[dropPoint=" + getDropPoint() + ",row=" + this.row + ",column=" + this.col + ",insertRow=" + this.isInsertRow + ",insertColumn=" + this.isInsertCol + "]"; }
  }
  
  static class GenericEditor extends DefaultCellEditor {
    Class[] argTypes = { String.class };
    
    Constructor constructor;
    
    Object value;
    
    public GenericEditor() {
      super(new JTextField());
      getComponent().setName("Table.editor");
    }
    
    public boolean stopCellEditing() {
      String str = (String)super.getCellEditorValue();
      try {
        if ("".equals(str)) {
          if (this.constructor.getDeclaringClass() == String.class)
            this.value = str; 
          return super.stopCellEditing();
        } 
        SwingUtilities2.checkAccess(this.constructor.getModifiers());
        this.value = this.constructor.newInstance(new Object[] { str });
      } catch (Exception exception) {
        ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
        return false;
      } 
      return super.stopCellEditing();
    }
    
    public Component getTableCellEditorComponent(JTable param1JTable, Object param1Object, boolean param1Boolean, int param1Int1, int param1Int2) {
      this.value = null;
      ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
      try {
        Class clazz = param1JTable.getColumnClass(param1Int2);
        if (clazz == Object.class)
          clazz = String.class; 
        ReflectUtil.checkPackageAccess(clazz);
        SwingUtilities2.checkAccess(clazz.getModifiers());
        this.constructor = clazz.getConstructor(this.argTypes);
      } catch (Exception exception) {
        return null;
      } 
      return super.getTableCellEditorComponent(param1JTable, param1Object, param1Boolean, param1Int1, param1Int2);
    }
    
    public Object getCellEditorValue() { return this.value; }
  }
  
  static class IconRenderer extends DefaultTableCellRenderer.UIResource {
    public IconRenderer() { setHorizontalAlignment(0); }
    
    public void setValue(Object param1Object) { setIcon((param1Object instanceof Icon) ? (Icon)param1Object : null); }
  }
  
  private final class ModelChange {
    int startModelIndex;
    
    int endModelIndex;
    
    int type;
    
    int modelRowCount;
    
    TableModelEvent event;
    
    int length;
    
    boolean allRowsChanged;
    
    ModelChange(TableModelEvent param1TableModelEvent) {
      this.startModelIndex = Math.max(0, param1TableModelEvent.getFirstRow());
      this.endModelIndex = param1TableModelEvent.getLastRow();
      this.modelRowCount = this$0.getModel().getRowCount();
      if (this.endModelIndex < 0)
        this.endModelIndex = Math.max(0, this.modelRowCount - 1); 
      this.length = this.endModelIndex - this.startModelIndex + 1;
      this.type = param1TableModelEvent.getType();
      this.event = param1TableModelEvent;
      this.allRowsChanged = (param1TableModelEvent.getLastRow() == Integer.MAX_VALUE);
    }
  }
  
  static class NumberEditor extends GenericEditor {
    public NumberEditor() { ((JTextField)getComponent()).setHorizontalAlignment(4); }
  }
  
  static class NumberRenderer extends DefaultTableCellRenderer.UIResource {
    public NumberRenderer() { setHorizontalAlignment(4); }
  }
  
  public enum PrintMode {
    NORMAL, FIT_WIDTH;
  }
  
  private static interface Resizable2 {
    int getElementCount();
    
    int getLowerBoundAt(int param1Int);
    
    int getUpperBoundAt(int param1Int);
    
    void setSizeAt(int param1Int1, int param1Int2);
  }
  
  private static interface Resizable3 extends Resizable2 {
    int getMidPointAt(int param1Int);
  }
  
  private final class SortManager {
    RowSorter<? extends TableModel> sorter;
    
    private ListSelectionModel modelSelection;
    
    private int modelLeadIndex;
    
    private boolean syncingSelection;
    
    private int[] lastModelSelection;
    
    private SizeSequence modelRowSizes;
    
    SortManager(RowSorter<? extends TableModel> param1RowSorter) {
      this.sorter = param1RowSorter;
      param1RowSorter.addRowSorterListener(this$0);
    }
    
    public void dispose() {
      if (this.sorter != null)
        this.sorter.removeRowSorterListener(JTable.this); 
    }
    
    public void setViewRowHeight(int param1Int1, int param1Int2) {
      if (this.modelRowSizes == null)
        this.modelRowSizes = new SizeSequence(JTable.this.getModel().getRowCount(), JTable.this.getRowHeight()); 
      this.modelRowSizes.setSize(JTable.this.convertRowIndexToModel(param1Int1), param1Int2);
    }
    
    public void allChanged() {
      this.modelLeadIndex = -1;
      this.modelSelection = null;
      this.modelRowSizes = null;
    }
    
    public void viewSelectionChanged(ListSelectionEvent param1ListSelectionEvent) {
      if (!this.syncingSelection && this.modelSelection != null)
        this.modelSelection = null; 
    }
    
    public void prepareForChange(RowSorterEvent param1RowSorterEvent, JTable.ModelChange param1ModelChange) {
      if (JTable.this.getUpdateSelectionOnSort())
        cacheSelection(param1RowSorterEvent, param1ModelChange); 
    }
    
    private void cacheSelection(RowSorterEvent param1RowSorterEvent, JTable.ModelChange param1ModelChange) {
      if (param1RowSorterEvent != null) {
        if (this.modelSelection == null && this.sorter.getViewRowCount() != JTable.this.getModel().getRowCount()) {
          this.modelSelection = new DefaultListSelectionModel();
          ListSelectionModel listSelectionModel = JTable.this.getSelectionModel();
          int i = listSelectionModel.getMinSelectionIndex();
          int j = listSelectionModel.getMaxSelectionIndex();
          for (int m = i; m <= j; m++) {
            if (listSelectionModel.isSelectedIndex(m)) {
              int n = JTable.this.convertRowIndexToModel(param1RowSorterEvent, m);
              if (n != -1)
                this.modelSelection.addSelectionInterval(n, n); 
            } 
          } 
          int k = JTable.this.convertRowIndexToModel(param1RowSorterEvent, listSelectionModel.getLeadSelectionIndex());
          SwingUtilities2.setLeadAnchorWithoutSelection(this.modelSelection, k, k);
        } else if (this.modelSelection == null) {
          cacheModelSelection(param1RowSorterEvent);
        } 
      } else if (param1ModelChange.allRowsChanged) {
        this.modelSelection = null;
      } else if (this.modelSelection != null) {
        switch (param1ModelChange.type) {
          case -1:
            this.modelSelection.removeIndexInterval(param1ModelChange.startModelIndex, param1ModelChange.endModelIndex);
            break;
          case 1:
            this.modelSelection.insertIndexInterval(param1ModelChange.startModelIndex, param1ModelChange.length, true);
            break;
        } 
      } else {
        cacheModelSelection(null);
      } 
    }
    
    private void cacheModelSelection(RowSorterEvent param1RowSorterEvent) {
      this.lastModelSelection = JTable.this.convertSelectionToModel(param1RowSorterEvent);
      this.modelLeadIndex = JTable.this.convertRowIndexToModel(param1RowSorterEvent, JTable.this.selectionModel.getLeadSelectionIndex());
    }
    
    public void processChange(RowSorterEvent param1RowSorterEvent, JTable.ModelChange param1ModelChange, boolean param1Boolean) {
      if (param1ModelChange != null)
        if (param1ModelChange.allRowsChanged) {
          this.modelRowSizes = null;
          JTable.this.rowModel = null;
        } else if (this.modelRowSizes != null) {
          if (param1ModelChange.type == 1) {
            this.modelRowSizes.insertEntries(param1ModelChange.startModelIndex, param1ModelChange.endModelIndex - param1ModelChange.startModelIndex + 1, JTable.this.getRowHeight());
          } else if (param1ModelChange.type == -1) {
            this.modelRowSizes.removeEntries(param1ModelChange.startModelIndex, param1ModelChange.endModelIndex - param1ModelChange.startModelIndex + 1);
          } 
        }  
      if (param1Boolean) {
        setViewRowHeightsFromModel();
        restoreSelection(param1ModelChange);
      } 
    }
    
    private void setViewRowHeightsFromModel() {
      if (this.modelRowSizes != null) {
        JTable.this.rowModel.setSizes(JTable.this.getRowCount(), JTable.this.getRowHeight());
        for (int i = JTable.this.getRowCount() - 1; i >= 0; i--) {
          int j = JTable.this.convertRowIndexToModel(i);
          JTable.this.rowModel.setSize(i, this.modelRowSizes.getSize(j));
        } 
      } 
    }
    
    private void restoreSelection(JTable.ModelChange param1ModelChange) {
      this.syncingSelection = true;
      if (this.lastModelSelection != null) {
        JTable.this.restoreSortingSelection(this.lastModelSelection, this.modelLeadIndex, param1ModelChange);
        this.lastModelSelection = null;
      } else if (this.modelSelection != null) {
        ListSelectionModel listSelectionModel = JTable.this.getSelectionModel();
        listSelectionModel.setValueIsAdjusting(true);
        listSelectionModel.clearSelection();
        int i = this.modelSelection.getMinSelectionIndex();
        int j = this.modelSelection.getMaxSelectionIndex();
        int k;
        for (k = i; k <= j; k++) {
          if (this.modelSelection.isSelectedIndex(k)) {
            int m = JTable.this.convertRowIndexToView(k);
            if (m != -1)
              listSelectionModel.addSelectionInterval(m, m); 
          } 
        } 
        k = this.modelSelection.getLeadSelectionIndex();
        if (k != -1 && !this.modelSelection.isSelectionEmpty())
          k = JTable.this.convertRowIndexToView(k); 
        SwingUtilities2.setLeadAnchorWithoutSelection(listSelectionModel, k, k);
        listSelectionModel.setValueIsAdjusting(false);
      } 
      this.syncingSelection = false;
    }
  }
  
  private class ThreadSafePrintable implements Printable {
    private Printable printDelegate;
    
    private int retVal;
    
    private Throwable retThrowable;
    
    public ThreadSafePrintable(Printable param1Printable) { this.printDelegate = param1Printable; }
    
    public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex) throws PrinterException {
      Runnable runnable = new Runnable() {
          public void run() {
            try {
              JTable.ThreadSafePrintable.this.retVal = JTable.ThreadSafePrintable.this.printDelegate.print(graphics, pageFormat, pageIndex);
            } catch (Throwable throwable) {
              JTable.ThreadSafePrintable.this.retThrowable = throwable;
            } finally {
              notifyAll();
            } 
          }
        };
      synchronized (runnable) {
        this.retVal = -1;
        this.retThrowable = null;
        SwingUtilities.invokeLater(runnable);
        while (this.retVal == -1 && this.retThrowable == null) {
          try {
            runnable.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        if (this.retThrowable != null) {
          if (this.retThrowable instanceof PrinterException)
            throw (PrinterException)this.retThrowable; 
          if (this.retThrowable instanceof RuntimeException)
            throw (RuntimeException)this.retThrowable; 
          if (this.retThrowable instanceof Error)
            throw (Error)this.retThrowable; 
          throw new AssertionError(this.retThrowable);
        } 
        return this.retVal;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */