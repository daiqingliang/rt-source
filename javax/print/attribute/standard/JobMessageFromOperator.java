package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.TextSyntax;

public final class JobMessageFromOperator extends TextSyntax implements PrintJobAttribute {
  private static final long serialVersionUID = -4620751846003142047L;
  
  public JobMessageFromOperator(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof JobMessageFromOperator); }
  
  public final Class<? extends Attribute> getCategory() { return JobMessageFromOperator.class; }
  
  public final String getName() { return "job-message-from-operator"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\JobMessageFromOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */