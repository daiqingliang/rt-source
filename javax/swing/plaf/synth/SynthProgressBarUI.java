package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;
import sun.swing.SwingUtilities2;

public class SynthProgressBarUI extends BasicProgressBarUI implements SynthUI, PropertyChangeListener {
  private SynthStyle style;
  
  private int progressPadding;
  
  private boolean rotateText;
  
  private boolean paintOutsideClip;
  
  private boolean tileWhenIndeterminate;
  
  private int tileWidth;
  
  private Dimension minBarSize;
  
  private int glowWidth;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthProgressBarUI(); }
  
  protected void installListeners() {
    super.installListeners();
    this.progressBar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.progressBar.removePropertyChangeListener(this);
  }
  
  protected void installDefaults() { updateStyle(this.progressBar); }
  
  private void updateStyle(JProgressBar paramJProgressBar) {
    SynthContext synthContext = getContext(paramJProgressBar, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    setCellLength(this.style.getInt(synthContext, "ProgressBar.cellLength", 1));
    setCellSpacing(this.style.getInt(synthContext, "ProgressBar.cellSpacing", 0));
    this.progressPadding = this.style.getInt(synthContext, "ProgressBar.progressPadding", 0);
    this.paintOutsideClip = this.style.getBoolean(synthContext, "ProgressBar.paintOutsideClip", false);
    this.rotateText = this.style.getBoolean(synthContext, "ProgressBar.rotateText", false);
    this.tileWhenIndeterminate = this.style.getBoolean(synthContext, "ProgressBar.tileWhenIndeterminate", false);
    this.tileWidth = this.style.getInt(synthContext, "ProgressBar.tileWidth", 15);
    String str = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
    if (str != null)
      if ("large".equals(str)) {
        this.tileWidth = (int)(this.tileWidth * 1.15D);
      } else if ("small".equals(str)) {
        this.tileWidth = (int)(this.tileWidth * 0.857D);
      } else if ("mini".equals(str)) {
        this.tileWidth = (int)(this.tileWidth * 0.784D);
      }  
    this.minBarSize = (Dimension)this.style.get(synthContext, "ProgressBar.minBarSize");
    this.glowWidth = this.style.getInt(synthContext, "ProgressBar.glowWidth", 0);
    synthContext.dispose();
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.progressBar, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private int getComponentState(JComponent paramJComponent) { return SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2) {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
      SynthContext synthContext = getContext(paramJComponent);
      Font font = synthContext.getStyle().getFont(synthContext);
      FontMetrics fontMetrics = this.progressBar.getFontMetrics(font);
      synthContext.dispose();
      return (paramInt2 - fontMetrics.getAscent() - fontMetrics.getDescent()) / 2 + fontMetrics.getAscent();
    } 
    return -1;
  }
  
  protected Rectangle getBox(Rectangle paramRectangle) { return this.tileWhenIndeterminate ? SwingUtilities.calculateInnerArea(this.progressBar, paramRectangle) : super.getBox(paramRectangle); }
  
  protected void setAnimationIndex(int paramInt) {
    if (this.paintOutsideClip) {
      if (getAnimationIndex() == paramInt)
        return; 
      super.setAnimationIndex(paramInt);
      this.progressBar.repaint();
    } else {
      super.setAnimationIndex(paramInt);
    } 
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintProgressBarBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.progressBar.getOrientation());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    JProgressBar jProgressBar = (JProgressBar)paramSynthContext.getComponent();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (!jProgressBar.isIndeterminate()) {
      Insets insets = jProgressBar.getInsets();
      double d = jProgressBar.getPercentComplete();
      if (d != 0.0D)
        if (jProgressBar.getOrientation() == 0) {
          i = insets.left + this.progressPadding;
          j = insets.top + this.progressPadding;
          k = (int)(d * (jProgressBar.getWidth() - insets.left + this.progressPadding + insets.right + this.progressPadding));
          m = jProgressBar.getHeight() - insets.top + this.progressPadding + insets.bottom + this.progressPadding;
          if (!SynthLookAndFeel.isLeftToRight(jProgressBar))
            i = jProgressBar.getWidth() - insets.right - k - this.progressPadding - this.glowWidth; 
        } else {
          i = insets.left + this.progressPadding;
          k = jProgressBar.getWidth() - insets.left + this.progressPadding + insets.right + this.progressPadding;
          m = (int)(d * (jProgressBar.getHeight() - insets.top + this.progressPadding + insets.bottom + this.progressPadding));
          j = jProgressBar.getHeight() - insets.bottom - m - this.progressPadding;
          if (SynthLookAndFeel.isLeftToRight(jProgressBar))
            j -= this.glowWidth; 
        }  
    } else {
      this.boxRect = getBox(this.boxRect);
      i = this.boxRect.x + this.progressPadding;
      j = this.boxRect.y + this.progressPadding;
      k = this.boxRect.width - this.progressPadding - this.progressPadding;
      m = this.boxRect.height - this.progressPadding - this.progressPadding;
    } 
    if (this.tileWhenIndeterminate && jProgressBar.isIndeterminate()) {
      double d = getAnimationIndex() / getFrameCount();
      int n = (int)(d * this.tileWidth);
      Shape shape = paramGraphics.getClip();
      paramGraphics.clipRect(i, j, k, m);
      if (jProgressBar.getOrientation() == 0) {
        int i1;
        for (i1 = i - this.tileWidth + n; i1 <= k; i1 += this.tileWidth)
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i1, j, this.tileWidth, m, jProgressBar.getOrientation()); 
      } else {
        int i1;
        for (i1 = j - n; i1 < m + this.tileWidth; i1 += this.tileWidth)
          paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, i1, k, this.tileWidth, jProgressBar.getOrientation()); 
      } 
      paramGraphics.setClip(shape);
    } else if (this.minBarSize == null || (k >= this.minBarSize.width && m >= this.minBarSize.height)) {
      paramSynthContext.getPainter().paintProgressBarForeground(paramSynthContext, paramGraphics, i, j, k, m, jProgressBar.getOrientation());
    } 
    if (jProgressBar.isStringPainted())
      paintText(paramSynthContext, paramGraphics, jProgressBar.getString()); 
  }
  
  protected void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString) {
    if (this.progressBar.isStringPainted()) {
      SynthStyle synthStyle = paramSynthContext.getStyle();
      Font font = synthStyle.getFont(paramSynthContext);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.progressBar, paramGraphics, font);
      int i = synthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, font, fontMetrics, paramString);
      Rectangle rectangle = this.progressBar.getBounds();
      if (this.rotateText && this.progressBar.getOrientation() == 1) {
        AffineTransform affineTransform;
        Point point;
        Graphics2D graphics2D = (Graphics2D)paramGraphics;
        if (this.progressBar.getComponentOrientation().isLeftToRight()) {
          affineTransform = AffineTransform.getRotateInstance(-1.5707963267948966D);
          point = new Point((rectangle.width + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2, (rectangle.height + i) / 2);
        } else {
          affineTransform = AffineTransform.getRotateInstance(1.5707963267948966D);
          point = new Point((rectangle.width - fontMetrics.getAscent() + fontMetrics.getDescent()) / 2, (rectangle.height - i) / 2);
        } 
        if (point.x < 0)
          return; 
        font = font.deriveFont(affineTransform);
        graphics2D.setFont(font);
        graphics2D.setColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        synthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, point.x, point.y, -1);
      } else {
        Rectangle rectangle1 = new Rectangle(rectangle.width / 2 - i / 2, (rectangle.height - fontMetrics.getAscent() + fontMetrics.getDescent()) / 2, 0, 0);
        if (rectangle1.y < 0)
          return; 
        paramGraphics.setColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
        paramGraphics.setFont(font);
        synthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, paramString, rectangle1.x, rectangle1.y, -1);
      } 
    } 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintProgressBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.progressBar.getOrientation()); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent) || "indeterminate".equals(paramPropertyChangeEvent.getPropertyName()))
      updateStyle((JProgressBar)paramPropertyChangeEvent.getSource()); 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = null;
    Insets insets = this.progressBar.getInsets();
    FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
    String str1 = this.progressBar.getString();
    int i = fontMetrics.getHeight() + fontMetrics.getDescent();
    if (this.progressBar.getOrientation() == 0) {
      dimension = new Dimension(getPreferredInnerHorizontal());
      if (this.progressBar.isStringPainted()) {
        if (i > dimension.height)
          dimension.height = i; 
        int j = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, str1);
        if (j > dimension.width)
          dimension.width = j; 
      } 
    } else {
      dimension = new Dimension(getPreferredInnerVertical());
      if (this.progressBar.isStringPainted()) {
        if (i > dimension.width)
          dimension.width = i; 
        int j = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, str1);
        if (j > dimension.height)
          dimension.height = j; 
      } 
    } 
    String str2 = (String)this.progressBar.getClientProperty("JComponent.sizeVariant");
    if (str2 != null)
      if ("large".equals(str2)) {
        dimension.width = (int)(dimension.width * 1.15F);
        dimension.height = (int)(dimension.height * 1.15F);
      } else if ("small".equals(str2)) {
        dimension.width = (int)(dimension.width * 0.9F);
        dimension.height = (int)(dimension.height * 0.9F);
      } else if ("mini".equals(str2)) {
        dimension.width = (int)(dimension.width * 0.784F);
        dimension.height = (int)(dimension.height * 0.784F);
      }  
    dimension.width += insets.left + insets.right;
    dimension.height += insets.top + insets.bottom;
    return dimension;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthProgressBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */