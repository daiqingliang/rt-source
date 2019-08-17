package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterLocation extends TextSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = -1598610039865566337L;
  
  public PrinterLocation(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PrinterLocation); }
  
  public final Class<? extends Attribute> getCategory() { return PrinterLocation.class; }
  
  public final String getName() { return "printer-location"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */