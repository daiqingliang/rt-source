package sun.management.counter;

import java.io.Serializable;

public class Variability implements Serializable {
  private static final int NATTRIBUTES = 4;
  
  private static Variability[] map = new Variability[4];
  
  private String name;
  
  private int value;
  
  public static final Variability INVALID = new Variability("Invalid", 0);
  
  public static final Variability CONSTANT = new Variability("Constant", 1);
  
  public static final Variability MONOTONIC = new Variability("Monotonic", 2);
  
  public static final Variability VARIABLE = new Variability("Variable", 3);
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  public String toString() { return this.name; }
  
  public int intValue() { return this.value; }
  
  public static Variability toVariability(int paramInt) { return (paramInt < 0 || paramInt >= map.length || map[paramInt] == null) ? INVALID : map[paramInt]; }
  
  private Variability(String paramString, int paramInt) {
    this.name = paramString;
    this.value = paramInt;
    map[paramInt] = this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\counter\Variability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */