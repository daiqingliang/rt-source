package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.ResolutionSyntax;

public final class PrinterResolution extends ResolutionSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 13090306561090558L;
  
  public PrinterResolution(int paramInt1, int paramInt2, int paramInt3) { super(paramInt1, paramInt2, paramInt3); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PrinterResolution); }
  
  public final Class<? extends Attribute> getCategory() { return PrinterResolution.class; }
  
  public final String getName() { return "printer-resolution"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterResolution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */