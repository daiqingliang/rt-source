package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.CellRendererPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ListUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicListUI extends ListUI {
  private static final StringBuilder BASELINE_COMPONENT_KEY = new StringBuilder("List.baselineComponent");
  
  protected JList list = null;
  
  protected CellRendererPane rendererPane;
  
  protected FocusListener focusListener;
  
  protected MouseInputListener mouseInputListener;
  
  protected ListSelectionListener listSelectionListener;
  
  protected ListDataListener listDataListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  private Handler handler;
  
  protected int[] cellHeights = null;
  
  protected int cellHeight = -1;
  
  protected int cellWidth = -1;
  
  protected int updateLayoutStateNeeded = 1;
  
  private int listHeight;
  
  private int listWidth;
  
  private int layoutOrientation;
  
  private int columnCount;
  
  private int preferredHeight;
  
  private int rowsPerColumn;
  
  private long timeFactor = 1000L;
  
  private boolean isFileList = false;
  
  private boolean isLeftToRight = true;
  
  protected static final int modelChanged = 1;
  
  protected static final int selectionModelChanged = 2;
  
  protected static final int fontChanged = 4;
  
  protected static final int fixedCellWidthChanged = 8;
  
  protected static final int fixedCellHeightChanged = 16;
  
  protected static final int prototypeCellValueChanged = 32;
  
  protected static final int cellRendererChanged = 64;
  
  private static final int layoutOrientationChanged = 128;
  
  private static final int heightChanged = 256;
  
  private static final int widthChanged = 512;
  
  private static final int componentOrientationChanged = 1024;
  
  private static final int DROP_LINE_THICKNESS = 2;
  
  private static final int CHANGE_LEAD = 0;
  
  private static final int CHANGE_SELECTION = 1;
  
  private static final int EXTEND_SELECTION = 2;
  
  private static final TransferHandler defaultTransferHandler = new ListTransferHandler();
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("selectPreviousColumn"));
    paramLazyActionMap.put(new Actions("selectPreviousColumnExtendSelection"));
    paramLazyActionMap.put(new Actions("selectPreviousColumnChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextColumn"));
    paramLazyActionMap.put(new Actions("selectNextColumnExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNextColumnChangeLead"));
    paramLazyActionMap.put(new Actions("selectPreviousRow"));
    paramLazyActionMap.put(new Actions("selectPreviousRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectPreviousRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectNextRow"));
    paramLazyActionMap.put(new Actions("selectNextRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectNextRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectFirstRow"));
    paramLazyActionMap.put(new Actions("selectFirstRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectFirstRowChangeLead"));
    paramLazyActionMap.put(new Actions("selectLastRow"));
    paramLazyActionMap.put(new Actions("selectLastRowExtendSelection"));
    paramLazyActionMap.put(new Actions("selectLastRowChangeLead"));
    paramLazyActionMap.put(new Actions("scrollUp"));
    paramLazyActionMap.put(new Actions("scrollUpExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollUpChangeLead"));
    paramLazyActionMap.put(new Actions("scrollDown"));
    paramLazyActionMap.put(new Actions("scrollDownExtendSelection"));
    paramLazyActionMap.put(new Actions("scrollDownChangeLead"));
    paramLazyActionMap.put(new Actions("selectAll"));
    paramLazyActionMap.put(new Actions("clearSelection"));
    paramLazyActionMap.put(new Actions("addToSelection"));
    paramLazyActionMap.put(new Actions("toggleAndAnchor"));
    paramLazyActionMap.put(new Actions("extendTo"));
    paramLazyActionMap.put(new Actions("moveSelectionTo"));
    paramLazyActionMap.put(TransferHandler.getCutAction().getValue("Name"), TransferHandler.getCutAction());
    paramLazyActionMap.put(TransferHandler.getCopyAction().getValue("Name"), TransferHandler.getCopyAction());
    paramLazyActionMap.put(TransferHandler.getPasteAction().getValue("Name"), TransferHandler.getPasteAction());
  }
  
  protected void paintCell(Graphics paramGraphics, int paramInt1, Rectangle paramRectangle, ListCellRenderer paramListCellRenderer, ListModel paramListModel, ListSelectionModel paramListSelectionModel, int paramInt2) {
    Object object = paramListModel.getElementAt(paramInt1);
    boolean bool1 = (this.list.hasFocus() && paramInt1 == paramInt2);
    boolean bool2 = paramListSelectionModel.isSelectedIndex(paramInt1);
    Component component = paramListCellRenderer.getListCellRendererComponent(this.list, object, paramInt1, bool2, bool1);
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    if (this.isFileList) {
      int n = Math.min(k, (component.getPreferredSize()).width + 4);
      if (!this.isLeftToRight)
        i += k - n; 
      k = n;
    } 
    this.rendererPane.paintComponent(paramGraphics, component, this.list, i, j, k, m, true);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    Shape shape = paramGraphics.getClip();
    paintImpl(paramGraphics, paramJComponent);
    paramGraphics.setClip(shape);
    paintDropLine(paramGraphics);
  }
  
  private void paintImpl(Graphics paramGraphics, JComponent paramJComponent) {
    int k;
    int j;
    switch (this.layoutOrientation) {
      case 1:
        if (this.list.getHeight() != this.listHeight) {
          this.updateLayoutStateNeeded |= 0x100;
          redrawList();
        } 
        break;
      case 2:
        if (this.list.getWidth() != this.listWidth) {
          this.updateLayoutStateNeeded |= 0x200;
          redrawList();
        } 
        break;
    } 
    maybeUpdateLayoutState();
    ListCellRenderer listCellRenderer = this.list.getCellRenderer();
    ListModel listModel = this.list.getModel();
    ListSelectionModel listSelectionModel = this.list.getSelectionModel();
    int i;
    if (listCellRenderer == null || (i = listModel.getSize()) == 0)
      return; 
    Rectangle rectangle = paramGraphics.getClipBounds();
    if (paramJComponent.getComponentOrientation().isLeftToRight()) {
      j = convertLocationToColumn(rectangle.x, rectangle.y);
      k = convertLocationToColumn(rectangle.x + rectangle.width, rectangle.y);
    } else {
      j = convertLocationToColumn(rectangle.x + rectangle.width, rectangle.y);
      k = convertLocationToColumn(rectangle.x, rectangle.y);
    } 
    int m = rectangle.y + rectangle.height;
    int n = adjustIndex(this.list.getLeadSelectionIndex(), this.list);
    int i1 = (this.layoutOrientation == 2) ? this.columnCount : 1;
    for (int i2 = j; i2 <= k; i2++) {
      int i3 = convertLocationToRowInColumn(rectangle.y, i2);
      int i4 = getRowCount(i2);
      int i5 = getModelIndex(i2, i3);
      Rectangle rectangle1 = getCellBounds(this.list, i5, i5);
      if (rectangle1 == null)
        return; 
      while (i3 < i4 && rectangle1.y < m && i5 < i) {
        rectangle1.height = getHeight(i2, i3);
        paramGraphics.setClip(rectangle1.x, rectangle1.y, rectangle1.width, rectangle1.height);
        paramGraphics.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        paintCell(paramGraphics, i5, rectangle1, listCellRenderer, listModel, listSelectionModel, n);
        rectangle1.y += rectangle1.height;
        i5 += i1;
        i3++;
      } 
    } 
    this.rendererPane.removeAll();
  }
  
  private void paintDropLine(Graphics paramGraphics) {
    JList.DropLocation dropLocation = this.list.getDropLocation();
    if (dropLocation == null || !dropLocation.isInsert())
      return; 
    Color color = DefaultLookup.getColor(this.list, this, "List.dropLineColor", null);
    if (color != null) {
      paramGraphics.setColor(color);
      Rectangle rectangle = getDropLineRect(dropLocation);
      paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  private Rectangle getDropLineRect(JList.DropLocation paramDropLocation) {
    int i = this.list.getModel().getSize();
    if (i == 0) {
      Insets insets = this.list.getInsets();
      return (this.layoutOrientation == 2) ? (this.isLeftToRight ? new Rectangle(insets.left, insets.top, 2, 20) : new Rectangle(this.list.getWidth() - 2 - insets.right, insets.top, 2, 20)) : new Rectangle(insets.left, insets.top, this.list.getWidth() - insets.left - insets.right, 2);
    } 
    Rectangle rectangle = null;
    int j = paramDropLocation.getIndex();
    boolean bool = false;
    if (this.layoutOrientation == 2) {
      if (j == i) {
        bool = true;
      } else if (j != 0 && convertModelToRow(j) != convertModelToRow(j - 1)) {
        Rectangle rectangle1 = getCellBounds(this.list, j - 1);
        Rectangle rectangle2 = getCellBounds(this.list, j);
        Point point = paramDropLocation.getDropPoint();
        if (this.isLeftToRight) {
          bool = (Point2D.distance((rectangle1.x + rectangle1.width), (rectangle1.y + (int)(rectangle1.height / 2.0D)), point.x, point.y) < Point2D.distance(rectangle2.x, (rectangle2.y + (int)(rectangle2.height / 2.0D)), point.x, point.y)) ? 1 : 0;
        } else {
          bool = (Point2D.distance(rectangle1.x, (rectangle1.y + (int)(rectangle1.height / 2.0D)), point.x, point.y) < Point2D.distance((rectangle2.x + rectangle2.width), (rectangle2.y + (int)(rectangle1.height / 2.0D)), point.x, point.y)) ? 1 : 0;
        } 
      } 
      if (bool) {
        rectangle = getCellBounds(this.list, --j);
        if (this.isLeftToRight) {
          rectangle.x += rectangle.width;
        } else {
          rectangle.x -= 2;
        } 
      } else {
        rectangle = getCellBounds(this.list, j);
        if (!this.isLeftToRight)
          rectangle.x += rectangle.width - 2; 
      } 
      if (rectangle.x >= this.list.getWidth()) {
        rectangle.x = this.list.getWidth() - 2;
      } else if (rectangle.x < 0) {
        rectangle.x = 0;
      } 
      rectangle.width = 2;
    } else if (this.layoutOrientation == 1) {
      if (j == i) {
        rectangle = getCellBounds(this.list, --j);
        rectangle.y += rectangle.height;
      } else if (j != 0 && convertModelToColumn(j) != convertModelToColumn(j - 1)) {
        Rectangle rectangle1 = getCellBounds(this.list, j - 1);
        Rectangle rectangle2 = getCellBounds(this.list, j);
        Point point = paramDropLocation.getDropPoint();
        if (Point2D.distance((rectangle1.x + (int)(rectangle1.width / 2.0D)), (rectangle1.y + rectangle1.height), point.x, point.y) < Point2D.distance((rectangle2.x + (int)(rectangle2.width / 2.0D)), rectangle2.y, point.x, point.y)) {
          rectangle = getCellBounds(this.list, --j);
          rectangle.y += rectangle.height;
        } else {
          rectangle = getCellBounds(this.list, j);
        } 
      } else {
        rectangle = getCellBounds(this.list, j);
      } 
      if (rectangle.y >= this.list.getHeight())
        rectangle.y = this.list.getHeight() - 2; 
      rectangle.height = 2;
    } else {
      if (j == i) {
        rectangle = getCellBounds(this.list, --j);
        rectangle.y += rectangle.height;
      } else {
        rectangle = getCellBounds(this.list, j);
      } 
      if (rectangle.y >= this.list.getHeight())
        rectangle.y = this.list.getHeight() - 2; 
      rectangle.height = 2;
    } 
    return rectangle;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = this.list.getFixedCellHeight();
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    Component component = (Component)uIDefaults.get(BASELINE_COMPONENT_KEY);
    if (component == null) {
      ListCellRenderer listCellRenderer = (ListCellRenderer)UIManager.get("List.cellRenderer");
      if (listCellRenderer == null)
        listCellRenderer = new DefaultListCellRenderer(); 
      component = listCellRenderer.getListCellRendererComponent(this.list, "a", -1, false, false);
      uIDefaults.put(BASELINE_COMPONENT_KEY, component);
    } 
    component.setFont(this.list.getFont());
    if (i == -1)
      i = (component.getPreferredSize()).height; 
    return component.getBaseline(2147483647, i) + (this.list.getInsets()).top;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    byte b;
    maybeUpdateLayoutState();
    int i = this.list.getModel().getSize() - 1;
    if (i < 0)
      return new Dimension(0, 0); 
    Insets insets = this.list.getInsets();
    int j = this.cellWidth * this.columnCount + insets.left + insets.right;
    if (this.layoutOrientation != 0) {
      b = this.preferredHeight;
    } else {
      Rectangle rectangle = getCellBounds(this.list, i);
      if (rectangle != null) {
        b = rectangle.y + rectangle.height + insets.bottom;
      } else {
        b = 0;
      } 
    } 
    return new Dimension(j, b);
  }
  
  protected void selectPreviousIndex() {
    int i = this.list.getSelectedIndex();
    if (i > 0) {
      this.list.setSelectedIndex(--i);
      this.list.ensureIndexIsVisible(i);
    } 
  }
  
  protected void selectNextIndex() {
    int i = this.list.getSelectedIndex();
    if (i + 1 < this.list.getModel().getSize()) {
      this.list.setSelectedIndex(++i);
      this.list.ensureIndexIsVisible(i);
    } 
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.list, 0, inputMap);
    LazyActionMap.installLazyActionMap(this.list, BasicListUI.class, "List.actionMap");
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 0) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap");
      InputMap inputMap2;
      if (this.isLeftToRight || (inputMap2 = (InputMap)DefaultLookup.get(this.list, this, "List.focusInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.list, null);
    SwingUtilities.replaceUIInputMap(this.list, 0, null);
  }
  
  protected void installListeners() {
    TransferHandler transferHandler = this.list.getTransferHandler();
    if (transferHandler == null || transferHandler instanceof UIResource) {
      this.list.setTransferHandler(defaultTransferHandler);
      if (this.list.getDropTarget() instanceof UIResource)
        this.list.setDropTarget(null); 
    } 
    this.focusListener = createFocusListener();
    this.mouseInputListener = createMouseInputListener();
    this.propertyChangeListener = createPropertyChangeListener();
    this.listSelectionListener = createListSelectionListener();
    this.listDataListener = createListDataListener();
    this.list.addFocusListener(this.focusListener);
    this.list.addMouseListener(this.mouseInputListener);
    this.list.addMouseMotionListener(this.mouseInputListener);
    this.list.addPropertyChangeListener(this.propertyChangeListener);
    this.list.addKeyListener(getHandler());
    ListModel listModel = this.list.getModel();
    if (listModel != null)
      listModel.addListDataListener(this.listDataListener); 
    ListSelectionModel listSelectionModel = this.list.getSelectionModel();
    if (listSelectionModel != null)
      listSelectionModel.addListSelectionListener(this.listSelectionListener); 
  }
  
  protected void uninstallListeners() {
    this.list.removeFocusListener(this.focusListener);
    this.list.removeMouseListener(this.mouseInputListener);
    this.list.removeMouseMotionListener(this.mouseInputListener);
    this.list.removePropertyChangeListener(this.propertyChangeListener);
    this.list.removeKeyListener(getHandler());
    ListModel listModel = this.list.getModel();
    if (listModel != null)
      listModel.removeListDataListener(this.listDataListener); 
    ListSelectionModel listSelectionModel = this.list.getSelectionModel();
    if (listSelectionModel != null)
      listSelectionModel.removeListSelectionListener(this.listSelectionListener); 
    this.focusListener = null;
    this.mouseInputListener = null;
    this.listSelectionListener = null;
    this.listDataListener = null;
    this.propertyChangeListener = null;
    this.handler = null;
  }
  
  protected void installDefaults() {
    this.list.setLayout(null);
    LookAndFeel.installBorder(this.list, "List.border");
    LookAndFeel.installColorsAndFont(this.list, "List.background", "List.foreground", "List.font");
    LookAndFeel.installProperty(this.list, "opaque", Boolean.TRUE);
    if (this.list.getCellRenderer() == null)
      this.list.setCellRenderer((ListCellRenderer)UIManager.get("List.cellRenderer")); 
    Color color1 = this.list.getSelectionBackground();
    if (color1 == null || color1 instanceof UIResource)
      this.list.setSelectionBackground(UIManager.getColor("List.selectionBackground")); 
    Color color2 = this.list.getSelectionForeground();
    if (color2 == null || color2 instanceof UIResource)
      this.list.setSelectionForeground(UIManager.getColor("List.selectionForeground")); 
    Long long = (Long)UIManager.get("List.timeFactor");
    this.timeFactor = (long != null) ? long.longValue() : 1000L;
    updateIsFileList();
  }
  
  private void updateIsFileList() {
    boolean bool = Boolean.TRUE.equals(this.list.getClientProperty("List.isFileList"));
    if (bool != this.isFileList) {
      this.isFileList = bool;
      Font font = this.list.getFont();
      if (font == null || font instanceof UIResource) {
        Font font1 = UIManager.getFont(bool ? "FileChooser.listFont" : "List.font");
        if (font1 != null && font1 != font)
          this.list.setFont(font1); 
      } 
    } 
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.uninstallBorder(this.list);
    if (this.list.getFont() instanceof UIResource)
      this.list.setFont(null); 
    if (this.list.getForeground() instanceof UIResource)
      this.list.setForeground(null); 
    if (this.list.getBackground() instanceof UIResource)
      this.list.setBackground(null); 
    if (this.list.getSelectionBackground() instanceof UIResource)
      this.list.setSelectionBackground(null); 
    if (this.list.getSelectionForeground() instanceof UIResource)
      this.list.setSelectionForeground(null); 
    if (this.list.getCellRenderer() instanceof UIResource)
      this.list.setCellRenderer(null); 
    if (this.list.getTransferHandler() instanceof UIResource)
      this.list.setTransferHandler(null); 
  }
  
  public void installUI(JComponent paramJComponent) {
    this.list = (JList)paramJComponent;
    this.layoutOrientation = this.list.getLayoutOrientation();
    this.rendererPane = new CellRendererPane();
    this.list.add(this.rendererPane);
    this.columnCount = 1;
    this.updateLayoutStateNeeded = 1;
    this.isLeftToRight = this.list.getComponentOrientation().isLeftToRight();
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallListeners();
    uninstallDefaults();
    uninstallKeyboardActions();
    this.cellWidth = this.cellHeight = -1;
    this.cellHeights = null;
    this.listWidth = this.listHeight = -1;
    this.list.remove(this.rendererPane);
    this.rendererPane = null;
    this.list = null;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicListUI(); }
  
  public int locationToIndex(JList paramJList, Point paramPoint) {
    maybeUpdateLayoutState();
    return convertLocationToModel(paramPoint.x, paramPoint.y);
  }
  
  public Point indexToLocation(JList paramJList, int paramInt) {
    maybeUpdateLayoutState();
    Rectangle rectangle = getCellBounds(paramJList, paramInt, paramInt);
    return (rectangle != null) ? new Point(rectangle.x, rectangle.y) : null;
  }
  
  public Rectangle getCellBounds(JList paramJList, int paramInt1, int paramInt2) {
    maybeUpdateLayoutState();
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    if (i >= paramJList.getModel().getSize())
      return null; 
    Rectangle rectangle1 = getCellBounds(paramJList, i);
    if (rectangle1 == null)
      return null; 
    if (i == j)
      return rectangle1; 
    Rectangle rectangle2 = getCellBounds(paramJList, j);
    if (rectangle2 != null) {
      if (this.layoutOrientation == 2) {
        int k = convertModelToRow(i);
        int m = convertModelToRow(j);
        if (k != m) {
          rectangle1.x = 0;
          rectangle1.width = paramJList.getWidth();
        } 
      } else if (rectangle1.x != rectangle2.x) {
        rectangle1.y = 0;
        rectangle1.height = paramJList.getHeight();
      } 
      rectangle1.add(rectangle2);
    } 
    return rectangle1;
  }
  
  private Rectangle getCellBounds(JList paramJList, int paramInt) {
    maybeUpdateLayoutState();
    int i = convertModelToRow(paramInt);
    int j = convertModelToColumn(paramInt);
    if (i == -1 || j == -1)
      return null; 
    Insets insets = paramJList.getInsets();
    int m = this.cellWidth;
    int n = insets.top;
    switch (this.layoutOrientation) {
      case 1:
      case 2:
        if (this.isLeftToRight) {
          k = insets.left + j * this.cellWidth;
        } else {
          k = paramJList.getWidth() - insets.right - (j + 1) * this.cellWidth;
        } 
        n += this.cellHeight * i;
        i1 = this.cellHeight;
        return new Rectangle(k, n, m, i1);
    } 
    int k = insets.left;
    if (this.cellHeights == null) {
      n += this.cellHeight * i;
    } else if (i >= this.cellHeights.length) {
      n = 0;
    } else {
      for (byte b = 0; b < i; b++)
        n += this.cellHeights[b]; 
    } 
    m = paramJList.getWidth() - insets.left + insets.right;
    int i1 = getRowHeight(paramInt);
    return new Rectangle(k, n, m, i1);
  }
  
  protected int getRowHeight(int paramInt) { return getHeight(0, paramInt); }
  
  protected int convertYToRow(int paramInt) { return convertLocationToRow(0, paramInt, false); }
  
  protected int convertRowToY(int paramInt) {
    if (paramInt >= getRowCount(0) || paramInt < 0)
      return -1; 
    Rectangle rectangle = getCellBounds(this.list, paramInt, paramInt);
    return rectangle.y;
  }
  
  private int getHeight(int paramInt1, int paramInt2) { return (paramInt1 < 0 || paramInt1 > this.columnCount || paramInt2 < 0) ? -1 : ((this.layoutOrientation != 0) ? this.cellHeight : ((paramInt2 >= this.list.getModel().getSize()) ? -1 : ((this.cellHeights == null) ? this.cellHeight : ((paramInt2 < this.cellHeights.length) ? this.cellHeights[paramInt2] : -1)))); }
  
  private int convertLocationToRow(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = this.list.getModel().getSize();
    if (i <= 0)
      return -1; 
    Insets insets = this.list.getInsets();
    if (this.cellHeights == null) {
      int k = (this.cellHeight == 0) ? 0 : ((paramInt2 - insets.top) / this.cellHeight);
      if (paramBoolean)
        if (k) {
          k = 0;
        } else if (k >= i) {
          k = i - 1;
        }  
      return k;
    } 
    if (i > this.cellHeights.length)
      return -1; 
    int j = insets.top;
    byte b1 = 0;
    if (paramBoolean && paramInt2 < j)
      return 0; 
    byte b2;
    for (b2 = 0; b2 < i; b2++) {
      if (paramInt2 >= j && paramInt2 < j + this.cellHeights[b2])
        return b1; 
      j += this.cellHeights[b2];
      b1++;
    } 
    return b2 - 1;
  }
  
  private int convertLocationToRowInColumn(int paramInt1, int paramInt2) {
    int i = 0;
    if (this.layoutOrientation != 0)
      if (this.isLeftToRight) {
        i = paramInt2 * this.cellWidth;
      } else {
        i = this.list.getWidth() - (paramInt2 + 1) * this.cellWidth - (this.list.getInsets()).right;
      }  
    return convertLocationToRow(i, paramInt1, true);
  }
  
  private int convertLocationToModel(int paramInt1, int paramInt2) {
    int i = convertLocationToRow(paramInt1, paramInt2, true);
    int j = convertLocationToColumn(paramInt1, paramInt2);
    return (i >= 0 && j >= 0) ? getModelIndex(j, i) : -1;
  }
  
  private int getRowCount(int paramInt) {
    if (paramInt < 0 || paramInt >= this.columnCount)
      return -1; 
    if (this.layoutOrientation == 0 || (paramInt == 0 && this.columnCount == 1))
      return this.list.getModel().getSize(); 
    if (paramInt >= this.columnCount)
      return -1; 
    if (this.layoutOrientation == 1)
      return (paramInt < this.columnCount - 1) ? this.rowsPerColumn : (this.list.getModel().getSize() - (this.columnCount - 1) * this.rowsPerColumn); 
    int i = this.columnCount - this.columnCount * this.rowsPerColumn - this.list.getModel().getSize();
    return (paramInt >= i) ? Math.max(0, this.rowsPerColumn - 1) : this.rowsPerColumn;
  }
  
  private int getModelIndex(int paramInt1, int paramInt2) {
    switch (this.layoutOrientation) {
      case 1:
        return Math.min(this.list.getModel().getSize() - 1, this.rowsPerColumn * paramInt1 + Math.min(paramInt2, this.rowsPerColumn - 1));
      case 2:
        return Math.min(this.list.getModel().getSize() - 1, paramInt2 * this.columnCount + paramInt1);
    } 
    return paramInt2;
  }
  
  private int convertLocationToColumn(int paramInt1, int paramInt2) {
    if (this.cellWidth > 0) {
      int i;
      if (this.layoutOrientation == 0)
        return 0; 
      Insets insets = this.list.getInsets();
      if (this.isLeftToRight) {
        i = (paramInt1 - insets.left) / this.cellWidth;
      } else {
        i = (this.list.getWidth() - paramInt1 - insets.right - 1) / this.cellWidth;
      } 
      return (i < 0) ? 0 : ((i >= this.columnCount) ? (this.columnCount - 1) : i);
    } 
    return 0;
  }
  
  private int convertModelToRow(int paramInt) {
    int i = this.list.getModel().getSize();
    return (paramInt < 0 || paramInt >= i) ? -1 : ((this.layoutOrientation != 0 && this.columnCount > 1 && this.rowsPerColumn > 0) ? ((this.layoutOrientation == 1) ? (paramInt % this.rowsPerColumn) : (paramInt / this.columnCount)) : paramInt);
  }
  
  private int convertModelToColumn(int paramInt) {
    int i = this.list.getModel().getSize();
    return (paramInt < 0 || paramInt >= i) ? -1 : ((this.layoutOrientation != 0 && this.rowsPerColumn > 0 && this.columnCount > 1) ? ((this.layoutOrientation == 1) ? (paramInt / this.rowsPerColumn) : (paramInt % this.columnCount)) : 0);
  }
  
  protected void maybeUpdateLayoutState() {
    if (this.updateLayoutStateNeeded != 0) {
      updateLayoutState();
      this.updateLayoutStateNeeded = 0;
    } 
  }
  
  protected void updateLayoutState() {
    int i = this.list.getFixedCellHeight();
    int j = this.list.getFixedCellWidth();
    this.cellWidth = (j != -1) ? j : -1;
    if (i != -1) {
      this.cellHeight = i;
      this.cellHeights = null;
    } else {
      this.cellHeight = -1;
      this.cellHeights = new int[this.list.getModel().getSize()];
    } 
    if (j == -1 || i == -1) {
      ListModel listModel = this.list.getModel();
      int k = listModel.getSize();
      ListCellRenderer listCellRenderer = this.list.getCellRenderer();
      if (listCellRenderer != null) {
        for (byte b = 0; b < k; b++) {
          Object object = listModel.getElementAt(b);
          Component component = listCellRenderer.getListCellRendererComponent(this.list, object, b, false, false);
          this.rendererPane.add(component);
          Dimension dimension = component.getPreferredSize();
          if (j == -1)
            this.cellWidth = Math.max(dimension.width, this.cellWidth); 
          if (i == -1)
            this.cellHeights[b] = dimension.height; 
        } 
      } else {
        if (this.cellWidth == -1)
          this.cellWidth = 0; 
        if (this.cellHeights == null)
          this.cellHeights = new int[k]; 
        for (byte b = 0; b < k; b++)
          this.cellHeights[b] = 0; 
      } 
    } 
    this.columnCount = 1;
    if (this.layoutOrientation != 0)
      updateHorizontalLayoutState(j, i); 
  }
  
  private void updateHorizontalLayoutState(int paramInt1, int paramInt2) {
    int k;
    int i = this.list.getVisibleRowCount();
    int j = this.list.getModel().getSize();
    Insets insets = this.list.getInsets();
    this.listHeight = this.list.getHeight();
    this.listWidth = this.list.getWidth();
    if (j == 0) {
      this.rowsPerColumn = this.columnCount = 0;
      this.preferredHeight = insets.top + insets.bottom;
      return;
    } 
    if (paramInt2 != -1) {
      k = paramInt2;
    } else {
      int m = 0;
      if (this.cellHeights.length > 0) {
        m = this.cellHeights[this.cellHeights.length - 1];
        for (int n = this.cellHeights.length - 2; n >= 0; n--)
          m = Math.max(m, this.cellHeights[n]); 
      } 
      k = this.cellHeight = m;
      this.cellHeights = null;
    } 
    this.rowsPerColumn = j;
    if (i > 0) {
      this.rowsPerColumn = i;
      this.columnCount = Math.max(1, j / this.rowsPerColumn);
      if (j > 0 && j > this.rowsPerColumn && j % this.rowsPerColumn != 0)
        this.columnCount++; 
      if (this.layoutOrientation == 2) {
        this.rowsPerColumn = j / this.columnCount;
        if (j % this.columnCount > 0)
          this.rowsPerColumn++; 
      } 
    } else if (this.layoutOrientation == 1 && k != 0) {
      this.rowsPerColumn = Math.max(1, (this.listHeight - insets.top - insets.bottom) / k);
      this.columnCount = Math.max(1, j / this.rowsPerColumn);
      if (j > 0 && j > this.rowsPerColumn && j % this.rowsPerColumn != 0)
        this.columnCount++; 
    } else if (this.layoutOrientation == 2 && this.cellWidth > 0 && this.listWidth > 0) {
      this.columnCount = Math.max(1, (this.listWidth - insets.left - insets.right) / this.cellWidth);
      this.rowsPerColumn = j / this.columnCount;
      if (j % this.columnCount > 0)
        this.rowsPerColumn++; 
    } 
    this.preferredHeight = this.rowsPerColumn * this.cellHeight + insets.top + insets.bottom;
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected MouseInputListener createMouseInputListener() { return getHandler(); }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  protected ListSelectionListener createListSelectionListener() { return getHandler(); }
  
  private void redrawList() {
    this.list.revalidate();
    this.list.repaint();
  }
  
  protected ListDataListener createListDataListener() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private static int adjustIndex(int paramInt, JList paramJList) { return (paramInt < paramJList.getModel().getSize()) ? paramInt : -1; }
  
  private static class Actions extends UIAction {
    private static final String SELECT_PREVIOUS_COLUMN = "selectPreviousColumn";
    
    private static final String SELECT_PREVIOUS_COLUMN_EXTEND = "selectPreviousColumnExtendSelection";
    
    private static final String SELECT_PREVIOUS_COLUMN_CHANGE_LEAD = "selectPreviousColumnChangeLead";
    
    private static final String SELECT_NEXT_COLUMN = "selectNextColumn";
    
    private static final String SELECT_NEXT_COLUMN_EXTEND = "selectNextColumnExtendSelection";
    
    private static final String SELECT_NEXT_COLUMN_CHANGE_LEAD = "selectNextColumnChangeLead";
    
    private static final String SELECT_PREVIOUS_ROW = "selectPreviousRow";
    
    private static final String SELECT_PREVIOUS_ROW_EXTEND = "selectPreviousRowExtendSelection";
    
    private static final String SELECT_PREVIOUS_ROW_CHANGE_LEAD = "selectPreviousRowChangeLead";
    
    private static final String SELECT_NEXT_ROW = "selectNextRow";
    
    private static final String SELECT_NEXT_ROW_EXTEND = "selectNextRowExtendSelection";
    
    private static final String SELECT_NEXT_ROW_CHANGE_LEAD = "selectNextRowChangeLead";
    
    private static final String SELECT_FIRST_ROW = "selectFirstRow";
    
    private static final String SELECT_FIRST_ROW_EXTEND = "selectFirstRowExtendSelection";
    
    private static final String SELECT_FIRST_ROW_CHANGE_LEAD = "selectFirstRowChangeLead";
    
    private static final String SELECT_LAST_ROW = "selectLastRow";
    
    private static final String SELECT_LAST_ROW_EXTEND = "selectLastRowExtendSelection";
    
    private static final String SELECT_LAST_ROW_CHANGE_LEAD = "selectLastRowChangeLead";
    
    private static final String SCROLL_UP = "scrollUp";
    
    private static final String SCROLL_UP_EXTEND = "scrollUpExtendSelection";
    
    private static final String SCROLL_UP_CHANGE_LEAD = "scrollUpChangeLead";
    
    private static final String SCROLL_DOWN = "scrollDown";
    
    private static final String SCROLL_DOWN_EXTEND = "scrollDownExtendSelection";
    
    private static final String SCROLL_DOWN_CHANGE_LEAD = "scrollDownChangeLead";
    
    private static final String SELECT_ALL = "selectAll";
    
    private static final String CLEAR_SELECTION = "clearSelection";
    
    private static final String ADD_TO_SELECTION = "addToSelection";
    
    private static final String TOGGLE_AND_ANCHOR = "toggleAndAnchor";
    
    private static final String EXTEND_TO = "extendTo";
    
    private static final String MOVE_SELECTION_TO = "moveSelectionTo";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      JList jList = (JList)param1ActionEvent.getSource();
      BasicListUI basicListUI = (BasicListUI)BasicLookAndFeel.getUIOfType(jList.getUI(), BasicListUI.class);
      if (str == "selectPreviousColumn") {
        changeSelection(jList, 1, getNextColumnIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectPreviousColumnExtendSelection") {
        changeSelection(jList, 2, getNextColumnIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectPreviousColumnChangeLead") {
        changeSelection(jList, 0, getNextColumnIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectNextColumn") {
        changeSelection(jList, 1, getNextColumnIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectNextColumnExtendSelection") {
        changeSelection(jList, 2, getNextColumnIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectNextColumnChangeLead") {
        changeSelection(jList, 0, getNextColumnIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectPreviousRow") {
        changeSelection(jList, 1, getNextIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectPreviousRowExtendSelection") {
        changeSelection(jList, 2, getNextIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectPreviousRowChangeLead") {
        changeSelection(jList, 0, getNextIndex(jList, basicListUI, -1), -1);
      } else if (str == "selectNextRow") {
        changeSelection(jList, 1, getNextIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectNextRowExtendSelection") {
        changeSelection(jList, 2, getNextIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectNextRowChangeLead") {
        changeSelection(jList, 0, getNextIndex(jList, basicListUI, 1), 1);
      } else if (str == "selectFirstRow") {
        changeSelection(jList, 1, 0, -1);
      } else if (str == "selectFirstRowExtendSelection") {
        changeSelection(jList, 2, 0, -1);
      } else if (str == "selectFirstRowChangeLead") {
        changeSelection(jList, 0, 0, -1);
      } else if (str == "selectLastRow") {
        changeSelection(jList, 1, jList.getModel().getSize() - 1, 1);
      } else if (str == "selectLastRowExtendSelection") {
        changeSelection(jList, 2, jList.getModel().getSize() - 1, 1);
      } else if (str == "selectLastRowChangeLead") {
        changeSelection(jList, 0, jList.getModel().getSize() - 1, 1);
      } else if (str == "scrollUp") {
        changeSelection(jList, 1, getNextPageIndex(jList, -1), -1);
      } else if (str == "scrollUpExtendSelection") {
        changeSelection(jList, 2, getNextPageIndex(jList, -1), -1);
      } else if (str == "scrollUpChangeLead") {
        changeSelection(jList, 0, getNextPageIndex(jList, -1), -1);
      } else if (str == "scrollDown") {
        changeSelection(jList, 1, getNextPageIndex(jList, 1), 1);
      } else if (str == "scrollDownExtendSelection") {
        changeSelection(jList, 2, getNextPageIndex(jList, 1), 1);
      } else if (str == "scrollDownChangeLead") {
        changeSelection(jList, 0, getNextPageIndex(jList, 1), 1);
      } else if (str == "selectAll") {
        selectAll(jList);
      } else if (str == "clearSelection") {
        clearSelection(jList);
      } else if (str == "addToSelection") {
        int i = BasicListUI.adjustIndex(jList.getSelectionModel().getLeadSelectionIndex(), jList);
        if (!jList.isSelectedIndex(i)) {
          int j = jList.getSelectionModel().getAnchorSelectionIndex();
          jList.setValueIsAdjusting(true);
          jList.addSelectionInterval(i, i);
          jList.getSelectionModel().setAnchorSelectionIndex(j);
          jList.setValueIsAdjusting(false);
        } 
      } else if (str == "toggleAndAnchor") {
        int i = BasicListUI.adjustIndex(jList.getSelectionModel().getLeadSelectionIndex(), jList);
        if (jList.isSelectedIndex(i)) {
          jList.removeSelectionInterval(i, i);
        } else {
          jList.addSelectionInterval(i, i);
        } 
      } else if (str == "extendTo") {
        changeSelection(jList, 2, BasicListUI.adjustIndex(jList.getSelectionModel().getLeadSelectionIndex(), jList), 0);
      } else if (str == "moveSelectionTo") {
        changeSelection(jList, 1, BasicListUI.adjustIndex(jList.getSelectionModel().getLeadSelectionIndex(), jList), 0);
      } 
    }
    
    public boolean isEnabled(Object param1Object) {
      String str = getName();
      return (str == "selectPreviousColumnChangeLead" || str == "selectNextColumnChangeLead" || str == "selectPreviousRowChangeLead" || str == "selectNextRowChangeLead" || str == "selectFirstRowChangeLead" || str == "selectLastRowChangeLead" || str == "scrollUpChangeLead" || str == "scrollDownChangeLead") ? ((param1Object != null && ((JList)param1Object).getSelectionModel() instanceof DefaultListSelectionModel)) : true;
    }
    
    private void clearSelection(JList param1JList) { param1JList.clearSelection(); }
    
    private void selectAll(JList param1JList) {
      int i = param1JList.getModel().getSize();
      if (i > 0) {
        ListSelectionModel listSelectionModel = param1JList.getSelectionModel();
        int j = BasicListUI.adjustIndex(listSelectionModel.getLeadSelectionIndex(), param1JList);
        if (listSelectionModel.getSelectionMode() == 0) {
          if (j == -1) {
            int k = BasicListUI.adjustIndex(param1JList.getMinSelectionIndex(), param1JList);
            j = (k == -1) ? 0 : k;
          } 
          param1JList.setSelectionInterval(j, j);
          param1JList.ensureIndexIsVisible(j);
        } else {
          param1JList.setValueIsAdjusting(true);
          int k = BasicListUI.adjustIndex(listSelectionModel.getAnchorSelectionIndex(), param1JList);
          param1JList.setSelectionInterval(0, i - 1);
          SwingUtilities2.setLeadAnchorWithoutSelection(listSelectionModel, k, j);
          param1JList.setValueIsAdjusting(false);
        } 
      } 
    }
    
    private int getNextPageIndex(JList param1JList, int param1Int) {
      if (param1JList.getModel().getSize() == 0)
        return -1; 
      int i = -1;
      Rectangle rectangle1 = param1JList.getVisibleRect();
      ListSelectionModel listSelectionModel = param1JList.getSelectionModel();
      int j = BasicListUI.adjustIndex(listSelectionModel.getLeadSelectionIndex(), param1JList);
      Rectangle rectangle2 = (j == -1) ? new Rectangle() : param1JList.getCellBounds(j, j);
      if (param1JList.getLayoutOrientation() == 1 && param1JList.getVisibleRowCount() <= 0) {
        if (!param1JList.getComponentOrientation().isLeftToRight())
          param1Int = -param1Int; 
        if (param1Int < 0) {
          rectangle1.x = rectangle2.x + rectangle2.width - rectangle1.width;
          Point point = new Point(rectangle1.x - 1, rectangle2.y);
          i = param1JList.locationToIndex(point);
          Rectangle rectangle = param1JList.getCellBounds(i, i);
          if (rectangle1.intersects(rectangle)) {
            point.x = rectangle.x - 1;
            i = param1JList.locationToIndex(point);
            rectangle = param1JList.getCellBounds(i, i);
          } 
          if (rectangle.y != rectangle2.y) {
            point.x = rectangle.x + rectangle.width;
            i = param1JList.locationToIndex(point);
          } 
        } else {
          rectangle1.x = rectangle2.x;
          Point point = new Point(rectangle1.x + rectangle1.width, rectangle2.y);
          i = param1JList.locationToIndex(point);
          Rectangle rectangle = param1JList.getCellBounds(i, i);
          if (rectangle1.intersects(rectangle)) {
            point.x = rectangle.x + rectangle.width;
            i = param1JList.locationToIndex(point);
            rectangle = param1JList.getCellBounds(i, i);
          } 
          if (rectangle.y != rectangle2.y) {
            point.x = rectangle.x - 1;
            i = param1JList.locationToIndex(point);
          } 
        } 
      } else if (param1Int < 0) {
        Point point = new Point(rectangle2.x, rectangle1.y);
        i = param1JList.locationToIndex(point);
        if (j <= i) {
          rectangle1.y = rectangle2.y + rectangle2.height - rectangle1.height;
          point.y = rectangle1.y;
          i = param1JList.locationToIndex(point);
          Rectangle rectangle = param1JList.getCellBounds(i, i);
          if (rectangle.y < rectangle1.y) {
            point.y = rectangle.y + rectangle.height;
            i = param1JList.locationToIndex(point);
            rectangle = param1JList.getCellBounds(i, i);
          } 
          if (rectangle.y >= rectangle2.y) {
            point.y = rectangle2.y - 1;
            i = param1JList.locationToIndex(point);
          } 
        } 
      } else {
        Point point = new Point(rectangle2.x, rectangle1.y + rectangle1.height - 1);
        i = param1JList.locationToIndex(point);
        Rectangle rectangle = param1JList.getCellBounds(i, i);
        if (rectangle.y + rectangle.height > rectangle1.y + rectangle1.height) {
          point.y = rectangle.y - 1;
          i = param1JList.locationToIndex(point);
          rectangle = param1JList.getCellBounds(i, i);
          i = Math.max(i, j);
        } 
        if (j >= i) {
          rectangle1.y = rectangle2.y;
          point.y = rectangle1.y + rectangle1.height - 1;
          i = param1JList.locationToIndex(point);
          rectangle = param1JList.getCellBounds(i, i);
          if (rectangle.y + rectangle.height > rectangle1.y + rectangle1.height) {
            point.y = rectangle.y - 1;
            i = param1JList.locationToIndex(point);
            rectangle = param1JList.getCellBounds(i, i);
          } 
          if (rectangle.y <= rectangle2.y) {
            point.y = rectangle2.y + rectangle2.height;
            i = param1JList.locationToIndex(point);
          } 
        } 
      } 
      return i;
    }
    
    private void changeSelection(JList param1JList, int param1Int1, int param1Int2, int param1Int3) {
      if (param1Int2 >= 0 && param1Int2 < param1JList.getModel().getSize()) {
        ListSelectionModel listSelectionModel = param1JList.getSelectionModel();
        if (param1Int1 == 0 && param1JList.getSelectionMode() != 2)
          param1Int1 = 1; 
        adjustScrollPositionIfNecessary(param1JList, param1Int2, param1Int3);
        if (param1Int1 == 2) {
          int i = BasicListUI.adjustIndex(listSelectionModel.getAnchorSelectionIndex(), param1JList);
          if (i == -1)
            i = 0; 
          param1JList.setSelectionInterval(i, param1Int2);
        } else if (param1Int1 == 1) {
          param1JList.setSelectedIndex(param1Int2);
        } else {
          ((DefaultListSelectionModel)listSelectionModel).moveLeadSelectionIndex(param1Int2);
        } 
      } 
    }
    
    private void adjustScrollPositionIfNecessary(JList param1JList, int param1Int1, int param1Int2) {
      if (param1Int2 == 0)
        return; 
      Rectangle rectangle1 = param1JList.getCellBounds(param1Int1, param1Int1);
      Rectangle rectangle2 = param1JList.getVisibleRect();
      if (rectangle1 != null && !rectangle2.contains(rectangle1)) {
        if (param1JList.getLayoutOrientation() == 1 && param1JList.getVisibleRowCount() <= 0) {
          if (param1JList.getComponentOrientation().isLeftToRight()) {
            if (param1Int2 > 0) {
              int i = Math.max(0, rectangle1.x + rectangle1.width - rectangle2.width);
              int j = param1JList.locationToIndex(new Point(i, rectangle1.y));
              Rectangle rectangle = param1JList.getCellBounds(j, j);
              if (rectangle.x < i && rectangle.x < rectangle1.x) {
                rectangle.x += rectangle.width;
                j = param1JList.locationToIndex(rectangle.getLocation());
                rectangle = param1JList.getCellBounds(j, j);
              } 
              rectangle1 = rectangle;
            } 
            rectangle1.width = rectangle2.width;
          } else if (param1Int2 > 0) {
            int i = rectangle1.x + rectangle2.width;
            int j = param1JList.locationToIndex(new Point(i, rectangle1.y));
            Rectangle rectangle = param1JList.getCellBounds(j, j);
            if (rectangle.x + rectangle.width > i && rectangle.x > rectangle1.x)
              rectangle.width = 0; 
            rectangle1.x = Math.max(0, rectangle.x + rectangle.width - rectangle2.width);
            rectangle1.width = rectangle2.width;
          } else {
            rectangle1.x += Math.max(0, rectangle1.width - rectangle2.width);
            rectangle1.width = Math.min(rectangle1.width, rectangle2.width);
          } 
        } else if (param1Int2 > 0 && (rectangle1.y < rectangle2.y || rectangle1.y + rectangle1.height > rectangle2.y + rectangle2.height)) {
          int i = Math.max(0, rectangle1.y + rectangle1.height - rectangle2.height);
          int j = param1JList.locationToIndex(new Point(rectangle1.x, i));
          Rectangle rectangle = param1JList.getCellBounds(j, j);
          if (rectangle.y < i && rectangle.y < rectangle1.y) {
            rectangle.y += rectangle.height;
            j = param1JList.locationToIndex(rectangle.getLocation());
            rectangle = param1JList.getCellBounds(j, j);
          } 
          rectangle1 = rectangle;
          rectangle1.height = rectangle2.height;
        } else {
          rectangle1.height = Math.min(rectangle1.height, rectangle2.height);
        } 
        param1JList.scrollRectToVisible(rectangle1);
      } 
    }
    
    private int getNextColumnIndex(JList param1JList, BasicListUI param1BasicListUI, int param1Int) {
      if (param1JList.getLayoutOrientation() != 0) {
        int i = BasicListUI.adjustIndex(param1JList.getLeadSelectionIndex(), param1JList);
        int j = param1JList.getModel().getSize();
        if (i == -1)
          return 0; 
        if (j == 1)
          return 0; 
        if (param1BasicListUI == null || param1BasicListUI.columnCount <= 1)
          return -1; 
        int k = param1BasicListUI.convertModelToColumn(i);
        int m = param1BasicListUI.convertModelToRow(i);
        k += param1Int;
        if (k >= param1BasicListUI.columnCount || k < 0)
          return -1; 
        int n = param1BasicListUI.getRowCount(k);
        return (m >= n) ? -1 : param1BasicListUI.getModelIndex(k, m);
      } 
      return -1;
    }
    
    private int getNextIndex(JList param1JList, BasicListUI param1BasicListUI, int param1Int) {
      int i = BasicListUI.adjustIndex(param1JList.getLeadSelectionIndex(), param1JList);
      int j = param1JList.getModel().getSize();
      if (i == -1) {
        if (j > 0)
          if (param1Int > 0) {
            i = 0;
          } else {
            i = j - 1;
          }  
      } else if (j == 1) {
        i = 0;
      } else if (param1JList.getLayoutOrientation() == 2) {
        if (param1BasicListUI != null)
          i += param1BasicListUI.columnCount * param1Int; 
      } else {
        i += param1Int;
      } 
      return i;
    }
  }
  
  public class FocusHandler implements FocusListener {
    protected void repaintCellFocus() { BasicListUI.this.getHandler().repaintCellFocus(); }
    
    public void focusGained(FocusEvent param1FocusEvent) { BasicListUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicListUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements FocusListener, KeyListener, ListDataListener, ListSelectionListener, MouseInputListener, PropertyChangeListener, DragRecognitionSupport.BeforeDrag {
    private String prefix = "";
    
    private String typedString = "";
    
    private long lastTime = 0L;
    
    private boolean dragPressDidSelection;
    
    private Handler() {}
    
    public void keyTyped(KeyEvent param1KeyEvent) {
      JList jList = (JList)param1KeyEvent.getSource();
      ListModel listModel = jList.getModel();
      if (listModel.getSize() == 0 || param1KeyEvent.isAltDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(param1KeyEvent) || isNavigationKey(param1KeyEvent))
        return; 
      boolean bool = true;
      char c = param1KeyEvent.getKeyChar();
      long l = param1KeyEvent.getWhen();
      int i = BasicListUI.adjustIndex(jList.getLeadSelectionIndex(), BasicListUI.this.list);
      if (l - this.lastTime < BasicListUI.this.timeFactor) {
        this.typedString += c;
        if (this.prefix.length() == 1 && c == this.prefix.charAt(0)) {
          i++;
        } else {
          this.prefix = this.typedString;
        } 
      } else {
        i++;
        this.typedString = "" + c;
        this.prefix = this.typedString;
      } 
      this.lastTime = l;
      if (i < 0 || i >= listModel.getSize()) {
        bool = false;
        i = 0;
      } 
      int j = jList.getNextMatch(this.prefix, i, Position.Bias.Forward);
      if (j >= 0) {
        jList.setSelectedIndex(j);
        jList.ensureIndexIsVisible(j);
      } else if (bool) {
        j = jList.getNextMatch(this.prefix, 0, Position.Bias.Forward);
        if (j >= 0) {
          jList.setSelectedIndex(j);
          jList.ensureIndexIsVisible(j);
        } 
      } 
    }
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (isNavigationKey(param1KeyEvent)) {
        this.prefix = "";
        this.typedString = "";
        this.lastTime = 0L;
      } 
    }
    
    public void keyReleased(KeyEvent param1KeyEvent) {}
    
    private boolean isNavigationKey(KeyEvent param1KeyEvent) {
      InputMap inputMap = BasicListUI.this.list.getInputMap(1);
      KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(param1KeyEvent);
      return (inputMap != null && inputMap.get(keyStroke) != null);
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "model") {
        ListModel listModel1 = (ListModel)param1PropertyChangeEvent.getOldValue();
        ListModel listModel2 = (ListModel)param1PropertyChangeEvent.getNewValue();
        if (listModel1 != null)
          listModel1.removeListDataListener(BasicListUI.this.listDataListener); 
        if (listModel2 != null)
          listModel2.addListDataListener(BasicListUI.this.listDataListener); 
        BasicListUI.this.updateLayoutStateNeeded |= 0x1;
        BasicListUI.this.redrawList();
      } else if (str == "selectionModel") {
        ListSelectionModel listSelectionModel1 = (ListSelectionModel)param1PropertyChangeEvent.getOldValue();
        ListSelectionModel listSelectionModel2 = (ListSelectionModel)param1PropertyChangeEvent.getNewValue();
        if (listSelectionModel1 != null)
          listSelectionModel1.removeListSelectionListener(BasicListUI.this.listSelectionListener); 
        if (listSelectionModel2 != null)
          listSelectionModel2.addListSelectionListener(BasicListUI.this.listSelectionListener); 
        BasicListUI.this.updateLayoutStateNeeded |= 0x1;
        BasicListUI.this.redrawList();
      } else if (str == "cellRenderer") {
        BasicListUI.this.updateLayoutStateNeeded |= 0x40;
        BasicListUI.this.redrawList();
      } else if (str == "font") {
        BasicListUI.this.updateLayoutStateNeeded |= 0x4;
        BasicListUI.this.redrawList();
      } else if (str == "prototypeCellValue") {
        BasicListUI.this.updateLayoutStateNeeded |= 0x20;
        BasicListUI.this.redrawList();
      } else if (str == "fixedCellHeight") {
        BasicListUI.this.updateLayoutStateNeeded |= 0x10;
        BasicListUI.this.redrawList();
      } else if (str == "fixedCellWidth") {
        BasicListUI.this.updateLayoutStateNeeded |= 0x8;
        BasicListUI.this.redrawList();
      } else if (str == "selectionForeground") {
        BasicListUI.this.list.repaint();
      } else if (str == "selectionBackground") {
        BasicListUI.this.list.repaint();
      } else if ("layoutOrientation" == str) {
        BasicListUI.this.updateLayoutStateNeeded |= 0x80;
        BasicListUI.this.layoutOrientation = BasicListUI.this.list.getLayoutOrientation();
        BasicListUI.this.redrawList();
      } else if ("visibleRowCount" == str) {
        if (BasicListUI.this.layoutOrientation != 0) {
          BasicListUI.this.updateLayoutStateNeeded |= 0x80;
          BasicListUI.this.redrawList();
        } 
      } else if ("componentOrientation" == str) {
        BasicListUI.this.isLeftToRight = BasicListUI.this.list.getComponentOrientation().isLeftToRight();
        BasicListUI.this.updateLayoutStateNeeded |= 0x400;
        BasicListUI.this.redrawList();
        InputMap inputMap = BasicListUI.this.getInputMap(0);
        SwingUtilities.replaceUIInputMap(BasicListUI.this.list, 0, inputMap);
      } else if ("List.isFileList" == str) {
        BasicListUI.this.updateIsFileList();
        BasicListUI.this.redrawList();
      } else if ("dropLocation" == str) {
        JList.DropLocation dropLocation = (JList.DropLocation)param1PropertyChangeEvent.getOldValue();
        repaintDropLocation(dropLocation);
        repaintDropLocation(BasicListUI.this.list.getDropLocation());
      } 
    }
    
    private void repaintDropLocation(JList.DropLocation param1DropLocation) {
      Rectangle rectangle;
      if (param1DropLocation == null)
        return; 
      if (param1DropLocation.isInsert()) {
        rectangle = BasicListUI.this.getDropLineRect(param1DropLocation);
      } else {
        rectangle = BasicListUI.this.getCellBounds(BasicListUI.this.list, param1DropLocation.getIndex());
      } 
      if (rectangle != null)
        BasicListUI.this.list.repaint(rectangle); 
    }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) {
      BasicListUI.this.updateLayoutStateNeeded = 1;
      int i = Math.min(param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1());
      int j = Math.max(param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1());
      ListSelectionModel listSelectionModel = BasicListUI.this.list.getSelectionModel();
      if (listSelectionModel != null)
        listSelectionModel.insertIndexInterval(i, j - i + 1, true); 
      BasicListUI.this.redrawList();
    }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) {
      BasicListUI.this.updateLayoutStateNeeded = 1;
      ListSelectionModel listSelectionModel = BasicListUI.this.list.getSelectionModel();
      if (listSelectionModel != null)
        listSelectionModel.removeIndexInterval(param1ListDataEvent.getIndex0(), param1ListDataEvent.getIndex1()); 
      BasicListUI.this.redrawList();
    }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) {
      BasicListUI.this.updateLayoutStateNeeded = 1;
      BasicListUI.this.redrawList();
    }
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
      BasicListUI.this.maybeUpdateLayoutState();
      int i = BasicListUI.this.list.getModel().getSize();
      int j = Math.min(i - 1, Math.max(param1ListSelectionEvent.getFirstIndex(), 0));
      int k = Math.min(i - 1, Math.max(param1ListSelectionEvent.getLastIndex(), 0));
      Rectangle rectangle = BasicListUI.this.getCellBounds(BasicListUI.this.list, j, k);
      if (rectangle != null)
        BasicListUI.this.list.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height); 
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicListUI.this.list))
        return; 
      boolean bool = BasicListUI.this.list.getDragEnabled();
      boolean bool1 = true;
      if (bool) {
        int i = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, param1MouseEvent.getPoint());
        if (i != -1 && DragRecognitionSupport.mousePressed(param1MouseEvent)) {
          this.dragPressDidSelection = false;
          if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent))
            return; 
          if (!param1MouseEvent.isShiftDown() && BasicListUI.this.list.isSelectedIndex(i)) {
            BasicListUI.this.list.addSelectionInterval(i, i);
            return;
          } 
          bool1 = false;
          this.dragPressDidSelection = true;
        } 
      } else {
        BasicListUI.this.list.setValueIsAdjusting(true);
      } 
      if (bool1)
        SwingUtilities2.adjustFocus(BasicListUI.this.list); 
      adjustSelection(param1MouseEvent);
    }
    
    private void adjustSelection(MouseEvent param1MouseEvent) {
      int i = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, param1MouseEvent.getPoint());
      if (i < 0) {
        if (BasicListUI.this.isFileList && param1MouseEvent.getID() == 501 && (!param1MouseEvent.isShiftDown() || BasicListUI.this.list.getSelectionMode() == 0))
          BasicListUI.this.list.clearSelection(); 
      } else {
        boolean bool;
        int j = BasicListUI.adjustIndex(BasicListUI.this.list.getAnchorSelectionIndex(), BasicListUI.this.list);
        if (j == -1) {
          j = 0;
          bool = false;
        } else {
          bool = BasicListUI.this.list.isSelectedIndex(j);
        } 
        if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent)) {
          if (param1MouseEvent.isShiftDown()) {
            if (bool) {
              BasicListUI.this.list.addSelectionInterval(j, i);
            } else {
              BasicListUI.this.list.removeSelectionInterval(j, i);
              if (BasicListUI.this.isFileList) {
                BasicListUI.this.list.addSelectionInterval(i, i);
                BasicListUI.this.list.getSelectionModel().setAnchorSelectionIndex(j);
              } 
            } 
          } else if (BasicListUI.this.list.isSelectedIndex(i)) {
            BasicListUI.this.list.removeSelectionInterval(i, i);
          } else {
            BasicListUI.this.list.addSelectionInterval(i, i);
          } 
        } else if (param1MouseEvent.isShiftDown()) {
          BasicListUI.this.list.setSelectionInterval(j, i);
        } else {
          BasicListUI.this.list.setSelectionInterval(i, i);
        } 
      } 
    }
    
    public void dragStarting(MouseEvent param1MouseEvent) {
      if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent)) {
        int i = SwingUtilities2.loc2IndexFileList(BasicListUI.this.list, param1MouseEvent.getPoint());
        BasicListUI.this.list.addSelectionInterval(i, i);
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicListUI.this.list))
        return; 
      if (BasicListUI.this.list.getDragEnabled()) {
        DragRecognitionSupport.mouseDragged(param1MouseEvent, this);
        return;
      } 
      if (param1MouseEvent.isShiftDown() || BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent))
        return; 
      int i = BasicListUI.this.locationToIndex(BasicListUI.this.list, param1MouseEvent.getPoint());
      if (i != -1) {
        if (BasicListUI.this.isFileList)
          return; 
        Rectangle rectangle = BasicListUI.this.getCellBounds(BasicListUI.this.list, i, i);
        if (rectangle != null) {
          BasicListUI.this.list.scrollRectToVisible(rectangle);
          BasicListUI.this.list.setSelectionInterval(i, i);
        } 
      } 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (SwingUtilities2.shouldIgnore(param1MouseEvent, BasicListUI.this.list))
        return; 
      if (BasicListUI.this.list.getDragEnabled()) {
        MouseEvent mouseEvent = DragRecognitionSupport.mouseReleased(param1MouseEvent);
        if (mouseEvent != null) {
          SwingUtilities2.adjustFocus(BasicListUI.this.list);
          if (!this.dragPressDidSelection)
            adjustSelection(mouseEvent); 
        } 
      } else {
        BasicListUI.this.list.setValueIsAdjusting(false);
      } 
    }
    
    protected void repaintCellFocus() {
      int i = BasicListUI.adjustIndex(BasicListUI.this.list.getLeadSelectionIndex(), BasicListUI.this.list);
      if (i != -1) {
        Rectangle rectangle = BasicListUI.this.getCellBounds(BasicListUI.this.list, i, i);
        if (rectangle != null)
          BasicListUI.this.list.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height); 
      } 
    }
    
    public void focusGained(FocusEvent param1FocusEvent) { repaintCellFocus(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { repaintCellFocus(); }
  }
  
  public class ListDataHandler implements ListDataListener {
    public void intervalAdded(ListDataEvent param1ListDataEvent) { BasicListUI.this.getHandler().intervalAdded(param1ListDataEvent); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { BasicListUI.this.getHandler().intervalRemoved(param1ListDataEvent); }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) { BasicListUI.this.getHandler().contentsChanged(param1ListDataEvent); }
  }
  
  public class ListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) { BasicListUI.this.getHandler().valueChanged(param1ListSelectionEvent); }
  }
  
  static class ListTransferHandler extends TransferHandler implements UIResource {
    protected Transferable createTransferable(JComponent param1JComponent) {
      if (param1JComponent instanceof JList) {
        JList jList = (JList)param1JComponent;
        Object[] arrayOfObject = jList.getSelectedValues();
        if (arrayOfObject == null || arrayOfObject.length == 0)
          return null; 
        StringBuffer stringBuffer1 = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("<html>\n<body>\n<ul>\n");
        for (byte b = 0; b < arrayOfObject.length; b++) {
          Object object = arrayOfObject[b];
          String str = (object == null) ? "" : object.toString();
          stringBuffer1.append(str + "\n");
          stringBuffer2.append("  <li>" + str + "\n");
        } 
        stringBuffer1.deleteCharAt(stringBuffer1.length() - 1);
        stringBuffer2.append("</ul>\n</body>\n</html>");
        return new BasicTransferable(stringBuffer1.toString(), stringBuffer2.toString());
      } 
      return null;
    }
    
    public int getSourceActions(JComponent param1JComponent) { return 1; }
  }
  
  public class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mousePressed(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseDragged(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseMoved(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicListUI.this.getHandler().mouseReleased(param1MouseEvent); }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicListUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicListUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */