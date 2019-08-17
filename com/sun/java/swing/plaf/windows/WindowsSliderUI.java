package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class WindowsSliderUI extends BasicSliderUI {
  private boolean rollover = false;
  
  private boolean pressed = false;
  
  public WindowsSliderUI(JSlider paramJSlider) { super(paramJSlider); }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsSliderUI((JSlider)paramJComponent); }
  
  protected BasicSliderUI.TrackListener createTrackListener(JSlider paramJSlider) { return new WindowsTrackListener(null); }
  
  public void paintTrack(Graphics paramGraphics) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      boolean bool = (this.slider.getOrientation() == 1) ? 1 : 0;
      TMSchema.Part part = bool ? TMSchema.Part.TKP_TRACKVERT : TMSchema.Part.TKP_TRACK;
      XPStyle.Skin skin = xPStyle.getSkin(this.slider, part);
      if (bool) {
        int i = (this.trackRect.width - skin.getWidth()) / 2;
        skin.paintSkin(paramGraphics, this.trackRect.x + i, this.trackRect.y, skin.getWidth(), this.trackRect.height, null);
      } else {
        int i = (this.trackRect.height - skin.getHeight()) / 2;
        skin.paintSkin(paramGraphics, this.trackRect.x, this.trackRect.y + i, this.trackRect.width, skin.getHeight(), null);
      } 
    } else {
      super.paintTrack(paramGraphics);
    } 
  }
  
  protected void paintMinorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      paramGraphics.setColor(xPStyle.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black)); 
    super.paintMinorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForHorizSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      paramGraphics.setColor(xPStyle.getColor(this.slider, TMSchema.Part.TKP_TICS, null, TMSchema.Prop.COLOR, Color.black)); 
    super.paintMajorTickForHorizSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMinorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      paramGraphics.setColor(xPStyle.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black)); 
    super.paintMinorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  protected void paintMajorTickForVertSlider(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null)
      paramGraphics.setColor(xPStyle.getColor(this.slider, TMSchema.Part.TKP_TICSVERT, null, TMSchema.Prop.COLOR, Color.black)); 
    super.paintMajorTickForVertSlider(paramGraphics, paramRectangle, paramInt);
  }
  
  public void paintThumb(Graphics paramGraphics) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      TMSchema.Part part = getXPThumbPart();
      TMSchema.State state = TMSchema.State.NORMAL;
      if (this.slider.hasFocus())
        state = TMSchema.State.FOCUSED; 
      if (this.rollover)
        state = TMSchema.State.HOT; 
      if (this.pressed)
        state = TMSchema.State.PRESSED; 
      if (!this.slider.isEnabled())
        state = TMSchema.State.DISABLED; 
      xPStyle.getSkin(this.slider, part).paintSkin(paramGraphics, this.thumbRect.x, this.thumbRect.y, state);
    } else {
      super.paintThumb(paramGraphics);
    } 
  }
  
  protected Dimension getThumbSize() {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      Dimension dimension = new Dimension();
      XPStyle.Skin skin = xPStyle.getSkin(this.slider, getXPThumbPart());
      dimension.width = skin.getWidth();
      dimension.height = skin.getHeight();
      return dimension;
    } 
    return super.getThumbSize();
  }
  
  private TMSchema.Part getXPThumbPart() {
    TMSchema.Part part;
    boolean bool = (this.slider.getOrientation() == 1) ? 1 : 0;
    boolean bool1 = this.slider.getComponentOrientation().isLeftToRight();
    Boolean bool2 = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
    if ((!this.slider.getPaintTicks() && bool2 == null) || bool2 == Boolean.FALSE) {
      part = bool ? TMSchema.Part.TKP_THUMBVERT : TMSchema.Part.TKP_THUMB;
    } else {
      part = bool ? (bool1 ? TMSchema.Part.TKP_THUMBRIGHT : TMSchema.Part.TKP_THUMBLEFT) : TMSchema.Part.TKP_THUMBBOTTOM;
    } 
    return part;
  }
  
  private class WindowsTrackListener extends BasicSliderUI.TrackListener {
    private WindowsTrackListener() { super(WindowsSliderUI.this); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      updateRollover(WindowsSliderUI.this.thumbRect.contains(param1MouseEvent.getX(), param1MouseEvent.getY()));
      super.mouseMoved(param1MouseEvent);
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      updateRollover(WindowsSliderUI.this.thumbRect.contains(param1MouseEvent.getX(), param1MouseEvent.getY()));
      super.mouseEntered(param1MouseEvent);
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      updateRollover(false);
      super.mouseExited(param1MouseEvent);
    }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      updatePressed(WindowsSliderUI.this.thumbRect.contains(param1MouseEvent.getX(), param1MouseEvent.getY()));
      super.mousePressed(param1MouseEvent);
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      updatePressed(false);
      super.mouseReleased(param1MouseEvent);
    }
    
    public void updatePressed(boolean param1Boolean) {
      if (!WindowsSliderUI.this.slider.isEnabled())
        return; 
      if (WindowsSliderUI.this.pressed != param1Boolean) {
        WindowsSliderUI.this.pressed = param1Boolean;
        WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
      } 
    }
    
    public void updateRollover(boolean param1Boolean) {
      if (!WindowsSliderUI.this.slider.isEnabled())
        return; 
      if (WindowsSliderUI.this.rollover != param1Boolean) {
        WindowsSliderUI.this.rollover = param1Boolean;
        WindowsSliderUI.this.slider.repaint(WindowsSliderUI.this.thumbRect);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSliderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */