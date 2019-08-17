package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;

public final class DateTimeAtProcessing extends DateTimeSyntax implements PrintJobAttribute {
  private static final long serialVersionUID = -3710068197278263244L;
  
  public DateTimeAtProcessing(Date paramDate) { super(paramDate); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof DateTimeAtProcessing); }
  
  public final Class<? extends Attribute> getCategory() { return DateTimeAtProcessing.class; }
  
  public final String getName() { return "date-time-at-processing"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\DateTimeAtProcessing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */