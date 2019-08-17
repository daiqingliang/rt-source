package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import sun.awt.AWTAccessor;

public class BasicComboPopup extends JPopupMenu implements ComboPopup {
  static final ListModel EmptyListModel = new EmptyListModelClass(null);
  
  private static Border LIST_BORDER = new LineBorder(Color.BLACK, 1);
  
  protected JComboBox comboBox;
  
  protected JList list;
  
  protected JScrollPane scroller;
  
  protected boolean valueIsAdjusting = false;
  
  private Handler handler;
  
  protected MouseMotionListener mouseMotionListener;
  
  protected MouseListener mouseListener;
  
  protected KeyListener keyListener;
  
  protected ListSelectionListener listSelectionListener;
  
  protected MouseListener listMouseListener;
  
  protected MouseMotionListener listMouseMotionListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected ListDataListener listDataListener;
  
  protected ItemListener itemListener;
  
  private MouseWheelListener scrollerMouseWheelListener;
  
  protected Timer autoscrollTimer;
  
  protected boolean hasEntered = false;
  
  protected boolean isAutoScrolling = false;
  
  protected int scrollDirection = 0;
  
  protected static final int SCROLL_UP = 0;
  
  protected static final int SCROLL_DOWN = 1;
  
  public void show() {
    this.comboBox.firePopupMenuWillBecomeVisible();
    setListSelection(this.comboBox.getSelectedIndex());
    Point point = getPopupLocation();
    show(this.comboBox, point.x, point.y);
  }
  
  public void hide() {
    MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
    for (byte b = 0; b < arrayOfMenuElement.length; b++) {
      if (arrayOfMenuElement[b] == this) {
        menuSelectionManager.clearSelectedPath();
        break;
      } 
    } 
    if (arrayOfMenuElement.length > 0)
      this.comboBox.repaint(); 
  }
  
  public JList getList() { return this.list; }
  
  public MouseListener getMouseListener() {
    if (this.mouseListener == null)
      this.mouseListener = createMouseListener(); 
    return this.mouseListener;
  }
  
  public MouseMotionListener getMouseMotionListener() {
    if (this.mouseMotionListener == null)
      this.mouseMotionListener = createMouseMotionListener(); 
    return this.mouseMotionListener;
  }
  
  public KeyListener getKeyListener() {
    if (this.keyListener == null)
      this.keyListener = createKeyListener(); 
    return this.keyListener;
  }
  
  public void uninstallingUI() {
    if (this.propertyChangeListener != null)
      this.comboBox.removePropertyChangeListener(this.propertyChangeListener); 
    if (this.itemListener != null)
      this.comboBox.removeItemListener(this.itemListener); 
    uninstallComboBoxModelListeners(this.comboBox.getModel());
    uninstallKeyboardActions();
    uninstallListListeners();
    uninstallScrollerListeners();
    this.list.setModel(EmptyListModel);
  }
  
  protected void uninstallComboBoxModelListeners(ComboBoxModel paramComboBoxModel) {
    if (paramComboBoxModel != null && this.listDataListener != null)
      paramComboBoxModel.removeListDataListener(this.listDataListener); 
  }
  
  protected void uninstallKeyboardActions() {}
  
  public BasicComboPopup(JComboBox paramJComboBox) {
    setName("ComboPopup.popup");
    this.comboBox = paramJComboBox;
    setLightWeightPopupEnabled(this.comboBox.isLightWeightPopupEnabled());
    this.list = createList();
    this.list.setName("ComboBox.list");
    configureList();
    this.scroller = createScroller();
    this.scroller.setName("ComboBox.scrollPane");
    configureScroller();
    configurePopup();
    installComboBoxListeners();
    installKeyboardActions();
  }
  
  protected void firePopupMenuWillBecomeVisible() {
    if (this.scrollerMouseWheelListener != null)
      this.comboBox.addMouseWheelListener(this.scrollerMouseWheelListener); 
    super.firePopupMenuWillBecomeVisible();
  }
  
  protected void firePopupMenuWillBecomeInvisible() {
    if (this.scrollerMouseWheelListener != null)
      this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener); 
    super.firePopupMenuWillBecomeInvisible();
    this.comboBox.firePopupMenuWillBecomeInvisible();
  }
  
  protected void firePopupMenuCanceled() {
    if (this.scrollerMouseWheelListener != null)
      this.comboBox.removeMouseWheelListener(this.scrollerMouseWheelListener); 
    super.firePopupMenuCanceled();
    this.comboBox.firePopupMenuCanceled();
  }
  
  protected MouseListener createMouseListener() { return getHandler(); }
  
  protected MouseMotionListener createMouseMotionListener() { return getHandler(); }
  
  protected KeyListener createKeyListener() { return null; }
  
  protected ListSelectionListener createListSelectionListener() { return null; }
  
  protected ListDataListener createListDataListener() { return null; }
  
  protected MouseListener createListMouseListener() { return getHandler(); }
  
  protected MouseMotionListener createListMouseMotionListener() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  protected ItemListener createItemListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected JList createList() { return new JList(this.comboBox.getModel()) {
        public void processMouseEvent(MouseEvent param1MouseEvent) {
          if (BasicGraphicsUtils.isMenuShortcutKeyDown(param1MouseEvent)) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            MouseEvent mouseEvent = new MouseEvent((Component)param1MouseEvent.getSource(), param1MouseEvent.getID(), param1MouseEvent.getWhen(), param1MouseEvent.getModifiers() ^ toolkit.getMenuShortcutKeyMask(), param1MouseEvent.getX(), param1MouseEvent.getY(), param1MouseEvent.getXOnScreen(), param1MouseEvent.getYOnScreen(), param1MouseEvent.getClickCount(), param1MouseEvent.isPopupTrigger(), 0);
            AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
            mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(param1MouseEvent));
            param1MouseEvent = mouseEvent;
          } 
          super.processMouseEvent(param1MouseEvent);
        }
      }; }
  
  protected void configureList() {
    this.list.setFont(this.comboBox.getFont());
    this.list.setForeground(this.comboBox.getForeground());
    this.list.setBackground(this.comboBox.getBackground());
    this.list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
    this.list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
    this.list.setBorder(null);
    this.list.setCellRenderer(this.comboBox.getRenderer());
    this.list.setFocusable(false);
    this.list.setSelectionMode(0);
    setListSelection(this.comboBox.getSelectedIndex());
    installListListeners();
  }
  
  protected void installListListeners() {
    if ((this.listMouseListener = createListMouseListener()) != null)
      this.list.addMouseListener(this.listMouseListener); 
    if ((this.listMouseMotionListener = createListMouseMotionListener()) != null)
      this.list.addMouseMotionListener(this.listMouseMotionListener); 
    if ((this.listSelectionListener = createListSelectionListener()) != null)
      this.list.addListSelectionListener(this.listSelectionListener); 
  }
  
  void uninstallListListeners() {
    if (this.listMouseListener != null) {
      this.list.removeMouseListener(this.listMouseListener);
      this.listMouseListener = null;
    } 
    if (this.listMouseMotionListener != null) {
      this.list.removeMouseMotionListener(this.listMouseMotionListener);
      this.listMouseMotionListener = null;
    } 
    if (this.listSelectionListener != null) {
      this.list.removeListSelectionListener(this.listSelectionListener);
      this.listSelectionListener = null;
    } 
    this.handler = null;
  }
  
  protected JScrollPane createScroller() {
    JScrollPane jScrollPane = new JScrollPane(this.list, 20, 31);
    jScrollPane.setHorizontalScrollBar(null);
    return jScrollPane;
  }
  
  protected void configureScroller() {
    this.scroller.setFocusable(false);
    this.scroller.getVerticalScrollBar().setFocusable(false);
    this.scroller.setBorder(null);
    installScrollerListeners();
  }
  
  protected void configurePopup() {
    setLayout(new BoxLayout(this, 1));
    setBorderPainted(true);
    setBorder(LIST_BORDER);
    setOpaque(false);
    add(this.scroller);
    setDoubleBuffered(true);
    setFocusable(false);
  }
  
  private void installScrollerListeners() {
    this.scrollerMouseWheelListener = getHandler();
    if (this.scrollerMouseWheelListener != null)
      this.scroller.addMouseWheelListener(this.scrollerMouseWheelListener); 
  }
  
  private void uninstallScrollerListeners() {
    if (this.scrollerMouseWheelListener != null) {
      this.scroller.removeMouseWheelListener(this.scrollerMouseWheelListener);
      this.scrollerMouseWheelListener = null;
    } 
  }
  
  protected void installComboBoxListeners() {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.comboBox.addPropertyChangeListener(this.propertyChangeListener); 
    if ((this.itemListener = createItemListener()) != null)
      this.comboBox.addItemListener(this.itemListener); 
    installComboBoxModelListeners(this.comboBox.getModel());
  }
  
  protected void installComboBoxModelListeners(ComboBoxModel paramComboBoxModel) {
    if (paramComboBoxModel != null && (this.listDataListener = createListDataListener()) != null)
      paramComboBoxModel.addListDataListener(this.listDataListener); 
  }
  
  protected void installKeyboardActions() {}
  
  public boolean isFocusTraversable() { return false; }
  
  protected void startAutoScrolling(int paramInt) {
    if (this.isAutoScrolling)
      this.autoscrollTimer.stop(); 
    this.isAutoScrolling = true;
    if (paramInt == 0) {
      this.scrollDirection = 0;
      Point point = SwingUtilities.convertPoint(this.scroller, new Point(1, 1), this.list);
      int i = this.list.locationToIndex(point);
      this.list.setSelectedIndex(i);
      this.autoscrollTimer = new Timer(100, new AutoScrollActionHandler(this, 0));
    } else if (paramInt == 1) {
      this.scrollDirection = 1;
      Dimension dimension = this.scroller.getSize();
      Point point = SwingUtilities.convertPoint(this.scroller, new Point(1, dimension.height - 1 - 2), this.list);
      int i = this.list.locationToIndex(point);
      this.list.setSelectedIndex(i);
      this.autoscrollTimer = new Timer(100, new AutoScrollActionHandler(this, 1));
    } 
    this.autoscrollTimer.start();
  }
  
  protected void stopAutoScrolling() {
    this.isAutoScrolling = false;
    if (this.autoscrollTimer != null) {
      this.autoscrollTimer.stop();
      this.autoscrollTimer = null;
    } 
  }
  
  protected void autoScrollUp() {
    int i = this.list.getSelectedIndex();
    if (i > 0) {
      this.list.setSelectedIndex(i - 1);
      this.list.ensureIndexIsVisible(i - 1);
    } 
  }
  
  protected void autoScrollDown() {
    int i = this.list.getSelectedIndex();
    int j = this.list.getModel().getSize() - 1;
    if (i < j) {
      this.list.setSelectedIndex(i + 1);
      this.list.ensureIndexIsVisible(i + 1);
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    AccessibleContext accessibleContext = super.getAccessibleContext();
    accessibleContext.setAccessibleParent(this.comboBox);
    return accessibleContext;
  }
  
  protected void delegateFocus(MouseEvent paramMouseEvent) {
    if (this.comboBox.isEditable()) {
      Component component = this.comboBox.getEditor().getEditorComponent();
      if (!(component instanceof JComponent) || ((JComponent)component).isRequestFocusEnabled())
        component.requestFocus(); 
    } else if (this.comboBox.isRequestFocusEnabled()) {
      this.comboBox.requestFocus();
    } 
  }
  
  protected void togglePopup() {
    if (isVisible()) {
      hide();
    } else {
      show();
    } 
  }
  
  private void setListSelection(int paramInt) {
    if (paramInt == -1) {
      this.list.clearSelection();
    } else {
      this.list.setSelectedIndex(paramInt);
      this.list.ensureIndexIsVisible(paramInt);
    } 
  }
  
  protected MouseEvent convertMouseEvent(MouseEvent paramMouseEvent) {
    Point point = SwingUtilities.convertPoint((Component)paramMouseEvent.getSource(), paramMouseEvent.getPoint(), this.list);
    MouseEvent mouseEvent = new MouseEvent((Component)paramMouseEvent.getSource(), paramMouseEvent.getID(), paramMouseEvent.getWhen(), paramMouseEvent.getModifiers(), point.x, point.y, paramMouseEvent.getXOnScreen(), paramMouseEvent.getYOnScreen(), paramMouseEvent.getClickCount(), paramMouseEvent.isPopupTrigger(), 0);
    AWTAccessor.MouseEventAccessor mouseEventAccessor = AWTAccessor.getMouseEventAccessor();
    mouseEventAccessor.setCausedByTouchEvent(mouseEvent, mouseEventAccessor.isCausedByTouchEvent(paramMouseEvent));
    return mouseEvent;
  }
  
  protected int getPopupHeightForRowCount(int paramInt) {
    int i = Math.min(paramInt, this.comboBox.getItemCount());
    int j = 0;
    ListCellRenderer listCellRenderer = this.list.getCellRenderer();
    Object object = null;
    for (byte b = 0; b < i; b++) {
      object = this.list.getModel().getElementAt(b);
      Component component = listCellRenderer.getListCellRendererComponent(this.list, object, b, false, false);
      j += (component.getPreferredSize()).height;
    } 
    if (j == 0)
      j = this.comboBox.getHeight(); 
    Border border = this.scroller.getViewportBorder();
    if (border != null) {
      Insets insets = border.getBorderInsets(null);
      j += insets.top + insets.bottom;
    } 
    border = this.scroller.getBorder();
    if (border != null) {
      Insets insets = border.getBorderInsets(null);
      j += insets.top + insets.bottom;
    } 
    return j;
  }
  
  protected Rectangle computePopupBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Rectangle rectangle1;
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    GraphicsConfiguration graphicsConfiguration = this.comboBox.getGraphicsConfiguration();
    Point point = new Point();
    SwingUtilities.convertPointFromScreen(point, this.comboBox);
    if (graphicsConfiguration != null) {
      Insets insets = toolkit.getScreenInsets(graphicsConfiguration);
      rectangle1 = graphicsConfiguration.getBounds();
      rectangle1.width -= insets.left + insets.right;
      rectangle1.height -= insets.top + insets.bottom;
      rectangle1.x += point.x + insets.left;
      rectangle1.y += point.y + insets.top;
    } else {
      rectangle1 = new Rectangle(point, toolkit.getScreenSize());
    } 
    Rectangle rectangle2 = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt2 + paramInt4 > rectangle1.y + rectangle1.height && paramInt4 < rectangle1.height)
      rectangle2.y = -rectangle2.height; 
    return rectangle2;
  }
  
  private Point getPopupLocation() {
    Dimension dimension1 = this.comboBox.getSize();
    Insets insets = getInsets();
    dimension1.setSize(dimension1.width - insets.right + insets.left, getPopupHeightForRowCount(this.comboBox.getMaximumRowCount()));
    Rectangle rectangle = computePopupBounds(0, (this.comboBox.getBounds()).height, dimension1.width, dimension1.height);
    Dimension dimension2 = rectangle.getSize();
    Point point = rectangle.getLocation();
    this.scroller.setMaximumSize(dimension2);
    this.scroller.setPreferredSize(dimension2);
    this.scroller.setMinimumSize(dimension2);
    this.list.revalidate();
    return point;
  }
  
  protected void updateListBoxSelectionForEvent(MouseEvent paramMouseEvent, boolean paramBoolean) {
    Point point = paramMouseEvent.getPoint();
    if (this.list == null)
      return; 
    int i = this.list.locationToIndex(point);
    if (i == -1)
      if (point.y < 0) {
        i = 0;
      } else {
        i = this.comboBox.getModel().getSize() - 1;
      }  
    if (this.list.getSelectedIndex() != i) {
      this.list.setSelectedIndex(i);
      if (paramBoolean)
        this.list.ensureIndexIsVisible(i); 
    } 
  }
  
  private class AutoScrollActionHandler implements ActionListener {
    private int direction;
    
    AutoScrollActionHandler(int param1Int) { this.direction = param1Int; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.direction == 0) {
        BasicComboPopup.this.autoScrollUp();
      } else {
        BasicComboPopup.this.autoScrollDown();
      } 
    }
  }
  
  private static class EmptyListModelClass extends Object implements ListModel<Object>, Serializable {
    private EmptyListModelClass() {}
    
    public int getSize() { return 0; }
    
    public Object getElementAt(int param1Int) { return null; }
    
    public void addListDataListener(ListDataListener param1ListDataListener) {}
    
    public void removeListDataListener(ListDataListener param1ListDataListener) {}
  }
  
  private class Handler implements ItemListener, MouseListener, MouseMotionListener, MouseWheelListener, PropertyChangeListener, Serializable {
    private Handler() {}
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicComboPopup.this.list)
        return; 
      if (!SwingUtilities.isLeftMouseButton(param1MouseEvent) || !BasicComboPopup.this.comboBox.isEnabled())
        return; 
      if (BasicComboPopup.this.comboBox.isEditable()) {
        Component component = BasicComboPopup.this.comboBox.getEditor().getEditorComponent();
        if (!(component instanceof JComponent) || ((JComponent)component).isRequestFocusEnabled())
          component.requestFocus(); 
      } else if (BasicComboPopup.this.comboBox.isRequestFocusEnabled()) {
        BasicComboPopup.this.comboBox.requestFocus();
      } 
      BasicComboPopup.this.togglePopup();
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicComboPopup.this.list) {
        if (BasicComboPopup.this.list.getModel().getSize() > 0) {
          if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex())
            BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue()); 
          BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
        } 
        BasicComboPopup.this.comboBox.setPopupVisible(false);
        if (BasicComboPopup.this.comboBox.isEditable() && BasicComboPopup.this.comboBox.getEditor() != null)
          BasicComboPopup.this.comboBox.configureEditor(BasicComboPopup.this.comboBox.getEditor(), BasicComboPopup.this.comboBox.getSelectedItem()); 
        return;
      } 
      Component component = (Component)param1MouseEvent.getSource();
      Dimension dimension = component.getSize();
      Rectangle rectangle = new Rectangle(0, 0, dimension.width - 1, dimension.height - 1);
      if (!rectangle.contains(param1MouseEvent.getPoint())) {
        MouseEvent mouseEvent = BasicComboPopup.this.convertMouseEvent(param1MouseEvent);
        Point point = mouseEvent.getPoint();
        Rectangle rectangle1 = new Rectangle();
        BasicComboPopup.this.list.computeVisibleRect(rectangle1);
        if (rectangle1.contains(point)) {
          if (BasicComboPopup.this.comboBox.getSelectedIndex() == BasicComboPopup.this.list.getSelectedIndex())
            BasicComboPopup.this.comboBox.getEditor().setItem(BasicComboPopup.this.list.getSelectedValue()); 
          BasicComboPopup.this.comboBox.setSelectedIndex(BasicComboPopup.this.list.getSelectedIndex());
        } 
        BasicComboPopup.this.comboBox.setPopupVisible(false);
      } 
      BasicComboPopup.this.hasEntered = false;
      BasicComboPopup.this.stopAutoScrolling();
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicComboPopup.this.list) {
        Point point = param1MouseEvent.getPoint();
        Rectangle rectangle = new Rectangle();
        BasicComboPopup.this.list.computeVisibleRect(rectangle);
        if (rectangle.contains(point))
          BasicComboPopup.this.updateListBoxSelectionForEvent(param1MouseEvent, false); 
      } 
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == BasicComboPopup.this.list)
        return; 
      if (BasicComboPopup.this.isVisible()) {
        MouseEvent mouseEvent = BasicComboPopup.this.convertMouseEvent(param1MouseEvent);
        Rectangle rectangle = new Rectangle();
        BasicComboPopup.this.list.computeVisibleRect(rectangle);
        if ((mouseEvent.getPoint()).y >= rectangle.y && (mouseEvent.getPoint()).y <= rectangle.y + rectangle.height - 1) {
          BasicComboPopup.this.hasEntered = true;
          if (BasicComboPopup.this.isAutoScrolling)
            BasicComboPopup.this.stopAutoScrolling(); 
          Point point = mouseEvent.getPoint();
          if (rectangle.contains(point))
            BasicComboPopup.this.updateListBoxSelectionForEvent(mouseEvent, false); 
        } else if (BasicComboPopup.this.hasEntered) {
          byte b = ((mouseEvent.getPoint()).y < rectangle.y) ? 0 : 1;
          if (BasicComboPopup.this.isAutoScrolling && BasicComboPopup.this.scrollDirection != b) {
            BasicComboPopup.this.stopAutoScrolling();
            BasicComboPopup.this.startAutoScrolling(b);
          } else if (!BasicComboPopup.this.isAutoScrolling) {
            BasicComboPopup.this.startAutoScrolling(b);
          } 
        } else if ((param1MouseEvent.getPoint()).y < 0) {
          BasicComboPopup.this.hasEntered = true;
          BasicComboPopup.this.startAutoScrolling(0);
        } 
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      JComboBox jComboBox = (JComboBox)param1PropertyChangeEvent.getSource();
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "model") {
        ComboBoxModel comboBoxModel1 = (ComboBoxModel)param1PropertyChangeEvent.getOldValue();
        ComboBoxModel comboBoxModel2 = (ComboBoxModel)param1PropertyChangeEvent.getNewValue();
        BasicComboPopup.this.uninstallComboBoxModelListeners(comboBoxModel1);
        BasicComboPopup.this.installComboBoxModelListeners(comboBoxModel2);
        BasicComboPopup.this.list.setModel(comboBoxModel2);
        if (BasicComboPopup.this.isVisible())
          BasicComboPopup.this.hide(); 
      } else if (str == "renderer") {
        BasicComboPopup.this.list.setCellRenderer(jComboBox.getRenderer());
        if (BasicComboPopup.this.isVisible())
          BasicComboPopup.this.hide(); 
      } else if (str == "componentOrientation") {
        ComponentOrientation componentOrientation = (ComponentOrientation)param1PropertyChangeEvent.getNewValue();
        JList jList = BasicComboPopup.this.getList();
        if (jList != null && jList.getComponentOrientation() != componentOrientation)
          jList.setComponentOrientation(componentOrientation); 
        if (BasicComboPopup.this.scroller != null && BasicComboPopup.this.scroller.getComponentOrientation() != componentOrientation)
          BasicComboPopup.this.scroller.setComponentOrientation(componentOrientation); 
        if (componentOrientation != BasicComboPopup.this.getComponentOrientation())
          BasicComboPopup.this.setComponentOrientation(componentOrientation); 
      } else if (str == "lightWeightPopupEnabled") {
        BasicComboPopup.this.setLightWeightPopupEnabled(jComboBox.isLightWeightPopupEnabled());
      } 
    }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) {
      if (param1ItemEvent.getStateChange() == 1) {
        JComboBox jComboBox = (JComboBox)param1ItemEvent.getSource();
        BasicComboPopup.this.setListSelection(jComboBox.getSelectedIndex());
      } else {
        BasicComboPopup.this.setListSelection(-1);
      } 
    }
    
    public void mouseWheelMoved(MouseWheelEvent param1MouseWheelEvent) { param1MouseWheelEvent.consume(); }
  }
  
  public class InvocationKeyHandler extends KeyAdapter {
    public void keyReleased(KeyEvent param1KeyEvent) {}
  }
  
  protected class InvocationMouseHandler extends MouseAdapter {
    public void mousePressed(MouseEvent param1MouseEvent) { BasicComboPopup.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicComboPopup.this.getHandler().mouseReleased(param1MouseEvent); }
  }
  
  protected class InvocationMouseMotionHandler extends MouseMotionAdapter {
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicComboPopup.this.getHandler().mouseDragged(param1MouseEvent); }
  }
  
  protected class ItemHandler implements ItemListener {
    public void itemStateChanged(ItemEvent param1ItemEvent) { BasicComboPopup.this.getHandler().itemStateChanged(param1ItemEvent); }
  }
  
  public class ListDataHandler implements ListDataListener {
    public void contentsChanged(ListDataEvent param1ListDataEvent) {}
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) {}
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) {}
  }
  
  protected class ListMouseHandler extends MouseAdapter {
    public void mousePressed(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicComboPopup.this.getHandler().mouseReleased(param1MouseEvent); }
  }
  
  protected class ListMouseMotionHandler extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicComboPopup.this.getHandler().mouseMoved(param1MouseEvent); }
  }
  
  protected class ListSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent param1ListSelectionEvent) {}
  }
  
  protected class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicComboPopup.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */