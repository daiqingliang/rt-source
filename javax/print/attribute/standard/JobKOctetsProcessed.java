package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class JobKOctetsProcessed extends IntegerSyntax implements PrintJobAttribute {
  private static final long serialVersionUID = -6265238509657881806L;
  
  public JobKOctetsProcessed(int paramInt) { super(paramInt, 0, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof JobKOctetsProcessed); }
  
  public final Class<? extends Attribute> getCategory() { return JobKOctetsProcessed.class; }
  
  public final String getName() { return "job-k-octets-processed"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\JobKOctetsProcessed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */