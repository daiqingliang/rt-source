package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CompoundEnumeration<E> extends Object implements Enumeration<E> {
  private Enumeration<E>[] enums;
  
  private int index = 0;
  
  public CompoundEnumeration(Enumeration<E>[] paramArrayOfEnumeration) { this.enums = paramArrayOfEnumeration; }
  
  private boolean next() {
    while (this.index < this.enums.length) {
      if (this.enums[this.index] != null && this.enums[this.index].hasMoreElements())
        return true; 
      this.index++;
    } 
    return false;
  }
  
  public boolean hasMoreElements() { return next(); }
  
  public E nextElement() {
    if (!next())
      throw new NoSuchElementException(); 
    return (E)this.enums[this.index].nextElement();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\CompoundEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */