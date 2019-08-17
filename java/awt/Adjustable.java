package java.awt;

import java.awt.event.AdjustmentListener;

public interface Adjustable {
  public static final int HORIZONTAL = 0;
  
  public static final int VERTICAL = 1;
  
  public static final int NO_ORIENTATION = 2;
  
  int getOrientation();
  
  void setMinimum(int paramInt);
  
  int getMinimum();
  
  void setMaximum(int paramInt);
  
  int getMaximum();
  
  void setUnitIncrement(int paramInt);
  
  int getUnitIncrement();
  
  void setBlockIncrement(int paramInt);
  
  int getBlockIncrement();
  
  void setVisibleAmount(int paramInt);
  
  int getVisibleAmount();
  
  void setValue(int paramInt);
  
  int getValue();
  
  void addAdjustmentListener(AdjustmentListener paramAdjustmentListener);
  
  void removeAdjustmentListener(AdjustmentListener paramAdjustmentListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Adjustable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */