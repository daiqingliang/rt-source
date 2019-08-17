package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMakeAndModel extends TextSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = 4580461489499351411L;
  
  public PrinterMakeAndModel(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PrinterMakeAndModel); }
  
  public final Class<? extends Attribute> getCategory() { return PrinterMakeAndModel.class; }
  
  public final String getName() { return "printer-make-and-model"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterMakeAndModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */