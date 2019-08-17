package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class JobKOctets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = -8959710146498202869L;
  
  public JobKOctets(int paramInt) { super(paramInt, 0, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof JobKOctets); }
  
  public final Class<? extends Attribute> getCategory() { return JobKOctets.class; }
  
  public final String getName() { return "job-k-octets"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\JobKOctets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */