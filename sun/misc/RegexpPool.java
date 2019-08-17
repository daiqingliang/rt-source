package sun.misc;

import java.io.PrintStream;

public class RegexpPool {
  private RegexpNode prefixMachine = new RegexpNode();
  
  private RegexpNode suffixMachine = new RegexpNode();
  
  private static final int BIG = 2147483647;
  
  private int lastDepth = Integer.MAX_VALUE;
  
  public void add(String paramString, Object paramObject) throws REException { add(paramString, paramObject, false); }
  
  public void replace(String paramString, Object paramObject) throws REException {
    try {
      add(paramString, paramObject, true);
    } catch (Exception exception) {}
  }
  
  public Object delete(String paramString) {
    Object object = null;
    RegexpNode regexpNode1 = this.prefixMachine;
    RegexpNode regexpNode2 = regexpNode1;
    int i = paramString.length() - 1;
    boolean bool = true;
    if (!paramString.startsWith("*") || !paramString.endsWith("*"))
      i++; 
    if (i <= 0)
      return null; 
    int j;
    for (j = 0; regexpNode1 != null; j++) {
      if (regexpNode1.result != null && regexpNode1.depth < Integer.MAX_VALUE && (!regexpNode1.exact || j == i))
        regexpNode2 = regexpNode1; 
      if (j >= i)
        break; 
      regexpNode1 = regexpNode1.find(paramString.charAt(j));
    } 
    regexpNode1 = this.suffixMachine;
    j = i;
    while (--j >= 0 && regexpNode1 != null) {
      if (regexpNode1.result != null && regexpNode1.depth < Integer.MAX_VALUE) {
        bool = false;
        regexpNode2 = regexpNode1;
      } 
      regexpNode1 = regexpNode1.find(paramString.charAt(j));
    } 
    if (bool) {
      if (paramString.equals(regexpNode2.re)) {
        object = regexpNode2.result;
        regexpNode2.result = null;
      } 
    } else if (paramString.equals(regexpNode2.re)) {
      object = regexpNode2.result;
      regexpNode2.result = null;
    } 
    return object;
  }
  
  public Object match(String paramString) { return matchAfter(paramString, 2147483647); }
  
  public Object matchNext(String paramString) { return matchAfter(paramString, this.lastDepth); }
  
  private void add(String paramString, Object paramObject, boolean paramBoolean) throws REException {
    RegexpNode regexpNode;
    int i = paramString.length();
    if (paramString.charAt(0) == '*') {
      for (regexpNode = this.suffixMachine; i > 1; regexpNode = regexpNode.add(paramString.charAt(--i)));
    } else {
      boolean bool = false;
      if (paramString.charAt(i - 1) == '*') {
        i--;
      } else {
        bool = true;
      } 
      regexpNode = this.prefixMachine;
      for (byte b = 0; b < i; b++)
        regexpNode = regexpNode.add(paramString.charAt(b)); 
      regexpNode.exact = bool;
    } 
    if (regexpNode.result != null && !paramBoolean)
      throw new REException(paramString + " is a duplicate"); 
    regexpNode.re = paramString;
    regexpNode.result = paramObject;
  }
  
  private Object matchAfter(String paramString, int paramInt) {
    RegexpNode regexpNode1 = this.prefixMachine;
    RegexpNode regexpNode2 = regexpNode1;
    byte b = 0;
    int i = 0;
    int j = paramString.length();
    if (j <= 0)
      return null; 
    int k;
    for (k = 0; regexpNode1 != null; k++) {
      if (regexpNode1.result != null && regexpNode1.depth < paramInt && (!regexpNode1.exact || k == j)) {
        this.lastDepth = regexpNode1.depth;
        regexpNode2 = regexpNode1;
        b = k;
        i = j;
      } 
      if (k >= j)
        break; 
      regexpNode1 = regexpNode1.find(paramString.charAt(k));
    } 
    regexpNode1 = this.suffixMachine;
    k = j;
    while (--k >= 0 && regexpNode1 != null) {
      if (regexpNode1.result != null && regexpNode1.depth < paramInt) {
        this.lastDepth = regexpNode1.depth;
        regexpNode2 = regexpNode1;
        b = 0;
        i = k + 1;
      } 
      regexpNode1 = regexpNode1.find(paramString.charAt(k));
    } 
    Object object = regexpNode2.result;
    if (object != null && object instanceof RegexpTarget)
      object = ((RegexpTarget)object).found(paramString.substring(b, i)); 
    return object;
  }
  
  public void reset() { this.lastDepth = Integer.MAX_VALUE; }
  
  public void print(PrintStream paramPrintStream) {
    paramPrintStream.print("Regexp pool:\n");
    if (this.suffixMachine.firstchild != null) {
      paramPrintStream.print(" Suffix machine: ");
      this.suffixMachine.firstchild.print(paramPrintStream);
      paramPrintStream.print("\n");
    } 
    if (this.prefixMachine.firstchild != null) {
      paramPrintStream.print(" Prefix machine: ");
      this.prefixMachine.firstchild.print(paramPrintStream);
      paramPrintStream.print("\n");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\RegexpPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */