package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public final class SheetCollate extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 7080587914259873003L;
  
  public static final SheetCollate UNCOLLATED = new SheetCollate(0);
  
  public static final SheetCollate COLLATED = new SheetCollate(1);
  
  private static final String[] myStringTable = { "uncollated", "collated" };
  
  private static final SheetCollate[] myEnumValueTable = { UNCOLLATED, COLLATED };
  
  protected SheetCollate(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return myStringTable; }
  
  protected EnumSyntax[] getEnumValueTable() { return myEnumValueTable; }
  
  public final Class<? extends Attribute> getCategory() { return SheetCollate.class; }
  
  public final String getName() { return "sheet-collate"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\SheetCollate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */