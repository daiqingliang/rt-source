package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class NumberOfDocuments extends IntegerSyntax implements PrintJobAttribute {
  private static final long serialVersionUID = 7891881310684461097L;
  
  public NumberOfDocuments(int paramInt) { super(paramInt, 0, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof NumberOfDocuments); }
  
  public final Class<? extends Attribute> getCategory() { return NumberOfDocuments.class; }
  
  public final String getName() { return "number-of-documents"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\NumberOfDocuments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */