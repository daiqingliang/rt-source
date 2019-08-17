package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class ColorSupported extends EnumSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = -2700555589688535545L;
  
  public static final ColorSupported NOT_SUPPORTED = new ColorSupported(0);
  
  public static final ColorSupported SUPPORTED = new ColorSupported(1);
  
  private static final String[] myStringTable = { "not-supported", "supported" };
  
  private static final ColorSupported[] myEnumValueTable = { NOT_SUPPORTED, SUPPORTED };
  
  protected ColorSupported(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return myStringTable; }
  
  protected EnumSyntax[] getEnumValueTable() { return myEnumValueTable; }
  
  public final Class<? extends Attribute> getCategory() { return ColorSupported.class; }
  
  public final String getName() { return "color-supported"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\ColorSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */