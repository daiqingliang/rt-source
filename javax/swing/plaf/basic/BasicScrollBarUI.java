package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollBarUI;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicScrollBarUI extends ScrollBarUI implements LayoutManager, SwingConstants {
  private static final int POSITIVE_SCROLL = 1;
  
  private static final int NEGATIVE_SCROLL = -1;
  
  private static final int MIN_SCROLL = 2;
  
  private static final int MAX_SCROLL = 3;
  
  protected Dimension minimumThumbSize;
  
  protected Dimension maximumThumbSize;
  
  protected Color thumbHighlightColor;
  
  protected Color thumbLightShadowColor;
  
  protected Color thumbDarkShadowColor;
  
  protected Color thumbColor;
  
  protected Color trackColor;
  
  protected Color trackHighlightColor;
  
  protected JScrollBar scrollbar;
  
  protected JButton incrButton;
  
  protected JButton decrButton;
  
  protected boolean isDragging;
  
  protected TrackListener trackListener;
  
  protected ArrowButtonListener buttonListener;
  
  protected ModelListener modelListener;
  
  protected Rectangle thumbRect;
  
  protected Rectangle trackRect;
  
  protected int trackHighlight;
  
  protected static final int NO_HIGHLIGHT = 0;
  
  protected static final int DECREASE_HIGHLIGHT = 1;
  
  protected static final int INCREASE_HIGHLIGHT = 2;
  
  protected ScrollListener scrollListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected Timer scrollTimer;
  
  private static final int scrollSpeedThrottle = 60;
  
  private boolean supportsAbsolutePositioning;
  
  protected int scrollBarWidth;
  
  private Handler handler;
  
  private boolean thumbActive;
  
  private boolean useCachedValue = false;
  
  private int scrollBarValue;
  
  protected int incrGap;
  
  protected int decrGap;
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("positiveUnitIncrement"));
    paramLazyActionMap.put(new Actions("positiveBlockIncrement"));
    paramLazyActionMap.put(new Actions("negativeUnitIncrement"));
    paramLazyActionMap.put(new Actions("negativeBlockIncrement"));
    paramLazyActionMap.put(new Actions("minScroll"));
    paramLazyActionMap.put(new Actions("maxScroll"));
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicScrollBarUI(); }
  
  protected void configureScrollBarColors() {
    LookAndFeel.installColors(this.scrollbar, "ScrollBar.background", "ScrollBar.foreground");
    this.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
    this.thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
    this.thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
    this.thumbColor = UIManager.getColor("ScrollBar.thumb");
    this.trackColor = UIManager.getColor("ScrollBar.track");
    this.trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
  }
  
  public void installUI(JComponent paramJComponent) {
    this.scrollbar = (JScrollBar)paramJComponent;
    this.thumbRect = new Rectangle(0, 0, 0, 0);
    this.trackRect = new Rectangle(0, 0, 0, 0);
    installDefaults();
    installComponents();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    this.scrollbar = (JScrollBar)paramJComponent;
    uninstallListeners();
    uninstallDefaults();
    uninstallComponents();
    uninstallKeyboardActions();
    this.thumbRect = null;
    this.scrollbar = null;
    this.incrButton = null;
    this.decrButton = null;
  }
  
  protected void installDefaults() {
    this.scrollBarWidth = UIManager.getInt("ScrollBar.width");
    if (this.scrollBarWidth <= 0)
      this.scrollBarWidth = 16; 
    this.minimumThumbSize = (Dimension)UIManager.get("ScrollBar.minimumThumbSize");
    this.maximumThumbSize = (Dimension)UIManager.get("ScrollBar.maximumThumbSize");
    Boolean bool = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
    this.supportsAbsolutePositioning = (bool != null) ? bool.booleanValue() : 0;
    this.trackHighlight = 0;
    if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof javax.swing.plaf.UIResource)
      this.scrollbar.setLayout(this); 
    configureScrollBarColors();
    LookAndFeel.installBorder(this.scrollbar, "ScrollBar.border");
    LookAndFeel.installProperty(this.scrollbar, "opaque", Boolean.TRUE);
    this.scrollBarValue = this.scrollbar.getValue();
    this.incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
    this.decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");
    String str = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
    if (str != null)
      if ("large".equals(str)) {
        this.scrollBarWidth = (int)(this.scrollBarWidth * 1.15D);
        this.incrGap = (int)(this.incrGap * 1.15D);
        this.decrGap = (int)(this.decrGap * 1.15D);
      } else if ("small".equals(str)) {
        this.scrollBarWidth = (int)(this.scrollBarWidth * 0.857D);
        this.incrGap = (int)(this.incrGap * 0.857D);
        this.decrGap = (int)(this.decrGap * 0.714D);
      } else if ("mini".equals(str)) {
        this.scrollBarWidth = (int)(this.scrollBarWidth * 0.714D);
        this.incrGap = (int)(this.incrGap * 0.714D);
        this.decrGap = (int)(this.decrGap * 0.714D);
      }  
  }
  
  protected void installComponents() {
    switch (this.scrollbar.getOrientation()) {
      case 1:
        this.incrButton = createIncreaseButton(5);
        this.decrButton = createDecreaseButton(1);
        break;
      case 0:
        if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
          this.incrButton = createIncreaseButton(3);
          this.decrButton = createDecreaseButton(7);
          break;
        } 
        this.incrButton = createIncreaseButton(7);
        this.decrButton = createDecreaseButton(3);
        break;
    } 
    this.scrollbar.add(this.incrButton);
    this.scrollbar.add(this.decrButton);
    this.scrollbar.setEnabled(this.scrollbar.isEnabled());
  }
  
  protected void uninstallComponents() {
    this.scrollbar.remove(this.incrButton);
    this.scrollbar.remove(this.decrButton);
  }
  
  protected void installListeners() {
    this.trackListener = createTrackListener();
    this.buttonListener = createArrowButtonListener();
    this.modelListener = createModelListener();
    this.propertyChangeListener = createPropertyChangeListener();
    this.scrollbar.addMouseListener(this.trackListener);
    this.scrollbar.addMouseMotionListener(this.trackListener);
    this.scrollbar.getModel().addChangeListener(this.modelListener);
    this.scrollbar.addPropertyChangeListener(this.propertyChangeListener);
    this.scrollbar.addFocusListener(getHandler());
    if (this.incrButton != null)
      this.incrButton.addMouseListener(this.buttonListener); 
    if (this.decrButton != null)
      this.decrButton.addMouseListener(this.buttonListener); 
    this.scrollListener = createScrollListener();
    this.scrollTimer = new Timer(60, this.scrollListener);
    this.scrollTimer.setInitialDelay(300);
  }
  
  protected void installKeyboardActions() {
    LazyActionMap.installLazyActionMap(this.scrollbar, BasicScrollBarUI.class, "ScrollBar.actionMap");
    InputMap inputMap = getInputMap(0);
    SwingUtilities.replaceUIInputMap(this.scrollbar, 0, inputMap);
    inputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(this.scrollbar, 1, inputMap);
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.scrollbar, 0, null);
    SwingUtilities.replaceUIActionMap(this.scrollbar, null);
  }
  
  private InputMap getInputMap(int paramInt) {
    if (paramInt == 0) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap");
      InputMap inputMap2;
      if (this.scrollbar.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    if (paramInt == 1) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap");
      InputMap inputMap2;
      if (this.scrollbar.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  protected void uninstallListeners() {
    this.scrollTimer.stop();
    this.scrollTimer = null;
    if (this.decrButton != null)
      this.decrButton.removeMouseListener(this.buttonListener); 
    if (this.incrButton != null)
      this.incrButton.removeMouseListener(this.buttonListener); 
    this.scrollbar.getModel().removeChangeListener(this.modelListener);
    this.scrollbar.removeMouseListener(this.trackListener);
    this.scrollbar.removeMouseMotionListener(this.trackListener);
    this.scrollbar.removePropertyChangeListener(this.propertyChangeListener);
    this.scrollbar.removeFocusListener(getHandler());
    this.handler = null;
  }
  
  protected void uninstallDefaults() {
    LookAndFeel.uninstallBorder(this.scrollbar);
    if (this.scrollbar.getLayout() == this)
      this.scrollbar.setLayout(null); 
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected TrackListener createTrackListener() { return new TrackListener(); }
  
  protected ArrowButtonListener createArrowButtonListener() { return new ArrowButtonListener(); }
  
  protected ModelListener createModelListener() { return new ModelListener(); }
  
  protected ScrollListener createScrollListener() { return new ScrollListener(); }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  private void updateThumbState(int paramInt1, int paramInt2) {
    Rectangle rectangle = getThumbBounds();
    setThumbRollover(rectangle.contains(paramInt1, paramInt2));
  }
  
  protected void setThumbRollover(boolean paramBoolean) {
    if (this.thumbActive != paramBoolean) {
      this.thumbActive = paramBoolean;
      this.scrollbar.repaint(getThumbBounds());
    } 
  }
  
  public boolean isThumbRollover() { return this.thumbActive; }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    paintTrack(paramGraphics, paramJComponent, getTrackBounds());
    Rectangle rectangle = getThumbBounds();
    if (rectangle.intersects(paramGraphics.getClipBounds()))
      paintThumb(paramGraphics, paramJComponent, rectangle); 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return (this.scrollbar.getOrientation() == 1) ? new Dimension(this.scrollBarWidth, 48) : new Dimension(48, this.scrollBarWidth); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return new Dimension(2147483647, 2147483647); }
  
  protected JButton createDecreaseButton(int paramInt) { return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight")); }
  
  protected JButton createIncreaseButton(int paramInt) { return new BasicArrowButton(paramInt, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight")); }
  
  protected void paintDecreaseHighlight(Graphics paramGraphics) {
    Insets insets = this.scrollbar.getInsets();
    Rectangle rectangle = getThumbBounds();
    paramGraphics.setColor(this.trackHighlightColor);
    if (this.scrollbar.getOrientation() == 1) {
      int i = insets.left;
      int j = this.trackRect.y;
      int k = this.scrollbar.getWidth() - insets.left + insets.right;
      int m = rectangle.y - j;
      paramGraphics.fillRect(i, j, k, m);
    } else {
      int j;
      int i;
      if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
        i = this.trackRect.x;
        j = rectangle.x - i;
      } else {
        i = rectangle.x + rectangle.width;
        j = this.trackRect.x + this.trackRect.width - i;
      } 
      int k = insets.top;
      int m = this.scrollbar.getHeight() - insets.top + insets.bottom;
      paramGraphics.fillRect(i, k, j, m);
    } 
  }
  
  protected void paintIncreaseHighlight(Graphics paramGraphics) {
    Insets insets = this.scrollbar.getInsets();
    Rectangle rectangle = getThumbBounds();
    paramGraphics.setColor(this.trackHighlightColor);
    if (this.scrollbar.getOrientation() == 1) {
      int i = insets.left;
      int j = rectangle.y + rectangle.height;
      int k = this.scrollbar.getWidth() - insets.left + insets.right;
      int m = this.trackRect.y + this.trackRect.height - j;
      paramGraphics.fillRect(i, j, k, m);
    } else {
      int j;
      int i;
      if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
        i = rectangle.x + rectangle.width;
        j = this.trackRect.x + this.trackRect.width - i;
      } else {
        i = this.trackRect.x;
        j = rectangle.x - i;
      } 
      int k = insets.top;
      int m = this.scrollbar.getHeight() - insets.top + insets.bottom;
      paramGraphics.fillRect(i, k, j, m);
    } 
  }
  
  protected void paintTrack(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    paramGraphics.setColor(this.trackColor);
    paramGraphics.fillRect(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
    if (this.trackHighlight == 1) {
      paintDecreaseHighlight(paramGraphics);
    } else if (this.trackHighlight == 2) {
      paintIncreaseHighlight(paramGraphics);
    } 
  }
  
  protected void paintThumb(Graphics paramGraphics, JComponent paramJComponent, Rectangle paramRectangle) {
    if (paramRectangle.isEmpty() || !this.scrollbar.isEnabled())
      return; 
    int i = paramRectangle.width;
    int j = paramRectangle.height;
    paramGraphics.translate(paramRectangle.x, paramRectangle.y);
    paramGraphics.setColor(this.thumbDarkShadowColor);
    SwingUtilities2.drawRect(paramGraphics, 0, 0, i - 1, j - 1);
    paramGraphics.setColor(this.thumbColor);
    paramGraphics.fillRect(0, 0, i - 1, j - 1);
    paramGraphics.setColor(this.thumbHighlightColor);
    SwingUtilities2.drawVLine(paramGraphics, 1, 1, j - 2);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 3, 1);
    paramGraphics.setColor(this.thumbLightShadowColor);
    SwingUtilities2.drawHLine(paramGraphics, 2, i - 2, j - 2);
    SwingUtilities2.drawVLine(paramGraphics, i - 2, 1, j - 3);
    paramGraphics.translate(-paramRectangle.x, -paramRectangle.y);
  }
  
  protected Dimension getMinimumThumbSize() { return this.minimumThumbSize; }
  
  protected Dimension getMaximumThumbSize() { return this.maximumThumbSize; }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer) { return getPreferredSize((JComponent)paramContainer); }
  
  public Dimension minimumLayoutSize(Container paramContainer) { return getMinimumSize((JComponent)paramContainer); }
  
  private int getValue(JScrollBar paramJScrollBar) { return this.useCachedValue ? this.scrollBarValue : paramJScrollBar.getValue(); }
  
  protected void layoutVScrollbar(JScrollBar paramJScrollBar) {
    Dimension dimension = paramJScrollBar.getSize();
    Insets insets = paramJScrollBar.getInsets();
    int i = dimension.width - insets.left + insets.right;
    int j = insets.left;
    boolean bool = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool ? i : (this.decrButton.getPreferredSize()).height;
    int m = insets.top;
    int n = bool ? i : (this.incrButton.getPreferredSize()).height;
    int i1 = dimension.height - insets.bottom + n;
    int i2 = insets.top + insets.bottom;
    int i3 = k + n;
    int i4 = this.decrGap + this.incrGap;
    float f1 = (dimension.height - i2 + i3 - i4);
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getVisibleAmount();
    float f4 = paramJScrollBar.getMaximum() - f2;
    float f5 = getValue(paramJScrollBar);
    int i5 = (f4 <= 0.0F) ? (getMaximumThumbSize()).height : (int)(f1 * f3 / f4);
    i5 = Math.max(i5, (getMinimumThumbSize()).height);
    i5 = Math.min(i5, (getMaximumThumbSize()).height);
    int i6 = i1 - this.incrGap - i5;
    if (f5 < (paramJScrollBar.getMaximum() - paramJScrollBar.getVisibleAmount())) {
      float f = f1 - i5;
      i6 = (int)(0.5F + f * (f5 - f2) / (f4 - f3));
      i6 += m + k + this.decrGap;
    } 
    int i7 = dimension.height - i2;
    if (i7 < i3) {
      n = k = i7 / 2;
      i1 = dimension.height - insets.bottom + n;
    } 
    this.decrButton.setBounds(j, m, i, k);
    this.incrButton.setBounds(j, i1, i, n);
    int i8 = m + k + this.decrGap;
    int i9 = i1 - this.incrGap - i8;
    this.trackRect.setBounds(j, i8, i, i9);
    if (i5 >= (int)f1) {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(j, i8, i, i9);
      } else {
        setThumbBounds(0, 0, 0, 0);
      } 
    } else {
      if (i6 + i5 > i1 - this.incrGap)
        i6 = i1 - this.incrGap - i5; 
      if (i6 < m + k + this.decrGap)
        i6 = m + k + this.decrGap + 1; 
      setThumbBounds(j, i6, i, i5);
    } 
  }
  
  protected void layoutHScrollbar(JScrollBar paramJScrollBar) {
    Dimension dimension = paramJScrollBar.getSize();
    Insets insets = paramJScrollBar.getInsets();
    int i = dimension.height - insets.top + insets.bottom;
    int j = insets.top;
    boolean bool1 = paramJScrollBar.getComponentOrientation().isLeftToRight();
    boolean bool2 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
    int k = bool2 ? i : (this.decrButton.getPreferredSize()).width;
    int m = bool2 ? i : (this.incrButton.getPreferredSize()).width;
    if (!bool1) {
      int i11 = k;
      k = m;
      m = i11;
    } 
    int n = insets.left;
    int i1 = dimension.width - insets.right + m;
    int i2 = bool1 ? this.decrGap : this.incrGap;
    int i3 = bool1 ? this.incrGap : this.decrGap;
    int i4 = insets.left + insets.right;
    int i5 = k + m;
    float f1 = (dimension.width - i4 + i5 - i2 + i3);
    float f2 = paramJScrollBar.getMinimum();
    float f3 = paramJScrollBar.getMaximum();
    float f4 = paramJScrollBar.getVisibleAmount();
    float f5 = f3 - f2;
    float f6 = getValue(paramJScrollBar);
    int i6 = (f5 <= 0.0F) ? (getMaximumThumbSize()).width : (int)(f1 * f4 / f5);
    i6 = Math.max(i6, (getMinimumThumbSize()).width);
    i6 = Math.min(i6, (getMaximumThumbSize()).width);
    int i7 = bool1 ? (i1 - i3 - i6) : (n + k + i2);
    if (f6 < f3 - paramJScrollBar.getVisibleAmount()) {
      float f = f1 - i6;
      if (bool1) {
        i7 = (int)(0.5F + f * (f6 - f2) / (f5 - f4));
      } else {
        i7 = (int)(0.5F + f * (f3 - f4 - f6) / (f5 - f4));
      } 
      i7 += n + k + i2;
    } 
    int i8 = dimension.width - i4;
    if (i8 < i5) {
      m = k = i8 / 2;
      i1 = dimension.width - insets.right + m + i3;
    } 
    (bool1 ? this.decrButton : this.incrButton).setBounds(n, j, k, i);
    (bool1 ? this.incrButton : this.decrButton).setBounds(i1, j, m, i);
    int i9 = n + k + i2;
    int i10 = i1 - i3 - i9;
    this.trackRect.setBounds(i9, j, i10, i);
    if (i6 >= (int)f1) {
      if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
        setThumbBounds(i9, j, i10, i);
      } else {
        setThumbBounds(0, 0, 0, 0);
      } 
    } else {
      if (i7 + i6 > i1 - i3)
        i7 = i1 - i3 - i6; 
      if (i7 < n + k + i2)
        i7 = n + k + i2 + 1; 
      setThumbBounds(i7, j, i6, i);
    } 
  }
  
  public void layoutContainer(Container paramContainer) {
    if (this.isDragging)
      return; 
    JScrollBar jScrollBar = (JScrollBar)paramContainer;
    switch (jScrollBar.getOrientation()) {
      case 1:
        layoutVScrollbar(jScrollBar);
        break;
      case 0:
        layoutHScrollbar(jScrollBar);
        break;
    } 
  }
  
  protected void setThumbBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.thumbRect.x == paramInt1 && this.thumbRect.y == paramInt2 && this.thumbRect.width == paramInt3 && this.thumbRect.height == paramInt4)
      return; 
    int i = Math.min(paramInt1, this.thumbRect.x);
    int j = Math.min(paramInt2, this.thumbRect.y);
    int k = Math.max(paramInt1 + paramInt3, this.thumbRect.x + this.thumbRect.width);
    int m = Math.max(paramInt2 + paramInt4, this.thumbRect.y + this.thumbRect.height);
    this.thumbRect.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    this.scrollbar.repaint(i, j, k - i, m - j);
    setThumbRollover(false);
  }
  
  protected Rectangle getThumbBounds() { return this.thumbRect; }
  
  protected Rectangle getTrackBounds() { return this.trackRect; }
  
  static void scrollByBlock(JScrollBar paramJScrollBar, int paramInt) {
    int i = paramJScrollBar.getValue();
    int j = paramJScrollBar.getBlockIncrement(paramInt);
    int k = j * ((paramInt > 0) ? 1 : -1);
    int m = i + k;
    if (k > 0 && m < i) {
      m = paramJScrollBar.getMaximum();
    } else if (k < 0 && m > i) {
      m = paramJScrollBar.getMinimum();
    } 
    paramJScrollBar.setValue(m);
  }
  
  protected void scrollByBlock(int paramInt) {
    scrollByBlock(this.scrollbar, paramInt);
    this.trackHighlight = (paramInt > 0) ? 2 : 1;
    Rectangle rectangle = getTrackBounds();
    this.scrollbar.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
  }
  
  static void scrollByUnits(JScrollBar paramJScrollBar, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = -1;
    if (paramBoolean)
      if (paramInt1 < 0) {
        i = paramJScrollBar.getValue() - paramJScrollBar.getBlockIncrement(paramInt1);
      } else {
        i = paramJScrollBar.getValue() + paramJScrollBar.getBlockIncrement(paramInt1);
      }  
    for (byte b = 0; b < paramInt2; b++) {
      int j;
      if (paramInt1 > 0) {
        j = paramJScrollBar.getUnitIncrement(paramInt1);
      } else {
        j = -paramJScrollBar.getUnitIncrement(paramInt1);
      } 
      int k = paramJScrollBar.getValue();
      int m = k + j;
      if (j > 0 && m < k) {
        m = paramJScrollBar.getMaximum();
      } else if (j < 0 && m > k) {
        m = paramJScrollBar.getMinimum();
      } 
      if (k == m)
        break; 
      if (paramBoolean && b) {
        assert i != -1;
        if ((paramInt1 < 0 && m < i) || (paramInt1 > 0 && m > i))
          break; 
      } 
      paramJScrollBar.setValue(m);
    } 
  }
  
  protected void scrollByUnit(int paramInt) { scrollByUnits(this.scrollbar, paramInt, 1, false); }
  
  public boolean getSupportsAbsolutePositioning() { return this.supportsAbsolutePositioning; }
  
  private boolean isMouseLeftOfThumb() { return (this.trackListener.currentMouseX < (getThumbBounds()).x); }
  
  private boolean isMouseRightOfThumb() {
    Rectangle rectangle = getThumbBounds();
    return (this.trackListener.currentMouseX > rectangle.x + rectangle.width);
  }
  
  private boolean isMouseBeforeThumb() { return this.scrollbar.getComponentOrientation().isLeftToRight() ? isMouseLeftOfThumb() : isMouseRightOfThumb(); }
  
  private boolean isMouseAfterThumb() { return this.scrollbar.getComponentOrientation().isLeftToRight() ? isMouseRightOfThumb() : isMouseLeftOfThumb(); }
  
  private void updateButtonDirections() {
    int i = this.scrollbar.getOrientation();
    if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
      if (this.incrButton instanceof BasicArrowButton)
        ((BasicArrowButton)this.incrButton).setDirection((i == 0) ? 3 : 5); 
      if (this.decrButton instanceof BasicArrowButton)
        ((BasicArrowButton)this.decrButton).setDirection((i == 0) ? 7 : 1); 
    } else {
      if (this.incrButton instanceof BasicArrowButton)
        ((BasicArrowButton)this.incrButton).setDirection((i == 0) ? 7 : 5); 
      if (this.decrButton instanceof BasicArrowButton)
        ((BasicArrowButton)this.decrButton).setDirection((i == 0) ? 3 : 1); 
    } 
  }
  
  private static class Actions extends UIAction {
    private static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
    
    private static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
    
    private static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
    
    private static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
    
    private static final String MIN_SCROLL = "minScroll";
    
    private static final String MAX_SCROLL = "maxScroll";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JScrollBar jScrollBar = (JScrollBar)param1ActionEvent.getSource();
      String str = getName();
      if (str == "positiveUnitIncrement") {
        scroll(jScrollBar, 1, false);
      } else if (str == "positiveBlockIncrement") {
        scroll(jScrollBar, 1, true);
      } else if (str == "negativeUnitIncrement") {
        scroll(jScrollBar, -1, false);
      } else if (str == "negativeBlockIncrement") {
        scroll(jScrollBar, -1, true);
      } else if (str == "minScroll") {
        scroll(jScrollBar, 2, true);
      } else if (str == "maxScroll") {
        scroll(jScrollBar, 3, true);
      } 
    }
    
    private void scroll(JScrollBar param1JScrollBar, int param1Int, boolean param1Boolean) {
      if (param1Int == -1 || param1Int == 1) {
        int i;
        if (param1Boolean) {
          if (param1Int == -1) {
            i = -1 * param1JScrollBar.getBlockIncrement(-1);
          } else {
            i = param1JScrollBar.getBlockIncrement(1);
          } 
        } else if (param1Int == -1) {
          i = -1 * param1JScrollBar.getUnitIncrement(-1);
        } else {
          i = param1JScrollBar.getUnitIncrement(1);
        } 
        param1JScrollBar.setValue(param1JScrollBar.getValue() + i);
      } else if (param1Int == 2) {
        param1JScrollBar.setValue(param1JScrollBar.getMinimum());
      } else if (param1Int == 3) {
        param1JScrollBar.setValue(param1JScrollBar.getMaximum());
      } 
    }
  }
  
  protected class ArrowButtonListener extends MouseAdapter {
    boolean handledEvent;
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (!BasicScrollBarUI.this.scrollbar.isEnabled())
        return; 
      if (!SwingUtilities.isLeftMouseButton(param1MouseEvent))
        return; 
      byte b = (param1MouseEvent.getSource() == BasicScrollBarUI.this.incrButton) ? 1 : -1;
      BasicScrollBarUI.this.scrollByUnit(b);
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.scrollListener.setDirection(b);
      BasicScrollBarUI.this.scrollListener.setScrollByBlock(false);
      BasicScrollBarUI.this.scrollTimer.start();
      this.handledEvent = true;
      if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled())
        BasicScrollBarUI.this.scrollbar.requestFocus(); 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      BasicScrollBarUI.this.scrollTimer.stop();
      this.handledEvent = false;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
    }
  }
  
  private class Handler implements FocusListener, PropertyChangeListener {
    private Handler() {}
    
    public void focusGained(FocusEvent param1FocusEvent) { BasicScrollBarUI.this.scrollbar.repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicScrollBarUI.this.scrollbar.repaint(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("model" == str) {
        BoundedRangeModel boundedRangeModel1 = (BoundedRangeModel)param1PropertyChangeEvent.getOldValue();
        BoundedRangeModel boundedRangeModel2 = (BoundedRangeModel)param1PropertyChangeEvent.getNewValue();
        boundedRangeModel1.removeChangeListener(BasicScrollBarUI.this.modelListener);
        boundedRangeModel2.addChangeListener(BasicScrollBarUI.this.modelListener);
        BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
        BasicScrollBarUI.this.scrollbar.repaint();
        BasicScrollBarUI.this.scrollbar.revalidate();
      } else if ("orientation" == str) {
        BasicScrollBarUI.this.updateButtonDirections();
      } else if ("componentOrientation" == str) {
        BasicScrollBarUI.this.updateButtonDirections();
        InputMap inputMap = BasicScrollBarUI.this.getInputMap(0);
        SwingUtilities.replaceUIInputMap(BasicScrollBarUI.this.scrollbar, 0, inputMap);
      } 
    }
  }
  
  protected class ModelListener implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (!BasicScrollBarUI.this.useCachedValue)
        BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue(); 
      BasicScrollBarUI.this.layoutContainer(BasicScrollBarUI.this.scrollbar);
      BasicScrollBarUI.this.useCachedValue = false;
    }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicScrollBarUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  protected class ScrollListener implements ActionListener {
    int direction = 1;
    
    boolean useBlockIncrement;
    
    public ScrollListener() {
      this.direction = 1;
      this.useBlockIncrement = false;
    }
    
    public ScrollListener(int param1Int, boolean param1Boolean) {
      this.direction = param1Int;
      this.useBlockIncrement = param1Boolean;
    }
    
    public void setDirection(int param1Int) { this.direction = param1Int; }
    
    public void setScrollByBlock(boolean param1Boolean) { this.useBlockIncrement = param1Boolean; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.useBlockIncrement) {
        BasicScrollBarUI.this.scrollByBlock(this.direction);
        if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
          if (this.direction > 0) {
            if ((this.this$0.getThumbBounds()).y + (this.this$0.getThumbBounds()).height >= this.this$0.trackListener.currentMouseY)
              ((Timer)param1ActionEvent.getSource()).stop(); 
          } else if ((this.this$0.getThumbBounds()).y <= this.this$0.trackListener.currentMouseY) {
            ((Timer)param1ActionEvent.getSource()).stop();
          } 
        } else if ((this.direction > 0 && !BasicScrollBarUI.this.isMouseAfterThumb()) || (this.direction < 0 && !BasicScrollBarUI.this.isMouseBeforeThumb())) {
          ((Timer)param1ActionEvent.getSource()).stop();
        } 
      } else {
        BasicScrollBarUI.this.scrollByUnit(this.direction);
      } 
      if (this.direction > 0 && BasicScrollBarUI.this.scrollbar.getValue() + BasicScrollBarUI.this.scrollbar.getVisibleAmount() >= BasicScrollBarUI.this.scrollbar.getMaximum()) {
        ((Timer)param1ActionEvent.getSource()).stop();
      } else if (this.direction < 0 && BasicScrollBarUI.this.scrollbar.getValue() <= BasicScrollBarUI.this.scrollbar.getMinimum()) {
        ((Timer)param1ActionEvent.getSource()).stop();
      } 
    }
  }
  
  protected class TrackListener extends MouseAdapter implements MouseMotionListener {
    protected int offset;
    
    protected int currentMouseX;
    
    protected int currentMouseY;
    
    private int direction = 1;
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (BasicScrollBarUI.this.isDragging)
        BasicScrollBarUI.this.updateThumbState(param1MouseEvent.getX(), param1MouseEvent.getY()); 
      if (SwingUtilities.isRightMouseButton(param1MouseEvent) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(param1MouseEvent)))
        return; 
      if (!BasicScrollBarUI.this.scrollbar.isEnabled())
        return; 
      Rectangle rectangle = BasicScrollBarUI.this.getTrackBounds();
      BasicScrollBarUI.this.scrollbar.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      BasicScrollBarUI.this.trackHighlight = 0;
      BasicScrollBarUI.this.isDragging = false;
      this.offset = 0;
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.useCachedValue = true;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      int i;
      if (SwingUtilities.isRightMouseButton(param1MouseEvent) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(param1MouseEvent)))
        return; 
      if (!BasicScrollBarUI.this.scrollbar.isEnabled())
        return; 
      if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled())
        BasicScrollBarUI.this.scrollbar.requestFocus(); 
      BasicScrollBarUI.this.useCachedValue = true;
      BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(true);
      this.currentMouseX = param1MouseEvent.getX();
      this.currentMouseY = param1MouseEvent.getY();
      if (BasicScrollBarUI.this.getThumbBounds().contains(this.currentMouseX, this.currentMouseY)) {
        switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
          case 1:
            this.offset = this.currentMouseY - (this.this$0.getThumbBounds()).y;
            break;
          case 0:
            this.offset = this.currentMouseX - (this.this$0.getThumbBounds()).x;
            break;
        } 
        BasicScrollBarUI.this.isDragging = true;
        return;
      } 
      if (BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(param1MouseEvent)) {
        switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
          case 1:
            this.offset = (this.this$0.getThumbBounds()).height / 2;
            break;
          case 0:
            this.offset = (this.this$0.getThumbBounds()).width / 2;
            break;
        } 
        BasicScrollBarUI.this.isDragging = true;
        setValueFrom(param1MouseEvent);
        return;
      } 
      BasicScrollBarUI.this.isDragging = false;
      Dimension dimension = BasicScrollBarUI.this.scrollbar.getSize();
      this.direction = 1;
      switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
        case 1:
          if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
            int j = dimension.height / 2;
            this.direction = (this.currentMouseY < j) ? -1 : 1;
            break;
          } 
          i = (this.this$0.getThumbBounds()).y;
          this.direction = (this.currentMouseY < i) ? -1 : 1;
          break;
        case 0:
          if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
            i = dimension.width / 2;
            this.direction = (this.currentMouseX < i) ? -1 : 1;
          } else {
            i = (this.this$0.getThumbBounds()).x;
            this.direction = (this.currentMouseX < i) ? -1 : 1;
          } 
          if (!BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight())
            this.direction = -this.direction; 
          break;
      } 
      BasicScrollBarUI.this.scrollByBlock(this.direction);
      BasicScrollBarUI.this.scrollTimer.stop();
      BasicScrollBarUI.this.scrollListener.setDirection(this.direction);
      BasicScrollBarUI.this.scrollListener.setScrollByBlock(true);
      startScrollTimerIfNecessary();
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      if (SwingUtilities.isRightMouseButton(param1MouseEvent) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(param1MouseEvent)))
        return; 
      if (!BasicScrollBarUI.this.scrollbar.isEnabled() || BasicScrollBarUI.this.getThumbBounds().isEmpty())
        return; 
      if (BasicScrollBarUI.this.isDragging) {
        setValueFrom(param1MouseEvent);
      } else {
        this.currentMouseX = param1MouseEvent.getX();
        this.currentMouseY = param1MouseEvent.getY();
        BasicScrollBarUI.this.updateThumbState(this.currentMouseX, this.currentMouseY);
        startScrollTimerIfNecessary();
      } 
    }
    
    private void setValueFrom(MouseEvent param1MouseEvent) {
      int k;
      int j;
      int i;
      boolean bool = BasicScrollBarUI.this.isThumbRollover();
      BoundedRangeModel boundedRangeModel = BasicScrollBarUI.this.scrollbar.getModel();
      Rectangle rectangle = BasicScrollBarUI.this.getThumbBounds();
      if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
        i = this.this$0.trackRect.y;
        j = this.this$0.trackRect.y + this.this$0.trackRect.height - rectangle.height;
        k = Math.min(j, Math.max(i, param1MouseEvent.getY() - this.offset));
        BasicScrollBarUI.this.setThumbBounds(rectangle.x, k, rectangle.width, rectangle.height);
        float f = (this.this$0.getTrackBounds()).height;
      } else {
        i = this.this$0.trackRect.x;
        j = this.this$0.trackRect.x + this.this$0.trackRect.width - rectangle.width;
        k = Math.min(j, Math.max(i, param1MouseEvent.getX() - this.offset));
        BasicScrollBarUI.this.setThumbBounds(k, rectangle.y, rectangle.width, rectangle.height);
        float f = (this.this$0.getTrackBounds()).width;
      } 
      if (k == j) {
        if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1 || BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
          BasicScrollBarUI.this.scrollbar.setValue(boundedRangeModel.getMaximum() - boundedRangeModel.getExtent());
        } else {
          BasicScrollBarUI.this.scrollbar.setValue(boundedRangeModel.getMinimum());
        } 
      } else {
        int m;
        float f1 = (boundedRangeModel.getMaximum() - boundedRangeModel.getExtent());
        float f2 = f1 - boundedRangeModel.getMinimum();
        float f3 = (k - i);
        float f4 = (j - i);
        if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1 || BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
          m = (int)(0.5D + (f3 / f4 * f2));
        } else {
          m = (int)(0.5D + ((j - k) / f4 * f2));
        } 
        BasicScrollBarUI.this.useCachedValue = true;
        BasicScrollBarUI.this.scrollBarValue = m + boundedRangeModel.getMinimum();
        BasicScrollBarUI.this.scrollbar.setValue(adjustValueIfNecessary(BasicScrollBarUI.this.scrollBarValue));
      } 
      BasicScrollBarUI.this.setThumbRollover(bool);
    }
    
    private int adjustValueIfNecessary(int param1Int) {
      if (BasicScrollBarUI.this.scrollbar.getParent() instanceof JScrollPane) {
        JScrollPane jScrollPane = (JScrollPane)BasicScrollBarUI.this.scrollbar.getParent();
        JViewport jViewport = jScrollPane.getViewport();
        Component component = jViewport.getView();
        if (component instanceof JList) {
          JList jList = (JList)component;
          if (DefaultLookup.getBoolean(jList, jList.getUI(), "List.lockToPositionOnScroll", false)) {
            int i = param1Int;
            int j = jList.getLayoutOrientation();
            int k = BasicScrollBarUI.this.scrollbar.getOrientation();
            if (k == 1 && j == 0) {
              int m = jList.locationToIndex(new Point(0, param1Int));
              Rectangle rectangle = jList.getCellBounds(m, m);
              if (rectangle != null)
                i = rectangle.y; 
            } 
            if (k == 0 && (j == 1 || j == 2))
              if (jScrollPane.getComponentOrientation().isLeftToRight()) {
                int m = jList.locationToIndex(new Point(param1Int, 0));
                Rectangle rectangle = jList.getCellBounds(m, m);
                if (rectangle != null)
                  i = rectangle.x; 
              } else {
                Point point = new Point(param1Int, 0);
                int m = (jViewport.getExtentSize()).width;
                point.x += m - 1;
                int n = jList.locationToIndex(point);
                Rectangle rectangle = jList.getCellBounds(n, n);
                if (rectangle != null)
                  i = rectangle.x + rectangle.width - m; 
              }  
            param1Int = i;
          } 
        } 
      } 
      return param1Int;
    }
    
    private void startScrollTimerIfNecessary() {
      if (BasicScrollBarUI.this.scrollTimer.isRunning())
        return; 
      Rectangle rectangle = BasicScrollBarUI.this.getThumbBounds();
      switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
        case 1:
          if (this.direction > 0) {
            if (rectangle.y + rectangle.height < this.this$0.trackListener.currentMouseY)
              BasicScrollBarUI.this.scrollTimer.start(); 
            break;
          } 
          if (rectangle.y > this.this$0.trackListener.currentMouseY)
            BasicScrollBarUI.this.scrollTimer.start(); 
          break;
        case 0:
          if ((this.direction > 0 && BasicScrollBarUI.this.isMouseAfterThumb()) || (this.direction < 0 && BasicScrollBarUI.this.isMouseBeforeThumb()))
            BasicScrollBarUI.this.scrollTimer.start(); 
          break;
      } 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      if (!BasicScrollBarUI.this.isDragging)
        BasicScrollBarUI.this.updateThumbState(param1MouseEvent.getX(), param1MouseEvent.getY()); 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      if (!BasicScrollBarUI.this.isDragging)
        BasicScrollBarUI.this.setThumbRollover(false); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */