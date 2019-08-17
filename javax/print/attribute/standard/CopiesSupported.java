package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class CopiesSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
  private static final long serialVersionUID = 6927711687034846001L;
  
  public CopiesSupported(int paramInt) {
    super(paramInt);
    if (paramInt < 1)
      throw new IllegalArgumentException("Copies value < 1 specified"); 
  }
  
  public CopiesSupported(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("Null range specified"); 
    if (paramInt1 < 1)
      throw new IllegalArgumentException("Copies value < 1 specified"); 
  }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof CopiesSupported); }
  
  public final Class<? extends Attribute> getCategory() { return CopiesSupported.class; }
  
  public final String getName() { return "copies-supported"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\CopiesSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */