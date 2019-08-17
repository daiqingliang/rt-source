package javax.swing.plaf.synth;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SynthScrollBarUI extends BasicScrollBarUI implements PropertyChangeListener, SynthUI {
  private SynthStyle style;
  
  private SynthStyle thumbStyle;
  
  private SynthStyle trackStyle;
  
  private boolean validMinimumThumbSize;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthScrollBarUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    this.trackHighlight = 0;
    if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof javax.swing.plaf.UIResource)
      this.scrollbar.setLayout(this); 
    configureScrollBarColors();
    updateStyle(this.scrollbar);
  }
  
  protected void configureScrollBarColors() {}
  
  private void updateStyle(JScrollBar paramJScrollBar) {
    SynthStyle synthStyle = this.style;
    SynthContext synthContext = getContext(paramJScrollBar, 1);
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.scrollBarWidth = this.style.getInt(synthContext, "ScrollBar.thumbHeight", 14);
      this.minimumThumbSize = (Dimension)this.style.get(synthContext, "ScrollBar.minimumThumbSize");
      if (this.minimumThumbSize == null) {
        this.minimumThumbSize = new Dimension();
        this.validMinimumThumbSize = false;
      } else {
        this.validMinimumThumbSize = true;
      } 
      this.maximumThumbSize = (Dimension)this.style.get(synthContext, "ScrollBar.maximumThumbSize");
      if (this.maximumThumbSize == null)
        this.maximumThumbSize = new Dimension(4096, 4097); 
      this.incrGap = this.style.getInt(synthContext, "ScrollBar.incrementButtonGap", 0);
      this.decrGap = this.style.getInt(synthContext, "ScrollBar.decrementButtonGap", 0);
      String str = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
      if (str != null)
        if ("large".equals(str)) {
          this.scrollBarWidth = (int)(this.scrollBarWidth * 1.15D);
          this.incrGap = (int)(this.incrGap * 1.15D);
          this.decrGap = (int)(this.decrGap * 1.15D);
        } else if ("small".equals(str)) {
          this.scrollBarWidth = (int)(this.scrollBarWidth * 0.857D);
          this.incrGap = (int)(this.incrGap * 0.857D);
          this.decrGap = (int)(this.decrGap * 0.857D);
        } else if ("mini".equals(str)) {
          this.scrollBarWidth = (int)(this.scrollBarWidth * 0.714D);
          this.incrGap = (int)(this.incrGap * 0.714D);
          this.decrGap = (int)(this.decrGap * 0.714D);
        }  
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
    synthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_TRACK, 1);
    this.trackStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
    synthContext = getContext(paramJScrollBar, Region.SCROLL_BAR_THUMB, 1);
    this.thumbStyle = SynthLookAndFeel.updateStyle(synthContext, this);
    synthContext.dispose();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.scrollbar.addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.scrollbar.removePropertyChangeListener(this);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this.scrollbar, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    synthContext = getContext(this.scrollbar, Region.SCROLL_BAR_TRACK, 1);
    this.trackStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.trackStyle = null;
    synthContext = getContext(this.scrollbar, Region.SCROLL_BAR_THUMB, 1);
    this.thumbStyle.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.thumbStyle = null;
    super.uninstallDefaults();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion) { return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion)); }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt) {
    SynthStyle synthStyle = this.trackStyle;
    if (paramRegion == Region.SCROLL_BAR_THUMB)
      synthStyle = this.thumbStyle; 
    return SynthContext.getContext(paramJComponent, paramRegion, synthStyle, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion) { return (paramRegion == Region.SCROLL_BAR_THUMB && isThumbRollover() && paramJComponent.isEnabled()) ? 2 : SynthLookAndFeel.getComponentState(paramJComponent); }
  
  public boolean getSupportsAbsolutePositioning() {
    SynthContext synthContext = getContext(this.scrollbar);
    boolean bool = this.style.getBoolean(synthContext, "ScrollBar.allowsAbsolutePositioning", false);
    synthContext.dispose();
    return bool;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintScrollBarBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), this.scrollbar.getOrientation());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    SynthContext synthContext = getContext(this.scrollbar, Region.SCROLL_BAR_TRACK);
    paintTrack(synthContext, paramGraphics, getTrackBounds());
    synthContext.dispose();
    synthContext = getContext(this.scrollbar, Region.SCROLL_BAR_THUMB);
    paintThumb(synthContext, paramGraphics, getThumbBounds());
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintScrollBarBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, this.scrollbar.getOrientation()); }
  
  protected void paintTrack(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    paramSynthContext.getPainter().paintScrollBarTrackBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.scrollbar.getOrientation());
    paramSynthContext.getPainter().paintScrollBarTrackBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, this.scrollbar.getOrientation());
  }
  
  protected void paintThumb(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle) {
    SynthLookAndFeel.updateSubregion(paramSynthContext, paramGraphics, paramRectangle);
    int i = this.scrollbar.getOrientation();
    paramSynthContext.getPainter().paintScrollBarThumbBackground(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
    paramSynthContext.getPainter().paintScrollBarThumbBorder(paramSynthContext, paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, i);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Insets insets = paramJComponent.getInsets();
    return (this.scrollbar.getOrientation() == 1) ? new Dimension(this.scrollBarWidth + insets.left + insets.right, 48) : new Dimension(48, this.scrollBarWidth + insets.top + insets.bottom);
  }
  
  protected Dimension getMinimumThumbSize() {
    if (!this.validMinimumThumbSize)
      if (this.scrollbar.getOrientation() == 1) {
        this.minimumThumbSize.width = this.scrollBarWidth;
        this.minimumThumbSize.height = 7;
      } else {
        this.minimumThumbSize.width = 7;
        this.minimumThumbSize.height = this.scrollBarWidth;
      }  
    return this.minimumThumbSize;
  }
  
  protected JButton createDecreaseButton(int paramInt) {
    SynthArrowButton synthArrowButton = new SynthArrowButton(paramInt) {
        public boolean contains(int param1Int1, int param1Int2) {
          if (SynthScrollBarUI.this.decrGap < 0) {
            int i = getWidth();
            int j = getHeight();
            if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
              j += SynthScrollBarUI.this.decrGap;
            } else {
              i += SynthScrollBarUI.this.decrGap;
            } 
            return (param1Int1 >= 0 && param1Int1 < i && param1Int2 >= 0 && param1Int2 < j);
          } 
          return super.contains(param1Int1, param1Int2);
        }
      };
    synthArrowButton.setName("ScrollBar.button");
    return synthArrowButton;
  }
  
  protected JButton createIncreaseButton(int paramInt) {
    SynthArrowButton synthArrowButton = new SynthArrowButton(paramInt) {
        public boolean contains(int param1Int1, int param1Int2) {
          if (SynthScrollBarUI.this.incrGap < 0) {
            int i = getWidth();
            int j = getHeight();
            if (SynthScrollBarUI.this.scrollbar.getOrientation() == 1) {
              j += SynthScrollBarUI.this.incrGap;
              param1Int2 += SynthScrollBarUI.this.incrGap;
            } else {
              i += SynthScrollBarUI.this.incrGap;
              param1Int1 += SynthScrollBarUI.this.incrGap;
            } 
            return (param1Int1 >= 0 && param1Int1 < i && param1Int2 >= 0 && param1Int2 < j);
          } 
          return super.contains(param1Int1, param1Int2);
        }
      };
    synthArrowButton.setName("ScrollBar.button");
    return synthArrowButton;
  }
  
  protected void setThumbRollover(boolean paramBoolean) {
    if (isThumbRollover() != paramBoolean) {
      this.scrollbar.repaint(getThumbBounds());
      super.setThumbRollover(paramBoolean);
    } 
  }
  
  private void updateButtonDirections() {
    int i = this.scrollbar.getOrientation();
    if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
      ((SynthArrowButton)this.incrButton).setDirection((i == 0) ? 3 : 5);
      ((SynthArrowButton)this.decrButton).setDirection((i == 0) ? 7 : 1);
    } else {
      ((SynthArrowButton)this.incrButton).setDirection((i == 0) ? 7 : 5);
      ((SynthArrowButton)this.decrButton).setDirection((i == 0) ? 3 : 1);
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JScrollBar)paramPropertyChangeEvent.getSource()); 
    if ("orientation" == str) {
      updateButtonDirections();
    } else if ("componentOrientation" == str) {
      updateButtonDirections();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthScrollBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */