package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class PageRanges extends SetOfIntegerSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 8639895197656148392L;
  
  public PageRanges(int[][] paramArrayOfInt) {
    super(paramArrayOfInt);
    if (paramArrayOfInt == null)
      throw new NullPointerException("members is null"); 
    myPageRanges();
  }
  
  public PageRanges(String paramString) {
    super(paramString);
    if (paramString == null)
      throw new NullPointerException("members is null"); 
    myPageRanges();
  }
  
  private void myPageRanges() {
    int[][] arrayOfInt = getMembers();
    int i = arrayOfInt.length;
    if (i == 0)
      throw new IllegalArgumentException("members is zero-length"); 
    for (byte b = 0; b < i; b++) {
      if (arrayOfInt[b][0] < 1)
        throw new IllegalArgumentException("Page value < 1 specified"); 
    } 
  }
  
  public PageRanges(int paramInt) {
    super(paramInt);
    if (paramInt < 1)
      throw new IllegalArgumentException("Page value < 1 specified"); 
  }
  
  public PageRanges(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("Null range specified"); 
    if (paramInt1 < 1)
      throw new IllegalArgumentException("Page value < 1 specified"); 
  }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PageRanges); }
  
  public final Class<? extends Attribute> getCategory() { return PageRanges.class; }
  
  public final String getName() { return "page-ranges"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PageRanges.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */