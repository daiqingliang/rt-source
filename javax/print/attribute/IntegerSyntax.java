package javax.print.attribute;

import java.io.Serializable;

public abstract class IntegerSyntax implements Serializable, Cloneable {
  private static final long serialVersionUID = 3644574816328081943L;
  
  private int value;
  
  protected IntegerSyntax(int paramInt) { this.value = paramInt; }
  
  protected IntegerSyntax(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 > paramInt1 || paramInt1 > paramInt3)
      throw new IllegalArgumentException("Value " + paramInt1 + " not in range " + paramInt2 + ".." + paramInt3); 
    this.value = paramInt1;
  }
  
  public int getValue() { return this.value; }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof IntegerSyntax && this.value == ((IntegerSyntax)paramObject).value); }
  
  public int hashCode() { return this.value; }
  
  public String toString() { return "" + this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\IntegerSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */