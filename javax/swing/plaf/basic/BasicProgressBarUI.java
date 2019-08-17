package javax.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ProgressBarUI;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicProgressBarUI extends ProgressBarUI {
  private int cachedPercent;
  
  private int cellLength;
  
  private int cellSpacing;
  
  private Color selectionForeground;
  
  private Color selectionBackground;
  
  private Animator animator;
  
  protected JProgressBar progressBar;
  
  protected ChangeListener changeListener;
  
  private Handler handler;
  
  private int animationIndex = 0;
  
  private int numFrames;
  
  private int repaintInterval;
  
  private int cycleTime;
  
  private static boolean ADJUSTTIMER = true;
  
  protected Rectangle boxRect;
  
  private Rectangle nextPaintRect;
  
  private Rectangle componentInnards;
  
  private Rectangle oldComponentInnards;
  
  private double delta = 0.0D;
  
  private int maxPosition = 0;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicProgressBarUI(); }
  
  public void installUI(JComponent paramJComponent) {
    this.progressBar = (JProgressBar)paramJComponent;
    installDefaults();
    installListeners();
    if (this.progressBar.isIndeterminate())
      initIndeterminateValues(); 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    if (this.progressBar.isIndeterminate())
      cleanUpIndeterminateValues(); 
    uninstallDefaults();
    uninstallListeners();
    this.progressBar = null;
  }
  
  protected void installDefaults() {
    LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(this.progressBar, "ProgressBar.border");
    LookAndFeel.installColorsAndFont(this.progressBar, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
    this.cellLength = UIManager.getInt("ProgressBar.cellLength");
    if (this.cellLength == 0)
      this.cellLength = 1; 
    this.cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
    this.selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
    this.selectionBackground = UIManager.getColor("ProgressBar.selectionBackground");
  }
  
  protected void uninstallDefaults() { LookAndFeel.uninstallBorder(this.progressBar); }
  
  protected void installListeners() {
    this.changeListener = getHandler();
    this.progressBar.addChangeListener(this.changeListener);
    this.progressBar.addPropertyChangeListener(getHandler());
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void startAnimationTimer() {
    if (this.animator == null)
      this.animator = new Animator(null); 
    this.animator.start(getRepaintInterval());
  }
  
  protected void stopAnimationTimer() {
    if (this.animator != null)
      this.animator.stop(); 
  }
  
  protected void uninstallListeners() {
    this.progressBar.removeChangeListener(this.changeListener);
    this.progressBar.removePropertyChangeListener(getHandler());
    this.handler = null;
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
      FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
      Insets insets = this.progressBar.getInsets();
      int i = insets.top;
      paramInt2 = paramInt2 - insets.top - insets.bottom;
      return i + (paramInt2 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
    } 
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent) {
    super.getBaselineResizeBehavior(paramJComponent);
    return (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) ? Component.BaselineResizeBehavior.CENTER_OFFSET : Component.BaselineResizeBehavior.OTHER;
  }
  
  protected Dimension getPreferredInnerHorizontal() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.horizontalSize");
    if (dimension == null)
      dimension = new Dimension(146, 12); 
    return dimension;
  }
  
  protected Dimension getPreferredInnerVertical() {
    Dimension dimension = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.verticalSize");
    if (dimension == null)
      dimension = new Dimension(12, 146); 
    return dimension;
  }
  
  protected Color getSelectionForeground() { return this.selectionForeground; }
  
  protected Color getSelectionBackground() { return this.selectionBackground; }
  
  private int getCachedPercent() { return this.cachedPercent; }
  
  private void setCachedPercent(int paramInt) { this.cachedPercent = paramInt; }
  
  protected int getCellLength() { return this.progressBar.isStringPainted() ? 1 : this.cellLength; }
  
  protected void setCellLength(int paramInt) { this.cellLength = paramInt; }
  
  protected int getCellSpacing() { return this.progressBar.isStringPainted() ? 0 : this.cellSpacing; }
  
  protected void setCellSpacing(int paramInt) { this.cellSpacing = paramInt; }
  
  protected int getAmountFull(Insets paramInsets, int paramInt1, int paramInt2) {
    int i = 0;
    BoundedRangeModel boundedRangeModel = this.progressBar.getModel();
    if (boundedRangeModel.getMaximum() - boundedRangeModel.getMinimum() != 0)
      if (this.progressBar.getOrientation() == 0) {
        i = (int)Math.round(paramInt1 * this.progressBar.getPercentComplete());
      } else {
        i = (int)Math.round(paramInt2 * this.progressBar.getPercentComplete());
      }  
    return i;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (this.progressBar.isIndeterminate()) {
      paintIndeterminate(paramGraphics, paramJComponent);
    } else {
      paintDeterminate(paramGraphics, paramJComponent);
    } 
  }
  
  protected Rectangle getBox(Rectangle paramRectangle) {
    int i = getAnimationIndex();
    int j = this.numFrames / 2;
    if (sizeChanged() || this.delta == 0.0D || this.maxPosition == 0.0D)
      updateSizes(); 
    paramRectangle = getGenericBox(paramRectangle);
    if (paramRectangle == null)
      return null; 
    if (j <= 0)
      return null; 
    if (this.progressBar.getOrientation() == 0) {
      if (i < j) {
        this.componentInnards.x += (int)Math.round(this.delta * i);
      } else {
        paramRectangle.x = this.maxPosition - (int)Math.round(this.delta * (i - j));
      } 
    } else if (i < j) {
      this.componentInnards.y += (int)Math.round(this.delta * i);
    } else {
      paramRectangle.y = this.maxPosition - (int)Math.round(this.delta * (i - j));
    } 
    return paramRectangle;
  }
  
  private void updateSizes() {
    int i = 0;
    if (this.progressBar.getOrientation() == 0) {
      i = getBoxLength(this.componentInnards.width, this.componentInnards.height);
      this.maxPosition = this.componentInnards.x + this.componentInnards.width - i;
    } else {
      i = getBoxLength(this.componentInnards.height, this.componentInnards.width);
      this.maxPosition = this.componentInnards.y + this.componentInnards.height - i;
    } 
    this.delta = 2.0D * this.maxPosition / this.numFrames;
  }
  
  private Rectangle getGenericBox(Rectangle paramRectangle) {
    if (paramRectangle == null)
      paramRectangle = new Rectangle(); 
    if (this.progressBar.getOrientation() == 0) {
      paramRectangle.width = getBoxLength(this.componentInnards.width, this.componentInnards.height);
      if (paramRectangle.width < 0) {
        paramRectangle = null;
      } else {
        paramRectangle.height = this.componentInnards.height;
        paramRectangle.y = this.componentInnards.y;
      } 
    } else {
      paramRectangle.height = getBoxLength(this.componentInnards.height, this.componentInnards.width);
      if (paramRectangle.height < 0) {
        paramRectangle = null;
      } else {
        paramRectangle.width = this.componentInnards.width;
        paramRectangle.x = this.componentInnards.x;
      } 
    } 
    return paramRectangle;
  }
  
  protected int getBoxLength(int paramInt1, int paramInt2) { return (int)Math.round(paramInt1 / 6.0D); }
  
  protected void paintIndeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    if (!(paramGraphics instanceof Graphics2D))
      return; 
    Insets insets = this.progressBar.getInsets();
    int i = this.progressBar.getWidth() - insets.right + insets.left;
    int j = this.progressBar.getHeight() - insets.top + insets.bottom;
    if (i <= 0 || j <= 0)
      return; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    this.boxRect = getBox(this.boxRect);
    if (this.boxRect != null) {
      graphics2D.setColor(this.progressBar.getForeground());
      graphics2D.fillRect(this.boxRect.x, this.boxRect.y, this.boxRect.width, this.boxRect.height);
    } 
    if (this.progressBar.isStringPainted())
      if (this.progressBar.getOrientation() == 0) {
        paintString(graphics2D, insets.left, insets.top, i, j, this.boxRect.x, this.boxRect.width, insets);
      } else {
        paintString(graphics2D, insets.left, insets.top, i, j, this.boxRect.y, this.boxRect.height, insets);
      }  
  }
  
  protected void paintDeterminate(Graphics paramGraphics, JComponent paramJComponent) {
    if (!(paramGraphics instanceof Graphics2D))
      return; 
    Insets insets = this.progressBar.getInsets();
    int i = this.progressBar.getWidth() - insets.right + insets.left;
    int j = this.progressBar.getHeight() - insets.top + insets.bottom;
    if (i <= 0 || j <= 0)
      return; 
    int k = getCellLength();
    int m = getCellSpacing();
    int n = getAmountFull(insets, i, j);
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    graphics2D.setColor(this.progressBar.getForeground());
    if (this.progressBar.getOrientation() == 0) {
      if (m == 0 && n > 0) {
        graphics2D.setStroke(new BasicStroke(j, 0, 2));
      } else {
        graphics2D.setStroke(new BasicStroke(j, 0, 2, 0.0F, new float[] { k, m }, 0.0F));
      } 
      if (BasicGraphicsUtils.isLeftToRight(paramJComponent)) {
        graphics2D.drawLine(insets.left, j / 2 + insets.top, n + insets.left, j / 2 + insets.top);
      } else {
        graphics2D.drawLine(i + insets.left, j / 2 + insets.top, i + insets.left - n, j / 2 + insets.top);
      } 
    } else {
      if (m == 0 && n > 0) {
        graphics2D.setStroke(new BasicStroke(i, 0, 2));
      } else {
        graphics2D.setStroke(new BasicStroke(i, 0, 2, 0.0F, new float[] { k, m }, 0.0F));
      } 
      graphics2D.drawLine(i / 2 + insets.left, insets.top + j, i / 2 + insets.left, insets.top + j - n);
    } 
    if (this.progressBar.isStringPainted())
      paintString(paramGraphics, insets.left, insets.top, i, j, n, insets); 
  }
  
  protected void paintString(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Insets paramInsets) {
    if (this.progressBar.getOrientation() == 0) {
      if (BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
        if (this.progressBar.isIndeterminate()) {
          this.boxRect = getBox(this.boxRect);
          paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.boxRect.x, this.boxRect.width, paramInsets);
        } else {
          paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1, paramInt5, paramInsets);
        } 
      } else {
        paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3 - paramInt5, paramInt5, paramInsets);
      } 
    } else if (this.progressBar.isIndeterminate()) {
      this.boxRect = getBox(this.boxRect);
      paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.boxRect.y, this.boxRect.height, paramInsets);
    } else {
      paintString(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt2 + paramInt4 - paramInt5, paramInt5, paramInsets);
    } 
  }
  
  private void paintString(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Insets paramInsets) {
    if (!(paramGraphics instanceof Graphics2D))
      return; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    String str = this.progressBar.getString();
    graphics2D.setFont(this.progressBar.getFont());
    Point point = getStringPlacement(graphics2D, str, paramInt1, paramInt2, paramInt3, paramInt4);
    Rectangle rectangle = graphics2D.getClipBounds();
    if (this.progressBar.getOrientation() == 0) {
      graphics2D.setColor(getSelectionBackground());
      SwingUtilities2.drawString(this.progressBar, graphics2D, str, point.x, point.y);
      graphics2D.setColor(getSelectionForeground());
      graphics2D.clipRect(paramInt5, paramInt2, paramInt6, paramInt4);
      SwingUtilities2.drawString(this.progressBar, graphics2D, str, point.x, point.y);
    } else {
      graphics2D.setColor(getSelectionBackground());
      AffineTransform affineTransform = AffineTransform.getRotateInstance(1.5707963267948966D);
      graphics2D.setFont(this.progressBar.getFont().deriveFont(affineTransform));
      point = getStringPlacement(graphics2D, str, paramInt1, paramInt2, paramInt3, paramInt4);
      SwingUtilities2.drawString(this.progressBar, graphics2D, str, point.x, point.y);
      graphics2D.setColor(getSelectionForeground());
      graphics2D.clipRect(paramInt1, paramInt5, paramInt3, paramInt6);
      SwingUtilities2.drawString(this.progressBar, graphics2D, str, point.x, point.y);
    } 
    graphics2D.setClip(rectangle);
  }
  
  protected Point getStringPlacement(Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.progressBar, paramGraphics, this.progressBar.getFont());
    int i = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, paramString);
    return (this.progressBar.getOrientation() == 0) ? new Point(paramInt1 + Math.round((paramInt3 / 2 - i / 2)), paramInt2 + (paramInt4 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2) : new Point(paramInt1 + (paramInt3 - fontMetrics.getAscent() + fontMetrics.getLeading() + fontMetrics.getDescent()) / 2, paramInt2 + Math.round((paramInt4 / 2 - i / 2)));
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension;
    Insets insets = this.progressBar.getInsets();
    FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
    if (this.progressBar.getOrientation() == 0) {
      dimension = new Dimension(getPreferredInnerHorizontal());
      if (this.progressBar.isStringPainted()) {
        String str = this.progressBar.getString();
        int i = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, str);
        if (i > dimension.width)
          dimension.width = i; 
        int j = fontMetrics.getHeight() + fontMetrics.getDescent();
        if (j > dimension.height)
          dimension.height = j; 
      } 
    } else {
      dimension = new Dimension(getPreferredInnerVertical());
      if (this.progressBar.isStringPainted()) {
        String str = this.progressBar.getString();
        int i = fontMetrics.getHeight() + fontMetrics.getDescent();
        if (i > dimension.width)
          dimension.width = i; 
        int j = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, str);
        if (j > dimension.height)
          dimension.height = j; 
      } 
    } 
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(this.progressBar);
    if (this.progressBar.getOrientation() == 0) {
      dimension.width = 10;
    } else {
      dimension.height = 10;
    } 
    return dimension;
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    Dimension dimension = getPreferredSize(this.progressBar);
    if (this.progressBar.getOrientation() == 0) {
      dimension.width = 32767;
    } else {
      dimension.height = 32767;
    } 
    return dimension;
  }
  
  protected int getAnimationIndex() { return this.animationIndex; }
  
  protected final int getFrameCount() { return this.numFrames; }
  
  protected void setAnimationIndex(int paramInt) {
    if (this.animationIndex != paramInt) {
      if (sizeChanged()) {
        this.animationIndex = paramInt;
        this.maxPosition = 0;
        this.delta = 0.0D;
        this.progressBar.repaint();
        return;
      } 
      this.nextPaintRect = getBox(this.nextPaintRect);
      this.animationIndex = paramInt;
      if (this.nextPaintRect != null) {
        this.boxRect = getBox(this.boxRect);
        if (this.boxRect != null)
          this.nextPaintRect.add(this.boxRect); 
      } 
    } else {
      return;
    } 
    if (this.nextPaintRect != null) {
      this.progressBar.repaint(this.nextPaintRect);
    } else {
      this.progressBar.repaint();
    } 
  }
  
  private boolean sizeChanged() {
    if (this.oldComponentInnards == null || this.componentInnards == null)
      return true; 
    this.oldComponentInnards.setRect(this.componentInnards);
    this.componentInnards = SwingUtilities.calculateInnerArea(this.progressBar, this.componentInnards);
    return !this.oldComponentInnards.equals(this.componentInnards);
  }
  
  protected void incrementAnimationIndex() {
    int i = getAnimationIndex() + 1;
    if (i < this.numFrames) {
      setAnimationIndex(i);
    } else {
      setAnimationIndex(0);
    } 
  }
  
  private int getRepaintInterval() { return this.repaintInterval; }
  
  private int initRepaintInterval() {
    this.repaintInterval = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.repaintInterval", 50);
    return this.repaintInterval;
  }
  
  private int getCycleTime() { return this.cycleTime; }
  
  private int initCycleTime() {
    this.cycleTime = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.cycleTime", 3000);
    return this.cycleTime;
  }
  
  private void initIndeterminateDefaults() {
    initRepaintInterval();
    initCycleTime();
    if (this.repaintInterval <= 0)
      this.repaintInterval = 100; 
    if (this.repaintInterval > this.cycleTime) {
      this.cycleTime = this.repaintInterval * 20;
    } else {
      int i = (int)Math.ceil(this.cycleTime / this.repaintInterval * 2.0D);
      this.cycleTime = this.repaintInterval * i * 2;
    } 
  }
  
  private void initIndeterminateValues() {
    initIndeterminateDefaults();
    this.numFrames = this.cycleTime / this.repaintInterval;
    initAnimationIndex();
    this.boxRect = new Rectangle();
    this.nextPaintRect = new Rectangle();
    this.componentInnards = new Rectangle();
    this.oldComponentInnards = new Rectangle();
    this.progressBar.addHierarchyListener(getHandler());
    if (this.progressBar.isDisplayable())
      startAnimationTimer(); 
  }
  
  private void cleanUpIndeterminateValues() {
    if (this.progressBar.isDisplayable())
      stopAnimationTimer(); 
    this.cycleTime = this.repaintInterval = 0;
    this.numFrames = this.animationIndex = 0;
    this.maxPosition = 0;
    this.delta = 0.0D;
    this.boxRect = this.nextPaintRect = null;
    this.componentInnards = this.oldComponentInnards = null;
    this.progressBar.removeHierarchyListener(getHandler());
  }
  
  private void initAnimationIndex() {
    if (this.progressBar.getOrientation() == 0 && BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
      setAnimationIndex(0);
    } else {
      setAnimationIndex(this.numFrames / 2);
    } 
  }
  
  private class Animator implements ActionListener {
    private Timer timer;
    
    private long previousDelay;
    
    private int interval;
    
    private long lastCall;
    
    private int MINIMUM_DELAY = 5;
    
    private Animator() {}
    
    private void start(int param1Int) {
      this.previousDelay = param1Int;
      this.lastCall = 0L;
      if (this.timer == null) {
        this.timer = new Timer(param1Int, this);
      } else {
        this.timer.setDelay(param1Int);
      } 
      if (ADJUSTTIMER) {
        this.timer.setRepeats(false);
        this.timer.setCoalesce(false);
      } 
      this.timer.start();
    }
    
    private void stop() { this.timer.stop(); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (ADJUSTTIMER) {
        long l = System.currentTimeMillis();
        if (this.lastCall > 0L) {
          int i = (int)(this.previousDelay - l + this.lastCall + BasicProgressBarUI.this.getRepaintInterval());
          if (i < this.MINIMUM_DELAY)
            i = this.MINIMUM_DELAY; 
          this.timer.setInitialDelay(i);
          this.previousDelay = i;
        } 
        this.timer.start();
        this.lastCall = l;
      } 
      BasicProgressBarUI.this.incrementAnimationIndex();
    }
  }
  
  public class ChangeHandler implements ChangeListener {
    public void stateChanged(ChangeEvent param1ChangeEvent) { BasicProgressBarUI.this.getHandler().stateChanged(param1ChangeEvent); }
  }
  
  private class Handler implements ChangeListener, PropertyChangeListener, HierarchyListener {
    private Handler() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      byte b;
      BoundedRangeModel boundedRangeModel = BasicProgressBarUI.this.progressBar.getModel();
      int i = boundedRangeModel.getMaximum() - boundedRangeModel.getMinimum();
      int j = BasicProgressBarUI.this.getCachedPercent();
      if (i > 0) {
        b = (int)(100L * boundedRangeModel.getValue() / i);
      } else {
        b = 0;
      } 
      if (b != j) {
        BasicProgressBarUI.this.setCachedPercent(b);
        BasicProgressBarUI.this.progressBar.repaint();
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("indeterminate" == str) {
        if (BasicProgressBarUI.this.progressBar.isIndeterminate()) {
          BasicProgressBarUI.this.initIndeterminateValues();
        } else {
          BasicProgressBarUI.this.cleanUpIndeterminateValues();
        } 
        BasicProgressBarUI.this.progressBar.repaint();
      } 
    }
    
    public void hierarchyChanged(HierarchyEvent param1HierarchyEvent) {
      if ((param1HierarchyEvent.getChangeFlags() & 0x2L) != 0L && BasicProgressBarUI.this.progressBar.isIndeterminate())
        if (BasicProgressBarUI.this.progressBar.isDisplayable()) {
          BasicProgressBarUI.this.startAnimationTimer();
        } else {
          BasicProgressBarUI.this.stopAnimationTimer();
        }  
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */