package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class PagesPerMinute extends IntegerSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = -6366403993072862015L;
  
  public PagesPerMinute(int paramInt) { super(paramInt, 0, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PagesPerMinute); }
  
  public final Class<? extends Attribute> getCategory() { return PagesPerMinute.class; }
  
  public final String getName() { return "pages-per-minute"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PagesPerMinute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */