package javax.print.attribute;

import java.io.Serializable;

public class HashPrintRequestAttributeSet extends HashAttributeSet implements PrintRequestAttributeSet, Serializable {
  private static final long serialVersionUID = 2364756266107751933L;
  
  public HashPrintRequestAttributeSet() { super(PrintRequestAttribute.class); }
  
  public HashPrintRequestAttributeSet(PrintRequestAttribute paramPrintRequestAttribute) { super(paramPrintRequestAttribute, PrintRequestAttribute.class); }
  
  public HashPrintRequestAttributeSet(PrintRequestAttribute[] paramArrayOfPrintRequestAttribute) { super(paramArrayOfPrintRequestAttribute, PrintRequestAttribute.class); }
  
  public HashPrintRequestAttributeSet(PrintRequestAttributeSet paramPrintRequestAttributeSet) { super(paramPrintRequestAttributeSet, PrintRequestAttribute.class); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\HashPrintRequestAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */