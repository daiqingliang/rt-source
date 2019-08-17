package java.time.chrono;

import java.time.DateTimeException;

public static enum ThaiBuddhistEra implements Era {
  BEFORE_BE, BE;
  
  public static ThaiBuddhistEra of(int paramInt) {
    switch (paramInt) {
      case 0:
        return BEFORE_BE;
      case 1:
        return BE;
    } 
    throw new DateTimeException("Invalid era: " + paramInt);
  }
  
  public int getValue() { return ordinal(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\chrono\ThaiBuddhistEra.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */