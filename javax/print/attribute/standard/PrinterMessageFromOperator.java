package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.TextSyntax;

public final class PrinterMessageFromOperator extends TextSyntax implements PrintServiceAttribute {
  static final long serialVersionUID = -4486871203218629318L;
  
  public PrinterMessageFromOperator(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof PrinterMessageFromOperator); }
  
  public final Class<? extends Attribute> getCategory() { return PrinterMessageFromOperator.class; }
  
  public final String getName() { return "printer-message-from-operator"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterMessageFromOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */