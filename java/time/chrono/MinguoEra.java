package java.time.chrono;

import java.time.DateTimeException;

public static enum MinguoEra implements Era {
  BEFORE_ROC, ROC;
  
  public static MinguoEra of(int paramInt) {
    switch (paramInt) {
      case 0:
        return BEFORE_ROC;
      case 1:
        return ROC;
    } 
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue() { return ordinal(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\MinguoEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */