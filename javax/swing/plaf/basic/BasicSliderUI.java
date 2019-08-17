package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.SliderUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicSliderUI extends SliderUI {
  private static final Actions SHARED_ACTION = new Actions();
  
  public static final int POSITIVE_SCROLL = 1;
  
  public static final int NEGATIVE_SCROLL = -1;
  
  public static final int MIN_SCROLL = -2;
  
  public static final int MAX_SCROLL = 2;
  
  protected Timer scrollTimer;
  
  protected JSlider slider;
  
  protected Insets focusInsets = null;
  
  protected Insets insetCache = null;
  
  protected boolean leftToRightCache = true;
  
  protected Rectangle focusRect = null;
  
  protected Rectangle contentRect = null;
  
  protected Rectangle labelRect = null;
  
  protected Rectangle tickRect = null;
  
  protected Rectangle trackRect = null;
  
  protected Rectangle thumbRect = null;
  
  protected int trackBuffer = 0;
  
  private boolean isDragging;
  
  protected TrackListener trackListener;
  
  protected ChangeListener changeListener;
  
  protected ComponentListener componentListener;
  
  protected FocusListener focusListener;
  
  protected ScrollListener scrollListener;
  
  protected PropertyChangeListener propertyChangeListener;
  
  private Handler handler;
  
  private int lastValue;
  
  private Color shadowColor;
  
  private Color highlightColor;
  
  private Color focusColor;
  
  private boolean checkedLabelBaselines;
  
  private boolean sameLabelBaselines;
  
  private static Rectangle unionRect = new Rectangle();
  
  protected Color getShadowColor() { return this.shadowColor; }
  
  protected Color getHighlightColor() { return this.highlightColor; }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  protected boolean isDragging() { return this.isDragging; }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicSliderUI((JSlider)paramJComponent); }
  
  public BasicSliderUI(JSlider paramJSlider) {}
  
  public void installUI(JComponent paramJComponent) {
    this.slider = (JSlider)paramJComponent;
    this.checkedLabelBaselines = false;
    this.slider.setEnabled(this.slider.isEnabled());
    LookAndFeel.installProperty(this.slider, "opaque", Boolean.TRUE);
    this.isDragging = false;
    this.trackListener = createTrackListener(this.slider);
    this.changeListener = createChangeListener(this.slider);
    this.componentListener = createComponentListener(this.slider);
    this.focusListener = createFocusListener(this.slider);
    this.scrollListener = createScrollListener(this.slider);
    this.propertyChangeListener = createPropertyChangeListener(this.slider);
    installDefaults(this.slider);
    installListeners(this.slider);
    installKeyboardActions(this.slider);
    this.scrollTimer = new Timer(100, this.scrollListener);
    this.scrollTimer.setInitialDelay(300);
    this.insetCache = this.slider.getInsets();
    this.leftToRightCache = BasicGraphicsUtils.isLeftToRight(this.slider);
    this.focusRect = new Rectangle();
    this.contentRect = new Rectangle();
    this.labelRect = new Rectangle();
    this.tickRect = new Rectangle();
    this.trackRect = new Rectangle();
    this.thumbRect = new Rectangle();
    this.lastValue = this.slider.getValue();
    calculateGeometry();
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    if (paramJComponent != this.slider)
      throw new IllegalComponentStateException(this + " was asked to deinstall() " + paramJComponent + " when it only knows about " + this.slider + "."); 
    this.scrollTimer.stop();
    this.scrollTimer = null;
    uninstallDefaults(this.slider);
    uninstallListeners(this.slider);
    uninstallKeyboardActions(this.slider);
    this.insetCache = null;
    this.leftToRightCache = true;
    this.focusRect = null;
    this.contentRect = null;
    this.labelRect = null;
    this.tickRect = null;
    this.trackRect = null;
    this.thumbRect = null;
    this.trackListener = null;
    this.changeListener = null;
    this.componentListener = null;
    this.focusListener = null;
    this.scrollListener = null;
    this.propertyChangeListener = null;
    this.slider = null;
  }
  
  protected void installDefaults(JSlider paramJSlider) {
    LookAndFeel.installBorder(paramJSlider, "Slider.border");
    LookAndFeel.installColorsAndFont(paramJSlider, "Slider.background", "Slider.foreground", "Slider.font");
    this.highlightColor = UIManager.getColor("Slider.highlight");
    this.shadowColor = UIManager.getColor("Slider.shadow");
    this.focusColor = UIManager.getColor("Slider.focus");
    this.focusInsets = (Insets)UIManager.get("Slider.focusInsets");
    if (this.focusInsets == null)
      this.focusInsets = new InsetsUIResource(2, 2, 2, 2); 
  }
  
  protected void uninstallDefaults(JSlider paramJSlider) {
    LookAndFeel.uninstallBorder(paramJSlider);
    this.focusInsets = null;
  }
  
  protected TrackListener createTrackListener(JSlider paramJSlider) { return new TrackListener(); }
  
  protected ChangeListener createChangeListener(JSlider paramJSlider) { return getHandler(); }
  
  protected ComponentListener createComponentListener(JSlider paramJSlider) { return getHandler(); }
  
  protected FocusListener createFocusListener(JSlider paramJSlider) { return getHandler(); }
  
  protected ScrollListener createScrollListener(JSlider paramJSlider) { return new ScrollListener(); }
  
  protected PropertyChangeListener createPropertyChangeListener(JSlider paramJSlider) { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void installListeners(JSlider paramJSlider) {
    paramJSlider.addMouseListener(this.trackListener);
    paramJSlider.addMouseMotionListener(this.trackListener);
    paramJSlider.addFocusListener(this.focusListener);
    paramJSlider.addComponentListener(this.componentListener);
    paramJSlider.addPropertyChangeListener(this.propertyChangeListener);
    paramJSlider.getModel().addChangeListener(this.changeListener);
  }
  
  protected void uninstallListeners(JSlider paramJSlider) {
    paramJSlider.removeMouseListener(this.trackListener);
    paramJSlider.removeMouseMotionListener(this.trackListener);
    paramJSlider.removeFocusListener(this.focusListener);
    paramJSlider.removeComponentListener(this.componentListener);
    paramJSlider.removePropertyChangeListener(this.propertyChangeListener);
    paramJSlider.getModel().removeChangeListener(this.changeListener);
    this.handler = null;
  }
  
  protected void installKeyboardActions(JSlider paramJSlider) {
    InputMap inputMap = getInputMap(0, paramJSlider);
    SwingUtilities.replaceUIInputMap(paramJSlider, 0, inputMap);
    LazyActionMap.installLazyActionMap(paramJSlider, BasicSliderUI.class, "Slider.actionMap");
  }
  
  InputMap getInputMap(int paramInt, JSlider paramJSlider) {
    if (paramInt == 0) {
      InputMap inputMap1 = (InputMap)DefaultLookup.get(paramJSlider, this, "Slider.focusInputMap");
      InputMap inputMap2;
      if (paramJSlider.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(paramJSlider, this, "Slider.focusInputMap.RightToLeft")) == null)
        return inputMap1; 
      inputMap2.setParent(inputMap1);
      return inputMap2;
    } 
    return null;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("positiveUnitIncrement"));
    paramLazyActionMap.put(new Actions("positiveBlockIncrement"));
    paramLazyActionMap.put(new Actions("negativeUnitIncrement"));
    paramLazyActionMap.put(new Actions("negativeBlockIncrement"));
    paramLazyActionMap.put(new Actions("minScroll"));
    paramLazyActionMap.put(new Actions("maxScroll"));
  }
  
  protected void uninstallKeyboardActions(JSlider paramJSlider) {
    SwingUtilities.replaceUIActionMap(paramJSlider, null);
    SwingUtilities.replaceUIInputMap(paramJSlider, 0, null);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if (this.slider.getPaintLabels() && labelsHaveSameBaselines()) {
      FontMetrics fontMetrics = this.slider.getFontMetrics(this.slider.getFont());
      Insets insets = this.slider.getInsets();
      Dimension dimension = getThumbSize();
      if (this.slider.getOrientation() == 0) {
        int i = getTickLength();
        int j = paramInt2 - insets.top - insets.bottom - this.focusInsets.top - this.focusInsets.bottom;
        int k = dimension.height;
        int m = k;
        if (this.slider.getPaintTicks())
          m += i; 
        m += getHeightOfTallestLabel();
        int n = insets.top + this.focusInsets.top + (j - m - 1) / 2;
        int i1 = k;
        int i2 = n + i1;
        int i3 = i;
        if (!this.slider.getPaintTicks())
          i3 = 0; 
        int i4 = i2 + i3;
        return i4 + fontMetrics.getAscent();
      } 
      boolean bool = this.slider.getInverted();
      Integer integer = bool ? getLowestValue() : getHighestValue();
      if (integer != null) {
        int i = dimension.height;
        int j = Math.max(fontMetrics.getHeight() / 2, i / 2);
        int k = this.focusInsets.top + insets.top;
        int m = k + j;
        int n = paramInt2 - this.focusInsets.top - this.focusInsets.bottom - insets.top - insets.bottom - j - j;
        int i1 = yPositionForValue(integer.intValue(), m, n);
        return i1 - fontMetrics.getHeight() / 2 + fontMetrics.getAscent();
      } 
    } 
    return 0;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  protected boolean labelsHaveSameBaselines() {
    if (!this.checkedLabelBaselines) {
      this.checkedLabelBaselines = true;
      Dictionary dictionary = this.slider.getLabelTable();
      if (dictionary != null) {
        this.sameLabelBaselines = true;
        Enumeration enumeration = dictionary.elements();
        int i = -1;
        while (enumeration.hasMoreElements()) {
          JComponent jComponent = (JComponent)enumeration.nextElement();
          Dimension dimension = jComponent.getPreferredSize();
          int j = jComponent.getBaseline(dimension.width, dimension.height);
          if (j >= 0) {
            if (i == -1) {
              i = j;
              continue;
            } 
            if (i != j) {
              this.sameLabelBaselines = false;
              break;
            } 
            continue;
          } 
          this.sameLabelBaselines = false;
        } 
      } else {
        this.sameLabelBaselines = false;
      } 
    } 
    return this.sameLabelBaselines;
  }
  
  public Dimension getPreferredHorizontalSize() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.horizontalSize");
    if (dimension == null)
      dimension = new Dimension(200, 21); 
    return dimension;
  }
  
  public Dimension getPreferredVerticalSize() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.verticalSize");
    if (dimension == null)
      dimension = new Dimension(21, 200); 
    return dimension;
  }
  
  public Dimension getMinimumHorizontalSize() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumHorizontalSize");
    if (dimension == null)
      dimension = new Dimension(36, 21); 
    return dimension;
  }
  
  public Dimension getMinimumVerticalSize() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumVerticalSize");
    if (dimension == null)
      dimension = new Dimension(21, 36); 
    return dimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension;
    recalculateIfInsetsChanged();
    if (this.slider.getOrientation() == 1) {
      dimension = new Dimension(getPreferredVerticalSize());
      dimension.width = this.insetCache.left + this.insetCache.right;
      dimension.width += this.focusInsets.left + this.focusInsets.right;
      dimension.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
    } else {
      dimension = new Dimension(getPreferredHorizontalSize());
      dimension.height = this.insetCache.top + this.insetCache.bottom;
      dimension.height += this.focusInsets.top + this.focusInsets.bottom;
      dimension.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
    } 
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension;
    recalculateIfInsetsChanged();
    if (this.slider.getOrientation() == 1) {
      dimension = new Dimension(getMinimumVerticalSize());
      dimension.width = this.insetCache.left + this.insetCache.right;
      dimension.width += this.focusInsets.left + this.focusInsets.right;
      dimension.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
    } else {
      dimension = new Dimension(getMinimumHorizontalSize());
      dimension.height = this.insetCache.top + this.insetCache.bottom;
      dimension.height += this.focusInsets.top + this.focusInsets.bottom;
      dimension.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
    } 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(paramJComponent);
    if (this.slider.getOrientation() == 1) {
      dimension.height = 32767;
    } else {
      dimension.width = 32767;
    } 
    return dimension;
  }
  
  protected void calculateGeometry() {
    calculateFocusRect();
    calculateContentRect();
    calculateThumbSize();
    calculateTrackBuffer();
    calculateTrackRect();
    calculateTickRect();
    calculateLabelRect();
    calculateThumbLocation();
  }
  
  protected void calculateFocusRect() {
    this.focusRect.x = this.insetCache.left;
    this.focusRect.y = this.insetCache.top;
    this.focusRect.width = this.slider.getWidth() - this.insetCache.left + this.insetCache.right;
    this.focusRect.height = this.slider.getHeight() - this.insetCache.top + this.insetCache.bottom;
  }
  
  protected void calculateThumbSize() {
    Dimension dimension = getThumbSize();
    this.thumbRect.setSize(dimension.width, dimension.height);
  }
  
  protected void calculateContentRect() {
    this.focusRect.x += this.focusInsets.left;
    this.focusRect.y += this.focusInsets.top;
    this.focusRect.width -= this.focusInsets.left + this.focusInsets.right;
    this.focusRect.height -= this.focusInsets.top + this.focusInsets.bottom;
  }
  
  private int getTickSpacing() {
    byte b;
    int i = this.slider.getMajorTickSpacing();
    int j = this.slider.getMinorTickSpacing();
    if (j > 0) {
      b = j;
    } else if (i > 0) {
      b = i;
    } else {
      b = 0;
    } 
    return b;
  }
  
  protected void calculateThumbLocation() {
    if (this.slider.getSnapToTicks()) {
      int i = this.slider.getValue();
      int j = i;
      int k = getTickSpacing();
      if (k != 0) {
        if ((i - this.slider.getMinimum()) % k != 0) {
          float f = (i - this.slider.getMinimum()) / k;
          int m = Math.round(f);
          if ((f - (int)f) == 0.5D && i < this.lastValue)
            m--; 
          j = this.slider.getMinimum() + m * k;
        } 
        if (j != i)
          this.slider.setValue(j); 
      } 
    } 
    if (this.slider.getOrientation() == 0) {
      int i = xPositionForValue(this.slider.getValue());
      this.thumbRect.x = i - this.thumbRect.width / 2;
      this.thumbRect.y = this.trackRect.y;
    } else {
      int i = yPositionForValue(this.slider.getValue());
      this.thumbRect.x = this.trackRect.x;
      this.thumbRect.y = i - this.thumbRect.height / 2;
    } 
  }
  
  protected void calculateTrackBuffer() {
    if (this.slider.getPaintLabels() && this.slider.getLabelTable() != null) {
      Component component1 = getHighestValueLabel();
      Component component2 = getLowestValueLabel();
      if (this.slider.getOrientation() == 0) {
        this.trackBuffer = Math.max((component1.getBounds()).width, (component2.getBounds()).width) / 2;
        this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.width / 2);
      } else {
        this.trackBuffer = Math.max((component1.getBounds()).height, (component2.getBounds()).height) / 2;
        this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.height / 2);
      } 
    } else if (this.slider.getOrientation() == 0) {
      this.trackBuffer = this.thumbRect.width / 2;
    } else {
      this.trackBuffer = this.thumbRect.height / 2;
    } 
  }
  
  protected void calculateTrackRect() {
    if (this.slider.getOrientation() == 0) {
      int i = this.thumbRect.height;
      if (this.slider.getPaintTicks())
        i += getTickLength(); 
      if (this.slider.getPaintLabels())
        i += getHeightOfTallestLabel(); 
      this.contentRect.x += this.trackBuffer;
      this.contentRect.y += (this.contentRect.height - i - 1) / 2;
      this.contentRect.width -= this.trackBuffer * 2;
      this.trackRect.height = this.thumbRect.height;
    } else {
      int i = this.thumbRect.width;
      if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
        if (this.slider.getPaintTicks())
          i += getTickLength(); 
        if (this.slider.getPaintLabels())
          i += getWidthOfWidestLabel(); 
      } else {
        if (this.slider.getPaintTicks())
          i -= getTickLength(); 
        if (this.slider.getPaintLabels())
          i -= getWidthOfWidestLabel(); 
      } 
      this.contentRect.x += (this.contentRect.width - i - 1) / 2;
      this.contentRect.y += this.trackBuffer;
      this.trackRect.width = this.thumbRect.width;
      this.contentRect.height -= this.trackBuffer * 2;
    } 
  }
  
  protected int getTickLength() { return 8; }
  
  protected void calculateTickRect() {
    if (this.slider.getOrientation() == 0) {
      this.tickRect.x = this.trackRect.x;
      this.trackRect.y += this.trackRect.height;
      this.tickRect.width = this.trackRect.width;
      this.tickRect.height = this.slider.getPaintTicks() ? getTickLength() : 0;
    } else {
      this.tickRect.width = this.slider.getPaintTicks() ? getTickLength() : 0;
      if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
        this.trackRect.x += this.trackRect.width;
      } else {
        this.trackRect.x -= this.tickRect.width;
      } 
      this.tickRect.y = this.trackRect.y;
      this.tickRect.height = this.trackRect.height;
    } 
  }
  
  protected void calculateLabelRect() {
    if (this.slider.getPaintLabels()) {
      if (this.slider.getOrientation() == 0) {
        this.tickRect.x -= this.trackBuffer;
        this.tickRect.y += this.tickRect.height;
        this.tickRect.width += this.trackBuffer * 2;
        this.labelRect.height = getHeightOfTallestLabel();
      } else {
        if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
          this.tickRect.x += this.tickRect.width;
          this.labelRect.width = getWidthOfWidestLabel();
        } else {
          this.labelRect.width = getWidthOfWidestLabel();
          this.tickRect.x -= this.labelRect.width;
        } 
        this.tickRect.y -= this.trackBuffer;
        this.tickRect.height += this.trackBuffer * 2;
      } 
    } else if (this.slider.getOrientation() == 0) {
      this.labelRect.x = this.tickRect.x;
      this.tickRect.y += this.tickRect.height;
      this.labelRect.width = this.tickRect.width;
      this.labelRect.height = 0;
    } else {
      if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
        this.tickRect.x += this.tickRect.width;
      } else {
        this.labelRect.x = this.tickRect.x;
      } 
      this.labelRect.y = this.tickRect.y;
      this.labelRect.width = 0;
      this.labelRect.height = this.tickRect.height;
    } 
  }
  
  protected Dimension getThumbSize() {
    Dimension dimension = new Dimension();
    if (this.slider.getOrientation() == 1) {
      dimension.width = 20;
      dimension.height = 11;
    } else {
      dimension.width = 11;
      dimension.height = 20;
    } 
    return dimension;
  }
  
  protected int getWidthOfWidestLabel() {
    Dictionary dictionary = this.slider.getLabelTable();
    int i = 0;
    if (dictionary != null) {
      Enumeration enumeration = dictionary.keys();
      while (enumeration.hasMoreElements()) {
        JComponent jComponent = (JComponent)dictionary.get(enumeration.nextElement());
        i = Math.max((jComponent.getPreferredSize()).width, i);
      } 
    } 
    return i;
  }
  
  protected int getHeightOfTallestLabel() {
    Dictionary dictionary = this.slider.getLabelTable();
    int i = 0;
    if (dictionary != null) {
      Enumeration enumeration = dictionary.keys();
      while (enumeration.hasMoreElements()) {
        JComponent jComponent = (JComponent)dictionary.get(enumeration.nextElement());
        i = Math.max((jComponent.getPreferredSize()).height, i);
      } 
    } 
    return i;
  }
  
  protected int getWidthOfHighValueLabel() {
    Component component = getHighestValueLabel();
    int i = 0;
    if (component != null)
      i = (component.getPreferredSize()).width; 
    return i;
  }
  
  protected int getWidthOfLowValueLabel() {
    Component component = getLowestValueLabel();
    int i = 0;
    if (component != null)
      i = (component.getPreferredSize()).width; 
    return i;
  }
  
  protected int getHeightOfHighValueLabel() {
    Component component = getHighestValueLabel();
    int i = 0;
    if (component != null)
      i = (component.getPreferredSize()).height; 
    return i;
  }
  
  protected int getHeightOfLowValueLabel() {
    Component component = getLowestValueLabel();
    int i = 0;
    if (component != null)
      i = (component.getPreferredSize()).height; 
    return i;
  }
  
  protected boolean drawInverted() { return (this.slider.getOrientation() == 0) ? (BasicGraphicsUtils.isLeftToRight(this.slider) ? this.slider.getInverted() : (!this.slider.getInverted() ? 1 : 0)) : this.slider.getInverted(); }
  
  protected Integer getHighestValue() {
    Dictionary dictionary = this.slider.getLabelTable();
    if (dictionary == null)
      return null; 
    Enumeration enumeration = dictionary.keys();
    Integer integer = null;
    while (enumeration.hasMoreElements()) {
      Integer integer1 = (Integer)enumeration.nextElement();
      if (integer == null || integer1.intValue() > integer.intValue())
        integer = integer1; 
    } 
    return integer;
  }
  
  protected Integer getLowestValue() {
    Dictionary dictionary = this.slider.getLabelTable();
    if (dictionary == null)
      return null; 
    Enumeration enumeration = dictionary.keys();
    Integer integer = null;
    while (enumeration.hasMoreElements()) {
      Integer integer1 = (Integer)enumeration.nextElement();
      if (integer == null || integer1.intValue() < integer.intValue())
        integer = integer1; 
    } 
    return integer;
  }
  
  protected Component getLowestValueLabel() {
    Integer integer = getLowestValue();
    return (integer != null) ? (Component)this.slider.getLabelTable().get(integer) : null;
  }
  
  protected Component getHighestValueLabel() {
    Integer integer = getHighestValue();
    return (integer != null) ? (Component)this.slider.getLabelTable().get(integer) : null;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    recalculateIfInsetsChanged();
    recalculateIfOrientationChanged();
    Rectangle rectangle = paramGraphics.getClipBounds();
    if (!rectangle.intersects(this.trackRect) && this.slider.getPaintTrack())
      calculateGeometry(); 
    if (this.slider.getPaintTrack() && rectangle.intersects(this.trackRect))
      paintTrack(paramGraphics); 
    if (this.slider.getPaintTicks() && rectangle.intersects(this.tickRect))
      paintTicks(paramGraphics); 
    if (this.slider.getPaintLabels() && rectangle.intersects(this.labelRect))
      paintLabels(paramGraphics); 
    if (this.slider.hasFocus() && rectangle.intersects(this.focusRect))
      paintFocus(paramGraphics); 
    if (rectangle.intersects(this.thumbRect))
      paintThumb(paramGraphics); 
  }
  
  protected void recalculateIfInsetsChanged() {
    Insets insets = this.slider.getInsets();
    if (!insets.equals(this.insetCache)) {
      this.insetCache = insets;
      calculateGeometry();
    } 
  }
  
  protected void recalculateIfOrientationChanged() {
    boolean bool = BasicGraphicsUtils.isLeftToRight(this.slider);
    if (bool != this.leftToRightCache) {
      this.leftToRightCache = bool;
      calculateGeometry();
    } 
  }
  
  public void paintFocus(Graphics paramGraphics) {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, this.focusRect.x, this.focusRect.y, this.focusRect.width, this.focusRect.height);
  }
  
  public void paintTrack(Graphics paramGraphics) {
    Rectangle rectangle = this.trackRect;
    if (this.slider.getOrientation() == 0) {
      int i = rectangle.height / 2 - 2;
      int j = rectangle.width;
      paramGraphics.translate(rectangle.x, rectangle.y + i);
      paramGraphics.setColor(getShadowColor());
      paramGraphics.drawLine(0, 0, j - 1, 0);
      paramGraphics.drawLine(0, 1, 0, 2);
      paramGraphics.setColor(getHighlightColor());
      paramGraphics.drawLine(0, 3, j, 3);
      paramGraphics.drawLine(j, 0, j, 3);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(1, 1, j - 2, 1);
      paramGraphics.translate(-rectangle.x, -(rectangle.y + i));
    } else {
      int i = rectangle.width / 2 - 2;
      int j = rectangle.height;
      paramGraphics.translate(rectangle.x + i, rectangle.y);
      paramGraphics.setColor(getShadowColor());
      paramGraphics.drawLine(0, 0, 0, j - 1);
      paramGraphics.drawLine(1, 0, 2, 0);
      paramGraphics.setColor(getHighlightColor());
      paramGraphics.drawLine(3, 0, 3, j);
      paramGraphics.drawLine(0, j, 3, j);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(1, 1, 1, j - 2);
      paramGraphics.translate(-(rectangle.x + i), -rectangle.y);
    } 
  }
  
  public void paintTicks(Graphics paramGraphics) {
    Rectangle rectangle = this.tickRect;
    paramGraphics.setColor(DefaultLookup.getColor(this.slider, this, "Slider.tickColor", Color.black));
    if (this.slider.getOrientation() == 0) {
      paramGraphics.translate(0, rectangle.y);
      if (this.slider.getMinorTickSpacing() > 0)
        for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i += this.slider.getMinorTickSpacing()) {
          int j = xPositionForValue(i);
          paintMinorTickForHorizSlider(paramGraphics, rectangle, j);
          if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < i)
            break; 
        }  
      if (this.slider.getMajorTickSpacing() > 0)
        for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i += this.slider.getMajorTickSpacing()) {
          int j = xPositionForValue(i);
          paintMajorTickForHorizSlider(paramGraphics, rectangle, j);
          if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < i)
            break; 
        }  
      paramGraphics.translate(0, -rectangle.y);
    } else {
      paramGraphics.translate(rectangle.x, 0);
      if (this.slider.getMinorTickSpacing() > 0) {
        int i = 0;
        if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
          i = rectangle.width - rectangle.width / 2;
          paramGraphics.translate(i, 0);
        } 
        int j;
        for (j = this.slider.getMinimum(); j <= this.slider.getMaximum(); j += this.slider.getMinorTickSpacing()) {
          int k = yPositionForValue(j);
          paintMinorTickForVertSlider(paramGraphics, rectangle, k);
          if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < j)
            break; 
        } 
        if (!BasicGraphicsUtils.isLeftToRight(this.slider))
          paramGraphics.translate(-i, 0); 
      } 
      if (this.slider.getMajorTickSpacing() > 0) {
        if (!BasicGraphicsUtils.isLeftToRight(this.slider))
          paramGraphics.translate(2, 0); 
        for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i += this.slider.getMajorTickSpacing()) {
          int j = yPositionForValue(i);
          paintMajorTickForVertSlider(paramGraphics, rectangle, j);
          if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < i)
            break; 
        } 
        if (!BasicGraphicsUtils.isLeftToRight(this.slider))
          paramGraphics.translate(-2, 0); 
      } 
      paramGraphics.translate(-rectangle.x, 0);
    } 
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) { paramGraphics.drawLine(paramInt, 0, paramInt, paramRectangle.height / 2 - 1); }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) { paramGraphics.drawLine(paramInt, 0, paramInt, paramRectangle.height - 2); }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) { paramGraphics.drawLine(0, paramInt, paramRectangle.width / 2 - 1, paramInt); }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) { paramGraphics.drawLine(0, paramInt, paramRectangle.width - 2, paramInt); }
  
  public void paintLabels(Graphics paramGraphics) {
    Rectangle rectangle = this.labelRect;
    Dictionary dictionary = this.slider.getLabelTable();
    if (dictionary != null) {
      Enumeration enumeration = dictionary.keys();
      int i = this.slider.getMinimum();
      int j = this.slider.getMaximum();
      boolean bool = this.slider.isEnabled();
      while (enumeration.hasMoreElements()) {
        Integer integer = (Integer)enumeration.nextElement();
        int k = integer.intValue();
        if (k >= i && k <= j) {
          JComponent jComponent = (JComponent)dictionary.get(integer);
          jComponent.setEnabled(bool);
          if (jComponent instanceof JLabel) {
            Icon icon = jComponent.isEnabled() ? ((JLabel)jComponent).getIcon() : ((JLabel)jComponent).getDisabledIcon();
            if (icon instanceof ImageIcon)
              Toolkit.getDefaultToolkit().checkImage(((ImageIcon)icon).getImage(), -1, -1, this.slider); 
          } 
          if (this.slider.getOrientation() == 0) {
            paramGraphics.translate(0, rectangle.y);
            paintHorizontalLabel(paramGraphics, k, jComponent);
            paramGraphics.translate(0, -rectangle.y);
            continue;
          } 
          int m = 0;
          if (!BasicGraphicsUtils.isLeftToRight(this.slider))
            m = rectangle.width - (jComponent.getPreferredSize()).width; 
          paramGraphics.translate(rectangle.x + m, 0);
          paintVerticalLabel(paramGraphics, k, jComponent);
          paramGraphics.translate(-rectangle.x - m, 0);
        } 
      } 
    } 
  }
  
  protected void paintHorizontalLabel(Graphics paramGraphics, int paramInt, Component paramComponent) {
    int i = xPositionForValue(paramInt);
    int j = i - (paramComponent.getPreferredSize()).width / 2;
    paramGraphics.translate(j, 0);
    paramComponent.paint(paramGraphics);
    paramGraphics.translate(-j, 0);
  }
  
  protected void paintVerticalLabel(Graphics paramGraphics, int paramInt, Component paramComponent) {
    int i = yPositionForValue(paramInt);
    int j = i - (paramComponent.getPreferredSize()).height / 2;
    paramGraphics.translate(0, j);
    paramComponent.paint(paramGraphics);
    paramGraphics.translate(0, -j);
  }
  
  public void paintThumb(Graphics paramGraphics) {
    Rectangle rectangle = this.thumbRect;
    int i = rectangle.width;
    int j = rectangle.height;
    paramGraphics.translate(rectangle.x, rectangle.y);
    if (this.slider.isEnabled()) {
      paramGraphics.setColor(this.slider.getBackground());
    } else {
      paramGraphics.setColor(this.slider.getBackground().darker());
    } 
    Boolean bool = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
    if ((!this.slider.getPaintTicks() && bool == null) || bool == Boolean.FALSE) {
      paramGraphics.fillRect(0, 0, i, j);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(0, j - 1, i - 1, j - 1);
      paramGraphics.drawLine(i - 1, 0, i - 1, j - 1);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(0, 0, 0, j - 2);
      paramGraphics.drawLine(1, 0, i - 2, 0);
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(1, j - 2, i - 2, j - 2);
      paramGraphics.drawLine(i - 2, 1, i - 2, j - 3);
    } else if (this.slider.getOrientation() == 0) {
      int k = i / 2;
      paramGraphics.fillRect(1, 1, i - 3, j - 1 - k);
      Polygon polygon = new Polygon();
      polygon.addPoint(1, j - k);
      polygon.addPoint(k - 1, j - 1);
      polygon.addPoint(i - 2, j - 1 - k);
      paramGraphics.fillPolygon(polygon);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(0, 0, i - 2, 0);
      paramGraphics.drawLine(0, 1, 0, j - 1 - k);
      paramGraphics.drawLine(0, j - k, k - 1, j - 1);
      paramGraphics.setColor(Color.black);
      paramGraphics.drawLine(i - 1, 0, i - 1, j - 2 - k);
      paramGraphics.drawLine(i - 1, j - 1 - k, i - 1 - k, j - 1);
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(i - 2, 1, i - 2, j - 2 - k);
      paramGraphics.drawLine(i - 2, j - 1 - k, i - 1 - k, j - 2);
    } else {
      int k = j / 2;
      if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
        paramGraphics.fillRect(1, 1, i - 1 - k, j - 3);
        Polygon polygon = new Polygon();
        polygon.addPoint(i - k - 1, 0);
        polygon.addPoint(i - 1, k);
        polygon.addPoint(i - 1 - k, j - 2);
        paramGraphics.fillPolygon(polygon);
        paramGraphics.setColor(this.highlightColor);
        paramGraphics.drawLine(0, 0, 0, j - 2);
        paramGraphics.drawLine(1, 0, i - 1 - k, 0);
        paramGraphics.drawLine(i - k - 1, 0, i - 1, k);
        paramGraphics.setColor(Color.black);
        paramGraphics.drawLine(0, j - 1, i - 2 - k, j - 1);
        paramGraphics.drawLine(i - 1 - k, j - 1, i - 1, j - 1 - k);
        paramGraphics.setColor(this.shadowColor);
        paramGraphics.drawLine(1, j - 2, i - 2 - k, j - 2);
        paramGraphics.drawLine(i - 1 - k, j - 2, i - 2, j - k - 1);
      } else {
        paramGraphics.fillRect(5, 1, i - 1 - k, j - 3);
        Polygon polygon = new Polygon();
        polygon.addPoint(k, 0);
        polygon.addPoint(0, k);
        polygon.addPoint(k, j - 2);
        paramGraphics.fillPolygon(polygon);
        paramGraphics.setColor(this.highlightColor);
        paramGraphics.drawLine(k - 1, 0, i - 2, 0);
        paramGraphics.drawLine(0, k, k, 0);
        paramGraphics.setColor(Color.black);
        paramGraphics.drawLine(0, j - 1 - k, k, j - 1);
        paramGraphics.drawLine(k, j - 1, i - 1, j - 1);
        paramGraphics.setColor(this.shadowColor);
        paramGraphics.drawLine(k, j - 2, i - 2, j - 2);
        paramGraphics.drawLine(i - 1, 1, i - 1, j - 2);
      } 
    } 
    paramGraphics.translate(-rectangle.x, -rectangle.y);
  }
  
  public void setThumbLocation(int paramInt1, int paramInt2) {
    unionRect.setBounds(this.thumbRect);
    this.thumbRect.setLocation(paramInt1, paramInt2);
    SwingUtilities.computeUnion(this.thumbRect.x, this.thumbRect.y, this.thumbRect.width, this.thumbRect.height, unionRect);
    this.slider.repaint(unionRect.x, unionRect.y, unionRect.width, unionRect.height);
  }
  
  public void scrollByBlock(int paramInt) {
    synchronized (this.slider) {
      int i = (this.slider.getMaximum() - this.slider.getMinimum()) / 10;
      if (i == 0)
        i = 1; 
      if (this.slider.getSnapToTicks()) {
        int k = getTickSpacing();
        if (i < k)
          i = k; 
      } 
      int j = i * ((paramInt > 0) ? 1 : -1);
      this.slider.setValue(this.slider.getValue() + j);
    } 
  }
  
  public void scrollByUnit(int paramInt) {
    synchronized (this.slider) {
      int i = (paramInt > 0) ? 1 : -1;
      if (this.slider.getSnapToTicks())
        i *= getTickSpacing(); 
      this.slider.setValue(this.slider.getValue() + i);
    } 
  }
  
  protected void scrollDueToClickInTrack(int paramInt) { scrollByBlock(paramInt); }
  
  protected int xPositionForValue(int paramInt) {
    int i = this.slider.getMinimum();
    int j = this.slider.getMaximum();
    int k = this.trackRect.width;
    double d1 = j - i;
    double d2 = k / d1;
    int m = this.trackRect.x;
    int n = this.trackRect.x + this.trackRect.width - 1;
    if (!drawInverted()) {
      null = m;
      null = (int)(null + Math.round(d2 * (paramInt - i)));
    } else {
      null = n;
      null = (int)(null - Math.round(d2 * (paramInt - i)));
    } 
    null = Math.max(m, null);
    return Math.min(n, null);
  }
  
  protected int yPositionForValue(int paramInt) { return yPositionForValue(paramInt, this.trackRect.y, this.trackRect.height); }
  
  protected int yPositionForValue(int paramInt1, int paramInt2, int paramInt3) {
    int i = this.slider.getMinimum();
    int j = this.slider.getMaximum();
    double d1 = j - i;
    double d2 = paramInt3 / d1;
    int k = paramInt2 + paramInt3 - 1;
    if (!drawInverted()) {
      null = paramInt2;
      null = (int)(null + Math.round(d2 * (j - paramInt1)));
    } else {
      null = paramInt2;
      null = (int)(null + Math.round(d2 * (paramInt1 - i)));
    } 
    null = Math.max(paramInt2, null);
    return Math.min(k, null);
  }
  
  public int valueForYPosition(int paramInt) {
    int i;
    int j = this.slider.getMinimum();
    int k = this.slider.getMaximum();
    int m = this.trackRect.height;
    int n = this.trackRect.y;
    int i1 = this.trackRect.y + this.trackRect.height - 1;
    if (paramInt <= n) {
      i = drawInverted() ? j : k;
    } else if (paramInt >= i1) {
      i = drawInverted() ? k : j;
    } else {
      int i2 = paramInt - n;
      double d1 = k - j;
      double d2 = d1 / m;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? (j + i3) : (k - i3);
    } 
    return i;
  }
  
  public int valueForXPosition(int paramInt) {
    int i;
    int j = this.slider.getMinimum();
    int k = this.slider.getMaximum();
    int m = this.trackRect.width;
    int n = this.trackRect.x;
    int i1 = this.trackRect.x + this.trackRect.width - 1;
    if (paramInt <= n) {
      i = drawInverted() ? k : j;
    } else if (paramInt >= i1) {
      i = drawInverted() ? j : k;
    } else {
      int i2 = paramInt - n;
      double d1 = k - j;
      double d2 = d1 / m;
      int i3 = (int)Math.round(i2 * d2);
      i = drawInverted() ? (k - i3) : (j + i3);
    } 
    return i;
  }
  
  public class ActionScroller extends AbstractAction {
    int dir;
    
    boolean block;
    
    JSlider slider;
    
    public ActionScroller(JSlider param1JSlider, int param1Int, boolean param1Boolean) {
      this.dir = param1Int;
      this.block = param1Boolean;
      this.slider = param1JSlider;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { SHARED_ACTION.scroll(this.slider, BasicSliderUI.this, this.dir, this.block); }
    
    public boolean isEnabled() {
      boolean bool = true;
      if (this.slider != null)
        bool = this.slider.isEnabled(); 
      return bool;
    }
  }
  
  private static class Actions extends UIAction {
    public static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
    
    public static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
    
    public static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
    
    public static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
    
    public static final String MIN_SCROLL_INCREMENT = "minScroll";
    
    public static final String MAX_SCROLL_INCREMENT = "maxScroll";
    
    Actions() { super(null); }
    
    public Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JSlider jSlider = (JSlider)param1ActionEvent.getSource();
      BasicSliderUI basicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(jSlider.getUI(), BasicSliderUI.class);
      String str = getName();
      if (basicSliderUI == null)
        return; 
      if ("positiveUnitIncrement" == str) {
        scroll(jSlider, basicSliderUI, 1, false);
      } else if ("negativeUnitIncrement" == str) {
        scroll(jSlider, basicSliderUI, -1, false);
      } else if ("positiveBlockIncrement" == str) {
        scroll(jSlider, basicSliderUI, 1, true);
      } else if ("negativeBlockIncrement" == str) {
        scroll(jSlider, basicSliderUI, -1, true);
      } else if ("minScroll" == str) {
        scroll(jSlider, basicSliderUI, -2, false);
      } else if ("maxScroll" == str) {
        scroll(jSlider, basicSliderUI, 2, false);
      } 
    }
    
    private void scroll(JSlider param1JSlider, BasicSliderUI param1BasicSliderUI, int param1Int, boolean param1Boolean) {
      boolean bool = param1JSlider.getInverted();
      if (param1Int == -1 || param1Int == 1) {
        if (bool)
          param1Int = (param1Int == 1) ? -1 : 1; 
        if (param1Boolean) {
          param1BasicSliderUI.scrollByBlock(param1Int);
        } else {
          param1BasicSliderUI.scrollByUnit(param1Int);
        } 
      } else {
        if (bool)
          param1Int = (param1Int == -2) ? 2 : -2; 
        param1JSlider.setValue((param1Int == -2) ? param1JSlider.getMinimum() : param1JSlider.getMaximum());
      } 
    }
  }
  
  public class ChangeHandler implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicSliderUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  public class ComponentHandler extends ComponentAdapter {
    public void componentResized(ComponentEvent param1ComponentEvent) { BasicSliderUI.this.getHandler().componentResized(param1ComponentEvent); }
  }
  
  public class FocusHandler implements FocusListener {
    public void focusGained(FocusEvent param1FocusEvent) { BasicSliderUI.this.getHandler().focusGained(param1FocusEvent); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicSliderUI.this.getHandler().focusLost(param1FocusEvent); }
  }
  
  private class Handler implements ChangeListener, ComponentListener, FocusListener, PropertyChangeListener {
    private Handler() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (!BasicSliderUI.this.isDragging) {
        BasicSliderUI.this.calculateThumbLocation();
        BasicSliderUI.this.slider.repaint();
      } 
      BasicSliderUI.this.lastValue = BasicSliderUI.this.slider.getValue();
    }
    
    public void componentHidden(ComponentEvent param1ComponentEvent) {}
    
    public void componentMoved(ComponentEvent param1ComponentEvent) {}
    
    public void componentResized(ComponentEvent param1ComponentEvent) {
      BasicSliderUI.this.calculateGeometry();
      BasicSliderUI.this.slider.repaint();
    }
    
    public void componentShown(ComponentEvent param1ComponentEvent) {}
    
    public void focusGained(FocusEvent param1FocusEvent) { BasicSliderUI.this.slider.repaint(); }
    
    public void focusLost(FocusEvent param1FocusEvent) { BasicSliderUI.this.slider.repaint(); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "orientation" || str == "inverted" || str == "labelTable" || str == "majorTickSpacing" || str == "minorTickSpacing" || str == "paintTicks" || str == "paintTrack" || str == "font" || str == "paintLabels" || str == "Slider.paintThumbArrowShape") {
        BasicSliderUI.this.checkedLabelBaselines = false;
        BasicSliderUI.this.calculateGeometry();
        BasicSliderUI.this.slider.repaint();
      } else if (str == "componentOrientation") {
        BasicSliderUI.this.calculateGeometry();
        BasicSliderUI.this.slider.repaint();
        InputMap inputMap = BasicSliderUI.this.getInputMap(0, BasicSliderUI.this.slider);
        SwingUtilities.replaceUIInputMap(BasicSliderUI.this.slider, 0, inputMap);
      } else if (str == "model") {
        ((BoundedRangeModel)param1PropertyChangeEvent.getOldValue()).removeChangeListener(BasicSliderUI.this.changeListener);
        ((BoundedRangeModel)param1PropertyChangeEvent.getNewValue()).addChangeListener(BasicSliderUI.this.changeListener);
        BasicSliderUI.this.calculateThumbLocation();
        BasicSliderUI.this.slider.repaint();
      } 
    }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicSliderUI.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  public class ScrollListener implements ActionListener {
    int direction = 1;
    
    boolean useBlockIncrement;
    
    public ScrollListener() {
      this.direction = 1;
      this.useBlockIncrement = true;
    }
    
    public ScrollListener(int param1Int, boolean param1Boolean) {
      this.direction = param1Int;
      this.useBlockIncrement = param1Boolean;
    }
    
    public void setDirection(int param1Int) { this.direction = param1Int; }
    
    public void setScrollByBlock(boolean param1Boolean) { this.useBlockIncrement = param1Boolean; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.useBlockIncrement) {
        BasicSliderUI.this.scrollByBlock(this.direction);
      } else {
        BasicSliderUI.this.scrollByUnit(this.direction);
      } 
      if (!BasicSliderUI.this.trackListener.shouldScroll(this.direction))
        ((Timer)param1ActionEvent.getSource()).stop(); 
    }
  }
  
  static class SharedActionScroller extends AbstractAction {
    int dir;
    
    boolean block;
    
    public SharedActionScroller(int param1Int, boolean param1Boolean) {
      this.dir = param1Int;
      this.block = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JSlider jSlider = (JSlider)param1ActionEvent.getSource();
      BasicSliderUI basicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(jSlider.getUI(), BasicSliderUI.class);
      if (basicSliderUI == null)
        return; 
      SHARED_ACTION.scroll(jSlider, basicSliderUI, this.dir, this.block);
    }
  }
  
  public class TrackListener extends MouseInputAdapter {
    protected int offset;
    
    protected int currentMouseX;
    
    protected int currentMouseY;
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      if (!BasicSliderUI.this.slider.isEnabled())
        return; 
      this.offset = 0;
      BasicSliderUI.this.scrollTimer.stop();
      BasicSliderUI.this.isDragging = false;
      BasicSliderUI.this.slider.setValueIsAdjusting(false);
      BasicSliderUI.this.slider.repaint();
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      int i;
      if (!BasicSliderUI.this.slider.isEnabled())
        return; 
      BasicSliderUI.this.calculateGeometry();
      this.currentMouseX = param1MouseEvent.getX();
      this.currentMouseY = param1MouseEvent.getY();
      if (BasicSliderUI.this.slider.isRequestFocusEnabled())
        BasicSliderUI.this.slider.requestFocus(); 
      if (BasicSliderUI.this.thumbRect.contains(this.currentMouseX, this.currentMouseY)) {
        if (UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag") && !SwingUtilities.isLeftMouseButton(param1MouseEvent))
          return; 
        switch (BasicSliderUI.this.slider.getOrientation()) {
          case 1:
            this.offset = this.currentMouseY - this.this$0.thumbRect.y;
            break;
          case 0:
            this.offset = this.currentMouseX - this.this$0.thumbRect.x;
            break;
        } 
        BasicSliderUI.this.isDragging = true;
        return;
      } 
      if (!SwingUtilities.isLeftMouseButton(param1MouseEvent))
        return; 
      BasicSliderUI.this.isDragging = false;
      BasicSliderUI.this.slider.setValueIsAdjusting(true);
      Dimension dimension = BasicSliderUI.this.slider.getSize();
      byte b = 1;
      switch (BasicSliderUI.this.slider.getOrientation()) {
        case 1:
          if (BasicSliderUI.this.thumbRect.isEmpty()) {
            int j = dimension.height / 2;
            if (!BasicSliderUI.this.drawInverted()) {
              b = (this.currentMouseY < j) ? 1 : -1;
              break;
            } 
            b = (this.currentMouseY < j) ? -1 : 1;
            break;
          } 
          i = this.this$0.thumbRect.y;
          if (!BasicSliderUI.this.drawInverted()) {
            b = (this.currentMouseY < i) ? 1 : -1;
            break;
          } 
          b = (this.currentMouseY < i) ? -1 : 1;
          break;
        case 0:
          if (BasicSliderUI.this.thumbRect.isEmpty()) {
            i = dimension.width / 2;
            if (!BasicSliderUI.this.drawInverted()) {
              b = (this.currentMouseX < i) ? -1 : 1;
              break;
            } 
            b = (this.currentMouseX < i) ? 1 : -1;
            break;
          } 
          i = this.this$0.thumbRect.x;
          if (!BasicSliderUI.this.drawInverted()) {
            b = (this.currentMouseX < i) ? -1 : 1;
            break;
          } 
          b = (this.currentMouseX < i) ? 1 : -1;
          break;
      } 
      if (shouldScroll(b))
        BasicSliderUI.this.scrollDueToClickInTrack(b); 
      if (shouldScroll(b)) {
        BasicSliderUI.this.scrollTimer.stop();
        BasicSliderUI.this.scrollListener.setDirection(b);
        BasicSliderUI.this.scrollTimer.start();
      } 
    }
    
    public boolean shouldScroll(int param1Int) {
      Rectangle rectangle = BasicSliderUI.this.thumbRect;
      if (BasicSliderUI.this.slider.getOrientation() == 1) {
        if (BasicSliderUI.this.drawInverted() ? (param1Int < 0) : (param1Int > 0)) {
          if (rectangle.y <= this.currentMouseY)
            return false; 
        } else if (rectangle.y + rectangle.height >= this.currentMouseY) {
          return false;
        } 
      } else if (BasicSliderUI.this.drawInverted() ? (param1Int < 0) : (param1Int > 0)) {
        if (rectangle.x + rectangle.width >= this.currentMouseX)
          return false; 
      } else if (rectangle.x <= this.currentMouseX) {
        return false;
      } 
      return (param1Int > 0 && BasicSliderUI.this.slider.getValue() + BasicSliderUI.this.slider.getExtent() >= BasicSliderUI.this.slider.getMaximum()) ? false : (!(param1Int < 0 && BasicSliderUI.this.slider.getValue() <= BasicSliderUI.this.slider.getMinimum()));
    }
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      int i6;
      int i5;
      int i4;
      int i3;
      int i2;
      int i1;
      int n;
      int m;
      int k;
      int j;
      int i;
      if (!BasicSliderUI.this.slider.isEnabled())
        return; 
      this.currentMouseX = param1MouseEvent.getX();
      this.currentMouseY = param1MouseEvent.getY();
      if (!BasicSliderUI.this.isDragging)
        return; 
      BasicSliderUI.this.slider.setValueIsAdjusting(true);
      switch (BasicSliderUI.this.slider.getOrientation()) {
        case 1:
          j = this.this$0.thumbRect.height / 2;
          k = param1MouseEvent.getY() - this.offset;
          m = this.this$0.trackRect.y;
          n = this.this$0.trackRect.y + this.this$0.trackRect.height - 1;
          i1 = BasicSliderUI.this.yPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
          if (BasicSliderUI.this.drawInverted()) {
            n = i1;
          } else {
            m = i1;
          } 
          k = Math.max(k, m - j);
          k = Math.min(k, n - j);
          BasicSliderUI.this.setThumbLocation(this.this$0.thumbRect.x, k);
          i = k + j;
          BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForYPosition(i));
          break;
        case 0:
          i2 = this.this$0.thumbRect.width / 2;
          i3 = param1MouseEvent.getX() - this.offset;
          i4 = this.this$0.trackRect.x;
          i5 = this.this$0.trackRect.x + this.this$0.trackRect.width - 1;
          i6 = BasicSliderUI.this.xPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
          if (BasicSliderUI.this.drawInverted()) {
            i4 = i6;
          } else {
            i5 = i6;
          } 
          i3 = Math.max(i3, i4 - i2);
          i3 = Math.min(i3, i5 - i2);
          BasicSliderUI.this.setThumbLocation(i3, this.this$0.thumbRect.y);
          i = i3 + i2;
          BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForXPosition(i));
          break;
      } 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */