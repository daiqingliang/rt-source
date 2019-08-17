package javax.print.attribute;

import java.io.Serializable;

public class HashPrintServiceAttributeSet extends HashAttributeSet implements PrintServiceAttributeSet, Serializable {
  private static final long serialVersionUID = 6642904616179203070L;
  
  public HashPrintServiceAttributeSet() { super(PrintServiceAttribute.class); }
  
  public HashPrintServiceAttributeSet(PrintServiceAttribute paramPrintServiceAttribute) { super(paramPrintServiceAttribute, PrintServiceAttribute.class); }
  
  public HashPrintServiceAttributeSet(PrintServiceAttribute[] paramArrayOfPrintServiceAttribute) { super(paramArrayOfPrintServiceAttribute, PrintServiceAttribute.class); }
  
  public HashPrintServiceAttributeSet(PrintServiceAttributeSet paramPrintServiceAttributeSet) { super(paramPrintServiceAttributeSet, PrintServiceAttribute.class); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\HashPrintServiceAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */