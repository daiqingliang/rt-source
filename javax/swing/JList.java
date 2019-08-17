package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ListUI;
import javax.swing.text.Position;
import sun.awt.AWTAccessor;
import sun.swing.SwingUtilities2;

public class JList<E> extends JComponent implements Scrollable, Accessible {
  private static final String uiClassID = "ListUI";
  
  public static final int VERTICAL = 0;
  
  public static final int VERTICAL_WRAP = 1;
  
  public static final int HORIZONTAL_WRAP = 2;
  
  private int fixedCellWidth = -1;
  
  private int fixedCellHeight = -1;
  
  private int horizontalScrollIncrement = -1;
  
  private E prototypeCellValue;
  
  private int visibleRowCount = 8;
  
  private Color selectionForeground;
  
  private Color selectionBackground;
  
  private boolean dragEnabled;
  
  private ListSelectionModel selectionModel;
  
  private ListModel<E> dataModel;
  
  private ListCellRenderer<? super E> cellRenderer;
  
  private ListSelectionListener selectionListener;
  
  private int layoutOrientation;
  
  private DropMode dropMode = DropMode.USE_SELECTION;
  
  private DropLocation dropLocation;
  
  public JList(ListModel<E> paramListModel) {
    if (paramListModel == null)
      throw new IllegalArgumentException("dataModel must be non null"); 
    ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent(this);
    this.layoutOrientation = 0;
    this.dataModel = paramListModel;
    this.selectionModel = createSelectionModel();
    setAutoscrolls(true);
    setOpaque(true);
    updateUI();
  }
  
  public JList(final E[] listData) { this(new AbstractListModel<E>() {
          public int getSize() { return listData.length; }
          
          public E getElementAt(int param1Int) { return (E)listData[param1Int]; }
        }); }
  
  public JList(final Vector<? extends E> listData) { this(new AbstractListModel<E>() {
          public int getSize() { return listData.size(); }
          
          public E getElementAt(int param1Int) { return (E)listData.elementAt(param1Int); }
        }); }
  
  public JList() { this(new AbstractListModel<E>() {
          public int getSize() { return 0; }
          
          public E getElementAt(int param1Int) { throw new IndexOutOfBoundsException("No Data Model"); }
        }); }
  
  public ListUI getUI() { return (ListUI)this.ui; }
  
  public void setUI(ListUI paramListUI) { setUI(paramListUI); }
  
  public void updateUI() {
    setUI((ListUI)UIManager.getUI(this));
    ListCellRenderer listCellRenderer = getCellRenderer();
    if (listCellRenderer instanceof Component)
      SwingUtilities.updateComponentTreeUI((Component)listCellRenderer); 
  }
  
  public String getUIClassID() { return "ListUI"; }
  
  private void updateFixedCellSize() {
    ListCellRenderer listCellRenderer = getCellRenderer();
    Object object = getPrototypeCellValue();
    if (listCellRenderer != null && object != null) {
      Component component = listCellRenderer.getListCellRendererComponent(this, object, 0, false, false);
      Font font = component.getFont();
      component.setFont(getFont());
      Dimension dimension = component.getPreferredSize();
      this.fixedCellWidth = dimension.width;
      this.fixedCellHeight = dimension.height;
      component.setFont(font);
    } 
  }
  
  public E getPrototypeCellValue() { return (E)this.prototypeCellValue; }
  
  public void setPrototypeCellValue(E paramE) {
    Object object = this.prototypeCellValue;
    this.prototypeCellValue = paramE;
    if (paramE != null && !paramE.equals(object))
      updateFixedCellSize(); 
    firePropertyChange("prototypeCellValue", object, paramE);
  }
  
  public int getFixedCellWidth() { return this.fixedCellWidth; }
  
  public void setFixedCellWidth(int paramInt) {
    int i = this.fixedCellWidth;
    this.fixedCellWidth = paramInt;
    firePropertyChange("fixedCellWidth", i, this.fixedCellWidth);
  }
  
  public int getFixedCellHeight() { return this.fixedCellHeight; }
  
  public void setFixedCellHeight(int paramInt) {
    int i = this.fixedCellHeight;
    this.fixedCellHeight = paramInt;
    firePropertyChange("fixedCellHeight", i, this.fixedCellHeight);
  }
  
  @Transient
  public ListCellRenderer<? super E> getCellRenderer() { return this.cellRenderer; }
  
  public void setCellRenderer(ListCellRenderer<? super E> paramListCellRenderer) {
    ListCellRenderer listCellRenderer = this.cellRenderer;
    this.cellRenderer = paramListCellRenderer;
    if (paramListCellRenderer != null && !paramListCellRenderer.equals(listCellRenderer))
      updateFixedCellSize(); 
    firePropertyChange("cellRenderer", listCellRenderer, paramListCellRenderer);
  }
  
  public Color getSelectionForeground() { return this.selectionForeground; }
  
  public void setSelectionForeground(Color paramColor) {
    Color color = this.selectionForeground;
    this.selectionForeground = paramColor;
    firePropertyChange("selectionForeground", color, paramColor);
  }
  
  public Color getSelectionBackground() { return this.selectionBackground; }
  
  public void setSelectionBackground(Color paramColor) {
    Color color = this.selectionBackground;
    this.selectionBackground = paramColor;
    firePropertyChange("selectionBackground", color, paramColor);
  }
  
  public int getVisibleRowCount() { return this.visibleRowCount; }
  
  public void setVisibleRowCount(int paramInt) {
    int i = this.visibleRowCount;
    this.visibleRowCount = Math.max(0, paramInt);
    firePropertyChange("visibleRowCount", i, paramInt);
  }
  
  public int getLayoutOrientation() { return this.layoutOrientation; }
  
  public void setLayoutOrientation(int paramInt) {
    int i = this.layoutOrientation;
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        this.layoutOrientation = paramInt;
        firePropertyChange("layoutOrientation", i, paramInt);
        return;
    } 
    throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
  }
  
  public int getFirstVisibleIndex() {
    int i;
    Rectangle rectangle = getVisibleRect();
    if (getComponentOrientation().isLeftToRight()) {
      i = locationToIndex(rectangle.getLocation());
    } else {
      i = locationToIndex(new Point(rectangle.x + rectangle.width - 1, rectangle.y));
    } 
    if (i != -1) {
      Rectangle rectangle1 = getCellBounds(i, i);
      if (rectangle1 != null) {
        SwingUtilities.computeIntersection(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle1);
        if (rectangle1.width == 0 || rectangle1.height == 0)
          i = -1; 
      } 
    } 
    return i;
  }
  
  public int getLastVisibleIndex() {
    Point point;
    boolean bool = getComponentOrientation().isLeftToRight();
    Rectangle rectangle = getVisibleRect();
    if (bool) {
      point = new Point(rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1);
    } else {
      point = new Point(rectangle.x, rectangle.y + rectangle.height - 1);
    } 
    int i = locationToIndex(point);
    if (i != -1) {
      Rectangle rectangle1 = getCellBounds(i, i);
      if (rectangle1 != null) {
        SwingUtilities.computeIntersection(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle1);
        if (rectangle1.width == 0 || rectangle1.height == 0) {
          int j;
          boolean bool1 = (getLayoutOrientation() == 2) ? 1 : 0;
          Point point1 = bool1 ? new Point(point.x, rectangle.y) : new Point(rectangle.x, point.y);
          int k = -1;
          int m = i;
          i = -1;
          do {
            j = k;
            k = locationToIndex(point1);
            if (k == -1)
              continue; 
            rectangle1 = getCellBounds(k, k);
            if (k != m && rectangle1 != null && rectangle1.contains(point1)) {
              i = k;
              if (bool1) {
                point1.y = rectangle1.y + rectangle1.height;
                if (point1.y >= point.y)
                  j = k; 
              } else {
                point1.x = rectangle1.x + rectangle1.width;
                if (point1.x >= point.x)
                  j = k; 
              } 
            } else {
              j = k;
            } 
          } while (k != -1 && j != k);
        } 
      } 
    } 
    return i;
  }
  
  public void ensureIndexIsVisible(int paramInt) {
    Rectangle rectangle = getCellBounds(paramInt, paramInt);
    if (rectangle != null)
      scrollRectToVisible(rectangle); 
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
        case ON_OR_INSERT:
          this.dropMode = paramDropMode;
          return;
      }  
    throw new IllegalArgumentException(paramDropMode + ": Unsupported drop mode for list");
  }
  
  public final DropMode getDropMode() { return this.dropMode; }
  
  DropLocation dropLocationForPoint(Point paramPoint) {
    DropLocation dropLocation1;
    null = null;
    Rectangle rectangle = null;
    int i = locationToIndex(paramPoint);
    if (i != -1)
      rectangle = getCellBounds(i, i); 
    switch (this.dropMode) {
      case USE_SELECTION:
      case ON:
        return new DropLocation(paramPoint, (rectangle != null && rectangle.contains(paramPoint)) ? i : -1, false, null);
      case INSERT:
        if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, getModel().getSize(), true, null);
        } else {
          if (this.layoutOrientation == 2) {
            boolean bool = getComponentOrientation().isLeftToRight();
            if (SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool, false) == SwingUtilities2.Section.TRAILING) {
              i++;
            } else if (i == getModel().getSize() - 1 && paramPoint.y >= rectangle.y + rectangle.height) {
              i++;
            } 
          } else if (SwingUtilities2.liesInVertical(rectangle, paramPoint, false) == SwingUtilities2.Section.TRAILING) {
            i++;
          } 
          dropLocation1 = new DropLocation(paramPoint, i, true, null);
        } 
        return dropLocation1;
      case ON_OR_INSERT:
        if (i == -1) {
          dropLocation1 = new DropLocation(paramPoint, getModel().getSize(), true, null);
        } else {
          boolean bool = false;
          if (this.layoutOrientation == 2) {
            boolean bool1 = getComponentOrientation().isLeftToRight();
            SwingUtilities2.Section section = SwingUtilities2.liesInHorizontal(rectangle, paramPoint, bool1, true);
            if (section == SwingUtilities2.Section.TRAILING) {
              i++;
              bool = true;
            } else if (i == getModel().getSize() - 1 && paramPoint.y >= rectangle.y + rectangle.height) {
              i++;
              bool = true;
            } else if (section == SwingUtilities2.Section.LEADING) {
              bool = true;
            } 
          } else {
            SwingUtilities2.Section section = SwingUtilities2.liesInVertical(rectangle, paramPoint, true);
            if (section == SwingUtilities2.Section.LEADING) {
              bool = true;
            } else if (section == SwingUtilities2.Section.TRAILING) {
              i++;
              bool = true;
            } 
          } 
          dropLocation1 = new DropLocation(paramPoint, i, bool, null);
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
          setSelectedIndices((int[][])paramObject[0]);
          int i = (int[][])paramObject[1][0];
          int j = (int[][])paramObject[1][1];
          SwingUtilities2.setLeadAnchorWithoutSelection(getSelectionModel(), j, i);
        } 
      } else {
        if (this.dropLocation == null) {
          int[] arrayOfInt1 = getSelectedIndices();
          arrayOfInt = new int[][] { arrayOfInt1, { getAnchorSelectionIndex(), getLeadSelectionIndex() } };
        } else {
          arrayOfInt = paramObject;
        } 
        int i = dropLocation1.getIndex();
        if (i == -1) {
          clearSelection();
          getSelectionModel().setAnchorSelectionIndex(-1);
          getSelectionModel().setLeadSelectionIndex(-1);
        } else {
          setSelectionInterval(i, i);
        } 
      }  
    DropLocation dropLocation2 = this.dropLocation;
    this.dropLocation = dropLocation1;
    firePropertyChange("dropLocation", dropLocation2, this.dropLocation);
    return arrayOfInt;
  }
  
  public final DropLocation getDropLocation() { return this.dropLocation; }
  
  public int getNextMatch(String paramString, int paramInt, Position.Bias paramBias) {
    ListModel listModel = getModel();
    int i = listModel.getSize();
    if (paramString == null)
      throw new IllegalArgumentException(); 
    if (paramInt < 0 || paramInt >= i)
      throw new IllegalArgumentException(); 
    paramString = paramString.toUpperCase();
    int j = (paramBias == Position.Bias.Forward) ? 1 : -1;
    int k = paramInt;
    do {
      Object object = listModel.getElementAt(k);
      if (object != null) {
        String str;
        if (object instanceof String) {
          str = ((String)object).toUpperCase();
        } else {
          str = object.toString();
          if (str != null)
            str = str.toUpperCase(); 
        } 
        if (str != null && str.startsWith(paramString))
          return k; 
      } 
      k = (k + j + i) % i;
    } while (k != paramInt);
    return -1;
  }
  
  public String getToolTipText(MouseEvent paramMouseEvent) {
    if (paramMouseEvent != null) {
      Point point = paramMouseEvent.getPoint();
      int i = locationToIndex(point);
      ListCellRenderer listCellRenderer = getCellRenderer();
      Rectangle rectangle;
      if (i != -1 && listCellRenderer != null && (rectangle = getCellBounds(i, i)) != null && rectangle.contains(point.x, point.y)) {
        ListSelectionModel listSelectionModel = getSelectionModel();
        Component component = listCellRenderer.getListCellRendererComponent(this, getModel().getElementAt(i), i, listSelectionModel.isSelectedIndex(i), (hasFocus() && listSelectionModel.getLeadSelectionIndex() == i));
        if (component instanceof JComponent) {
          point.translate(-rectangle.x, -rectangle.y);
          MouseEvent mouseEvent = new MouseEvent(component, paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
          AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
          mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
          String str = ((JComponent)component).getToolTipText(mouseEvent);
          if (str != null)
            return str; 
        } 
      } 
    } 
    return getToolTipText();
  }
  
  public int locationToIndex(Point paramPoint) {
    ListUI listUI = getUI();
    return (listUI != null) ? listUI.locationToIndex(this, paramPoint) : -1;
  }
  
  public Point indexToLocation(int paramInt) {
    ListUI listUI = getUI();
    return (listUI != null) ? listUI.indexToLocation(this, paramInt) : null;
  }
  
  public Rectangle getCellBounds(int paramInt1, int paramInt2) {
    ListUI listUI = getUI();
    return (listUI != null) ? listUI.getCellBounds(this, paramInt1, paramInt2) : null;
  }
  
  public ListModel<E> getModel() { return this.dataModel; }
  
  public void setModel(ListModel<E> paramListModel) {
    if (paramListModel == null)
      throw new IllegalArgumentException("model must be non null"); 
    ListModel listModel = this.dataModel;
    this.dataModel = paramListModel;
    firePropertyChange("model", listModel, this.dataModel);
    clearSelection();
  }
  
  public void setListData(final E[] listData) { setModel(new AbstractListModel<E>() {
          public int getSize() { return listData.length; }
          
          public E getElementAt(int param1Int) { return (E)listData[param1Int]; }
        }); }
  
  public void setListData(final Vector<? extends E> listData) { setModel(new AbstractListModel<E>() {
          public int getSize() { return listData.size(); }
          
          public E getElementAt(int param1Int) { return (E)listData.elementAt(param1Int); }
        }); }
  
  protected ListSelectionModel createSelectionModel() { return new DefaultListSelectionModel(); }
  
  public ListSelectionModel getSelectionModel() { return this.selectionModel; }
  
  protected void fireSelectionValueChanged(int paramInt1, int paramInt2, boolean paramBoolean) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListSelectionEvent listSelectionEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListSelectionListener.class) {
        if (listSelectionEvent == null)
          listSelectionEvent = new ListSelectionEvent(this, paramInt1, paramInt2, paramBoolean); 
        ((ListSelectionListener)arrayOfObject[i + 1]).valueChanged(listSelectionEvent);
      } 
    } 
  }
  
  public void addListSelectionListener(ListSelectionListener paramListSelectionListener) {
    if (this.selectionListener == null) {
      this.selectionListener = new ListSelectionHandler(null);
      getSelectionModel().addListSelectionListener(this.selectionListener);
    } 
    this.listenerList.add(ListSelectionListener.class, paramListSelectionListener);
  }
  
  public void removeListSelectionListener(ListSelectionListener paramListSelectionListener) { this.listenerList.remove(ListSelectionListener.class, paramListSelectionListener); }
  
  public ListSelectionListener[] getListSelectionListeners() { return (ListSelectionListener[])this.listenerList.getListeners(ListSelectionListener.class); }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel) {
    if (paramListSelectionModel == null)
      throw new IllegalArgumentException("selectionModel must be non null"); 
    if (this.selectionListener != null) {
      this.selectionModel.removeListSelectionListener(this.selectionListener);
      paramListSelectionModel.addListSelectionListener(this.selectionListener);
    } 
    ListSelectionModel listSelectionModel = this.selectionModel;
    this.selectionModel = paramListSelectionModel;
    firePropertyChange("selectionModel", listSelectionModel, paramListSelectionModel);
  }
  
  public void setSelectionMode(int paramInt) { getSelectionModel().setSelectionMode(paramInt); }
  
  public int getSelectionMode() { return getSelectionModel().getSelectionMode(); }
  
  public int getAnchorSelectionIndex() { return getSelectionModel().getAnchorSelectionIndex(); }
  
  public int getLeadSelectionIndex() { return getSelectionModel().getLeadSelectionIndex(); }
  
  public int getMinSelectionIndex() { return getSelectionModel().getMinSelectionIndex(); }
  
  public int getMaxSelectionIndex() { return getSelectionModel().getMaxSelectionIndex(); }
  
  public boolean isSelectedIndex(int paramInt) { return getSelectionModel().isSelectedIndex(paramInt); }
  
  public boolean isSelectionEmpty() { return getSelectionModel().isSelectionEmpty(); }
  
  public void clearSelection() { getSelectionModel().clearSelection(); }
  
  public void setSelectionInterval(int paramInt1, int paramInt2) { getSelectionModel().setSelectionInterval(paramInt1, paramInt2); }
  
  public void addSelectionInterval(int paramInt1, int paramInt2) { getSelectionModel().addSelectionInterval(paramInt1, paramInt2); }
  
  public void removeSelectionInterval(int paramInt1, int paramInt2) { getSelectionModel().removeSelectionInterval(paramInt1, paramInt2); }
  
  public void setValueIsAdjusting(boolean paramBoolean) { getSelectionModel().setValueIsAdjusting(paramBoolean); }
  
  public boolean getValueIsAdjusting() { return getSelectionModel().getValueIsAdjusting(); }
  
  @Transient
  public int[] getSelectedIndices() {
    ListSelectionModel listSelectionModel = getSelectionModel();
    int i = listSelectionModel.getMinSelectionIndex();
    int j = listSelectionModel.getMaxSelectionIndex();
    if (i < 0 || j < 0)
      return new int[0]; 
    int[] arrayOfInt1 = new int[1 + j - i];
    byte b = 0;
    for (int k = i; k <= j; k++) {
      if (listSelectionModel.isSelectedIndex(k))
        arrayOfInt1[b++] = k; 
    } 
    int[] arrayOfInt2 = new int[b];
    System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b);
    return arrayOfInt2;
  }
  
  public void setSelectedIndex(int paramInt) {
    if (paramInt >= getModel().getSize())
      return; 
    getSelectionModel().setSelectionInterval(paramInt, paramInt);
  }
  
  public void setSelectedIndices(int[] paramArrayOfInt) {
    ListSelectionModel listSelectionModel = getSelectionModel();
    listSelectionModel.clearSelection();
    int i = getModel().getSize();
    for (int j : paramArrayOfInt) {
      if (j < i)
        listSelectionModel.addSelectionInterval(j, j); 
    } 
  }
  
  @Deprecated
  public Object[] getSelectedValues() {
    ListSelectionModel listSelectionModel = getSelectionModel();
    ListModel listModel = getModel();
    int i = listSelectionModel.getMinSelectionIndex();
    int j = listSelectionModel.getMaxSelectionIndex();
    if (i < 0 || j < 0)
      return new Object[0]; 
    Object[] arrayOfObject1 = new Object[1 + j - i];
    byte b = 0;
    for (int k = i; k <= j; k++) {
      if (listSelectionModel.isSelectedIndex(k))
        arrayOfObject1[b++] = listModel.getElementAt(k); 
    } 
    Object[] arrayOfObject2 = new Object[b];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, b);
    return arrayOfObject2;
  }
  
  public List<E> getSelectedValuesList() {
    ListSelectionModel listSelectionModel = getSelectionModel();
    ListModel listModel = getModel();
    int i = listSelectionModel.getMinSelectionIndex();
    int j = listSelectionModel.getMaxSelectionIndex();
    if (i < 0 || j < 0)
      return Collections.emptyList(); 
    ArrayList arrayList = new ArrayList();
    for (int k = i; k <= j; k++) {
      if (listSelectionModel.isSelectedIndex(k))
        arrayList.add(listModel.getElementAt(k)); 
    } 
    return arrayList;
  }
  
  public int getSelectedIndex() { return getMinSelectionIndex(); }
  
  public E getSelectedValue() {
    int i = getMinSelectionIndex();
    return (E)((i == -1) ? null : getModel().getElementAt(i));
  }
  
  public void setSelectedValue(Object paramObject, boolean paramBoolean) {
    if (paramObject == null) {
      setSelectedIndex(-1);
    } else if (!paramObject.equals(getSelectedValue())) {
      ListModel listModel = getModel();
      byte b = 0;
      int i = listModel.getSize();
      while (b < i) {
        if (paramObject.equals(listModel.getElementAt(b))) {
          setSelectedIndex(b);
          if (paramBoolean)
            ensureIndexIsVisible(b); 
          repaint();
          return;
        } 
        b++;
      } 
      setSelectedIndex(-1);
    } 
    repaint();
  }
  
  private void checkScrollableParameters(Rectangle paramRectangle, int paramInt) {
    if (paramRectangle == null)
      throw new IllegalArgumentException("visibleRect must be non-null"); 
    switch (paramInt) {
      case 0:
      case 1:
        return;
    } 
    throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
  }
  
  public Dimension getPreferredScrollableViewportSize() {
    if (getLayoutOrientation() != 0)
      return getPreferredSize(); 
    Insets insets = getInsets();
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    int k = getVisibleRowCount();
    int m = getFixedCellWidth();
    int n = getFixedCellHeight();
    if (m > 0 && n > 0) {
      int i1 = m + i;
      int i2 = k * n + j;
      return new Dimension(i1, i2);
    } 
    if (getModel().getSize() > 0) {
      byte b;
      int i1 = (getPreferredSize()).width;
      Rectangle rectangle = getCellBounds(0, 0);
      if (rectangle != null) {
        b = k * rectangle.height + j;
      } else {
        b = 1;
      } 
      return new Dimension(i1, b);
    } 
    m = (m > 0) ? m : 256;
    n = (n > 0) ? n : 16;
    return new Dimension(m, n * k);
  }
  
  public int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    checkScrollableParameters(paramRectangle, paramInt1);
    if (paramInt1 == 1) {
      int i = locationToIndex(paramRectangle.getLocation());
      if (i == -1)
        return 0; 
      if (paramInt2 > 0) {
        Rectangle rectangle1 = getCellBounds(i, i);
        return (rectangle1 == null) ? 0 : (rectangle1.height - paramRectangle.y - rectangle1.y);
      } 
      Rectangle rectangle = getCellBounds(i, i);
      if (rectangle.y == paramRectangle.y && i == 0)
        return 0; 
      if (rectangle.y == paramRectangle.y) {
        Point point = rectangle.getLocation();
        point.y--;
        int j = locationToIndex(point);
        Rectangle rectangle1 = getCellBounds(j, j);
        return (rectangle1 == null || rectangle1.y >= rectangle.y) ? 0 : rectangle1.height;
      } 
      return paramRectangle.y - rectangle.y;
    } 
    if (paramInt1 == 0 && getLayoutOrientation() != 0) {
      Point point;
      boolean bool = getComponentOrientation().isLeftToRight();
      if (bool) {
        point = paramRectangle.getLocation();
      } else {
        point = new Point(paramRectangle.x + paramRectangle.width - 1, paramRectangle.y);
      } 
      int i = locationToIndex(point);
      if (i != -1) {
        Rectangle rectangle = getCellBounds(i, i);
        if (rectangle != null && rectangle.contains(point)) {
          int k;
          int j;
          if (bool) {
            j = paramRectangle.x;
            k = rectangle.x;
          } else {
            j = paramRectangle.x + paramRectangle.width;
            k = rectangle.x + rectangle.width;
          } 
          return (k != j) ? ((paramInt2 < 0) ? Math.abs(j - k) : (bool ? (k + rectangle.width - j) : (j - rectangle.x))) : rectangle.width;
        } 
      } 
    } 
    Font font = getFont();
    return (font != null) ? font.getSize() : 1;
  }
  
  public int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2) {
    checkScrollableParameters(paramRectangle, paramInt1);
    if (paramInt1 == 1) {
      int i = paramRectangle.height;
      if (paramInt2 > 0) {
        int j = locationToIndex(new Point(paramRectangle.x, paramRectangle.y + paramRectangle.height - 1));
        if (j != -1) {
          Rectangle rectangle = getCellBounds(j, j);
          if (rectangle != null) {
            i = rectangle.y - paramRectangle.y;
            if (i == 0 && j < getModel().getSize() - 1)
              i = rectangle.height; 
          } 
        } 
      } else {
        int j = locationToIndex(new Point(paramRectangle.x, paramRectangle.y - paramRectangle.height));
        int k = getFirstVisibleIndex();
        if (j != -1) {
          if (k == -1)
            k = locationToIndex(paramRectangle.getLocation()); 
          Rectangle rectangle1 = getCellBounds(j, j);
          Rectangle rectangle2 = getCellBounds(k, k);
          if (rectangle1 != null && rectangle2 != null) {
            while (rectangle1.y + paramRectangle.height < rectangle2.y + rectangle2.height && rectangle1.y < rectangle2.y)
              rectangle1 = getCellBounds(++j, j); 
            i = paramRectangle.y - rectangle1.y;
            if (i <= 0 && rectangle1.y > 0) {
              rectangle1 = getCellBounds(--j, j);
              if (rectangle1 != null)
                i = paramRectangle.y - rectangle1.y; 
            } 
          } 
        } 
      } 
      return i;
    } 
    if (paramInt1 == 0 && getLayoutOrientation() != 0) {
      boolean bool = getComponentOrientation().isLeftToRight();
      int i = paramRectangle.width;
      if (paramInt2 > 0) {
        int j = paramRectangle.x + (bool ? (paramRectangle.width - 1) : 0);
        int k = locationToIndex(new Point(j, paramRectangle.y));
        if (k != -1) {
          Rectangle rectangle = getCellBounds(k, k);
          if (rectangle != null) {
            if (bool) {
              i = rectangle.x - paramRectangle.x;
            } else {
              i = paramRectangle.x + paramRectangle.width - rectangle.x + rectangle.width;
            } 
            if (i < 0) {
              i += rectangle.width;
            } else if (i == 0 && k < getModel().getSize() - 1) {
              i = rectangle.width;
            } 
          } 
        } 
      } else {
        int j = paramRectangle.x + (bool ? -paramRectangle.width : (paramRectangle.width - 1 + paramRectangle.width));
        int k = locationToIndex(new Point(j, paramRectangle.y));
        if (k != -1) {
          Rectangle rectangle = getCellBounds(k, k);
          if (rectangle != null) {
            int m = rectangle.x + rectangle.width;
            if (bool) {
              if (rectangle.x < paramRectangle.x - paramRectangle.width && m < paramRectangle.x) {
                i = paramRectangle.x - m;
              } else {
                i = paramRectangle.x - rectangle.x;
              } 
            } else {
              int n = paramRectangle.x + paramRectangle.width;
              if (m > n + paramRectangle.width && rectangle.x > n) {
                i = rectangle.x - n;
              } else {
                i = m - n;
              } 
            } 
          } 
        } 
      } 
      return i;
    } 
    return paramRectangle.width;
  }
  
  public boolean getScrollableTracksViewportWidth() {
    if (getLayoutOrientation() == 2 && getVisibleRowCount() <= 0)
      return true; 
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof JViewport) ? ((container.getWidth() > (getPreferredSize()).width)) : false;
  }
  
  public boolean getScrollableTracksViewportHeight() {
    if (getLayoutOrientation() == 1 && getVisibleRowCount() <= 0)
      return true; 
    Container container = SwingUtilities.getUnwrappedParent(this);
    return (container instanceof JViewport) ? ((container.getHeight() > (getPreferredSize()).height)) : false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("ListUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = (this.selectionForeground != null) ? this.selectionForeground.toString() : "";
    String str2 = (this.selectionBackground != null) ? this.selectionBackground.toString() : "";
    return super.paramString() + ",fixedCellHeight=" + this.fixedCellHeight + ",fixedCellWidth=" + this.fixedCellWidth + ",horizontalScrollIncrement=" + this.horizontalScrollIncrement + ",selectionBackground=" + str2 + ",selectionForeground=" + str1 + ",visibleRowCount=" + this.visibleRowCount + ",layoutOrientation=" + this.layoutOrientation;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJList(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJList extends JComponent.AccessibleJComponent implements AccessibleSelection, PropertyChangeListener, ListSelectionListener, ListDataListener {
    int leadSelectionIndex;
    
    public AccessibleJList() {
      super(JList.this);
      this$0.addPropertyChangeListener(this);
      this$0.getSelectionModel().addListSelectionListener(this);
      this$0.getModel().addListDataListener(this);
      this.leadSelectionIndex = this$0.getLeadSelectionIndex();
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      Object object1 = param1PropertyChangeEvent.getOldValue();
      Object object2 = param1PropertyChangeEvent.getNewValue();
      if (str.compareTo("model") == 0) {
        if (object1 != null && object1 instanceof ListModel)
          ((ListModel)object1).removeListDataListener(this); 
        if (object2 != null && object2 instanceof ListModel)
          ((ListModel)object2).addListDataListener(this); 
      } else if (str.compareTo("selectionModel") == 0) {
        if (object1 != null && object1 instanceof ListSelectionModel)
          ((ListSelectionModel)object1).removeListSelectionListener(this); 
        if (object2 != null && object2 instanceof ListSelectionModel)
          ((ListSelectionModel)object2).addListSelectionListener(this); 
        firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      } 
    }
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {
      int i = this.leadSelectionIndex;
      this.leadSelectionIndex = JList.this.getLeadSelectionIndex();
      if (i != this.leadSelectionIndex) {
        Accessible accessible1 = (i >= 0) ? getAccessibleChild(i) : null;
        Accessible accessible2 = (this.leadSelectionIndex >= 0) ? getAccessibleChild(this.leadSelectionIndex) : null;
        firePropertyChange("AccessibleActiveDescendant", accessible1, accessible2);
      } 
      firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true));
      firePropertyChange("AccessibleSelection", Boolean.valueOf(false), Boolean.valueOf(true));
      AccessibleStateSet accessibleStateSet = getAccessibleStateSet();
      ListSelectionModel listSelectionModel = JList.this.getSelectionModel();
      if (listSelectionModel.getSelectionMode() != 0) {
        if (!accessibleStateSet.contains(AccessibleState.MULTISELECTABLE)) {
          accessibleStateSet.add(AccessibleState.MULTISELECTABLE);
          firePropertyChange("AccessibleState", null, AccessibleState.MULTISELECTABLE);
        } 
      } else if (accessibleStateSet.contains(AccessibleState.MULTISELECTABLE)) {
        accessibleStateSet.remove(AccessibleState.MULTISELECTABLE);
        firePropertyChange("AccessibleState", AccessibleState.MULTISELECTABLE, null);
      } 
    }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) { firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true)); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true)); }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) { firePropertyChange("AccessibleVisibleData", Boolean.valueOf(false), Boolean.valueOf(true)); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JList.this.selectionModel.getSelectionMode() != 0)
        accessibleStateSet.add(AccessibleState.MULTISELECTABLE); 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.LIST; }
    
    public Accessible getAccessibleAt(Point param1Point) {
      int i = JList.this.locationToIndex(param1Point);
      return (i >= 0) ? new ActionableAccessibleJListChild(JList.this, i) : null;
    }
    
    public int getAccessibleChildrenCount() { return JList.this.getModel().getSize(); }
    
    public Accessible getAccessibleChild(int param1Int) { return (param1Int >= JList.this.getModel().getSize()) ? null : new ActionableAccessibleJListChild(JList.this, param1Int); }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() { return JList.this.getSelectedIndices().length; }
    
    public Accessible getAccessibleSelection(int param1Int) {
      int i = getAccessibleSelectionCount();
      return (param1Int < 0 || param1Int >= i) ? null : getAccessibleChild(JList.this.getSelectedIndices()[param1Int]);
    }
    
    public boolean isAccessibleChildSelected(int param1Int) { return JList.this.isSelectedIndex(param1Int); }
    
    public void addAccessibleSelection(int param1Int) { JList.this.addSelectionInterval(param1Int, param1Int); }
    
    public void removeAccessibleSelection(int param1Int) { JList.this.removeSelectionInterval(param1Int, param1Int); }
    
    public void clearAccessibleSelection() { JList.this.clearSelection(); }
    
    public void selectAllAccessibleSelection() { JList.this.addSelectionInterval(0, getAccessibleChildrenCount() - 1); }
    
    protected class AccessibleJListChild extends AccessibleContext implements Accessible, AccessibleComponent {
      private JList<E> parent = null;
      
      int indexInParent;
      
      private Component component = null;
      
      private AccessibleContext accessibleContext = null;
      
      private ListModel<E> listModel;
      
      private ListCellRenderer<? super E> cellRenderer = null;
      
      public AccessibleJListChild(JList<E> param2JList, int param2Int) {
        this.parent = param2JList;
        setAccessibleParent(param2JList);
        this.indexInParent = param2Int;
        if (param2JList != null) {
          this.listModel = param2JList.getModel();
          this.cellRenderer = param2JList.getCellRenderer();
        } 
      }
      
      private Component getCurrentComponent() { return getComponentAtIndex(this.indexInParent); }
      
      AccessibleContext getCurrentAccessibleContext() {
        Component component1 = getComponentAtIndex(this.indexInParent);
        return (component1 instanceof Accessible) ? component1.getAccessibleContext() : null;
      }
      
      private Component getComponentAtIndex(int param2Int) {
        if (param2Int < 0 || param2Int >= this.listModel.getSize())
          return null; 
        if (this.parent != null && this.listModel != null && this.cellRenderer != null) {
          Object object = this.listModel.getElementAt(param2Int);
          boolean bool1 = this.parent.isSelectedIndex(param2Int);
          boolean bool2 = (this.parent.isFocusOwner() && param2Int == this.parent.getLeadSelectionIndex());
          return this.cellRenderer.getListCellRendererComponent(this.parent, object, param2Int, bool1, bool2);
        } 
        return null;
      }
      
      public AccessibleContext getAccessibleContext() { return this; }
      
      public String getAccessibleName() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleName() : null;
      }
      
      public void setAccessibleName(String param2String) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null)
          accessibleContext1.setAccessibleName(param2String); 
      }
      
      public String getAccessibleDescription() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleDescription() : null;
      }
      
      public void setAccessibleDescription(String param2String) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null)
          accessibleContext1.setAccessibleDescription(param2String); 
      }
      
      public AccessibleRole getAccessibleRole() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleRole() : null;
      }
      
      public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet accessibleStateSet;
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null) {
          accessibleStateSet = accessibleContext1.getAccessibleStateSet();
        } else {
          accessibleStateSet = new AccessibleStateSet();
        } 
        accessibleStateSet.add(AccessibleState.SELECTABLE);
        if (this.parent.isFocusOwner() && this.indexInParent == this.parent.getLeadSelectionIndex())
          accessibleStateSet.add(AccessibleState.ACTIVE); 
        if (this.parent.isSelectedIndex(this.indexInParent))
          accessibleStateSet.add(AccessibleState.SELECTED); 
        if (isShowing()) {
          accessibleStateSet.add(AccessibleState.SHOWING);
        } else if (accessibleStateSet.contains(AccessibleState.SHOWING)) {
          accessibleStateSet.remove(AccessibleState.SHOWING);
        } 
        if (isVisible()) {
          accessibleStateSet.add(AccessibleState.VISIBLE);
        } else if (accessibleStateSet.contains(AccessibleState.VISIBLE)) {
          accessibleStateSet.remove(AccessibleState.VISIBLE);
        } 
        accessibleStateSet.add(AccessibleState.TRANSIENT);
        return accessibleStateSet;
      }
      
      public int getAccessibleIndexInParent() { return this.indexInParent; }
      
      public int getAccessibleChildrenCount() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleChildrenCount() : 0;
      }
      
      public Accessible getAccessibleChild(int param2Int) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null) {
          Accessible accessible = accessibleContext1.getAccessibleChild(param2Int);
          accessibleContext1.setAccessibleParent(this);
          return accessible;
        } 
        return null;
      }
      
      public Locale getLocale() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getLocale() : null;
      }
      
      public void addPropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null)
          accessibleContext1.addPropertyChangeListener(param2PropertyChangeListener); 
      }
      
      public void removePropertyChangeListener(PropertyChangeListener param2PropertyChangeListener) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 != null)
          accessibleContext1.removePropertyChangeListener(param2PropertyChangeListener); 
      }
      
      public AccessibleComponent getAccessibleComponent() { return this; }
      
      public AccessibleSelection getAccessibleSelection() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleSelection() : null;
      }
      
      public AccessibleText getAccessibleText() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleText() : null;
      }
      
      public AccessibleValue getAccessibleValue() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleValue() : null;
      }
      
      public Color getBackground() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).getBackground(); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.getBackground() : null;
      }
      
      public void setBackground(Color param2Color) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setBackground(param2Color);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setBackground(param2Color); 
        } 
      }
      
      public Color getForeground() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).getForeground(); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.getForeground() : null;
      }
      
      public void setForeground(Color param2Color) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setForeground(param2Color);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setForeground(param2Color); 
        } 
      }
      
      public Cursor getCursor() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).getCursor(); 
        Component component1 = getCurrentComponent();
        if (component1 != null)
          return component1.getCursor(); 
        Accessible accessible = getAccessibleParent();
        return (accessible instanceof AccessibleComponent) ? ((AccessibleComponent)accessible).getCursor() : null;
      }
      
      public void setCursor(Cursor param2Cursor) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setCursor(param2Cursor);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setCursor(param2Cursor); 
        } 
      }
      
      public Font getFont() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).getFont(); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.getFont() : null;
      }
      
      public void setFont(Font param2Font) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setFont(param2Font);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setFont(param2Font); 
        } 
      }
      
      public FontMetrics getFontMetrics(Font param2Font) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).getFontMetrics(param2Font); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.getFontMetrics(param2Font) : null;
      }
      
      public boolean isEnabled() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).isEnabled(); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.isEnabled() : 0;
      }
      
      public void setEnabled(boolean param2Boolean) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setEnabled(param2Boolean);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setEnabled(param2Boolean); 
        } 
      }
      
      public boolean isVisible() {
        int i = this.parent.getFirstVisibleIndex();
        int j = this.parent.getLastVisibleIndex();
        if (j == -1)
          j = this.parent.getModel().getSize() - 1; 
        return (this.indexInParent >= i && this.indexInParent <= j);
      }
      
      public void setVisible(boolean param2Boolean) {}
      
      public boolean isShowing() { return (this.parent.isShowing() && isVisible()); }
      
      public boolean contains(Point param2Point) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          Rectangle rectangle = ((AccessibleComponent)accessibleContext1).getBounds();
          return rectangle.contains(param2Point);
        } 
        Component component1 = getCurrentComponent();
        if (component1 != null) {
          Rectangle rectangle = component1.getBounds();
          return rectangle.contains(param2Point);
        } 
        return getBounds().contains(param2Point);
      }
      
      public Point getLocationOnScreen() {
        if (this.parent != null) {
          Point point1;
          try {
            point1 = this.parent.getLocationOnScreen();
          } catch (IllegalComponentStateException illegalComponentStateException) {
            return null;
          } 
          Point point2 = this.parent.indexToLocation(this.indexInParent);
          if (point2 != null) {
            point2.translate(point1.x, point1.y);
            return point2;
          } 
          return null;
        } 
        return null;
      }
      
      public Point getLocation() { return (this.parent != null) ? this.parent.indexToLocation(this.indexInParent) : null; }
      
      public void setLocation(Point param2Point) {
        if (this.parent != null && this.parent.contains(param2Point))
          JList.AccessibleJList.this.this$0.ensureIndexIsVisible(this.indexInParent); 
      }
      
      public Rectangle getBounds() { return (this.parent != null) ? this.parent.getCellBounds(this.indexInParent, this.indexInParent) : null; }
      
      public void setBounds(Rectangle param2Rectangle) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          ((AccessibleComponent)accessibleContext1).setBounds(param2Rectangle); 
      }
      
      public Dimension getSize() {
        Rectangle rectangle = getBounds();
        return (rectangle != null) ? rectangle.getSize() : null;
      }
      
      public void setSize(Dimension param2Dimension) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).setSize(param2Dimension);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.setSize(param2Dimension); 
        } 
      }
      
      public Accessible getAccessibleAt(Point param2Point) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 instanceof AccessibleComponent) ? ((AccessibleComponent)accessibleContext1).getAccessibleAt(param2Point) : null;
      }
      
      public boolean isFocusTraversable() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent)
          return ((AccessibleComponent)accessibleContext1).isFocusTraversable(); 
        Component component1 = getCurrentComponent();
        return (component1 != null) ? component1.isFocusTraversable() : 0;
      }
      
      public void requestFocus() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).requestFocus();
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.requestFocus(); 
        } 
      }
      
      public void addFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).addFocusListener(param2FocusListener);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.addFocusListener(param2FocusListener); 
        } 
      }
      
      public void removeFocusListener(FocusListener param2FocusListener) {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        if (accessibleContext1 instanceof AccessibleComponent) {
          ((AccessibleComponent)accessibleContext1).removeFocusListener(param2FocusListener);
        } else {
          Component component1 = getCurrentComponent();
          if (component1 != null)
            component1.removeFocusListener(param2FocusListener); 
        } 
      }
      
      public AccessibleIcon[] getAccessibleIcon() {
        AccessibleContext accessibleContext1 = getCurrentAccessibleContext();
        return (accessibleContext1 != null) ? accessibleContext1.getAccessibleIcon() : null;
      }
    }
    
    private class ActionableAccessibleJListChild extends JList<E>.AccessibleJList.AccessibleJListChild implements AccessibleAction {
      ActionableAccessibleJListChild(JList<E> param2JList, int param2Int) { super(JList.AccessibleJList.this, param2JList, param2Int); }
      
      public AccessibleAction getAccessibleAction() {
        AccessibleContext accessibleContext = getCurrentAccessibleContext();
        if (accessibleContext == null)
          return null; 
        AccessibleAction accessibleAction = accessibleContext.getAccessibleAction();
        return (accessibleAction != null) ? accessibleAction : this;
      }
      
      public boolean doAccessibleAction(int param2Int) {
        if (param2Int == 0) {
          JList.AccessibleJList.this.this$0.setSelectedIndex(this.indexInParent);
          return true;
        } 
        return false;
      }
      
      public String getAccessibleActionDescription(int param2Int) { return (param2Int == 0) ? UIManager.getString("AbstractButton.clickText") : null; }
      
      public int getAccessibleActionCount() { return 1; }
    }
  }
  
  public static final class DropLocation extends TransferHandler.DropLocation {
    private final int index;
    
    private final boolean isInsert;
    
    private DropLocation(Point param1Point, int param1Int, boolean param1Boolean) {
      super(param1Point);
      this.index = param1Int;
      this.isInsert = param1Boolean;
    }
    
    public int getIndex() { return this.index; }
    
    public boolean isInsert() { return this.isInsert; }
    
    public String toString() { return getClass().getName() + "[dropPoint=" + getDropPoint() + ",index=" + this.index + ",insert=" + this.isInsert + "]"; }
  }
  
  private class ListSelectionHandler implements ListSelectionListener, Serializable {
    private ListSelectionHandler() {}
    
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) { JList.this.fireSelectionValueChanged(param1ListSelectionEvent.getFirstIndex(), param1ListSelectionEvent.getLastIndex(), param1ListSelectionEvent.getValueIsAdjusting()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */