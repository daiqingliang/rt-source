package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class OrientationRequested extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = -4447437289862822276L;
  
  public static final OrientationRequested PORTRAIT = new OrientationRequested(3);
  
  public static final OrientationRequested LANDSCAPE = new OrientationRequested(4);
  
  public static final OrientationRequested REVERSE_LANDSCAPE = new OrientationRequested(5);
  
  public static final OrientationRequested REVERSE_PORTRAIT = new OrientationRequested(6);
  
  private static final String[] myStringTable = { "portrait", "landscape", "reverse-landscape", "reverse-portrait" };
  
  private static final OrientationRequested[] myEnumValueTable = { PORTRAIT, LANDSCAPE, REVERSE_LANDSCAPE, REVERSE_PORTRAIT };
  
  protected OrientationRequested(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return myStringTable; }
  
  protected EnumSyntax[] getEnumValueTable() { return myEnumValueTable; }
  
  protected int getOffset() { return 3; }
  
  public final Class<? extends Attribute> getCategory() { return OrientationRequested.class; }
  
  public final String getName() { return "orientation-requested"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\OrientationRequested.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */