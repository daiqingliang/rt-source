package sun.print;

import javax.print.attribute.PrintRequestAttribute;

public final class SunPageSelection implements PrintRequestAttribute {
  public static final SunPageSelection ALL = new SunPageSelection(0);
  
  public static final SunPageSelection RANGE = new SunPageSelection(1);
  
  public static final SunPageSelection SELECTION = new SunPageSelection(2);
  
  private int pages;
  
  public SunPageSelection(int paramInt) { this.pages = paramInt; }
  
  public final Class getCategory() { return SunPageSelection.class; }
  
  public final String getName() { return "sun-page-selection"; }
  
  public String toString() { return "page-selection: " + this.pages; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\SunPageSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */