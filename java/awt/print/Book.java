package java.awt.print;

import java.util.Vector;

public class Book implements Pageable {
  private Vector mPages = new Vector();
  
  public int getNumberOfPages() { return this.mPages.size(); }
  
  public PageFormat getPageFormat(int paramInt) throws IndexOutOfBoundsException { return getPage(paramInt).getPageFormat(); }
  
  public Printable getPrintable(int paramInt) throws IndexOutOfBoundsException { return getPage(paramInt).getPrintable(); }
  
  public void setPage(int paramInt, Printable paramPrintable, PageFormat paramPageFormat) throws IndexOutOfBoundsException {
    if (paramPrintable == null)
      throw new NullPointerException("painter is null"); 
    if (paramPageFormat == null)
      throw new NullPointerException("page is null"); 
    this.mPages.setElementAt(new BookPage(paramPrintable, paramPageFormat), paramInt);
  }
  
  public void append(Printable paramPrintable, PageFormat paramPageFormat) { this.mPages.addElement(new BookPage(paramPrintable, paramPageFormat)); }
  
  public void append(Printable paramPrintable, PageFormat paramPageFormat, int paramInt) {
    BookPage bookPage = new BookPage(paramPrintable, paramPageFormat);
    int i = this.mPages.size();
    int j = i + paramInt;
    this.mPages.setSize(j);
    for (int k = i; k < j; k++)
      this.mPages.setElementAt(bookPage, k); 
  }
  
  private BookPage getPage(int paramInt) throws ArrayIndexOutOfBoundsException { return (BookPage)this.mPages.elementAt(paramInt); }
  
  private class BookPage {
    private PageFormat mFormat;
    
    private Printable mPainter;
    
    BookPage(Printable param1Printable, PageFormat param1PageFormat) {
      if (param1Printable == null || param1PageFormat == null)
        throw new NullPointerException(); 
      this.mFormat = param1PageFormat;
      this.mPainter = param1Printable;
    }
    
    Printable getPrintable() { return this.mPainter; }
    
    PageFormat getPageFormat() { return this.mFormat; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\Book.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */