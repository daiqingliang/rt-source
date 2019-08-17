package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicComboBoxUI extends ComboBoxUI {
  protected JComboBox comboBox;
  
  protected boolean hasFocus = false;
  
  private boolean isTableCellEditor = false;
  
  private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
  
  protected JList listBox;
  
  protected CellRendererPane currentValuePane = new CellRendererPane();
  
  protected ComboPopup popup;
  
  protected Component editor;
  
  protected JButton arrowButton;
  
  protected KeyListener keyListener;
  
  protected FocusListener focusListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected ItemListener itemListener;
  
  protected MouseListener popupMouseListener;
  
  protected MouseMotionListener popupMouseMotionListener;
  
  protected KeyListener popupKeyListener;
  
  protected ListDataListener listDataListener;
  
  private Handler handler;
  
  private long timeFactor = 1000L;
  
  private long lastTime = 0L;
  
  private long time = 0L;
  
  JComboBox.KeySelectionManager keySelectionManager;
  
  protected boolean isMinimumSizeDirty = true;
  
  protected Dimension cachedMinimumSize = new Dimension(0, 0);
  
  private boolean isDisplaySizeDirty = true;
  
  private Dimension cachedDisplaySize = new Dimension(0, 0);
  
  private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY = new StringBuffer("DefaultListCellRendererKey");
  
  static final StringBuffer HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");
  
  private boolean sameBaseline;
  
  protected boolean squareButton = true;
  
  protected Insets padding;
  
  private static ListCellRenderer getDefaultListCellRenderer() {
    ListCellRenderer listCellRenderer = (ListCellRenderer)AppContext.getAppContext().get(COMBO_UI_LIST_CELL_RENDERER_KEY);
    if (listCellRenderer == null) {
      listCellRenderer = new DefaultListCellRenderer();
      AppContext.getAppContext().put(COMBO_UI_LIST_CELL_RENDERER_KEY, new DefaultListCellRenderer());
    } 
    return listCellRenderer;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("hidePopup"));
    paramLazyActionMap.put(new Actions("pageDownPassThrough"));
    paramLazyActionMap.put(new Actions("pageUpPassThrough"));
    paramLazyActionMap.put(new Actions("homePassThrough"));
    paramLazyActionMap.put(new Actions("endPassThrough"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectNext2"));
    paramLazyActionMap.put(new Actions("togglePopup"));
    paramLazyActionMap.put(new Actions("spacePopup"));
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectPrevious2"));
    paramLazyActionMap.put(new Actions("enterPressed"));
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicComboBoxUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.isMinimumSizeDirty = true;
    this.comboBox = (JComboBox)paramJComponent;
    installDefaults();
    this.popup = createPopup();
    this.listBox = this.popup.getList();
    Boolean bool = (Boolean)paramJComponent.getClientProperty("JComboBox.isTableCellEditor");
    if (bool != null)
      this.isTableCellEditor = bool.equals(Boolean.TRUE); 
    if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource)
      this.comboBox.setRenderer(createRenderer()); 
    if (this.comboBox.getEditor() == null || this.comboBox.getEditor() instanceof UIResource)
      this.comboBox.setEditor(createEditor()); 
    installListeners();
    installComponents();
    this.comboBox.setLayout(createLayoutManager());
    this.comboBox.setRequestFocusEnabled(true);
    installKeyboardActions();
    this.comboBox.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
    if (this.keySelectionManager == null || this.keySelectionManager instanceof UIResource)
      this.keySelectionManager = new DefaultKeySelectionManager(); 
    this.comboBox.setKeySelectionManager(this.keySelectionManager);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    setPopupVisible(this.comboBox, false);
    this.popup.uninstallingUI();
    uninstallKeyboardActions();
    this.comboBox.setLayout(null);
    uninstallComponents();
    uninstallListeners();
    uninstallDefaults();
    if (this.comboBox.getRenderer() == null || this.comboBox.getRenderer() instanceof UIResource)
      this.comboBox.setRenderer(null); 
    ComboBoxEditor comboBoxEditor = this.comboBox.getEditor();
    if (comboBoxEditor instanceof UIResource) {
      if (comboBoxEditor.getEditorComponent().hasFocus())
        this.comboBox.requestFocusInWindow(); 
      this.comboBox.setEditor(null);
    } 
    if (this.keySelectionManager instanceof UIResource)
      this.comboBox.setKeySelectionManager(null); 
    this.handler = null;
    this.keyListener = null;
    this.focusListener = null;
    this.listDataListener = null;
    this.propertyChangeListener = null;
    this.popup = null;
    this.listBox = null;
    this.comboBox = null;
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
    LookAndFeel.installBorder(this.comboBox, "ComboBox.border");
    LookAndFeel.installProperty(this.comboBox, "opaque", Boolean.TRUE);
    Long long = (Long)UIManager.get("ComboBox.timeFactor");
    this.timeFactor = (long == null) ? 1000L : long.longValue();
    Boolean bool = (Boolean)UIManager.get("ComboBox.squareButton");
    this.squareButton = (bool == null) ? true : bool.booleanValue();
    this.padding = UIManager.getInsets("ComboBox.padding");
  }
  
  protected void installListeners() {
    if ((this.itemListener = createItemListener()) != null)
      this.comboBox.addItemListener(this.itemListener); 
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.comboBox.addPropertyChangeListener(this.propertyChangeListener); 
    if ((this.keyListener = createKeyListener()) != null)
      this.comboBox.addKeyListener(this.keyListener); 
    if ((this.focusListener = createFocusListener()) != null)
      this.comboBox.addFocusListener(this.focusListener); 
    if ((this.popupMouseListener = this.popup.getMouseListener()) != null)
      this.comboBox.addMouseListener(this.popupMouseListener); 
    if ((this.popupMouseMotionListener = this.popup.getMouseMotionListener()) != null)
      this.comboBox.addMouseMotionListener(this.popupMouseMotionListener); 
    if ((this.popupKeyListener = this.popup.getKeyListener()) != null)
      this.comboBox.addKeyListener(this.popupKeyListener); 
    if (this.comboBox.getModel() != null && (this.listDataListener = createListDataListener()) != null)
      this.comboBox.getModel().addListDataListener(this.listDataListener); 
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.installColorsAndFont(this.comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
    LookAndFeel.uninstallBorder(this.comboBox);
  }
  
  protected void uninstallListeners() {
    if (this.keyListener != null)
      this.comboBox.removeKeyListener(this.keyListener); 
    if (this.itemListener != null)
      this.comboBox.removeItemListener(this.itemListener); 
    if (this.propertyChangeListener != null)
      this.comboBox.removePropertyChangeListener(this.propertyChangeListener); 
    if (this.focusListener != null)
      this.comboBox.removeFocusListener(this.focusListener); 
    if (this.popupMouseListener != null)
      this.comboBox.removeMouseListener(this.popupMouseListener); 
    if (this.popupMouseMotionListener != null)
      this.comboBox.removeMouseMotionListener(this.popupMouseMotionListener); 
    if (this.popupKeyListener != null)
      this.comboBox.removeKeyListener(this.popupKeyListener); 
    if (this.comboBox.getModel() != null && this.listDataListener != null)
      this.comboBox.getModel().removeListDataListener(this.listDataListener); 
  }
  
  protected ComboPopup createPopup() { return new BasicComboPopup(this.comboBox); }
  
  protected KeyListener createKeyListener() { return getHandler(); }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  protected ListDataListener createListDataListener() { return getHandler(); }
  
  protected ItemListener createItemListener() { return null; }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  protected LayoutManager createLayoutManager() { return getHandler(); }
  
  protected ListCellRenderer createRenderer() { return new BasicComboBoxRenderer.UIResource(); }
  
  protected ComboBoxEditor createEditor() { return new BasicComboBoxEditor.UIResource(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  private void updateToolTipTextForChildren() {
    Component[] arrayOfComponent = this.comboBox.getComponents();
    for (byte b = 0; b < arrayOfComponent.length; b++) {
      if (arrayOfComponent[b] instanceof JComponent)
        ((JComponent)arrayOfComponent[b]).setToolTipText(this.comboBox.getToolTipText()); 
    } 
  }
  
  protected void installComponents() {
    this.arrowButton = createArrowButton();
    if (this.arrowButton != null) {
      this.comboBox.add(this.arrowButton);
      configureArrowButton();
    } 
    if (this.comboBox.isEditable())
      addEditor(); 
    this.comboBox.add(this.currentValuePane);
  }
  
  protected void uninstallComponents() {
    if (this.arrowButton != null)
      unconfigureArrowButton(); 
    if (this.editor != null)
      unconfigureEditor(); 
    this.comboBox.removeAll();
    this.arrowButton = null;
  }
  
  public void addEditor() {
    removeEditor();
    this.editor = this.comboBox.getEditor().getEditorComponent();
    if (this.editor != null) {
      configureEditor();
      this.comboBox.add(this.editor);
      if (this.comboBox.isFocusOwner())
        this.editor.requestFocusInWindow(); 
    } 
  }
  
  public void removeEditor() {
    if (this.editor != null) {
      unconfigureEditor();
      this.comboBox.remove(this.editor);
      this.editor = null;
    } 
  }
  
  protected void configureEditor() {
    this.editor.setEnabled(this.comboBox.isEnabled());
    this.editor.setFocusable(this.comboBox.isFocusable());
    this.editor.setFont(this.comboBox.getFont());
    if (this.focusListener != null)
      this.editor.addFocusListener(this.focusListener); 
    this.editor.addFocusListener(getHandler());
    this.comboBox.getEditor().addActionListener(getHandler());
    if (this.editor instanceof JComponent) {
      ((JComponent)this.editor).putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
      ((JComponent)this.editor).setInheritsPopupMenu(true);
    } 
    this.comboBox.configureEditor(this.comboBox.getEditor(), this.comboBox.getSelectedItem());
    this.editor.addPropertyChangeListener(this.propertyChangeListener);
  }
  
  protected void unconfigureEditor() {
    if (this.focusListener != null)
      this.editor.removeFocusListener(this.focusListener); 
    this.editor.removePropertyChangeListener(this.propertyChangeListener);
    this.editor.removeFocusListener(getHandler());
    this.comboBox.getEditor().removeActionListener(getHandler());
  }
  
  public void configureArrowButton() {
    if (this.arrowButton != null) {
      this.arrowButton.setEnabled(this.comboBox.isEnabled());
      this.arrowButton.setFocusable(this.comboBox.isFocusable());
      this.arrowButton.setRequestFocusEnabled(false);
      this.arrowButton.addMouseListener(this.popup.getMouseListener());
      this.arrowButton.addMouseMotionListener(this.popup.getMouseMotionListener());
      this.arrowButton.resetKeyboardActions();
      this.arrowButton.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
      this.arrowButton.setInheritsPopupMenu(true);
    } 
  }
  
  public void unconfigureArrowButton() {
    if (this.arrowButton != null) {
      this.arrowButton.removeMouseListener(this.popup.getMouseListener());
      this.arrowButton.removeMouseMotionListener(this.popup.getMouseMotionListener());
    } 
  }
  
  protected JButton createArrowButton() {
    BasicArrowButton basicArrowButton = new BasicArrowButton(5, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
    basicArrowButton.setName("ComboBox.arrowButton");
    return basicArrowButton;
  }
  
  public boolean isPopupVisible(JComboBox paramJComboBox) { return this.popup.isVisible(); }
  
  public void setPopupVisible(JComboBox paramJComboBox, boolean paramBoolean) {
    if (paramBoolean) {
      this.popup.show();
    } else {
      this.popup.hide();
    } 
  }
  
  public boolean isFocusTraversable(JComboBox paramJComboBox) { return !this.comboBox.isEditable(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    this.hasFocus = this.comboBox.hasFocus();
    if (!this.comboBox.isEditable()) {
      Rectangle rectangle = rectangleForCurrentValue();
      paintCurrentValueBackground(paramGraphics, rectangle, this.hasFocus);
      paintCurrentValue(paramGraphics, rectangle, this.hasFocus);
    } 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    if (!this.isMinimumSizeDirty)
      return new Dimension(this.cachedMinimumSize); 
    Dimension dimension = getDisplaySize();
    Insets insets = getInsets();
    int i = dimension.height;
    int j = this.squareButton ? i : (this.arrowButton.getPreferredSize()).width;
    dimension.height += insets.top + insets.bottom;
    dimension.width += insets.left + insets.right + j;
    this.cachedMinimumSize.setSize(dimension.width, dimension.height);
    this.isMinimumSizeDirty = false;
    return new Dimension(dimension);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(32767, 32767); }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = -1;
    getDisplaySize();
    if (this.sameBaseline) {
      Insets insets = paramJComponent.getInsets();
      paramInt2 = paramInt2 - insets.top - insets.bottom;
      if (!this.comboBox.isEditable()) {
        ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
        if (listCellRenderer == null)
          listCellRenderer = new DefaultListCellRenderer(); 
        Object object1 = null;
        Object object2 = this.comboBox.getPrototypeDisplayValue();
        if (object2 != null) {
          object1 = object2;
        } else if (this.comboBox.getModel().getSize() > 0) {
          object1 = this.comboBox.getModel().getElementAt(0);
        } 
        Component component = listCellRenderer.getListCellRendererComponent(this.listBox, object1, -1, false, false);
        if (component instanceof JLabel) {
          JLabel jLabel = (JLabel)component;
          String str = jLabel.getText();
          if (str == null || str.isEmpty())
            jLabel.setText(" "); 
        } 
        if (component instanceof JComponent)
          component.setFont(this.comboBox.getFont()); 
        i = component.getBaseline(paramInt1, paramInt2);
      } else {
        i = this.editor.getBaseline(paramInt1, paramInt2);
      } 
      if (i > 0)
        i += insets.top; 
    } 
    return i;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    getDisplaySize();
    if (this.comboBox.isEditable())
      return this.editor.getBaselineResizeBehavior(); 
    if (this.sameBaseline) {
      ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
      if (listCellRenderer == null)
        listCellRenderer = new DefaultListCellRenderer(); 
      Object object1 = null;
      Object object2 = this.comboBox.getPrototypeDisplayValue();
      if (object2 != null) {
        object1 = object2;
      } else if (this.comboBox.getModel().getSize() > 0) {
        object1 = this.comboBox.getModel().getElementAt(0);
      } 
      if (object1 != null) {
        Component component = listCellRenderer.getListCellRendererComponent(this.listBox, object1, -1, false, false);
        return component.getBaselineResizeBehavior();
      } 
    } 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent) { return this.comboBox.isEditable() ? 2 : 1; }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt) {
    switch (paramInt) {
      case 0:
        if (this.popup instanceof Accessible) {
          AccessibleContext accessibleContext = ((Accessible)this.popup).getAccessibleContext();
          accessibleContext.setAccessibleParent(this.comboBox);
          return (Accessible)this.popup;
        } 
        break;
      case 1:
        if (this.comboBox.isEditable() && this.editor instanceof Accessible) {
          AccessibleContext accessibleContext = ((Accessible)this.editor).getAccessibleContext();
          accessibleContext.setAccessibleParent(this.comboBox);
          return (Accessible)this.editor;
        } 
        break;
    } 
    return null;
  }
  
  protected boolean isNavigationKey(int paramInt) { return (paramInt == 38 || paramInt == 40 || paramInt == 224 || paramInt == 225); }
  
  private boolean isNavigationKey(int paramInt1, int paramInt2) {
    InputMap inputMap = this.comboBox.getInputMap(1);
    KeyStroke keyStroke = KeyStroke.getKeyStroke(paramInt1, paramInt2);
    return (inputMap != null && inputMap.get(keyStroke) != null);
  }
  
  protected void selectNextPossibleValue() {
    int i;
    if (this.comboBox.isPopupVisible()) {
      i = this.listBox.getSelectedIndex();
    } else {
      i = this.comboBox.getSelectedIndex();
    } 
    if (i < this.comboBox.getModel().getSize() - 1) {
      this.listBox.setSelectedIndex(i + 1);
      this.listBox.ensureIndexIsVisible(i + 1);
      if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible()))
        this.comboBox.setSelectedIndex(i + 1); 
      this.comboBox.repaint();
    } 
  }
  
  protected void selectPreviousPossibleValue() {
    int i;
    if (this.comboBox.isPopupVisible()) {
      i = this.listBox.getSelectedIndex();
    } else {
      i = this.comboBox.getSelectedIndex();
    } 
    if (i > 0) {
      this.listBox.setSelectedIndex(i - 1);
      this.listBox.ensureIndexIsVisible(i - 1);
      if (!this.isTableCellEditor && (!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") || !this.comboBox.isPopupVisible()))
        this.comboBox.setSelectedIndex(i - 1); 
      this.comboBox.repaint();
    } 
  }
  
  protected void toggleOpenClose() { setPopupVisible(this.comboBox, !isPopupVisible(this.comboBox)); }
  
  protected Rectangle rectangleForCurrentValue() {
    int i = this.comboBox.getWidth();
    int j = this.comboBox.getHeight();
    Insets insets = getInsets();
    int k = j - insets.top + insets.bottom;
    if (this.arrowButton != null)
      k = this.arrowButton.getWidth(); 
    return BasicGraphicsUtils.isLeftToRight(this.comboBox) ? new Rectangle(insets.left, insets.top, i - insets.left + insets.right + k, j - insets.top + insets.bottom) : new Rectangle(insets.left + k, insets.top, i - insets.left + insets.right + k, j - insets.top + insets.bottom);
  }
  
  protected Insets getInsets() { return this.comboBox.getInsets(); }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    Component component;
    ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
    if (paramBoolean && !isPopupVisible(this.comboBox)) {
      component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, true, false);
    } else {
      component = listCellRenderer.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
      component.setBackground(UIManager.getColor("ComboBox.background"));
    } 
    component.setFont(this.comboBox.getFont());
    if (paramBoolean && !isPopupVisible(this.comboBox)) {
      component.setForeground(this.listBox.getSelectionForeground());
      component.setBackground(this.listBox.getSelectionBackground());
    } else if (this.comboBox.isEnabled()) {
      component.setForeground(this.comboBox.getForeground());
      component.setBackground(this.comboBox.getBackground());
    } else {
      component.setForeground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledForeground", null));
      component.setBackground(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
    } 
    boolean bool = false;
    if (component instanceof javax.swing.JPanel)
      bool = true; 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    if (this.padding != null) {
      i = paramRectangle.x + this.padding.left;
      j = paramRectangle.y + this.padding.top;
      k = paramRectangle.width - this.padding.left + this.padding.right;
      m = paramRectangle.height - this.padding.top + this.padding.bottom;
    } 
    this.currentValuePane.paintComponent(paramGraphics, component, this.comboBox, i, j, k, m, bool);
  }
  
  public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean) {
    Color color = paramGraphics.getColor();
    if (this.comboBox.isEnabled()) {
      paramGraphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.background", null));
    } else {
      paramGraphics.setColor(DefaultLookup.getColor(this.comboBox, this, "ComboBox.disabledBackground", null));
    } 
    paramGraphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    paramGraphics.setColor(color);
  }
  
  void repaintCurrentValue() {
    Rectangle rectangle = rectangleForCurrentValue();
    this.comboBox.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }
  
  protected Dimension getDefaultSize() {
    Dimension dimension = getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(this.listBox, " ", -1, false, false));
    return new Dimension(dimension.width, dimension.height);
  }
  
  protected Dimension getDisplaySize() {
    if (!this.isDisplaySizeDirty)
      return new Dimension(this.cachedDisplaySize); 
    Dimension dimension = new Dimension();
    ListCellRenderer listCellRenderer = this.comboBox.getRenderer();
    if (listCellRenderer == null)
      listCellRenderer = new DefaultListCellRenderer(); 
    this.sameBaseline = true;
    Object object = this.comboBox.getPrototypeDisplayValue();
    if (object != null) {
      dimension = getSizeForComponent(listCellRenderer.getListCellRendererComponent(this.listBox, object, -1, false, false));
    } else {
      ComboBoxModel comboBoxModel = this.comboBox.getModel();
      int i = comboBoxModel.getSize();
      int j = -1;
      if (i > 0) {
        for (byte b = 0; b < i; b++) {
          Object object1 = comboBoxModel.getElementAt(b);
          Component component = listCellRenderer.getListCellRendererComponent(this.listBox, object1, -1, false, false);
          Dimension dimension1 = getSizeForComponent(component);
          if (this.sameBaseline && object1 != null && (!(object1 instanceof String) || !"".equals(object1))) {
            int k = component.getBaseline(dimension1.width, dimension1.height);
            if (k == -1) {
              this.sameBaseline = false;
            } else if (j == -1) {
              j = k;
            } else if (j != k) {
              this.sameBaseline = false;
            } 
          } 
          dimension.width = Math.max(dimension.width, dimension1.width);
          dimension.height = Math.max(dimension.height, dimension1.height);
        } 
      } else {
        dimension = getDefaultSize();
        if (this.comboBox.isEditable())
          dimension.width = 100; 
      } 
    } 
    if (this.comboBox.isEditable()) {
      Dimension dimension1 = this.editor.getPreferredSize();
      dimension.width = Math.max(dimension.width, dimension1.width);
      dimension.height = Math.max(dimension.height, dimension1.height);
    } 
    if (this.padding != null) {
      dimension.width += this.padding.left + this.padding.right;
      dimension.height += this.padding.top + this.padding.bottom;
    } 
    this.cachedDisplaySize.setSize(dimension.width, dimension.height);
    this.isDisplaySizeDirty = false;
    return dimension;
  }
  
  protected Dimension getSizeForComponent(Component paramComponent) {
    this.currentValuePane.add(paramComponent);
    paramComponent.setFont(this.comboBox.getFont());
    Dimension dimension = paramComponent.getPreferredSize();
    this.currentValuePane.remove(paramComponent);
    return dimension;
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.comboBox, 1, inputMap);
    LazyActionMap.installLazyActionMap(this.comboBox, BasicComboBoxUI.class, "ComboBox.actionMap");
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(this.comboBox, this, "ComboBox.ancestorInputMap") : null; }
  
  boolean isTableCellEditor() { return this.isTableCellEditor; }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.comboBox, 1, null);
    SwingUtilities.replaceUIActionMap(this.comboBox, null);
  }
  
  private static class Actions extends UIAction {
    private static final String HIDE = "hidePopup";
    
    private static final String DOWN = "selectNext";
    
    private static final String DOWN_2 = "selectNext2";
    
    private static final String TOGGLE = "togglePopup";
    
    private static final String TOGGLE_2 = "spacePopup";
    
    private static final String UP = "selectPrevious";
    
    private static final String UP_2 = "selectPrevious2";
    
    private static final String ENTER = "enterPressed";
    
    private static final String PAGE_DOWN = "pageDownPassThrough";
    
    private static final String PAGE_UP = "pageUpPassThrough";
    
    private static final String HOME = "homePassThrough";
    
    private static final String END = "endPassThrough";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      JComboBox jComboBox = (JComboBox)param1ActionEvent.getSource();
      BasicComboBoxUI basicComboBoxUI = (BasicComboBoxUI)BasicLookAndFeel.getUIOfType(jComboBox.getUI(), BasicComboBoxUI.class);
      if (str == "hidePopup") {
        jComboBox.firePopupMenuCanceled();
        jComboBox.setPopupVisible(false);
      } else if (str == "pageDownPassThrough" || str == "pageUpPassThrough" || str == "homePassThrough" || str == "endPassThrough") {
        int i = getNextIndex(jComboBox, str);
        if (i >= 0 && i < jComboBox.getItemCount())
          if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && jComboBox.isPopupVisible()) {
            basicComboBoxUI.listBox.setSelectedIndex(i);
            basicComboBoxUI.listBox.ensureIndexIsVisible(i);
            jComboBox.repaint();
          } else {
            jComboBox.setSelectedIndex(i);
          }  
      } else if (str == "selectNext") {
        if (jComboBox.isShowing())
          if (jComboBox.isPopupVisible()) {
            if (basicComboBoxUI != null)
              basicComboBoxUI.selectNextPossibleValue(); 
          } else {
            jComboBox.setPopupVisible(true);
          }  
      } else if (str == "selectNext2") {
        if (jComboBox.isShowing())
          if ((jComboBox.isEditable() || (basicComboBoxUI != null && basicComboBoxUI.isTableCellEditor())) && !jComboBox.isPopupVisible()) {
            jComboBox.setPopupVisible(true);
          } else if (basicComboBoxUI != null) {
            basicComboBoxUI.selectNextPossibleValue();
          }  
      } else if (str == "togglePopup" || str == "spacePopup") {
        if (basicComboBoxUI != null && (str == "togglePopup" || !jComboBox.isEditable()))
          if (basicComboBoxUI.isTableCellEditor()) {
            jComboBox.setSelectedIndex(basicComboBoxUI.popup.getList().getSelectedIndex());
          } else {
            jComboBox.setPopupVisible(!jComboBox.isPopupVisible());
          }  
      } else if (str == "selectPrevious") {
        if (basicComboBoxUI != null)
          if (basicComboBoxUI.isPopupVisible(jComboBox)) {
            basicComboBoxUI.selectPreviousPossibleValue();
          } else if (DefaultLookup.getBoolean(jComboBox, basicComboBoxUI, "ComboBox.showPopupOnNavigation", false)) {
            basicComboBoxUI.setPopupVisible(jComboBox, true);
          }  
      } else if (str == "selectPrevious2") {
        if (jComboBox.isShowing() && basicComboBoxUI != null)
          if (jComboBox.isEditable() && !jComboBox.isPopupVisible()) {
            jComboBox.setPopupVisible(true);
          } else {
            basicComboBoxUI.selectPreviousPossibleValue();
          }  
      } else if (str == "enterPressed") {
        if (jComboBox.isPopupVisible()) {
          if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) {
            Object object = basicComboBoxUI.popup.getList().getSelectedValue();
            if (object != null) {
              jComboBox.getEditor().setItem(object);
              jComboBox.setSelectedItem(object);
            } 
            jComboBox.setPopupVisible(false);
          } else {
            boolean bool = UIManager.getBoolean("ComboBox.isEnterSelectablePopup");
            if (!jComboBox.isEditable() || bool || basicComboBoxUI.isTableCellEditor) {
              Object object = basicComboBoxUI.popup.getList().getSelectedValue();
              if (object != null) {
                jComboBox.getEditor().setItem(object);
                jComboBox.setSelectedItem(object);
              } 
            } 
            jComboBox.setPopupVisible(false);
          } 
        } else {
          if (basicComboBoxUI.isTableCellEditor && !jComboBox.isEditable())
            jComboBox.setSelectedItem(jComboBox.getSelectedItem()); 
          JRootPane jRootPane = SwingUtilities.getRootPane(jComboBox);
          if (jRootPane != null) {
            InputMap inputMap = jRootPane.getInputMap(2);
            ActionMap actionMap = jRootPane.getActionMap();
            if (inputMap != null && actionMap != null) {
              Object object = inputMap.get(KeyStroke.getKeyStroke(10, 0));
              if (object != null) {
                Action action = actionMap.get(object);
                if (action != null)
                  action.actionPerformed(new ActionEvent(jRootPane, param1ActionEvent.getID(), param1ActionEvent.getActionCommand(), param1ActionEvent.getWhen(), param1ActionEvent.getModifiers())); 
              } 
            } 
          } 
        } 
      } 
    }
    
    private int getNextIndex(JComboBox param1JComboBox, String param1String) {
      int i = param1JComboBox.getMaximumRowCount();
      int j = param1JComboBox.getSelectedIndex();
      if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation") && param1JComboBox.getUI() instanceof BasicComboBoxUI)
        j = ((BasicComboBoxUI)param1JComboBox.getUI()).listBox.getSelectedIndex(); 
      if (param1String == "pageUpPassThrough") {
        int k = j - i;
        return (k < 0) ? 0 : k;
      } 
      if (param1String == "pageDownPassThrough") {
        int k = j + i;
        int m = param1JComboBox.getItemCount();
        return (k < m) ? k : (m - 1);
      } 
      return (param1String == "homePassThrough") ? 0 : ((param1String == "endPassThrough") ? (param1JComboBox.getItemCount() - 1) : param1JComboBox.getSelectedIndex());
    }
    
    public boolean isEnabled(Object param1Object) { return (getName() == "hidePopup") ? ((param1Object != null && ((JComboBox)param1Object).isPopupVisible())) : true; }
  }
  
  public class ComboBoxLayoutManager implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return BasicComboBoxUI.this.getHandler().preferredLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return BasicComboBoxUI.this.getHandler().minimumLayoutSize(param1Container); }
    
    public void layoutContainer(Container param1Container) { BasicComboBoxUI.this.getHandler().layoutContainer(param1Container); }
  }
  
  class DefaultKeySelectionManager implements JComboBox.KeySelectionManager, UIResource {
    private String prefix = "";
    
    private String typedString = "";
    
    public int selectionForKey(char param1Char, ComboBoxModel param1ComboBoxModel) {
      if (BasicComboBoxUI.this.lastTime == 0L) {
        this.prefix = "";
        this.typedString = "";
      } 
      boolean bool = true;
      int i = BasicComboBoxUI.this.comboBox.getSelectedIndex();
      if (BasicComboBoxUI.this.time - BasicComboBoxUI.this.lastTime < BasicComboBoxUI.this.timeFactor) {
        this.typedString += param1Char;
        if (this.prefix.length() == 1 && param1Char == this.prefix.charAt(0)) {
          i++;
        } else {
          this.prefix = this.typedString;
        } 
      } else {
        i++;
        this.typedString = "" + param1Char;
        this.prefix = this.typedString;
      } 
      BasicComboBoxUI.this.lastTime = BasicComboBoxUI.this.time;
      if (i < 0 || i >= param1ComboBoxModel.getSize()) {
        bool = false;
        i = 0;
      } 
      int j = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, i, Position.Bias.Forward);
      if (j < 0 && bool)
        j = BasicComboBoxUI.this.listBox.getNextMatch(this.prefix, 0, Position.Bias.Forward); 
      return j;
    }
  }
  
  public class FocusHandler implements FocusListener {
    public void focusGained(FocusEvent param1FocusEvent) { BasicComboBoxUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicComboBoxUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements ActionListener, FocusListener, KeyListener, LayoutManager, ListDataListener, PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (param1PropertyChangeEvent.getSource() == BasicComboBoxUI.this.editor) {
        if ("border".equals(str)) {
          BasicComboBoxUI.this.isMinimumSizeDirty = true;
          BasicComboBoxUI.this.isDisplaySizeDirty = true;
          BasicComboBoxUI.this.comboBox.revalidate();
        } 
      } else {
        JComboBox jComboBox = (JComboBox)param1PropertyChangeEvent.getSource();
        if (str == "model") {
          ComboBoxModel comboBoxModel1 = (ComboBoxModel)param1PropertyChangeEvent.getNewValue();
          ComboBoxModel comboBoxModel2 = (ComboBoxModel)param1PropertyChangeEvent.getOldValue();
          if (comboBoxModel2 != null && BasicComboBoxUI.this.listDataListener != null)
            comboBoxModel2.removeListDataListener(BasicComboBoxUI.this.listDataListener); 
          if (comboBoxModel1 != null && BasicComboBoxUI.this.listDataListener != null)
            comboBoxModel1.addListDataListener(BasicComboBoxUI.this.listDataListener); 
          if (BasicComboBoxUI.this.editor != null)
            jComboBox.configureEditor(jComboBox.getEditor(), jComboBox.getSelectedItem()); 
          BasicComboBoxUI.this.isMinimumSizeDirty = true;
          BasicComboBoxUI.this.isDisplaySizeDirty = true;
          jComboBox.revalidate();
          jComboBox.repaint();
        } else if (str == "editor" && jComboBox.isEditable()) {
          BasicComboBoxUI.this.addEditor();
          jComboBox.revalidate();
        } else if (str == "editable") {
          if (jComboBox.isEditable()) {
            jComboBox.setRequestFocusEnabled(false);
            BasicComboBoxUI.this.addEditor();
          } else {
            jComboBox.setRequestFocusEnabled(true);
            BasicComboBoxUI.this.removeEditor();
          } 
          BasicComboBoxUI.this.updateToolTipTextForChildren();
          jComboBox.revalidate();
        } else if (str == "enabled") {
          boolean bool = jComboBox.isEnabled();
          if (BasicComboBoxUI.this.editor != null)
            BasicComboBoxUI.this.editor.setEnabled(bool); 
          if (BasicComboBoxUI.this.arrowButton != null)
            BasicComboBoxUI.this.arrowButton.setEnabled(bool); 
          jComboBox.repaint();
        } else if (str == "focusable") {
          boolean bool = jComboBox.isFocusable();
          if (BasicComboBoxUI.this.editor != null)
            BasicComboBoxUI.this.editor.setFocusable(bool); 
          if (BasicComboBoxUI.this.arrowButton != null)
            BasicComboBoxUI.this.arrowButton.setFocusable(bool); 
          jComboBox.repaint();
        } else if (str == "maximumRowCount") {
          if (BasicComboBoxUI.this.isPopupVisible(jComboBox)) {
            BasicComboBoxUI.this.setPopupVisible(jComboBox, false);
            BasicComboBoxUI.this.setPopupVisible(jComboBox, true);
          } 
        } else if (str == "font") {
          BasicComboBoxUI.this.listBox.setFont(jComboBox.getFont());
          if (BasicComboBoxUI.this.editor != null)
            BasicComboBoxUI.this.editor.setFont(jComboBox.getFont()); 
          BasicComboBoxUI.this.isMinimumSizeDirty = true;
          BasicComboBoxUI.this.isDisplaySizeDirty = true;
          jComboBox.validate();
        } else if (str == "ToolTipText") {
          BasicComboBoxUI.this.updateToolTipTextForChildren();
        } else if (str == "JComboBox.isTableCellEditor") {
          Boolean bool = (Boolean)param1PropertyChangeEvent.getNewValue();
          BasicComboBoxUI.this.isTableCellEditor = bool.equals(Boolean.TRUE);
        } else if (str == "prototypeDisplayValue") {
          BasicComboBoxUI.this.isMinimumSizeDirty = true;
          BasicComboBoxUI.this.isDisplaySizeDirty = true;
          jComboBox.revalidate();
        } else if (str == "renderer") {
          BasicComboBoxUI.this.isMinimumSizeDirty = true;
          BasicComboBoxUI.this.isDisplaySizeDirty = true;
          jComboBox.revalidate();
        } 
      } 
    }
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (BasicComboBoxUI.this.isNavigationKey(param1KeyEvent.getKeyCode(), param1KeyEvent.getModifiers())) {
        BasicComboBoxUI.this.lastTime = 0L;
      } else if (BasicComboBoxUI.this.comboBox.isEnabled() && BasicComboBoxUI.this.comboBox.getModel().getSize() != 0 && isTypeAheadKey(param1KeyEvent) && param1KeyEvent.getKeyChar() != Character.MAX_VALUE) {
        BasicComboBoxUI.this.time = param1KeyEvent.getWhen();
        if (BasicComboBoxUI.this.comboBox.selectWithKeyChar(param1KeyEvent.getKeyChar()))
          param1KeyEvent.consume(); 
      } 
    }
    
    public void keyTyped(KeyEvent param1KeyEvent) {}
    
    public void keyReleased(KeyEvent param1KeyEvent) {}
    
    private boolean isTypeAheadKey(KeyEvent param1KeyEvent) { return (!param1KeyEvent.isAltDown() && !BasicGraphicsUtils.isMenuShortcutKeyDown(param1KeyEvent)); }
    
    public void focusGained(FocusEvent param1FocusEvent) {
      ComboBoxEditor comboBoxEditor = BasicComboBoxUI.this.comboBox.getEditor();
      if (comboBoxEditor != null && param1FocusEvent.getSource() == comboBoxEditor.getEditorComponent())
        return; 
      BasicComboBoxUI.this.hasFocus = true;
      BasicComboBoxUI.this.comboBox.repaint();
      if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null)
        BasicComboBoxUI.this.editor.requestFocus(); 
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      ComboBoxEditor comboBoxEditor = BasicComboBoxUI.this.comboBox.getEditor();
      if (comboBoxEditor != null && param1FocusEvent.getSource() == comboBoxEditor.getEditorComponent()) {
        Object object1 = comboBoxEditor.getItem();
        Object object2 = BasicComboBoxUI.this.comboBox.getSelectedItem();
        if (!param1FocusEvent.isTemporary() && object1 != null && !object1.equals((object2 == null) ? "" : object2))
          BasicComboBoxUI.this.comboBox.actionPerformed(new ActionEvent(comboBoxEditor, 0, "", EventQueue.getMostRecentEventTime(), 0)); 
      } 
      BasicComboBoxUI.this.hasFocus = false;
      if (!param1FocusEvent.isTemporary())
        BasicComboBoxUI.this.setPopupVisible(BasicComboBoxUI.this.comboBox, false); 
      BasicComboBoxUI.this.comboBox.repaint();
    }
    
    public void contentsChanged(ListDataEvent param1ListDataEvent) {
      if (param1ListDataEvent.getIndex0() != -1 || param1ListDataEvent.getIndex1() != -1) {
        BasicComboBoxUI.this.isMinimumSizeDirty = true;
        BasicComboBoxUI.this.comboBox.revalidate();
      } 
      if (BasicComboBoxUI.this.comboBox.isEditable() && BasicComboBoxUI.this.editor != null)
        BasicComboBoxUI.this.comboBox.configureEditor(BasicComboBoxUI.this.comboBox.getEditor(), BasicComboBoxUI.this.comboBox.getSelectedItem()); 
      BasicComboBoxUI.this.isDisplaySizeDirty = true;
      BasicComboBoxUI.this.comboBox.repaint();
    }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) { contentsChanged(param1ListDataEvent); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { contentsChanged(param1ListDataEvent); }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return param1Container.getPreferredSize(); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return param1Container.getMinimumSize(); }
    
    public void layoutContainer(Container param1Container) {
      JComboBox jComboBox = (JComboBox)param1Container;
      int i = jComboBox.getWidth();
      int j = jComboBox.getHeight();
      Insets insets = BasicComboBoxUI.this.getInsets();
      int k = j - insets.top + insets.bottom;
      int m = k;
      if (BasicComboBoxUI.this.arrowButton != null) {
        Insets insets1 = BasicComboBoxUI.this.arrowButton.getInsets();
        m = BasicComboBoxUI.this.squareButton ? k : ((this.this$0.arrowButton.getPreferredSize()).width + insets1.left + insets1.right);
      } 
      if (BasicComboBoxUI.this.arrowButton != null)
        if (BasicGraphicsUtils.isLeftToRight(jComboBox)) {
          BasicComboBoxUI.this.arrowButton.setBounds(i - insets.right + m, insets.top, m, k);
        } else {
          BasicComboBoxUI.this.arrowButton.setBounds(insets.left, insets.top, m, k);
        }  
      if (BasicComboBoxUI.this.editor != null) {
        Rectangle rectangle = BasicComboBoxUI.this.rectangleForCurrentValue();
        BasicComboBoxUI.this.editor.setBounds(rectangle);
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      Object object = BasicComboBoxUI.this.comboBox.getEditor().getItem();
      if (object != null) {
        if (!BasicComboBoxUI.this.comboBox.isPopupVisible() && !object.equals(BasicComboBoxUI.this.comboBox.getSelectedItem()))
          BasicComboBoxUI.this.comboBox.setSelectedItem(BasicComboBoxUI.this.comboBox.getEditor().getItem()); 
        ActionMap actionMap = BasicComboBoxUI.this.comboBox.getActionMap();
        if (actionMap != null) {
          Action action = actionMap.get("enterPressed");
          if (action != null)
            action.actionPerformed(new ActionEvent(BasicComboBoxUI.this.comboBox, param1ActionEvent.getID(), param1ActionEvent.getActionCommand(), param1ActionEvent.getModifiers())); 
        } 
      } 
    }
  }
  
  public class ItemHandler implements ItemListener {
    public void itemStateChanged(ItemEvent param1ItemEvent) {}
  }
  
  public class KeyHandler extends KeyAdapter {
    public void keyPressed(KeyEvent param1KeyEvent) { BasicComboBoxUI.this.getHandler().keyPressed(param1KeyEvent); }
  }
  
  public class ListDataHandler implements ListDataListener {
    public void contentsChanged(ListDataEvent param1ListDataEvent) { BasicComboBoxUI.this.getHandler().contentsChanged(param1ListDataEvent); }
    
    public void intervalAdded(ListDataEvent param1ListDataEvent) { BasicComboBoxUI.this.getHandler().intervalAdded(param1ListDataEvent); }
    
    public void intervalRemoved(ListDataEvent param1ListDataEvent) { BasicComboBoxUI.this.getHandler().intervalRemoved(param1ListDataEvent); }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicComboBoxUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */