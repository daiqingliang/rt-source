package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Copies extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = -6426631521680023833L;
  
  public Copies(int paramInt) { super(paramInt, 1, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof Copies); }
  
  public final Class<? extends Attribute> getCategory() { return Copies.class; }
  
  public final String getName() { return "copies"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\Copies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */