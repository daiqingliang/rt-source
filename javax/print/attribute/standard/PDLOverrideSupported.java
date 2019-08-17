package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintServiceAttribute;

public class PDLOverrideSupported extends EnumSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = -4393264467928463934L;
  
  public static final PDLOverrideSupported NOT_ATTEMPTED = new PDLOverrideSupported(0);
  
  public static final PDLOverrideSupported ATTEMPTED = new PDLOverrideSupported(1);
  
  private static final String[] myStringTable = { "not-attempted", "attempted" };
  
  private static final PDLOverrideSupported[] myEnumValueTable = { NOT_ATTEMPTED, ATTEMPTED };
  
  protected PDLOverrideSupported(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return (String[])myStringTable.clone(); }
  
  protected EnumSyntax[] getEnumValueTable() { return (EnumSyntax[])myEnumValueTable.clone(); }
  
  public final Class<? extends Attribute> getCategory() { return PDLOverrideSupported.class; }
  
  public final String getName() { return "pdl-override-supported"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PDLOverrideSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */