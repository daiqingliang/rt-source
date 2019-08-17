package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public abstract class Media extends EnumSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = -2823970704630722439L;
  
  protected Media(int paramInt) { super(paramInt); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof Media && paramObject.getClass() == getClass() && ((Media)paramObject).getValue() == getValue()); }
  
  public final Class<? extends Attribute> getCategory() { return Media.class; }
  
  public final String getName() { return "media"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\Media.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */