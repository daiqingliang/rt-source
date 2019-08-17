package java.awt;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollPanePeer;
import java.io.Serializable;
import sun.awt.AWTAccessor;

public class ScrollPaneAdjustable implements Adjustable, Serializable {
  private ScrollPane sp;
  
  private int orientation;
  
  private int value;
  
  private int minimum;
  
  private int maximum;
  
  private int visibleAmount;
  
  private boolean isAdjusting;
  
  private int unitIncrement = 1;
  
  private int blockIncrement = 1;
  
  private AdjustmentListener adjustmentListener;
  
  private static final String SCROLLPANE_ONLY = "Can be set by scrollpane only";
  
  private static final long serialVersionUID = -3359745691033257079L;
  
  private static native void initIDs();
  
  ScrollPaneAdjustable(ScrollPane paramScrollPane, AdjustmentListener paramAdjustmentListener, int paramInt) {
    this.sp = paramScrollPane;
    this.orientation = paramInt;
    addAdjustmentListener(paramAdjustmentListener);
  }
  
  void setSpan(int paramInt1, int paramInt2, int paramInt3) {
    this.minimum = paramInt1;
    this.maximum = Math.max(paramInt2, this.minimum + 1);
    this.visibleAmount = Math.min(paramInt3, this.maximum - this.minimum);
    this.visibleAmount = Math.max(this.visibleAmount, 1);
    this.blockIncrement = Math.max((int)(paramInt3 * 0.9D), 1);
    setValue(this.value);
  }
  
  public int getOrientation() { return this.orientation; }
  
  public void setMinimum(int paramInt) { throw new AWTError("Can be set by scrollpane only"); }
  
  public int getMinimum() { return 0; }
  
  public void setMaximum(int paramInt) { throw new AWTError("Can be set by scrollpane only"); }
  
  public int getMaximum() { return this.maximum; }
  
  public void setUnitIncrement(int paramInt) {
    if (paramInt != this.unitIncrement) {
      this.unitIncrement = paramInt;
      if (this.sp.peer != null) {
        ScrollPanePeer scrollPanePeer = (ScrollPanePeer)this.sp.peer;
        scrollPanePeer.setUnitIncrement(this, paramInt);
      } 
    } 
  }
  
  public int getUnitIncrement() { return this.unitIncrement; }
  
  public void setBlockIncrement(int paramInt) { this.blockIncrement = paramInt; }
  
  public int getBlockIncrement() { return this.blockIncrement; }
  
  public void setVisibleAmount(int paramInt) { throw new AWTError("Can be set by scrollpane only"); }
  
  public int getVisibleAmount() { return this.visibleAmount; }
  
  public void setValueIsAdjusting(boolean paramBoolean) {
    if (this.isAdjusting != paramBoolean) {
      this.isAdjusting = paramBoolean;
      AdjustmentEvent adjustmentEvent = new AdjustmentEvent(this, 601, 5, this.value, paramBoolean);
      this.adjustmentListener.adjustmentValueChanged(adjustmentEvent);
    } 
  }
  
  public boolean getValueIsAdjusting() { return this.isAdjusting; }
  
  public void setValue(int paramInt) { setTypedValue(paramInt, 5); }
  
  private void setTypedValue(int paramInt1, int paramInt2) {
    paramInt1 = Math.max(paramInt1, this.minimum);
    paramInt1 = Math.min(paramInt1, this.maximum - this.visibleAmount);
    if (paramInt1 != this.value) {
      this.value = paramInt1;
      AdjustmentEvent adjustmentEvent = new AdjustmentEvent(this, 601, paramInt2, this.value, this.isAdjusting);
      this.adjustmentListener.adjustmentValueChanged(adjustmentEvent);
    } 
  }
  
  public int getValue() { return this.value; }
  
  public void addAdjustmentListener(AdjustmentListener paramAdjustmentListener) {
    if (paramAdjustmentListener == null)
      return; 
    this.adjustmentListener = AWTEventMulticaster.add(this.adjustmentListener, paramAdjustmentListener);
  }
  
  public void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener) {
    if (paramAdjustmentListener == null)
      return; 
    this.adjustmentListener = AWTEventMulticaster.remove(this.adjustmentListener, paramAdjustmentListener);
  }
  
  public AdjustmentListener[] getAdjustmentListeners() { return (AdjustmentListener[])AWTEventMulticaster.getListeners(this.adjustmentListener, AdjustmentListener.class); }
  
  public String toString() { return getClass().getName() + "[" + paramString() + "]"; }
  
  public String paramString() { return ((this.orientation == 1) ? "vertical," : "horizontal,") + "[0.." + this.maximum + "],val=" + this.value + ",vis=" + this.visibleAmount + ",unit=" + this.unitIncrement + ",block=" + this.blockIncrement + ",isAdjusting=" + this.isAdjusting; }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setScrollPaneAdjustableAccessor(new AWTAccessor.ScrollPaneAdjustableAccessor() {
          public void setTypedValue(ScrollPaneAdjustable param1ScrollPaneAdjustable, int param1Int1, int param1Int2) { param1ScrollPaneAdjustable.setTypedValue(param1Int1, param1Int2); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\ScrollPaneAdjustable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */