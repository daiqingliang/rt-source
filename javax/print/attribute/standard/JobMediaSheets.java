package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class JobMediaSheets extends IntegerSyntax implements PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 408871131531979741L;
  
  public JobMediaSheets(int paramInt) { super(paramInt, 0, 2147483647); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof JobMediaSheets); }
  
  public final Class<? extends Attribute> getCategory() { return JobMediaSheets.class; }
  
  public final String getName() { return "job-media-sheets"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\JobMediaSheets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */