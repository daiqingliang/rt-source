package java.awt.print;

public interface Pageable {
  public static final int UNKNOWN_NUMBER_OF_PAGES = -1;
  
  int getNumberOfPages();
  
  PageFormat getPageFormat(int paramInt) throws IndexOutOfBoundsException;
  
  Printable getPrintable(int paramInt) throws IndexOutOfBoundsException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\Pageable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */