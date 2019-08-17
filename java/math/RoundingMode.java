package java.math;

public static enum RoundingMode {
  UP(0),
  DOWN(1),
  CEILING(2),
  FLOOR(3),
  HALF_UP(4),
  HALF_DOWN(5),
  HALF_EVEN(6),
  UNNECESSARY(7);
  
  final int oldMode;
  
  RoundingMode(int paramInt1) { this.oldMode = paramInt1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\math\RoundingMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */