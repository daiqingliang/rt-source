package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

class OpenBook implements Pageable {
  private PageFormat mFormat;
  
  private Printable mPainter;
  
  OpenBook(PageFormat paramPageFormat, Printable paramPrintable) {
    this.mFormat = paramPageFormat;
    this.mPainter = paramPrintable;
  }
  
  public int getNumberOfPages() { return -1; }
  
  public PageFormat getPageFormat(int paramInt) { return this.mFormat; }
  
  public Printable getPrintable(int paramInt) throws IndexOutOfBoundsException { return this.mPainter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\OpenBook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */