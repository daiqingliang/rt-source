package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class Chromaticity extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 4660543931355214012L;
  
  public static final Chromaticity MONOCHROME = new Chromaticity(0);
  
  public static final Chromaticity COLOR = new Chromaticity(1);
  
  private static final String[] myStringTable = { "monochrome", "color" };
  
  private static final Chromaticity[] myEnumValueTable = { MONOCHROME, COLOR };
  
  protected Chromaticity(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return myStringTable; }
  
  protected EnumSyntax[] getEnumValueTable() { return myEnumValueTable; }
  
  public final Class<? extends Attribute> getCategory() { return Chromaticity.class; }
  
  public final String getName() { return "chromaticity"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\Chromaticity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */