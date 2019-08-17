package java.time.chrono;

import java.time.DateTimeException;

public static enum IsoEra implements Era {
  BCE, CE;
  
  public static IsoEra of(int paramInt) {
    switch (paramInt) {
      case 0:
        return BCE;
      case 1:
        return CE;
    } 
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue() { return ordinal(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\IsoEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */