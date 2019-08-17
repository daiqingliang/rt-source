package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicTabbedPaneUI extends TabbedPaneUI implements SwingConstants {
  protected JTabbedPane tabPane;
  
  protected Color highlight;
  
  protected Color lightHighlight;
  
  protected Color shadow;
  
  protected Color darkShadow;
  
  protected Color focus;
  
  private Color selectedColor;
  
  protected int textIconGap;
  
  protected int tabRunOverlay;
  
  protected Insets tabInsets;
  
  protected Insets selectedTabPadInsets;
  
  protected Insets tabAreaInsets;
  
  protected Insets contentBorderInsets;
  
  private boolean tabsOverlapBorder;
  
  private boolean tabsOpaque = true;
  
  private boolean contentOpaque = true;
  
  @Deprecated
  protected KeyStroke upKey;
  
  @Deprecated
  protected KeyStroke downKey;
  
  @Deprecated
  protected KeyStroke leftKey;
  
  @Deprecated
  protected KeyStroke rightKey;
  
  protected int[] tabRuns = new int[10];
  
  protected int runCount = 0;
  
  protected int selectedRun = -1;
  
  protected Rectangle[] rects = new Rectangle[0];
  
  protected int maxTabHeight;
  
  protected int maxTabWidth;
  
  protected ChangeListener tabChangeListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected MouseListener mouseListener;
  
  protected FocusListener focusListener;
  
  private Insets currentPadInsets = new Insets(0, 0, 0, 0);
  
  private Insets currentTabAreaInsets = new Insets(0, 0, 0, 0);
  
  private Component visibleComponent;
  
  private Vector<View> htmlViews;
  
  private Hashtable<Integer, Integer> mnemonicToIndexMap;
  
  private InputMap mnemonicInputMap;
  
  private ScrollableTabSupport tabScroller;
  
  private TabContainer tabContainer;
  
  protected Rectangle calcRect = new Rectangle(0, 0, 0, 0);
  
  private int focusIndex;
  
  private Handler handler;
  
  private int rolloverTabIndex;
  
  private boolean isRunsDirty;
  
  private boolean calculatedBaseline;
  
  private int baseline;
  
  private static int[] xCropLen = { 1, 1, 0, 0, 1, 1, 2, 2 };
  
  private static int[] yCropLen = { 0, 3, 3, 6, 6, 9, 9, 12 };
  
  private static final int CROP_SEGMENT = 12;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTabbedPaneUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("navigateNext"));
    paramLazyActionMap.put(new Actions("navigatePrevious"));
    paramLazyActionMap.put(new Actions("navigateRight"));
    paramLazyActionMap.put(new Actions("navigateLeft"));
    paramLazyActionMap.put(new Actions("navigateUp"));
    paramLazyActionMap.put(new Actions("navigateDown"));
    paramLazyActionMap.put(new Actions("navigatePageUp"));
    paramLazyActionMap.put(new Actions("navigatePageDown"));
    paramLazyActionMap.put(new Actions("requestFocus"));
    paramLazyActionMap.put(new Actions("requestFocusForVisibleComponent"));
    paramLazyActionMap.put(new Actions("setSelectedIndex"));
    paramLazyActionMap.put(new Actions("selectTabWithFocus"));
    paramLazyActionMap.put(new Actions("scrollTabsForwardAction"));
    paramLazyActionMap.put(new Actions("scrollTabsBackwardAction"));
  }
  
  public void installUI(JComponent paramJComponent) {
    this.tabPane = (JTabbedPane)paramJComponent;
    this.calculatedBaseline = false;
    this.rolloverTabIndex = -1;
    this.focusIndex = -1;
    paramJComponent.setLayout(createLayoutManager());
    installComponents();
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    paramJComponent.setLayout(null);
    this.tabPane = null;
  }
  
  protected LayoutManager createLayoutManager() { return (this.tabPane.getTabLayoutPolicy() == 1) ? new TabbedPaneScrollLayout(null) : new TabbedPaneLayout(); }
  
  private boolean scrollableTabLayoutEnabled() { return this.tabPane.getLayout() instanceof TabbedPaneScrollLayout; }
  
  protected void installComponents() {
    if (scrollableTabLayoutEnabled() && this.tabScroller == null) {
      this.tabScroller = new ScrollableTabSupport(this.tabPane.getTabPlacement());
      this.tabPane.add(this.tabScroller.viewport);
    } 
    installTabContainer();
  }
  
  private void installTabContainer() {
    for (byte b = 0; b < this.tabPane.getTabCount(); b++) {
      Component component = this.tabPane.getTabComponentAt(b);
      if (component != null) {
        if (this.tabContainer == null)
          this.tabContainer = new TabContainer(); 
        this.tabContainer.add(component);
      } 
    } 
    if (this.tabContainer == null)
      return; 
    if (scrollableTabLayoutEnabled()) {
      this.tabScroller.tabPanel.add(this.tabContainer);
    } else {
      this.tabPane.add(this.tabContainer);
    } 
  }
  
  protected JButton createScrollButton(int paramInt) {
    if (paramInt != 5 && paramInt != 1 && paramInt != 3 && paramInt != 7)
      throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST"); 
    return new ScrollableTabButton(paramInt);
  }
  
  protected void uninstallComponents() {
    uninstallTabContainer();
    if (scrollableTabLayoutEnabled()) {
      this.tabPane.remove(this.tabScroller.viewport);
      this.tabPane.remove(this.tabScroller.scrollForwardButton);
      this.tabPane.remove(this.tabScroller.scrollBackwardButton);
      this.tabScroller = null;
    } 
  }
  
  private void uninstallTabContainer() {
    if (this.tabContainer == null)
      return; 
    this.tabContainer.notifyTabbedPane = false;
    this.tabContainer.removeAll();
    if (scrollableTabLayoutEnabled()) {
      this.tabContainer.remove(this.tabScroller.croppedEdge);
      this.tabScroller.tabPanel.remove(this.tabContainer);
    } else {
      this.tabPane.remove(this.tabContainer);
    } 
    this.tabContainer = null;
  }
  
  protected void installDefaults() {
    LookAndFeel.installColorsAndFont(this.tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
    this.highlight = UIManager.getColor("TabbedPane.light");
    this.lightHighlight = UIManager.getColor("TabbedPane.highlight");
    this.shadow = UIManager.getColor("TabbedPane.shadow");
    this.darkShadow = UIManager.getColor("TabbedPane.darkShadow");
    this.focus = UIManager.getColor("TabbedPane.focus");
    this.selectedColor = UIManager.getColor("TabbedPane.selected");
    this.textIconGap = UIManager.getInt("TabbedPane.textIconGap");
    this.tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
    this.selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
    this.tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
    this.tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
    this.contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
    this.tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
    this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
    this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
    Object object = UIManager.get("TabbedPane.opaque");
    if (object == null)
      object = Boolean.FALSE; 
    LookAndFeel.installProperty(this.tabPane, "opaque", object);
    if (this.tabInsets == null)
      this.tabInsets = new Insets(0, 4, 1, 4); 
    if (this.selectedTabPadInsets == null)
      this.selectedTabPadInsets = new Insets(2, 2, 2, 1); 
    if (this.tabAreaInsets == null)
      this.tabAreaInsets = new Insets(3, 2, 0, 2); 
    if (this.contentBorderInsets == null)
      this.contentBorderInsets = new Insets(2, 2, 3, 3); 
  }
  
  protected void uninstallDefaults() {
    this.highlight = null;
    this.lightHighlight = null;
    this.shadow = null;
    this.darkShadow = null;
    this.focus = null;
    this.tabInsets = null;
    this.selectedTabPadInsets = null;
    this.tabAreaInsets = null;
    this.contentBorderInsets = null;
  }
  
  protected void installListeners() {
    if ((this.propertyChangeListener = createPropertyChangeListener()) != null)
      this.tabPane.addPropertyChangeListener(this.propertyChangeListener); 
    if ((this.tabChangeListener = createChangeListener()) != null)
      this.tabPane.addChangeListener(this.tabChangeListener); 
    if ((this.mouseListener = createMouseListener()) != null)
      this.tabPane.addMouseListener(this.mouseListener); 
    this.tabPane.addMouseMotionListener(getHandler());
    if ((this.focusListener = createFocusListener()) != null)
      this.tabPane.addFocusListener(this.focusListener); 
    this.tabPane.addContainerListener(getHandler());
    if (this.tabPane.getTabCount() > 0)
      this.htmlViews = createHTMLVector(); 
  }
  
  protected void uninstallListeners() {
    if (this.mouseListener != null) {
      this.tabPane.removeMouseListener(this.mouseListener);
      this.mouseListener = null;
    } 
    this.tabPane.removeMouseMotionListener(getHandler());
    if (this.focusListener != null) {
      this.tabPane.removeFocusListener(this.focusListener);
      this.focusListener = null;
    } 
    this.tabPane.removeContainerListener(getHandler());
    if (this.htmlViews != null) {
      this.htmlViews.removeAllElements();
      this.htmlViews = null;
    } 
    if (this.tabChangeListener != null) {
      this.tabPane.removeChangeListener(this.tabChangeListener);
      this.tabChangeListener = null;
    } 
    if (this.propertyChangeListener != null) {
      this.tabPane.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
    } 
    this.handler = null;
  }
  
  protected MouseListener createMouseListener() { return getHandler(); }
  
  protected FocusListener createFocusListener() { return getHandler(); }
  
  protected ChangeListener createChangeListener() { return getHandler(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.tabPane, 1, inputMap);
    inputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.tabPane, 0, inputMap);
    LazyActionMap.installLazyActionMap(this.tabPane, BasicTabbedPaneUI.class, "TabbedPane.actionMap");
    updateMnemonics();
  }
  
  InputMap getInputMap(int paramInt) { return (paramInt == 1) ? (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.ancestorInputMap") : ((paramInt == 0) ? (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.focusInputMap") : null); }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.tabPane, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 1, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 0, null);
    SwingUtilities.replaceUIInputMap(this.tabPane, 2, null);
    this.mnemonicToIndexMap = null;
    this.mnemonicInputMap = null;
  }
  
  private void updateMnemonics() {
    resetMnemonics();
    for (int i = this.tabPane.getTabCount() - 1; i >= 0; i--) {
      int j = this.tabPane.getMnemonicAt(i);
      if (j > 0)
        addMnemonic(i, j); 
    } 
  }
  
  private void resetMnemonics() {
    if (this.mnemonicToIndexMap != null) {
      this.mnemonicToIndexMap.clear();
      this.mnemonicInputMap.clear();
    } 
  }
  
  private void addMnemonic(int paramInt1, int paramInt2) {
    if (this.mnemonicToIndexMap == null)
      initMnemonics(); 
    this.mnemonicInputMap.put(KeyStroke.getKeyStroke(paramInt2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "setSelectedIndex");
    this.mnemonicToIndexMap.put(Integer.valueOf(paramInt2), Integer.valueOf(paramInt1));
  }
  
  private void initMnemonics() {
    this.mnemonicToIndexMap = new Hashtable();
    this.mnemonicInputMap = new ComponentInputMapUIResource(this.tabPane);
    this.mnemonicInputMap.setParent(SwingUtilities.getUIInputMap(this.tabPane, 2));
    SwingUtilities.replaceUIInputMap(this.tabPane, 2, this.mnemonicInputMap);
  }
  
  private void setRolloverTab(int paramInt1, int paramInt2) { setRolloverTab(tabForCoordinate(this.tabPane, paramInt1, paramInt2, false)); }
  
  protected void setRolloverTab(int paramInt) { this.rolloverTabIndex = paramInt; }
  
  protected int getRolloverTab() { return this.rolloverTabIndex; }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return null; }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return null; }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    null = calculateBaselineIfNecessary();
    if (null != -1) {
      int i = this.tabPane.getTabPlacement();
      Insets insets1 = this.tabPane.getInsets();
      Insets insets2 = getTabAreaInsets(i);
      switch (i) {
        case 1:
          return insets1.top + insets2.top;
        case 3:
          return paramInt2 - insets1.bottom - insets2.bottom - this.maxTabHeight + null;
        case 2:
        case 4:
          return insets1.top + insets2.top;
      } 
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    switch (this.tabPane.getTabPlacement()) {
      case 1:
      case 2:
      case 4:
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
      case 3:
        return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    } 
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected int getBaseline(int paramInt) {
    if (this.tabPane.getTabComponentAt(paramInt) != null) {
      int k = getBaselineOffset();
      if (k != 0)
        return -1; 
      Component component = this.tabPane.getTabComponentAt(paramInt);
      Dimension dimension = component.getPreferredSize();
      Insets insets = getTabInsets(this.tabPane.getTabPlacement(), paramInt);
      int m = this.maxTabHeight - insets.top - insets.bottom;
      return component.getBaseline(dimension.width, dimension.height) + (m - dimension.height) / 2 + insets.top;
    } 
    View view = getTextViewForTab(paramInt);
    if (view != null) {
      int k = (int)view.getPreferredSpan(1);
      int m = BasicHTML.getHTMLBaseline(view, (int)view.getPreferredSpan(0), k);
      return (m >= 0) ? (this.maxTabHeight / 2 - k / 2 + m + getBaselineOffset()) : -1;
    } 
    FontMetrics fontMetrics = getFontMetrics();
    int i = fontMetrics.getHeight();
    int j = fontMetrics.getAscent();
    return this.maxTabHeight / 2 - i / 2 + j + getBaselineOffset();
  }
  
  protected int getBaselineOffset() {
    switch (this.tabPane.getTabPlacement()) {
      case 1:
        return (this.tabPane.getTabCount() > 1) ? 1 : -1;
      case 3:
        return (this.tabPane.getTabCount() > 1) ? -1 : 1;
    } 
    return this.maxTabHeight % 2;
  }
  
  private int calculateBaselineIfNecessary() {
    if (!this.calculatedBaseline) {
      this.calculatedBaseline = true;
      this.baseline = -1;
      if (this.tabPane.getTabCount() > 0)
        calculateBaseline(); 
    } 
    return this.baseline;
  }
  
  private void calculateBaseline() {
    int i = this.tabPane.getTabCount();
    int j = this.tabPane.getTabPlacement();
    this.maxTabHeight = calculateMaxTabHeight(j);
    this.baseline = getBaseline(0);
    if (isHorizontalTabPlacement()) {
      for (byte b = 1; b < i; b++) {
        if (getBaseline(b) != this.baseline) {
          this.baseline = -1;
          break;
        } 
      } 
    } else {
      FontMetrics fontMetrics = getFontMetrics();
      int k = fontMetrics.getHeight();
      int m = calculateTabHeight(j, 0, k);
      for (byte b = 1; b < i; b++) {
        int n = calculateTabHeight(j, b, k);
        if (m != n) {
          this.baseline = -1;
          break;
        } 
      } 
    } 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    int i = this.tabPane.getSelectedIndex();
    int j = this.tabPane.getTabPlacement();
    ensureCurrentLayout();
    if (this.tabsOverlapBorder)
      paintContentBorder(paramGraphics, j, i); 
    if (!scrollableTabLayoutEnabled())
      paintTabArea(paramGraphics, j, i); 
    if (!this.tabsOverlapBorder)
      paintContentBorder(paramGraphics, j, i); 
  }
  
  protected void paintTabArea(Graphics paramGraphics, int paramInt1, int paramInt2) {
    int i = this.tabPane.getTabCount();
    Rectangle rectangle1 = new Rectangle();
    Rectangle rectangle2 = new Rectangle();
    Rectangle rectangle3 = paramGraphics.getClipBounds();
    for (int j = this.runCount - 1; j >= 0; j--) {
      int k = this.tabRuns[j];
      int m = this.tabRuns[(j == this.runCount - 1) ? 0 : (j + 1)];
      int n = (m != 0) ? (m - 1) : (i - 1);
      for (int i1 = k; i1 <= n; i1++) {
        if (i1 != paramInt2 && this.rects[i1].intersects(rectangle3))
          paintTab(paramGraphics, paramInt1, this.rects, i1, rectangle1, rectangle2); 
      } 
    } 
    if (paramInt2 >= 0 && this.rects[paramInt2].intersects(rectangle3))
      paintTab(paramGraphics, paramInt1, this.rects, paramInt2, rectangle1, rectangle2); 
  }
  
  protected void paintTab(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2) {
    Rectangle rectangle = paramArrayOfRectangle[paramInt2];
    int i = this.tabPane.getSelectedIndex();
    boolean bool = (i == paramInt2);
    if (this.tabsOpaque || this.tabPane.isOpaque())
      paintTabBackground(paramGraphics, paramInt1, paramInt2, rectangle.x, rectangle.y, rectangle.width, rectangle.height, bool); 
    paintTabBorder(paramGraphics, paramInt1, paramInt2, rectangle.x, rectangle.y, rectangle.width, rectangle.height, bool);
    String str = this.tabPane.getTitleAt(paramInt2);
    Font font = this.tabPane.getFont();
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, paramGraphics, font);
    Icon icon = getIconForTab(paramInt2);
    layoutLabel(paramInt1, fontMetrics, paramInt2, str, icon, rectangle, paramRectangle1, paramRectangle2, bool);
    if (this.tabPane.getTabComponentAt(paramInt2) == null) {
      String str1 = str;
      if (scrollableTabLayoutEnabled() && this.tabScroller.croppedEdge.isParamsSet() && this.tabScroller.croppedEdge.getTabIndex() == paramInt2 && isHorizontalTabPlacement()) {
        int j = this.tabScroller.croppedEdge.getCropline() - paramRectangle2.x - rectangle.x - this.tabScroller.croppedEdge.getCroppedSideWidth();
        str1 = SwingUtilities2.clipStringIfNecessary(null, fontMetrics, str, j);
      } else if (!scrollableTabLayoutEnabled() && isHorizontalTabPlacement()) {
        str1 = SwingUtilities2.clipStringIfNecessary(null, fontMetrics, str, paramRectangle2.width);
      } 
      paintText(paramGraphics, paramInt1, font, fontMetrics, paramInt2, str1, paramRectangle2, bool);
      paintIcon(paramGraphics, paramInt1, paramInt2, icon, paramRectangle1, bool);
    } 
    paintFocusIndicator(paramGraphics, paramInt1, paramArrayOfRectangle, paramInt2, paramRectangle1, paramRectangle2, bool);
  }
  
  private boolean isHorizontalTabPlacement() { return (this.tabPane.getTabPlacement() == 1 || this.tabPane.getTabPlacement() == 3); }
  
  private static Polygon createCroppedTabShape(int paramInt1, Rectangle paramRectangle, int paramInt2) {
    int m;
    int k;
    int j;
    int i;
    switch (paramInt1) {
      case 2:
      case 4:
        i = paramRectangle.width;
        j = paramRectangle.x;
        k = paramRectangle.x + paramRectangle.width;
        m = paramRectangle.y + paramRectangle.height;
        break;
      default:
        i = paramRectangle.height;
        j = paramRectangle.y;
        k = paramRectangle.y + paramRectangle.height;
        m = paramRectangle.x + paramRectangle.width;
        break;
    } 
    int n = i / 12;
    if (i % 12 > 0)
      n++; 
    int i1 = 2 + n * 8;
    int[] arrayOfInt1 = new int[i1];
    int[] arrayOfInt2 = new int[i1];
    byte b1 = 0;
    arrayOfInt1[b1] = m;
    arrayOfInt2[b1++] = k;
    arrayOfInt1[b1] = m;
    arrayOfInt2[b1++] = j;
    for (byte b2 = 0; b2 < n; b2++) {
      for (byte b = 0; b < xCropLen.length; b++) {
        arrayOfInt1[b1] = paramInt2 - xCropLen[b];
        arrayOfInt2[b1] = j + b2 * 12 + yCropLen[b];
        if (arrayOfInt2[b1] >= k) {
          arrayOfInt2[b1] = k;
          b1++;
          break;
        } 
        b1++;
      } 
    } 
    return (paramInt1 == 1 || paramInt1 == 3) ? new Polygon(arrayOfInt1, arrayOfInt2, b1) : new Polygon(arrayOfInt2, arrayOfInt1, b1);
  }
  
  private void paintCroppedTabEdge(Graphics paramGraphics) {
    int n;
    int i = this.tabScroller.croppedEdge.getTabIndex();
    int j = this.tabScroller.croppedEdge.getCropline();
    switch (this.tabPane.getTabPlacement()) {
      case 2:
      case 4:
        k = (this.rects[i]).x;
        m = j;
        n = k;
        paramGraphics.setColor(this.shadow);
        while (n <= k + (this.rects[i]).width) {
          for (boolean bool = false; bool < xCropLen.length; bool += true)
            paramGraphics.drawLine(n + yCropLen[bool], m - xCropLen[bool], n + yCropLen[bool + true] - 1, m - xCropLen[bool + true]); 
          n += 12;
        } 
        return;
    } 
    int k = j;
    int m = (this.rects[i]).y;
    int i1 = m;
    paramGraphics.setColor(this.shadow);
    while (i1 <= m + (this.rects[i]).height) {
      for (boolean bool = false; bool < xCropLen.length; bool += true)
        paramGraphics.drawLine(k - xCropLen[bool], i1 + yCropLen[bool], k - xCropLen[bool + true], i1 + yCropLen[bool + true] - 1); 
      i1 += 12;
    } 
  }
  
  protected void layoutLabel(int paramInt1, FontMetrics paramFontMetrics, int paramInt2, String paramString, Icon paramIcon, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, boolean paramBoolean) {
    paramRectangle3.x = paramRectangle3.y = paramRectangle2.x = paramRectangle2.y = 0;
    View view = getTextViewForTab(paramInt2);
    if (view != null)
      this.tabPane.putClientProperty("html", view); 
    SwingUtilities.layoutCompoundLabel(this.tabPane, paramFontMetrics, paramString, paramIcon, 0, 0, 0, 11, paramRectangle1, paramRectangle2, paramRectangle3, this.textIconGap);
    this.tabPane.putClientProperty("html", null);
    int i = getTabLabelShiftX(paramInt1, paramInt2, paramBoolean);
    int j = getTabLabelShiftY(paramInt1, paramInt2, paramBoolean);
    paramRectangle2.x += i;
    paramRectangle2.y += j;
    paramRectangle3.x += i;
    paramRectangle3.y += j;
  }
  
  protected void paintIcon(Graphics paramGraphics, int paramInt1, int paramInt2, Icon paramIcon, Rectangle paramRectangle, boolean paramBoolean) {
    if (paramIcon != null)
      paramIcon.paintIcon(this.tabPane, paramGraphics, paramRectangle.x, paramRectangle.y); 
  }
  
  protected void paintText(Graphics paramGraphics, int paramInt1, Font paramFont, FontMetrics paramFontMetrics, int paramInt2, String paramString, Rectangle paramRectangle, boolean paramBoolean) {
    paramGraphics.setFont(paramFont);
    View view = getTextViewForTab(paramInt2);
    if (view != null) {
      view.paint(paramGraphics, paramRectangle);
    } else {
      int i = this.tabPane.getDisplayedMnemonicIndexAt(paramInt2);
      if (this.tabPane.isEnabled() && this.tabPane.isEnabledAt(paramInt2)) {
        Color color = this.tabPane.getForegroundAt(paramInt2);
        if (paramBoolean && color instanceof UIResource) {
          Color color1 = UIManager.getColor("TabbedPane.selectedForeground");
          if (color1 != null)
            color = color1; 
        } 
        paramGraphics.setColor(color);
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
      } else {
        paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).brighter());
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x, paramRectangle.y + paramFontMetrics.getAscent());
        paramGraphics.setColor(this.tabPane.getBackgroundAt(paramInt2).darker());
        SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, paramGraphics, paramString, i, paramRectangle.x - 1, paramRectangle.y + paramFontMetrics.getAscent() - 1);
      } 
    } 
  }
  
  protected int getTabLabelShiftX(int paramInt1, int paramInt2, boolean paramBoolean) {
    Rectangle rectangle = this.rects[paramInt2];
    String str = paramBoolean ? "selectedLabelShift" : "labelShift";
    int i = DefaultLookup.getInt(this.tabPane, this, "TabbedPane." + str, 1);
    switch (paramInt1) {
      case 2:
        return i;
      case 4:
        return -i;
    } 
    return rectangle.width % 2;
  }
  
  protected int getTabLabelShiftY(int paramInt1, int paramInt2, boolean paramBoolean) {
    Rectangle rectangle = this.rects[paramInt2];
    int i = paramBoolean ? DefaultLookup.getInt(this.tabPane, this, "TabbedPane.selectedLabelShift", -1) : DefaultLookup.getInt(this.tabPane, this, "TabbedPane.labelShift", 1);
    switch (paramInt1) {
      case 3:
        return -i;
      case 2:
      case 4:
        return rectangle.height % 2;
    } 
    return i;
  }
  
  protected void paintFocusIndicator(Graphics paramGraphics, int paramInt1, Rectangle[] paramArrayOfRectangle, int paramInt2, Rectangle paramRectangle1, Rectangle paramRectangle2, boolean paramBoolean) {
    Rectangle rectangle = paramArrayOfRectangle[paramInt2];
    if (this.tabPane.hasFocus() && paramBoolean) {
      int m;
      int k;
      int j;
      int i;
      paramGraphics.setColor(this.focus);
      switch (paramInt1) {
        case 2:
          i = rectangle.x + 3;
          j = rectangle.y + 3;
          k = rectangle.width - 5;
          m = rectangle.height - 6;
          break;
        case 4:
          i = rectangle.x + 2;
          j = rectangle.y + 3;
          k = rectangle.width - 5;
          m = rectangle.height - 6;
          break;
        case 3:
          i = rectangle.x + 3;
          j = rectangle.y + 2;
          k = rectangle.width - 6;
          m = rectangle.height - 5;
          break;
        default:
          i = rectangle.x + 3;
          j = rectangle.y + 3;
          k = rectangle.width - 6;
          m = rectangle.height - 5;
          break;
      } 
      BasicGraphicsUtils.drawDashedRect(paramGraphics, i, j, k, m);
    } 
  }
  
  protected void paintTabBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    paramGraphics.setColor(this.lightHighlight);
    switch (paramInt1) {
      case 2:
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 1, paramInt4);
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
        return;
      case 4:
        paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
        return;
      case 3:
        paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 3);
        paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + 1, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(paramInt3 + 2, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 3, paramInt4 + paramInt6 - 1);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 3);
        return;
    } 
    paramGraphics.drawLine(paramInt3, paramInt4 + 2, paramInt3, paramInt4 + paramInt6 - 1);
    paramGraphics.drawLine(paramInt3 + 1, paramInt4 + 1, paramInt3 + 1, paramInt4 + 1);
    paramGraphics.drawLine(paramInt3 + 2, paramInt4, paramInt3 + paramInt5 - 3, paramInt4);
    paramGraphics.setColor(this.shadow);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 1);
    paramGraphics.setColor(this.darkShadow);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4 + 2, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + 1);
  }
  
  protected void paintTabBackground(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    paramGraphics.setColor((!paramBoolean || this.selectedColor == null) ? this.tabPane.getBackgroundAt(paramInt2) : this.selectedColor);
    switch (paramInt1) {
      case 2:
        paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 1, paramInt6 - 3);
        return;
      case 4:
        paramGraphics.fillRect(paramInt3, paramInt4 + 1, paramInt5 - 2, paramInt6 - 3);
        return;
      case 3:
        paramGraphics.fillRect(paramInt3 + 1, paramInt4, paramInt5 - 3, paramInt6 - 1);
        return;
    } 
    paramGraphics.fillRect(paramInt3 + 1, paramInt4 + 1, paramInt5 - 3, paramInt6 - 1);
  }
  
  protected void paintContentBorder(Graphics paramGraphics, int paramInt1, int paramInt2) {
    int i = this.tabPane.getWidth();
    int j = this.tabPane.getHeight();
    Insets insets1 = this.tabPane.getInsets();
    Insets insets2 = getTabAreaInsets(paramInt1);
    int k = insets1.left;
    int m = insets1.top;
    int n = i - insets1.right - insets1.left;
    int i1 = j - insets1.top - insets1.bottom;
    switch (paramInt1) {
      case 2:
        k += calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
        if (this.tabsOverlapBorder)
          k -= insets2.right; 
        n -= k - insets1.left;
        break;
      case 4:
        n -= calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth);
        if (this.tabsOverlapBorder)
          n += insets2.left; 
        break;
      case 3:
        i1 -= calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
        if (this.tabsOverlapBorder)
          i1 += insets2.top; 
        break;
      default:
        m += calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight);
        if (this.tabsOverlapBorder)
          m -= insets2.bottom; 
        i1 -= m - insets1.top;
        break;
    } 
    if (this.tabPane.getTabCount() > 0 && (this.contentOpaque || this.tabPane.isOpaque())) {
      Color color = UIManager.getColor("TabbedPane.contentAreaColor");
      if (color != null) {
        paramGraphics.setColor(color);
      } else if (this.selectedColor == null || paramInt2 == -1) {
        paramGraphics.setColor(this.tabPane.getBackground());
      } else {
        paramGraphics.setColor(this.selectedColor);
      } 
      paramGraphics.fillRect(k, m, n, i1);
    } 
    paintContentBorderTopEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderLeftEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderBottomEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
    paintContentBorderRightEdge(paramGraphics, paramInt1, paramInt2, k, m, n, i1);
  }
  
  protected void paintContentBorderTopEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.lightHighlight);
    if (paramInt1 != 1 || paramInt2 < 0 || rectangle.y + rectangle.height + 1 < paramInt4 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
    } else {
      paramGraphics.drawLine(paramInt3, paramInt4, rectangle.x - 1, paramInt4);
      if (rectangle.x + rectangle.width < paramInt3 + paramInt5 - 2) {
        paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      } else {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4, paramInt3 + paramInt5 - 2, paramInt4);
      } 
    } 
  }
  
  protected void paintContentBorderLeftEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.lightHighlight);
    if (paramInt1 != 2 || paramInt2 < 0 || rectangle.x + rectangle.width + 1 < paramInt3 || rectangle.y < paramInt4 || rectangle.y > paramInt4 + paramInt6) {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, paramInt4 + paramInt6 - 2);
    } else {
      paramGraphics.drawLine(paramInt3, paramInt4, paramInt3, rectangle.y - 1);
      if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2)
        paramGraphics.drawLine(paramInt3, rectangle.y + rectangle.height, paramInt3, paramInt4 + paramInt6 - 2); 
    } 
  }
  
  protected void paintContentBorderBottomEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if (paramInt1 != 3 || paramInt2 < 0 || rectangle.y - 1 > paramInt6 || rectangle.x < paramInt3 || rectangle.x > paramInt3 + paramInt5) {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      paramGraphics.drawLine(paramInt3 + 1, paramInt4 + paramInt6 - 2, rectangle.x - 1, paramInt4 + paramInt6 - 2);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3, paramInt4 + paramInt6 - 1, rectangle.x - 1, paramInt4 + paramInt6 - 1);
      if (rectangle.x + rectangle.width < paramInt3 + paramInt5 - 2) {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4 + paramInt6 - 2, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(rectangle.x + rectangle.width, paramInt4 + paramInt6 - 1, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
      } 
    } 
  }
  
  protected void paintContentBorderRightEdge(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Rectangle rectangle = (paramInt2 < 0) ? null : getTabBounds(paramInt2, this.calcRect);
    paramGraphics.setColor(this.shadow);
    if (paramInt1 != 4 || paramInt2 < 0 || rectangle.x - 1 > paramInt5 || rectangle.y < paramInt4 || rectangle.y > paramInt4 + paramInt6) {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 3);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 1);
    } else {
      paramGraphics.drawLine(paramInt3 + paramInt5 - 2, paramInt4 + 1, paramInt3 + paramInt5 - 2, rectangle.y - 1);
      paramGraphics.setColor(this.darkShadow);
      paramGraphics.drawLine(paramInt3 + paramInt5 - 1, paramInt4, paramInt3 + paramInt5 - 1, rectangle.y - 1);
      if (rectangle.y + rectangle.height < paramInt4 + paramInt6 - 2) {
        paramGraphics.setColor(this.shadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 2, rectangle.y + rectangle.height, paramInt3 + paramInt5 - 2, paramInt4 + paramInt6 - 2);
        paramGraphics.setColor(this.darkShadow);
        paramGraphics.drawLine(paramInt3 + paramInt5 - 1, rectangle.y + rectangle.height, paramInt3 + paramInt5 - 1, paramInt4 + paramInt6 - 2);
      } 
    } 
  }
  
  private void ensureCurrentLayout() {
    if (!this.tabPane.isValid())
      this.tabPane.validate(); 
    if (!this.tabPane.isValid()) {
      TabbedPaneLayout tabbedPaneLayout = (TabbedPaneLayout)this.tabPane.getLayout();
      tabbedPaneLayout.calculateLayoutInfo();
    } 
  }
  
  public Rectangle getTabBounds(JTabbedPane paramJTabbedPane, int paramInt) {
    ensureCurrentLayout();
    Rectangle rectangle = new Rectangle();
    return getTabBounds(paramInt, rectangle);
  }
  
  public int getTabRunCount(JTabbedPane paramJTabbedPane) {
    ensureCurrentLayout();
    return this.runCount;
  }
  
  public int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2) { return tabForCoordinate(paramJTabbedPane, paramInt1, paramInt2, true); }
  
  private int tabForCoordinate(JTabbedPane paramJTabbedPane, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramBoolean)
      ensureCurrentLayout(); 
    if (this.isRunsDirty)
      return -1; 
    Point point = new Point(paramInt1, paramInt2);
    if (scrollableTabLayoutEnabled()) {
      translatePointToTabPanel(paramInt1, paramInt2, point);
      Rectangle rectangle = this.tabScroller.viewport.getViewRect();
      if (!rectangle.contains(point))
        return -1; 
    } 
    int i = this.tabPane.getTabCount();
    for (byte b = 0; b < i; b++) {
      if (this.rects[b].contains(point.x, point.y))
        return b; 
    } 
    return -1;
  }
  
  protected Rectangle getTabBounds(int paramInt, Rectangle paramRectangle) {
    paramRectangle.width = (this.rects[paramInt]).width;
    paramRectangle.height = (this.rects[paramInt]).height;
    if (scrollableTabLayoutEnabled()) {
      Point point1 = this.tabScroller.viewport.getLocation();
      Point point2 = this.tabScroller.viewport.getViewPosition();
      paramRectangle.x = (this.rects[paramInt]).x + point1.x - point2.x;
      paramRectangle.y = (this.rects[paramInt]).y + point1.y - point2.y;
    } else {
      paramRectangle.x = (this.rects[paramInt]).x;
      paramRectangle.y = (this.rects[paramInt]).y;
    } 
    return paramRectangle;
  }
  
  private int getClosestTab(int paramInt1, int paramInt2) {
    int i = 0;
    int j = Math.min(this.rects.length, this.tabPane.getTabCount());
    int k = j;
    int m = this.tabPane.getTabPlacement();
    boolean bool = (m == 1 || m == 3) ? 1 : 0;
    int n = bool ? paramInt1 : paramInt2;
    while (i != k) {
      int i3;
      int i2;
      int i1 = (k + i) / 2;
      if (bool) {
        i2 = (this.rects[i1]).x;
        i3 = i2 + (this.rects[i1]).width;
      } else {
        i2 = (this.rects[i1]).y;
        i3 = i2 + (this.rects[i1]).height;
      } 
      if (n < i2) {
        k = i1;
        if (i == k)
          return Math.max(0, i1 - 1); 
        continue;
      } 
      if (n >= i3) {
        i = i1;
        if (k - i <= 1)
          return Math.max(i1 + 1, j - 1); 
        continue;
      } 
      return i1;
    } 
    return i;
  }
  
  private Point translatePointToTabPanel(int paramInt1, int paramInt2, Point paramPoint) {
    Point point1 = this.tabScroller.viewport.getLocation();
    Point point2 = this.tabScroller.viewport.getViewPosition();
    paramPoint.x = paramInt1 - point1.x + point2.x;
    paramPoint.y = paramInt2 - point1.y + point2.y;
    return paramPoint;
  }
  
  protected Component getVisibleComponent() { return this.visibleComponent; }
  
  protected void setVisibleComponent(Component paramComponent) {
    if (this.visibleComponent != null && this.visibleComponent != paramComponent && this.visibleComponent.getParent() == this.tabPane && this.visibleComponent.isVisible())
      this.visibleComponent.setVisible(false); 
    if (paramComponent != null && !paramComponent.isVisible())
      paramComponent.setVisible(true); 
    this.visibleComponent = paramComponent;
  }
  
  protected void assureRectsCreated(int paramInt) {
    int i = this.rects.length;
    if (paramInt != i) {
      Rectangle[] arrayOfRectangle = new Rectangle[paramInt];
      System.arraycopy(this.rects, 0, arrayOfRectangle, 0, Math.min(i, paramInt));
      this.rects = arrayOfRectangle;
      for (int j = i; j < paramInt; j++)
        this.rects[j] = new Rectangle(); 
    } 
  }
  
  protected void expandTabRunsArray() {
    int i = this.tabRuns.length;
    int[] arrayOfInt = new int[i + 10];
    System.arraycopy(this.tabRuns, 0, arrayOfInt, 0, this.runCount);
    this.tabRuns = arrayOfInt;
  }
  
  protected int getRunForTab(int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.runCount; b++) {
      int i = this.tabRuns[b];
      int j = lastTabInRun(paramInt1, b);
      if (paramInt2 >= i && paramInt2 <= j)
        return b; 
    } 
    return 0;
  }
  
  protected int lastTabInRun(int paramInt1, int paramInt2) {
    if (this.runCount == 1)
      return paramInt1 - 1; 
    boolean bool = (paramInt2 == this.runCount - 1) ? 0 : (paramInt2 + 1);
    return (this.tabRuns[bool] == 0) ? (paramInt1 - 1) : (this.tabRuns[bool] - 1);
  }
  
  protected int getTabRunOverlay(int paramInt) { return this.tabRunOverlay; }
  
  protected int getTabRunIndent(int paramInt1, int paramInt2) { return 0; }
  
  protected boolean shouldPadTabRun(int paramInt1, int paramInt2) { return (this.runCount > 1); }
  
  protected boolean shouldRotateTabRuns(int paramInt) { return true; }
  
  protected Icon getIconForTab(int paramInt) { return (!this.tabPane.isEnabled() || !this.tabPane.isEnabledAt(paramInt)) ? this.tabPane.getDisabledIconAt(paramInt) : this.tabPane.getIconAt(paramInt); }
  
  protected View getTextViewForTab(int paramInt) { return (this.htmlViews != null) ? (View)this.htmlViews.elementAt(paramInt) : null; }
  
  protected int calculateTabHeight(int paramInt1, int paramInt2, int paramInt3) {
    null = 0;
    Component component = this.tabPane.getTabComponentAt(paramInt2);
    if (component != null) {
      null = (component.getPreferredSize()).height;
    } else {
      View view = getTextViewForTab(paramInt2);
      if (view != null) {
        null += (int)view.getPreferredSpan(1);
      } else {
        null += paramInt3;
      } 
      Icon icon = getIconForTab(paramInt2);
      if (icon != null)
        null = Math.max(null, icon.getIconHeight()); 
    } 
    Insets insets = getTabInsets(paramInt1, paramInt2);
    return insets.top + insets.bottom + 2;
  }
  
  protected int calculateMaxTabHeight(int paramInt) {
    FontMetrics fontMetrics = getFontMetrics();
    int i = this.tabPane.getTabCount();
    int j = 0;
    int k = fontMetrics.getHeight();
    for (byte b = 0; b < i; b++)
      j = Math.max(calculateTabHeight(paramInt, b, k), j); 
    return j;
  }
  
  protected int calculateTabWidth(int paramInt1, int paramInt2, FontMetrics paramFontMetrics) {
    Insets insets = getTabInsets(paramInt1, paramInt2);
    int i = insets.left + insets.right + 3;
    Component component = this.tabPane.getTabComponentAt(paramInt2);
    if (component != null) {
      i += (component.getPreferredSize()).width;
    } else {
      Icon icon = getIconForTab(paramInt2);
      if (icon != null)
        i += icon.getIconWidth() + this.textIconGap; 
      View view = getTextViewForTab(paramInt2);
      if (view != null) {
        i += (int)view.getPreferredSpan(0);
      } else {
        String str = this.tabPane.getTitleAt(paramInt2);
        i += SwingUtilities2.stringWidth(this.tabPane, paramFontMetrics, str);
      } 
    } 
    return i;
  }
  
  protected int calculateMaxTabWidth(int paramInt) {
    FontMetrics fontMetrics = getFontMetrics();
    int i = this.tabPane.getTabCount();
    int j = 0;
    for (byte b = 0; b < i; b++)
      j = Math.max(calculateTabWidth(paramInt, b, fontMetrics), j); 
    return j;
  }
  
  protected int calculateTabAreaHeight(int paramInt1, int paramInt2, int paramInt3) {
    Insets insets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return (paramInt2 > 0) ? (paramInt2 * (paramInt3 - i) + i + insets.top + insets.bottom) : 0;
  }
  
  protected int calculateTabAreaWidth(int paramInt1, int paramInt2, int paramInt3) {
    Insets insets = getTabAreaInsets(paramInt1);
    int i = getTabRunOverlay(paramInt1);
    return (paramInt2 > 0) ? (paramInt2 * (paramInt3 - i) + i + insets.left + insets.right) : 0;
  }
  
  protected Insets getTabInsets(int paramInt1, int paramInt2) { return this.tabInsets; }
  
  protected Insets getSelectedTabPadInsets(int paramInt) {
    rotateInsets(this.selectedTabPadInsets, this.currentPadInsets, paramInt);
    return this.currentPadInsets;
  }
  
  protected Insets getTabAreaInsets(int paramInt) {
    rotateInsets(this.tabAreaInsets, this.currentTabAreaInsets, paramInt);
    return this.currentTabAreaInsets;
  }
  
  protected Insets getContentBorderInsets(int paramInt) { return this.contentBorderInsets; }
  
  protected FontMetrics getFontMetrics() {
    Font font = this.tabPane.getFont();
    return this.tabPane.getFontMetrics(font);
  }
  
  protected void navigateSelectedTab(int paramInt) {
    int m;
    int i = this.tabPane.getTabPlacement();
    int j = DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? this.tabPane.getSelectedIndex() : getFocusIndex();
    int k = this.tabPane.getTabCount();
    boolean bool = BasicGraphicsUtils.isLeftToRight(this.tabPane);
    if (k <= 0)
      return; 
    switch (i) {
      case 2:
      case 4:
        switch (paramInt) {
          case 12:
            selectNextTab(j);
            break;
          case 13:
            selectPreviousTab(j);
            break;
          case 1:
            selectPreviousTabInRun(j);
            break;
          case 5:
            selectNextTabInRun(j);
            break;
          case 7:
            m = getTabRunOffset(i, k, j, false);
            selectAdjacentRunTab(i, j, m);
            break;
          case 3:
            m = getTabRunOffset(i, k, j, true);
            selectAdjacentRunTab(i, j, m);
            break;
        } 
        return;
    } 
    switch (paramInt) {
      case 12:
        selectNextTab(j);
        break;
      case 13:
        selectPreviousTab(j);
        break;
      case 1:
        m = getTabRunOffset(i, k, j, false);
        selectAdjacentRunTab(i, j, m);
        break;
      case 5:
        m = getTabRunOffset(i, k, j, true);
        selectAdjacentRunTab(i, j, m);
        break;
      case 3:
        if (bool) {
          selectNextTabInRun(j);
          break;
        } 
        selectPreviousTabInRun(j);
        break;
      case 7:
        if (bool) {
          selectPreviousTabInRun(j);
          break;
        } 
        selectNextTabInRun(j);
        break;
    } 
  }
  
  protected void selectNextTabInRun(int paramInt) {
    int i = this.tabPane.getTabCount();
    int j;
    for (j = getNextTabIndexInRun(i, paramInt); j != paramInt && !this.tabPane.isEnabledAt(j); j = getNextTabIndexInRun(i, j));
    navigateTo(j);
  }
  
  protected void selectPreviousTabInRun(int paramInt) {
    int i = this.tabPane.getTabCount();
    int j;
    for (j = getPreviousTabIndexInRun(i, paramInt); j != paramInt && !this.tabPane.isEnabledAt(j); j = getPreviousTabIndexInRun(i, j));
    navigateTo(j);
  }
  
  protected void selectNextTab(int paramInt) {
    int i;
    for (i = getNextTabIndex(paramInt); i != paramInt && !this.tabPane.isEnabledAt(i); i = getNextTabIndex(i));
    navigateTo(i);
  }
  
  protected void selectPreviousTab(int paramInt) {
    int i;
    for (i = getPreviousTabIndex(paramInt); i != paramInt && !this.tabPane.isEnabledAt(i); i = getPreviousTabIndex(i));
    navigateTo(i);
  }
  
  protected void selectAdjacentRunTab(int paramInt1, int paramInt2, int paramInt3) {
    int i;
    if (this.runCount < 2)
      return; 
    Rectangle rectangle = this.rects[paramInt2];
    switch (paramInt1) {
      case 2:
      case 4:
        i = tabForCoordinate(this.tabPane, rectangle.x + rectangle.width / 2 + paramInt3, rectangle.y + rectangle.height / 2);
        break;
      default:
        i = tabForCoordinate(this.tabPane, rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2 + paramInt3);
        break;
    } 
    if (i != -1) {
      while (!this.tabPane.isEnabledAt(i) && i != paramInt2)
        i = getNextTabIndex(i); 
      navigateTo(i);
    } 
  }
  
  private void navigateTo(int paramInt) {
    if (DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true)) {
      this.tabPane.setSelectedIndex(paramInt);
    } else {
      setFocusIndex(paramInt, true);
    } 
  }
  
  void setFocusIndex(int paramInt, boolean paramBoolean) {
    if (paramBoolean && !this.isRunsDirty) {
      repaintTab(this.focusIndex);
      this.focusIndex = paramInt;
      repaintTab(this.focusIndex);
    } else {
      this.focusIndex = paramInt;
    } 
  }
  
  private void repaintTab(int paramInt) {
    if (!this.isRunsDirty && paramInt >= 0 && paramInt < this.tabPane.getTabCount())
      this.tabPane.repaint(getTabBounds(this.tabPane, paramInt)); 
  }
  
  private void validateFocusIndex() {
    if (this.focusIndex >= this.tabPane.getTabCount())
      setFocusIndex(this.tabPane.getSelectedIndex(), false); 
  }
  
  protected int getFocusIndex() { return this.focusIndex; }
  
  protected int getTabRunOffset(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    int j;
    int i = getRunForTab(paramInt2, paramInt3);
    switch (paramInt1) {
      case 2:
        if (i == 0) {
          j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
        } else if (i == this.runCount - 1) {
          j = paramBoolean ? this.maxTabWidth : (calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth);
        } else {
          j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
        } 
        return j;
      case 4:
        if (i == 0) {
          j = paramBoolean ? this.maxTabWidth : (calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth);
        } else if (i == this.runCount - 1) {
          j = paramBoolean ? -(calculateTabAreaWidth(paramInt1, this.runCount, this.maxTabWidth) - this.maxTabWidth) : -this.maxTabWidth;
        } else {
          j = paramBoolean ? this.maxTabWidth : -this.maxTabWidth;
        } 
        return j;
      case 3:
        if (i == 0) {
          j = paramBoolean ? this.maxTabHeight : (calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight);
        } else if (i == this.runCount - 1) {
          j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
        } else {
          j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
        } 
        return j;
    } 
    if (i == 0) {
      j = paramBoolean ? -(calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight) : -this.maxTabHeight;
    } else if (i == this.runCount - 1) {
      j = paramBoolean ? this.maxTabHeight : (calculateTabAreaHeight(paramInt1, this.runCount, this.maxTabHeight) - this.maxTabHeight);
    } else {
      j = paramBoolean ? this.maxTabHeight : -this.maxTabHeight;
    } 
    return j;
  }
  
  protected int getPreviousTabIndex(int paramInt) {
    int i = (paramInt - 1 >= 0) ? (paramInt - 1) : (this.tabPane.getTabCount() - 1);
    return (i >= 0) ? i : 0;
  }
  
  protected int getNextTabIndex(int paramInt) { return (paramInt + 1) % this.tabPane.getTabCount(); }
  
  protected int getNextTabIndexInRun(int paramInt1, int paramInt2) {
    if (this.runCount < 2)
      return getNextTabIndex(paramInt2); 
    int i = getRunForTab(paramInt1, paramInt2);
    int j = getNextTabIndex(paramInt2);
    return (j == this.tabRuns[getNextTabRun(i)]) ? this.tabRuns[i] : j;
  }
  
  protected int getPreviousTabIndexInRun(int paramInt1, int paramInt2) {
    if (this.runCount < 2)
      return getPreviousTabIndex(paramInt2); 
    int i = getRunForTab(paramInt1, paramInt2);
    if (paramInt2 == this.tabRuns[i]) {
      int j = this.tabRuns[getNextTabRun(i)] - 1;
      return (j != -1) ? j : (paramInt1 - 1);
    } 
    return getPreviousTabIndex(paramInt2);
  }
  
  protected int getPreviousTabRun(int paramInt) {
    int i = (paramInt - 1 >= 0) ? (paramInt - 1) : (this.runCount - 1);
    return (i >= 0) ? i : 0;
  }
  
  protected int getNextTabRun(int paramInt) { return (paramInt + 1) % this.runCount; }
  
  protected static void rotateInsets(Insets paramInsets1, Insets paramInsets2, int paramInt) {
    switch (paramInt) {
      case 2:
        paramInsets2.top = paramInsets1.left;
        paramInsets2.left = paramInsets1.top;
        paramInsets2.bottom = paramInsets1.right;
        paramInsets2.right = paramInsets1.bottom;
        return;
      case 3:
        paramInsets2.top = paramInsets1.bottom;
        paramInsets2.left = paramInsets1.left;
        paramInsets2.bottom = paramInsets1.top;
        paramInsets2.right = paramInsets1.right;
        return;
      case 4:
        paramInsets2.top = paramInsets1.left;
        paramInsets2.left = paramInsets1.bottom;
        paramInsets2.bottom = paramInsets1.right;
        paramInsets2.right = paramInsets1.top;
        return;
    } 
    paramInsets2.top = paramInsets1.top;
    paramInsets2.left = paramInsets1.left;
    paramInsets2.bottom = paramInsets1.bottom;
    paramInsets2.right = paramInsets1.right;
  }
  
  boolean requestFocusForVisibleComponent() { return SwingUtilities2.tabbedPaneChangeFocusTo(getVisibleComponent()); }
  
  private Vector<View> createHTMLVector() {
    Vector vector = new Vector();
    int i = this.tabPane.getTabCount();
    if (i > 0)
      for (byte b = 0; b < i; b++) {
        String str = this.tabPane.getTitleAt(b);
        if (BasicHTML.isHTMLString(str)) {
          vector.addElement(BasicHTML.createHTMLView(this.tabPane, str));
        } else {
          vector.addElement(null);
        } 
      }  
    return vector;
  }
  
  private static class Actions extends UIAction {
    static final String NEXT = "navigateNext";
    
    static final String PREVIOUS = "navigatePrevious";
    
    static final String RIGHT = "navigateRight";
    
    static final String LEFT = "navigateLeft";
    
    static final String UP = "navigateUp";
    
    static final String DOWN = "navigateDown";
    
    static final String PAGE_UP = "navigatePageUp";
    
    static final String PAGE_DOWN = "navigatePageDown";
    
    static final String REQUEST_FOCUS = "requestFocus";
    
    static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
    
    static final String SET_SELECTED = "setSelectedIndex";
    
    static final String SELECT_FOCUSED = "selectTabWithFocus";
    
    static final String SCROLL_FORWARD = "scrollTabsForwardAction";
    
    static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      JTabbedPane jTabbedPane = (JTabbedPane)param1ActionEvent.getSource();
      BasicTabbedPaneUI basicTabbedPaneUI = (BasicTabbedPaneUI)BasicLookAndFeel.getUIOfType(jTabbedPane.getUI(), BasicTabbedPaneUI.class);
      if (basicTabbedPaneUI == null)
        return; 
      if (str == "navigateNext") {
        basicTabbedPaneUI.navigateSelectedTab(12);
      } else if (str == "navigatePrevious") {
        basicTabbedPaneUI.navigateSelectedTab(13);
      } else if (str == "navigateRight") {
        basicTabbedPaneUI.navigateSelectedTab(3);
      } else if (str == "navigateLeft") {
        basicTabbedPaneUI.navigateSelectedTab(7);
      } else if (str == "navigateUp") {
        basicTabbedPaneUI.navigateSelectedTab(1);
      } else if (str == "navigateDown") {
        basicTabbedPaneUI.navigateSelectedTab(5);
      } else if (str == "navigatePageUp") {
        int i = jTabbedPane.getTabPlacement();
        if (i == 1 || i == 3) {
          basicTabbedPaneUI.navigateSelectedTab(7);
        } else {
          basicTabbedPaneUI.navigateSelectedTab(1);
        } 
      } else if (str == "navigatePageDown") {
        int i = jTabbedPane.getTabPlacement();
        if (i == 1 || i == 3) {
          basicTabbedPaneUI.navigateSelectedTab(3);
        } else {
          basicTabbedPaneUI.navigateSelectedTab(5);
        } 
      } else if (str == "requestFocus") {
        jTabbedPane.requestFocus();
      } else if (str == "requestFocusForVisibleComponent") {
        basicTabbedPaneUI.requestFocusForVisibleComponent();
      } else if (str == "setSelectedIndex") {
        String str1 = param1ActionEvent.getActionCommand();
        if (str1 != null && str1.length() > 0) {
          char c = param1ActionEvent.getActionCommand().charAt(0);
          if (c >= 'a' && c <= 'z')
            c -= ' '; 
          Integer integer = (Integer)basicTabbedPaneUI.mnemonicToIndexMap.get(Integer.valueOf(c));
          if (integer != null && jTabbedPane.isEnabledAt(integer.intValue()))
            jTabbedPane.setSelectedIndex(integer.intValue()); 
        } 
      } else if (str == "selectTabWithFocus") {
        int i = basicTabbedPaneUI.getFocusIndex();
        if (i != -1)
          jTabbedPane.setSelectedIndex(i); 
      } else if (str == "scrollTabsForwardAction") {
        if (basicTabbedPaneUI.scrollableTabLayoutEnabled())
          basicTabbedPaneUI.tabScroller.scrollForward(jTabbedPane.getTabPlacement()); 
      } else if (str == "scrollTabsBackwardAction" && basicTabbedPaneUI.scrollableTabLayoutEnabled()) {
        basicTabbedPaneUI.tabScroller.scrollBackward(jTabbedPane.getTabPlacement());
      } 
    }
  }
  
  private class CroppedEdge extends JPanel implements UIResource {
    private Shape shape;
    
    private int tabIndex;
    
    private int cropline;
    
    private int cropx;
    
    private int cropy;
    
    public CroppedEdge() { setOpaque(false); }
    
    public void setParams(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.tabIndex = param1Int1;
      this.cropline = param1Int2;
      this.cropx = param1Int3;
      this.cropy = param1Int4;
      Rectangle rectangle = BasicTabbedPaneUI.this.rects[param1Int1];
      setBounds(rectangle);
      this.shape = BasicTabbedPaneUI.createCroppedTabShape(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), rectangle, param1Int2);
      if (getParent() == null && BasicTabbedPaneUI.this.tabContainer != null)
        BasicTabbedPaneUI.this.tabContainer.add(this, 0); 
    }
    
    public void resetParams() {
      this.shape = null;
      if (getParent() == BasicTabbedPaneUI.this.tabContainer && BasicTabbedPaneUI.this.tabContainer != null)
        BasicTabbedPaneUI.this.tabContainer.remove(this); 
    }
    
    public boolean isParamsSet() { return (this.shape != null); }
    
    public int getTabIndex() { return this.tabIndex; }
    
    public int getCropline() { return this.cropline; }
    
    public int getCroppedSideWidth() { return 3; }
    
    private Color getBgColor() {
      Container container = BasicTabbedPaneUI.this.tabPane.getParent();
      if (container != null) {
        Color color = container.getBackground();
        if (color != null)
          return color; 
      } 
      return UIManager.getColor("control");
    }
    
    protected void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      if (isParamsSet() && param1Graphics instanceof Graphics2D) {
        Graphics2D graphics2D = (Graphics2D)param1Graphics;
        graphics2D.clipRect(0, 0, getWidth(), getHeight());
        graphics2D.setColor(getBgColor());
        graphics2D.translate(this.cropx, this.cropy);
        graphics2D.fill(this.shape);
        BasicTabbedPaneUI.this.paintCroppedTabEdge(param1Graphics);
        graphics2D.translate(-this.cropx, -this.cropy);
      } 
    }
  }
  
  public class FocusHandler extends FocusAdapter {
    public void focusGained(FocusEvent param1FocusEvent) { BasicTabbedPaneUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicTabbedPaneUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      JTabbedPane jTabbedPane = (JTabbedPane)param1PropertyChangeEvent.getSource();
      String str = param1PropertyChangeEvent.getPropertyName();
      boolean bool = BasicTabbedPaneUI.this.scrollableTabLayoutEnabled();
      if (str == "mnemonicAt") {
        BasicTabbedPaneUI.this.updateMnemonics();
        jTabbedPane.repaint();
      } else if (str == "displayedMnemonicIndexAt") {
        jTabbedPane.repaint();
      } else if (str == "indexForTitle") {
        BasicTabbedPaneUI.this.calculatedBaseline = false;
        Integer integer = (Integer)param1PropertyChangeEvent.getNewValue();
        if (BasicTabbedPaneUI.this.htmlViews != null)
          BasicTabbedPaneUI.this.htmlViews.removeElementAt(integer.intValue()); 
        updateHtmlViews(integer.intValue());
      } else if (str == "tabLayoutPolicy") {
        BasicTabbedPaneUI.this.uninstallUI(jTabbedPane);
        BasicTabbedPaneUI.this.installUI(jTabbedPane);
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      } else if (str == "tabPlacement") {
        if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
          BasicTabbedPaneUI.this.tabScroller.createButtons(); 
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      } else if (str == "opaque" && bool) {
        boolean bool1 = ((Boolean)param1PropertyChangeEvent.getNewValue()).booleanValue();
        this.this$0.tabScroller.tabPanel.setOpaque(bool1);
        this.this$0.tabScroller.viewport.setOpaque(bool1);
      } else if (str == "background" && bool) {
        Color color1 = (Color)param1PropertyChangeEvent.getNewValue();
        this.this$0.tabScroller.tabPanel.setBackground(color1);
        this.this$0.tabScroller.viewport.setBackground(color1);
        Color color2 = (BasicTabbedPaneUI.this.selectedColor == null) ? color1 : BasicTabbedPaneUI.this.selectedColor;
        this.this$0.tabScroller.scrollForwardButton.setBackground(color2);
        this.this$0.tabScroller.scrollBackwardButton.setBackground(color2);
      } else if (str == "indexForTabComponent") {
        if (BasicTabbedPaneUI.this.tabContainer != null)
          BasicTabbedPaneUI.this.tabContainer.removeUnusedTabComponents(); 
        Component component = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(((Integer)param1PropertyChangeEvent.getNewValue()).intValue());
        if (component != null)
          if (BasicTabbedPaneUI.this.tabContainer == null) {
            BasicTabbedPaneUI.this.installTabContainer();
          } else {
            BasicTabbedPaneUI.this.tabContainer.add(component);
          }  
        BasicTabbedPaneUI.this.tabPane.revalidate();
        BasicTabbedPaneUI.this.tabPane.repaint();
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      } else if (str == "indexForNullComponent") {
        BasicTabbedPaneUI.this.isRunsDirty = true;
        updateHtmlViews(((Integer)param1PropertyChangeEvent.getNewValue()).intValue());
      } else if (str == "font") {
        BasicTabbedPaneUI.this.calculatedBaseline = false;
      } 
    }
    
    private void updateHtmlViews(int param1Int) {
      String str = BasicTabbedPaneUI.this.tabPane.getTitleAt(param1Int);
      boolean bool = BasicHTML.isHTMLString(str);
      if (bool) {
        if (BasicTabbedPaneUI.this.htmlViews == null) {
          BasicTabbedPaneUI.this.htmlViews = BasicTabbedPaneUI.this.createHTMLVector();
        } else {
          View view = BasicHTML.createHTMLView(BasicTabbedPaneUI.this.tabPane, str);
          BasicTabbedPaneUI.this.htmlViews.insertElementAt(view, param1Int);
        } 
      } else if (BasicTabbedPaneUI.this.htmlViews != null) {
        BasicTabbedPaneUI.this.htmlViews.insertElementAt(null, param1Int);
      } 
      BasicTabbedPaneUI.this.updateMnemonics();
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      JTabbedPane jTabbedPane = (JTabbedPane)param1ChangeEvent.getSource();
      jTabbedPane.revalidate();
      jTabbedPane.repaint();
      BasicTabbedPaneUI.this.setFocusIndex(jTabbedPane.getSelectedIndex(), false);
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
        BasicTabbedPaneUI.this.ensureCurrentLayout();
        int i = jTabbedPane.getSelectedIndex();
        if (i < BasicTabbedPaneUI.this.rects.length && i != -1)
          this.this$0.tabScroller.tabPanel.scrollRectToVisible((Rectangle)BasicTabbedPaneUI.this.rects[i].clone()); 
      } 
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mouseReleased(MouseEvent param1MouseEvent) {}
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicTabbedPaneUI.this.setRolloverTab(param1MouseEvent.getX(), param1MouseEvent.getY()); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicTabbedPaneUI.this.setRolloverTab(-1); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (!BasicTabbedPaneUI.this.tabPane.isEnabled())
        return; 
      int i = BasicTabbedPaneUI.this.tabForCoordinate(BasicTabbedPaneUI.this.tabPane, param1MouseEvent.getX(), param1MouseEvent.getY());
      if (i >= 0 && BasicTabbedPaneUI.this.tabPane.isEnabledAt(i))
        if (i != BasicTabbedPaneUI.this.tabPane.getSelectedIndex()) {
          BasicTabbedPaneUI.this.tabPane.setSelectedIndex(i);
        } else if (BasicTabbedPaneUI.this.tabPane.isRequestFocusEnabled()) {
          BasicTabbedPaneUI.this.tabPane.requestFocus();
        }  
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {}
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicTabbedPaneUI.this.setRolloverTab(param1MouseEvent.getX(), param1MouseEvent.getY()); }
    
    public void focusGained(FocusEvent param1FocusEvent) { BasicTabbedPaneUI.this.setFocusIndex(BasicTabbedPaneUI.this.tabPane.getSelectedIndex(), true); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicTabbedPaneUI.this.repaintTab(BasicTabbedPaneUI.this.focusIndex); }
    
    public void componentAdded(ContainerEvent param1ContainerEvent) {
      JTabbedPane jTabbedPane = (JTabbedPane)param1ContainerEvent.getContainer();
      Component component = param1ContainerEvent.getChild();
      if (component instanceof UIResource)
        return; 
      BasicTabbedPaneUI.this.isRunsDirty = true;
      updateHtmlViews(jTabbedPane.indexOfComponent(component));
    }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) {
      JTabbedPane jTabbedPane = (JTabbedPane)param1ContainerEvent.getContainer();
      Component component = param1ContainerEvent.getChild();
      if (component instanceof UIResource)
        return; 
      Integer integer = (Integer)jTabbedPane.getClientProperty("__index_to_remove__");
      if (integer != null) {
        int i = integer.intValue();
        if (BasicTabbedPaneUI.this.htmlViews != null && BasicTabbedPaneUI.this.htmlViews.size() > i)
          BasicTabbedPaneUI.this.htmlViews.removeElementAt(i); 
        jTabbedPane.putClientProperty("__index_to_remove__", null);
      } 
      BasicTabbedPaneUI.this.isRunsDirty = true;
      BasicTabbedPaneUI.this.updateMnemonics();
      BasicTabbedPaneUI.this.validateFocusIndex();
    }
  }
  
  public class MouseHandler extends MouseAdapter {
    public void mousePressed(MouseEvent param1MouseEvent) { BasicTabbedPaneUI.this.getHandler().mousePressed(param1MouseEvent); }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicTabbedPaneUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  private class ScrollableTabButton extends BasicArrowButton implements UIResource, SwingConstants {
    public ScrollableTabButton(int param1Int) { super(param1Int, UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight")); }
  }
  
  private class ScrollableTabPanel extends JPanel implements UIResource {
    public ScrollableTabPanel() {
      super(null);
      setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
      Color color = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (color == null)
        color = BasicTabbedPaneUI.this.tabPane.getBackground(); 
      setBackground(color);
    }
    
    public void paintComponent(Graphics param1Graphics) {
      super.paintComponent(param1Graphics);
      BasicTabbedPaneUI.this.paintTabArea(param1Graphics, BasicTabbedPaneUI.this.tabPane.getTabPlacement(), BasicTabbedPaneUI.this.tabPane.getSelectedIndex());
      if (this.this$0.tabScroller.croppedEdge.isParamsSet() && BasicTabbedPaneUI.this.tabContainer == null) {
        Rectangle rectangle = BasicTabbedPaneUI.this.rects[this.this$0.tabScroller.croppedEdge.getTabIndex()];
        param1Graphics.translate(rectangle.x, rectangle.y);
        this.this$0.tabScroller.croppedEdge.paintComponent(param1Graphics);
        param1Graphics.translate(-rectangle.x, -rectangle.y);
      } 
    }
    
    public void doLayout() {
      if (getComponentCount() > 0) {
        Component component = getComponent(0);
        component.setBounds(0, 0, getWidth(), getHeight());
      } 
    }
  }
  
  private class ScrollableTabSupport implements ActionListener, ChangeListener {
    public BasicTabbedPaneUI.ScrollableTabViewport viewport;
    
    public BasicTabbedPaneUI.ScrollableTabPanel tabPanel;
    
    public JButton scrollForwardButton;
    
    public JButton scrollBackwardButton;
    
    public BasicTabbedPaneUI.CroppedEdge croppedEdge;
    
    public int leadingTabIndex;
    
    private Point tabViewPosition = new Point(0, 0);
    
    ScrollableTabSupport(int param1Int) {
      this.viewport = new BasicTabbedPaneUI.ScrollableTabViewport(this$0);
      this.tabPanel = new BasicTabbedPaneUI.ScrollableTabPanel(this$0);
      this.viewport.setView(this.tabPanel);
      this.viewport.addChangeListener(this);
      this.croppedEdge = new BasicTabbedPaneUI.CroppedEdge(this$0);
      createButtons();
    }
    
    void createButtons() {
      if (this.scrollForwardButton != null) {
        BasicTabbedPaneUI.this.tabPane.remove(this.scrollForwardButton);
        this.scrollForwardButton.removeActionListener(this);
        BasicTabbedPaneUI.this.tabPane.remove(this.scrollBackwardButton);
        this.scrollBackwardButton.removeActionListener(this);
      } 
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      if (i == 1 || i == 3) {
        this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(3);
        this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(7);
      } else {
        this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(5);
        this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(1);
      } 
      this.scrollForwardButton.addActionListener(this);
      this.scrollBackwardButton.addActionListener(this);
      BasicTabbedPaneUI.this.tabPane.add(this.scrollForwardButton);
      BasicTabbedPaneUI.this.tabPane.add(this.scrollBackwardButton);
    }
    
    public void scrollForward(int param1Int) {
      Dimension dimension = this.viewport.getViewSize();
      Rectangle rectangle = this.viewport.getViewRect();
      if (param1Int == 1 || param1Int == 3) {
        if (rectangle.width >= dimension.width - rectangle.x)
          return; 
      } else if (rectangle.height >= dimension.height - rectangle.y) {
        return;
      } 
      setLeadingTabIndex(param1Int, this.leadingTabIndex + 1);
    }
    
    public void scrollBackward(int param1Int) {
      if (this.leadingTabIndex == 0)
        return; 
      setLeadingTabIndex(param1Int, this.leadingTabIndex - 1);
    }
    
    public void setLeadingTabIndex(int param1Int1, int param1Int2) {
      this.leadingTabIndex = param1Int2;
      Dimension dimension = this.viewport.getViewSize();
      Rectangle rectangle = this.viewport.getViewRect();
      switch (param1Int1) {
        case 1:
        case 3:
          this.tabViewPosition.x = (this.leadingTabIndex == 0) ? 0 : (this.this$0.rects[this.leadingTabIndex]).x;
          if (dimension.width - this.tabViewPosition.x < rectangle.width) {
            Dimension dimension1 = new Dimension(dimension.width - this.tabViewPosition.x, rectangle.height);
            this.viewport.setExtentSize(dimension1);
          } 
          break;
        case 2:
        case 4:
          this.tabViewPosition.y = (this.leadingTabIndex == 0) ? 0 : (this.this$0.rects[this.leadingTabIndex]).y;
          if (dimension.height - this.tabViewPosition.y < rectangle.height) {
            Dimension dimension1 = new Dimension(rectangle.width, dimension.height - this.tabViewPosition.y);
            this.viewport.setExtentSize(dimension1);
          } 
          break;
      } 
      this.viewport.setViewPosition(this.tabViewPosition);
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { updateView(); }
    
    private void updateView() {
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
      BasicTabbedPaneUI.this.assureRectsCreated(j);
      Rectangle rectangle1 = this.viewport.getBounds();
      Dimension dimension = this.viewport.getViewSize();
      Rectangle rectangle2 = this.viewport.getViewRect();
      this.leadingTabIndex = BasicTabbedPaneUI.this.getClosestTab(rectangle2.x, rectangle2.y);
      if (this.leadingTabIndex + 1 < j)
        switch (i) {
          case 1:
          case 3:
            if ((this.this$0.rects[this.leadingTabIndex]).x < rectangle2.x)
              this.leadingTabIndex++; 
            break;
          case 2:
          case 4:
            if ((this.this$0.rects[this.leadingTabIndex]).y < rectangle2.y)
              this.leadingTabIndex++; 
            break;
        }  
      Insets insets = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      switch (i) {
        case 2:
          BasicTabbedPaneUI.this.tabPane.repaint(rectangle1.x + rectangle1.width, rectangle1.y, insets.left, rectangle1.height);
          this.scrollBackwardButton.setEnabled((rectangle2.y > 0 && this.leadingTabIndex > 0));
          this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1 && dimension.height - rectangle2.y > rectangle2.height));
          return;
        case 4:
          BasicTabbedPaneUI.this.tabPane.repaint(rectangle1.x - insets.right, rectangle1.y, insets.right, rectangle1.height);
          this.scrollBackwardButton.setEnabled((rectangle2.y > 0 && this.leadingTabIndex > 0));
          this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1 && dimension.height - rectangle2.y > rectangle2.height));
          return;
        case 3:
          BasicTabbedPaneUI.this.tabPane.repaint(rectangle1.x, rectangle1.y - insets.bottom, rectangle1.width, insets.bottom);
          this.scrollBackwardButton.setEnabled((rectangle2.x > 0 && this.leadingTabIndex > 0));
          this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1 && dimension.width - rectangle2.x > rectangle2.width));
          return;
      } 
      BasicTabbedPaneUI.this.tabPane.repaint(rectangle1.x, rectangle1.y + rectangle1.height, rectangle1.width, insets.top);
      this.scrollBackwardButton.setEnabled((rectangle2.x > 0 && this.leadingTabIndex > 0));
      this.scrollForwardButton.setEnabled((this.leadingTabIndex < j - 1 && dimension.width - rectangle2.x > rectangle2.width));
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      ActionMap actionMap = BasicTabbedPaneUI.this.tabPane.getActionMap();
      if (actionMap != null) {
        String str;
        if (param1ActionEvent.getSource() == this.scrollForwardButton) {
          str = "scrollTabsForwardAction";
        } else {
          str = "scrollTabsBackwardAction";
        } 
        Action action = actionMap.get(str);
        if (action != null && action.isEnabled())
          action.actionPerformed(new ActionEvent(BasicTabbedPaneUI.this.tabPane, 1001, null, param1ActionEvent.getWhen(), param1ActionEvent.getModifiers())); 
      } 
    }
    
    public String toString() { return "viewport.viewSize=" + this.viewport.getViewSize() + "\nviewport.viewRectangle=" + this.viewport.getViewRect() + "\nleadingTabIndex=" + this.leadingTabIndex + "\ntabViewPosition=" + this.tabViewPosition; }
  }
  
  private class ScrollableTabViewport extends JViewport implements UIResource {
    public ScrollableTabViewport() {
      setName("TabbedPane.scrollableViewport");
      setScrollMode(0);
      setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
      Color color = UIManager.getColor("TabbedPane.tabAreaBackground");
      if (color == null)
        color = BasicTabbedPaneUI.this.tabPane.getBackground(); 
      setBackground(color);
    }
  }
  
  private class TabContainer extends JPanel implements UIResource {
    private boolean notifyTabbedPane = true;
    
    public TabContainer() {
      super(null);
      setOpaque(false);
    }
    
    public void remove(Component param1Component) {
      int i = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(param1Component);
      super.remove(param1Component);
      if (this.notifyTabbedPane && i != -1)
        BasicTabbedPaneUI.this.tabPane.setTabComponentAt(i, null); 
    }
    
    private void removeUnusedTabComponents() {
      for (Component component : getComponents()) {
        if (!(component instanceof UIResource)) {
          int i = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(component);
          if (i == -1)
            super.remove(component); 
        } 
      } 
    }
    
    public boolean isOptimizedDrawingEnabled() { return (BasicTabbedPaneUI.this.tabScroller != null && !this.this$0.tabScroller.croppedEdge.isParamsSet()); }
    
    public void doLayout() {
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
        this.this$0.tabScroller.tabPanel.repaint();
        BasicTabbedPaneUI.this.tabScroller.updateView();
      } else {
        BasicTabbedPaneUI.this.tabPane.repaint(getBounds());
      } 
    }
  }
  
  public class TabSelectionHandler implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicTabbedPaneUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  public class TabbedPaneLayout implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return calculateSize(false); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return calculateSize(true); }
    
    protected Dimension calculateSize(boolean param1Boolean) {
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets insets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      Insets insets3 = BasicTabbedPaneUI.this.getTabAreaInsets(i);
      Dimension dimension = new Dimension(0, 0);
      int j = 0;
      int k = 0;
      int m = 0;
      int n = 0;
      int i1;
      for (i1 = 0; i1 < BasicTabbedPaneUI.this.tabPane.getTabCount(); i1++) {
        Component component = BasicTabbedPaneUI.this.tabPane.getComponentAt(i1);
        if (component != null) {
          Dimension dimension1 = param1Boolean ? component.getMinimumSize() : component.getPreferredSize();
          if (dimension1 != null) {
            n = Math.max(dimension1.height, n);
            m = Math.max(dimension1.width, m);
          } 
        } 
      } 
      k += m;
      j += n;
      switch (i) {
        case 2:
        case 4:
          j = Math.max(j, BasicTabbedPaneUI.this.calculateMaxTabHeight(i));
          i1 = preferredTabAreaWidth(i, j - insets3.top - insets3.bottom);
          k += i1;
          return new Dimension(k + insets1.left + insets1.right + insets2.left + insets2.right, j + insets1.bottom + insets1.top + insets2.top + insets2.bottom);
      } 
      k = Math.max(k, BasicTabbedPaneUI.this.calculateMaxTabWidth(i));
      i1 = preferredTabAreaHeight(i, k - insets3.left - insets3.right);
      j += i1;
      return new Dimension(k + insets1.left + insets1.right + insets2.left + insets2.right, j + insets1.bottom + insets1.top + insets2.top + insets2.bottom);
    }
    
    protected int preferredTabAreaHeight(int param1Int1, int param1Int2) {
      FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      int j = 0;
      if (i > 0) {
        byte b1 = 1;
        int k = 0;
        int m = BasicTabbedPaneUI.this.calculateMaxTabHeight(param1Int1);
        for (byte b2 = 0; b2 < i; b2++) {
          int n = BasicTabbedPaneUI.this.calculateTabWidth(param1Int1, b2, fontMetrics);
          if (k && k + n > param1Int2) {
            b1++;
            k = 0;
          } 
          k += n;
        } 
        j = BasicTabbedPaneUI.this.calculateTabAreaHeight(param1Int1, b1, m);
      } 
      return j;
    }
    
    protected int preferredTabAreaWidth(int param1Int1, int param1Int2) {
      FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      int j = 0;
      if (i > 0) {
        byte b1 = 1;
        int k = 0;
        int m = fontMetrics.getHeight();
        BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(param1Int1);
        for (byte b2 = 0; b2 < i; b2++) {
          int n = BasicTabbedPaneUI.this.calculateTabHeight(param1Int1, b2, m);
          if (k && k + n > param1Int2) {
            b1++;
            k = 0;
          } 
          k += n;
        } 
        j = BasicTabbedPaneUI.this.calculateTabAreaWidth(param1Int1, b1, BasicTabbedPaneUI.this.maxTabWidth);
      } 
      return j;
    }
    
    public void layoutContainer(Container param1Container) {
      BasicTabbedPaneUI.this.setRolloverTab(-1);
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      Component component1 = BasicTabbedPaneUI.this.getVisibleComponent();
      calculateLayoutInfo();
      Component component2 = null;
      if (j < 0) {
        if (component1 != null)
          BasicTabbedPaneUI.this.setVisibleComponent(null); 
      } else {
        component2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(j);
      } 
      int k = 0;
      int m = 0;
      Insets insets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      boolean bool = false;
      if (component2 != null) {
        if (component2 != component1 && component1 != null && SwingUtilities.findFocusOwner(component1) != null)
          bool = true; 
        BasicTabbedPaneUI.this.setVisibleComponent(component2);
      } 
      Rectangle rectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
      int n = BasicTabbedPaneUI.this.tabPane.getComponentCount();
      if (n > 0) {
        int i2;
        int i1;
        switch (i) {
          case 2:
            k = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
            i1 = insets1.left + k + insets2.left;
            i2 = insets1.top + insets2.top;
            break;
          case 4:
            k = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
            i1 = insets1.left + insets2.left;
            i2 = insets1.top + insets2.top;
            break;
          case 3:
            m = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
            i1 = insets1.left + insets2.left;
            i2 = insets1.top + insets2.top;
            break;
          default:
            m = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
            i1 = insets1.left + insets2.left;
            i2 = insets1.top + m + insets2.top;
            break;
        } 
        int i3 = rectangle.width - k - insets1.left - insets1.right - insets2.left - insets2.right;
        int i4 = rectangle.height - m - insets1.top - insets1.bottom - insets2.top - insets2.bottom;
        for (byte b = 0; b < n; b++) {
          Component component = BasicTabbedPaneUI.this.tabPane.getComponent(b);
          if (component == BasicTabbedPaneUI.this.tabContainer) {
            int i5 = (k == 0) ? rectangle.width : (k + insets1.left + insets1.right + insets2.left + insets2.right);
            int i6 = (m == 0) ? rectangle.height : (m + insets1.top + insets1.bottom + insets2.top + insets2.bottom);
            int i7 = 0;
            int i8 = 0;
            if (i == 3) {
              i8 = rectangle.height - i6;
            } else if (i == 4) {
              i7 = rectangle.width - i5;
            } 
            component.setBounds(i7, i8, i5, i6);
          } else {
            component.setBounds(i1, i2, i3, i4);
          } 
        } 
      } 
      layoutTabComponents();
      if (bool && !BasicTabbedPaneUI.this.requestFocusForVisibleComponent())
        BasicTabbedPaneUI.this.tabPane.requestFocus(); 
    }
    
    public void calculateLayoutInfo() {
      int i = BasicTabbedPaneUI.this.tabPane.getTabCount();
      BasicTabbedPaneUI.this.assureRectsCreated(i);
      calculateTabRects(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
      BasicTabbedPaneUI.this.isRunsDirty = false;
    }
    
    private void layoutTabComponents() {
      if (BasicTabbedPaneUI.this.tabContainer == null)
        return; 
      Rectangle rectangle = new Rectangle();
      Point point = new Point(-BasicTabbedPaneUI.this.tabContainer.getX(), -BasicTabbedPaneUI.this.tabContainer.getY());
      if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled())
        BasicTabbedPaneUI.this.translatePointToTabPanel(0, 0, point); 
      for (byte b = 0; b < BasicTabbedPaneUI.this.tabPane.getTabCount(); b++) {
        Component component = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(b);
        if (component != null) {
          BasicTabbedPaneUI.this.getTabBounds(b, rectangle);
          Dimension dimension = component.getPreferredSize();
          Insets insets = BasicTabbedPaneUI.this.getTabInsets(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), b);
          int i = rectangle.x + insets.left + point.x;
          int j = rectangle.y + insets.top + point.y;
          int k = rectangle.width - insets.left - insets.right;
          int m = rectangle.height - insets.top - insets.bottom;
          int n = i + (k - dimension.width) / 2;
          int i1 = j + (m - dimension.height) / 2;
          int i2 = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
          boolean bool = (b == BasicTabbedPaneUI.this.tabPane.getSelectedIndex());
          component.setBounds(n + BasicTabbedPaneUI.this.getTabLabelShiftX(i2, b, bool), i1 + BasicTabbedPaneUI.this.getTabLabelShiftY(i2, b, bool), dimension.width, dimension.height);
        } 
      } 
    }
    
    protected void calculateTabRects(int param1Int1, int param1Int2) {
      int i2;
      int i1;
      int n;
      FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      Dimension dimension = BasicTabbedPaneUI.this.tabPane.getSize();
      Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets insets2 = BasicTabbedPaneUI.this.getTabAreaInsets(param1Int1);
      int i = fontMetrics.getHeight();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      boolean bool = (param1Int1 == 2 || param1Int1 == 4) ? 1 : 0;
      boolean bool1 = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
      switch (param1Int1) {
        case 2:
          BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(param1Int1);
          n = insets1.left + insets2.left;
          i1 = insets1.top + insets2.top;
          i2 = dimension.height - insets1.bottom + insets2.bottom;
          break;
        case 4:
          BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(param1Int1);
          n = dimension.width - insets1.right - insets2.right - BasicTabbedPaneUI.this.maxTabWidth;
          i1 = insets1.top + insets2.top;
          i2 = dimension.height - insets1.bottom + insets2.bottom;
          break;
        case 3:
          BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(param1Int1);
          n = insets1.left + insets2.left;
          i1 = dimension.height - insets1.bottom - insets2.bottom - BasicTabbedPaneUI.this.maxTabHeight;
          i2 = dimension.width - insets1.right + insets2.right;
          break;
        default:
          BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(param1Int1);
          n = insets1.left + insets2.left;
          i1 = insets1.top + insets2.top;
          i2 = dimension.width - insets1.right + insets2.right;
          break;
      } 
      int k = BasicTabbedPaneUI.this.getTabRunOverlay(param1Int1);
      BasicTabbedPaneUI.this.runCount = 0;
      BasicTabbedPaneUI.this.selectedRun = -1;
      if (param1Int2 == 0)
        return; 
      int m;
      for (m = 0; m < param1Int2; m++) {
        Rectangle rectangle = BasicTabbedPaneUI.this.rects[m];
        if (!bool) {
          if (m) {
            (this.this$0.rects[m - true]).x += (this.this$0.rects[m - true]).width;
          } else {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.runCount = 1;
            BasicTabbedPaneUI.this.maxTabWidth = 0;
            rectangle.x = n;
          } 
          rectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(param1Int1, m, fontMetrics);
          BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, rectangle.width);
          if (rectangle.x != n && rectangle.x + rectangle.width > i2) {
            if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1)
              BasicTabbedPaneUI.this.expandTabRunsArray(); 
            BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
            BasicTabbedPaneUI.this.runCount++;
            rectangle.x = n;
          } 
          rectangle.y = i1;
          rectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
        } else {
          if (m > 0) {
            (this.this$0.rects[m - 1]).y += (this.this$0.rects[m - 1]).height;
          } else {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.runCount = 1;
            BasicTabbedPaneUI.this.maxTabHeight = 0;
            rectangle.y = i1;
          } 
          rectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(param1Int1, m, i);
          BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, rectangle.height);
          if (rectangle.y != i1 && rectangle.y + rectangle.height > i2) {
            if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1)
              BasicTabbedPaneUI.this.expandTabRunsArray(); 
            BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = m;
            BasicTabbedPaneUI.this.runCount++;
            rectangle.y = i1;
          } 
          rectangle.x = n;
          rectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
        } 
        if (m == j)
          BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.runCount - 1; 
      } 
      if (BasicTabbedPaneUI.this.runCount > 1) {
        normalizeTabRuns(param1Int1, param1Int2, bool ? i1 : n, i2);
        BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.getRunForTab(param1Int2, j);
        if (BasicTabbedPaneUI.this.shouldRotateTabRuns(param1Int1))
          rotateTabRuns(param1Int1, BasicTabbedPaneUI.this.selectedRun); 
      } 
      for (m = BasicTabbedPaneUI.this.runCount - 1; m >= 0; m--) {
        int i3 = BasicTabbedPaneUI.this.tabRuns[m];
        int i4 = BasicTabbedPaneUI.this.tabRuns[(m == BasicTabbedPaneUI.this.runCount - 1) ? 0 : (m + 1)];
        int i5 = (i4 != 0) ? (i4 - 1) : (param1Int2 - 1);
        if (!bool) {
          for (int i6 = i3; i6 <= i5; i6++) {
            Rectangle rectangle = BasicTabbedPaneUI.this.rects[i6];
            rectangle.y = i1;
            rectangle.x += BasicTabbedPaneUI.this.getTabRunIndent(param1Int1, m);
          } 
          if (BasicTabbedPaneUI.this.shouldPadTabRun(param1Int1, m))
            padTabRun(param1Int1, i3, i5, i2); 
          if (param1Int1 == 3) {
            i1 -= BasicTabbedPaneUI.this.maxTabHeight - k;
          } else {
            i1 += BasicTabbedPaneUI.this.maxTabHeight - k;
          } 
        } else {
          for (int i6 = i3; i6 <= i5; i6++) {
            Rectangle rectangle = BasicTabbedPaneUI.this.rects[i6];
            rectangle.x = n;
            rectangle.y += BasicTabbedPaneUI.this.getTabRunIndent(param1Int1, m);
          } 
          if (BasicTabbedPaneUI.this.shouldPadTabRun(param1Int1, m))
            padTabRun(param1Int1, i3, i5, i2); 
          if (param1Int1 == 4) {
            n -= BasicTabbedPaneUI.this.maxTabWidth - k;
          } else {
            n += BasicTabbedPaneUI.this.maxTabWidth - k;
          } 
        } 
      } 
      padSelectedTab(param1Int1, j);
      if (!bool1 && !bool) {
        int i3 = dimension.width - insets1.right + insets2.right;
        for (m = 0; m < param1Int2; m++)
          (this.this$0.rects[m]).x = i3 - (this.this$0.rects[m]).x - (this.this$0.rects[m]).width; 
      } 
    }
    
    protected void rotateTabRuns(int param1Int1, int param1Int2) {
      for (byte b = 0; b < param1Int2; b++) {
        int i = BasicTabbedPaneUI.this.tabRuns[0];
        for (byte b1 = 1; b1 < BasicTabbedPaneUI.this.runCount; b1++)
          BasicTabbedPaneUI.this.tabRuns[b1 - true] = BasicTabbedPaneUI.this.tabRuns[b1]; 
        BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount - 1] = i;
      } 
    }
    
    protected void normalizeTabRuns(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool1 = (param1Int1 == 2 || param1Int1 == 4) ? 1 : 0;
      int i = BasicTabbedPaneUI.this.runCount - 1;
      boolean bool2 = true;
      double d;
      for (d = 1.25D; bool2; d += 0.25D) {
        int n;
        int m;
        int j = BasicTabbedPaneUI.this.lastTabInRun(param1Int2, i);
        int k = BasicTabbedPaneUI.this.lastTabInRun(param1Int2, i - 1);
        if (!bool1) {
          m = (this.this$0.rects[j]).x + (this.this$0.rects[j]).width;
          n = (int)(BasicTabbedPaneUI.this.maxTabWidth * d);
        } else {
          m = (this.this$0.rects[j]).y + (this.this$0.rects[j]).height;
          n = (int)(BasicTabbedPaneUI.this.maxTabHeight * d * 2.0D);
        } 
        if (param1Int4 - m > n) {
          BasicTabbedPaneUI.this.tabRuns[i] = k;
          if (!bool1) {
            (this.this$0.rects[k]).x = param1Int3;
          } else {
            (this.this$0.rects[k]).y = param1Int3;
          } 
          for (int i1 = k + 1; i1 <= j; i1++) {
            if (!bool1) {
              (this.this$0.rects[i1 - 1]).x += (this.this$0.rects[i1 - 1]).width;
            } else {
              (this.this$0.rects[i1 - 1]).y += (this.this$0.rects[i1 - 1]).height;
            } 
          } 
        } else if (i == BasicTabbedPaneUI.this.runCount - 1) {
          bool2 = false;
        } 
        if (i - 1 > 0) {
          i--;
          continue;
        } 
        i = BasicTabbedPaneUI.this.runCount - 1;
      } 
    }
    
    protected void padTabRun(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rectangle rectangle = BasicTabbedPaneUI.this.rects[param1Int3];
      if (param1Int1 == 1 || param1Int1 == 3) {
        int i = rectangle.x + rectangle.width - (this.this$0.rects[param1Int2]).x;
        int j = param1Int4 - rectangle.x + rectangle.width;
        float f = j / i;
        for (int k = param1Int2; k <= param1Int3; k++) {
          Rectangle rectangle1 = BasicTabbedPaneUI.this.rects[k];
          if (k > param1Int2)
            (this.this$0.rects[k - 1]).x += (this.this$0.rects[k - 1]).width; 
          rectangle1.width += Math.round(rectangle1.width * f);
        } 
        rectangle.width = param1Int4 - rectangle.x;
      } else {
        int i = rectangle.y + rectangle.height - (this.this$0.rects[param1Int2]).y;
        int j = param1Int4 - rectangle.y + rectangle.height;
        float f = j / i;
        for (int k = param1Int2; k <= param1Int3; k++) {
          Rectangle rectangle1 = BasicTabbedPaneUI.this.rects[k];
          if (k > param1Int2)
            (this.this$0.rects[k - 1]).y += (this.this$0.rects[k - 1]).height; 
          rectangle1.height += Math.round(rectangle1.height * f);
        } 
        rectangle.height = param1Int4 - rectangle.y;
      } 
    }
    
    protected void padSelectedTab(int param1Int1, int param1Int2) {
      if (param1Int2 >= 0) {
        Rectangle rectangle = BasicTabbedPaneUI.this.rects[param1Int2];
        Insets insets = BasicTabbedPaneUI.this.getSelectedTabPadInsets(param1Int1);
        rectangle.x -= insets.left;
        rectangle.width += insets.left + insets.right;
        rectangle.y -= insets.top;
        rectangle.height += insets.top + insets.bottom;
        if (!BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
          Dimension dimension = BasicTabbedPaneUI.this.tabPane.getSize();
          Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
          if (param1Int1 == 2 || param1Int1 == 4) {
            int i = insets1.top - rectangle.y;
            if (i > 0) {
              rectangle.y += i;
              rectangle.height -= i;
            } 
            int j = rectangle.y + rectangle.height + insets1.bottom - dimension.height;
            if (j > 0)
              rectangle.height -= j; 
          } else {
            int i = insets1.left - rectangle.x;
            if (i > 0) {
              rectangle.x += i;
              rectangle.width -= i;
            } 
            int j = rectangle.x + rectangle.width + insets1.right - dimension.width;
            if (j > 0)
              rectangle.width -= j; 
          } 
        } 
      } 
    }
  }
  
  private class TabbedPaneScrollLayout extends TabbedPaneLayout {
    private TabbedPaneScrollLayout() { super(BasicTabbedPaneUI.this); }
    
    protected int preferredTabAreaHeight(int param1Int1, int param1Int2) { return BasicTabbedPaneUI.this.calculateMaxTabHeight(param1Int1); }
    
    protected int preferredTabAreaWidth(int param1Int1, int param1Int2) { return BasicTabbedPaneUI.this.calculateMaxTabWidth(param1Int1); }
    
    public void layoutContainer(Container param1Container) {
      BasicTabbedPaneUI.this.setRolloverTab(-1);
      int i = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
      int j = BasicTabbedPaneUI.this.tabPane.getTabCount();
      Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      int k = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      Component component1 = BasicTabbedPaneUI.this.getVisibleComponent();
      calculateLayoutInfo();
      Component component2 = null;
      if (k < 0) {
        if (component1 != null)
          BasicTabbedPaneUI.this.setVisibleComponent(null); 
      } else {
        component2 = BasicTabbedPaneUI.this.tabPane.getComponentAt(k);
      } 
      if (BasicTabbedPaneUI.this.tabPane.getTabCount() == 0) {
        this.this$0.tabScroller.croppedEdge.resetParams();
        this.this$0.tabScroller.scrollForwardButton.setVisible(false);
        this.this$0.tabScroller.scrollBackwardButton.setVisible(false);
        return;
      } 
      boolean bool = false;
      if (component2 != null) {
        if (component2 != component1 && component1 != null && SwingUtilities.findFocusOwner(component1) != null)
          bool = true; 
        BasicTabbedPaneUI.this.setVisibleComponent(component2);
      } 
      Insets insets2 = BasicTabbedPaneUI.this.getContentBorderInsets(i);
      Rectangle rectangle = BasicTabbedPaneUI.this.tabPane.getBounds();
      int m = BasicTabbedPaneUI.this.tabPane.getComponentCount();
      if (m > 0) {
        int i7;
        int i6;
        int i5;
        int i4;
        int i3;
        int i2;
        int i1;
        int n;
        switch (i) {
          case 2:
            i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
            i3 = rectangle.height - insets1.top - insets1.bottom;
            n = insets1.left;
            i1 = insets1.top;
            i4 = n + i2 + insets2.left;
            i5 = i1 + insets2.top;
            i6 = rectangle.width - insets1.left - insets1.right - i2 - insets2.left - insets2.right;
            i7 = rectangle.height - insets1.top - insets1.bottom - insets2.top - insets2.bottom;
            break;
          case 4:
            i2 = BasicTabbedPaneUI.this.calculateTabAreaWidth(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
            i3 = rectangle.height - insets1.top - insets1.bottom;
            n = rectangle.width - insets1.right - i2;
            i1 = insets1.top;
            i4 = insets1.left + insets2.left;
            i5 = insets1.top + insets2.top;
            i6 = rectangle.width - insets1.left - insets1.right - i2 - insets2.left - insets2.right;
            i7 = rectangle.height - insets1.top - insets1.bottom - insets2.top - insets2.bottom;
            break;
          case 3:
            i2 = rectangle.width - insets1.left - insets1.right;
            i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
            n = insets1.left;
            i1 = rectangle.height - insets1.bottom - i3;
            i4 = insets1.left + insets2.left;
            i5 = insets1.top + insets2.top;
            i6 = rectangle.width - insets1.left - insets1.right - insets2.left - insets2.right;
            i7 = rectangle.height - insets1.top - insets1.bottom - i3 - insets2.top - insets2.bottom;
            break;
          default:
            i2 = rectangle.width - insets1.left - insets1.right;
            i3 = BasicTabbedPaneUI.this.calculateTabAreaHeight(i, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
            n = insets1.left;
            i1 = insets1.top;
            i4 = n + insets2.left;
            i5 = i1 + i3 + insets2.top;
            i6 = rectangle.width - insets1.left - insets1.right - insets2.left - insets2.right;
            i7 = rectangle.height - insets1.top - insets1.bottom - i3 - insets2.top - insets2.bottom;
            break;
        } 
        for (byte b = 0; b < m; b++) {
          Component component = BasicTabbedPaneUI.this.tabPane.getComponent(b);
          if (BasicTabbedPaneUI.this.tabScroller != null && component == this.this$0.tabScroller.viewport) {
            int i11;
            int i10;
            JViewport jViewport = (JViewport)component;
            Rectangle rectangle1 = jViewport.getViewRect();
            int i8 = i2;
            int i9 = i3;
            Dimension dimension = this.this$0.tabScroller.scrollForwardButton.getPreferredSize();
            switch (i) {
              case 2:
              case 4:
                i10 = (this.this$0.rects[j - 1]).y + (this.this$0.rects[j - 1]).height;
                if (i10 > i3) {
                  i9 = (i3 > 2 * dimension.height) ? (i3 - 2 * dimension.height) : 0;
                  if (i10 - rectangle1.y <= i9)
                    i9 = i10 - rectangle1.y; 
                } 
                break;
              default:
                i11 = (this.this$0.rects[j - 1]).x + (this.this$0.rects[j - 1]).width;
                if (i11 > i2) {
                  i8 = (i2 > 2 * dimension.width) ? (i2 - 2 * dimension.width) : 0;
                  if (i11 - rectangle1.x <= i8)
                    i8 = i11 - rectangle1.x; 
                } 
                break;
            } 
            component.setBounds(n, i1, i8, i9);
          } else if (BasicTabbedPaneUI.this.tabScroller != null && (component == this.this$0.tabScroller.scrollForwardButton || component == this.this$0.tabScroller.scrollBackwardButton)) {
            int i13;
            int i12;
            Component component3 = component;
            Dimension dimension = component3.getPreferredSize();
            int i8 = 0;
            int i9 = 0;
            int i10 = dimension.width;
            int i11 = dimension.height;
            boolean bool1 = false;
            switch (i) {
              case 2:
              case 4:
                i12 = (this.this$0.rects[j - 1]).y + (this.this$0.rects[j - 1]).height;
                if (i12 > i3) {
                  bool1 = true;
                  i8 = (i == 2) ? (n + i2 - dimension.width) : n;
                  i9 = (component == this.this$0.tabScroller.scrollForwardButton) ? (rectangle.height - insets1.bottom - dimension.height) : (rectangle.height - insets1.bottom - 2 * dimension.height);
                } 
                break;
              default:
                i13 = (this.this$0.rects[j - 1]).x + (this.this$0.rects[j - 1]).width;
                if (i13 > i2) {
                  bool1 = true;
                  i8 = (component == this.this$0.tabScroller.scrollForwardButton) ? (rectangle.width - insets1.left - dimension.width) : (rectangle.width - insets1.left - 2 * dimension.width);
                  i9 = (i == 1) ? (i1 + i3 - dimension.height) : i1;
                } 
                break;
            } 
            component.setVisible(bool1);
            if (bool1)
              component.setBounds(i8, i9, i10, i11); 
          } else {
            component.setBounds(i4, i5, i6, i7);
          } 
        } 
        layoutTabComponents();
        layoutCroppedEdge();
        if (bool && !BasicTabbedPaneUI.this.requestFocusForVisibleComponent())
          BasicTabbedPaneUI.this.tabPane.requestFocus(); 
      } 
    }
    
    private void layoutCroppedEdge() {
      this.this$0.tabScroller.croppedEdge.resetParams();
      Rectangle rectangle = this.this$0.tabScroller.viewport.getViewRect();
      for (byte b = 0; b < BasicTabbedPaneUI.this.rects.length; b++) {
        int i;
        Rectangle rectangle1 = BasicTabbedPaneUI.this.rects[b];
        switch (BasicTabbedPaneUI.this.tabPane.getTabPlacement()) {
          case 2:
          case 4:
            i = rectangle.y + rectangle.height;
            if (rectangle1.y < i && rectangle1.y + rectangle1.height > i)
              this.this$0.tabScroller.croppedEdge.setParams(b, i - rectangle1.y - 1, -this.this$0.currentTabAreaInsets.left, 0); 
            break;
          default:
            i = rectangle.x + rectangle.width;
            if (rectangle1.x < i - 1 && rectangle1.x + rectangle1.width > i)
              this.this$0.tabScroller.croppedEdge.setParams(b, i - rectangle1.x - 1, 0, -this.this$0.currentTabAreaInsets.top); 
            break;
        } 
      } 
    }
    
    protected void calculateTabRects(int param1Int1, int param1Int2) {
      FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
      Dimension dimension = BasicTabbedPaneUI.this.tabPane.getSize();
      Insets insets1 = BasicTabbedPaneUI.this.tabPane.getInsets();
      Insets insets2 = BasicTabbedPaneUI.this.getTabAreaInsets(param1Int1);
      int i = fontMetrics.getHeight();
      int j = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
      boolean bool = (param1Int1 == 2 || param1Int1 == 4) ? 1 : 0;
      boolean bool1 = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
      int k = insets2.left;
      int m = insets2.top;
      int n = 0;
      int i1 = 0;
      switch (param1Int1) {
        case 2:
        case 4:
          BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(param1Int1);
          break;
        default:
          BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(param1Int1);
          break;
      } 
      BasicTabbedPaneUI.this.runCount = 0;
      BasicTabbedPaneUI.this.selectedRun = -1;
      if (param1Int2 == 0)
        return; 
      BasicTabbedPaneUI.this.selectedRun = 0;
      BasicTabbedPaneUI.this.runCount = 1;
      byte b;
      for (b = 0; b < param1Int2; b++) {
        Rectangle rectangle = BasicTabbedPaneUI.this.rects[b];
        if (!bool) {
          if (b) {
            (this.this$0.rects[b - true]).x += (this.this$0.rects[b - true]).width;
          } else {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.maxTabWidth = 0;
            i1 += BasicTabbedPaneUI.this.maxTabHeight;
            rectangle.x = k;
          } 
          rectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(param1Int1, b, fontMetrics);
          n = rectangle.x + rectangle.width;
          BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, rectangle.width);
          rectangle.y = m;
          rectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
        } else {
          if (b > 0) {
            (this.this$0.rects[b - 1]).y += (this.this$0.rects[b - 1]).height;
          } else {
            BasicTabbedPaneUI.this.tabRuns[0] = 0;
            BasicTabbedPaneUI.this.maxTabHeight = 0;
            n = BasicTabbedPaneUI.this.maxTabWidth;
            rectangle.y = m;
          } 
          rectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(param1Int1, b, i);
          i1 = rectangle.y + rectangle.height;
          BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, rectangle.height);
          rectangle.x = k;
          rectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
        } 
      } 
      if (BasicTabbedPaneUI.this.tabsOverlapBorder)
        padSelectedTab(param1Int1, j); 
      if (!bool1 && !bool) {
        int i2 = dimension.width - insets1.right + insets2.right;
        for (b = 0; b < param1Int2; b++)
          (this.this$0.rects[b]).x = i2 - (this.this$0.rects[b]).x - (this.this$0.rects[b]).width; 
      } 
      this.this$0.tabScroller.tabPanel.setPreferredSize(new Dimension(n, i1));
      this.this$0.tabScroller.tabPanel.invalidate();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTabbedPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */