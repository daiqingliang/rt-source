package javax.naming;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

final class NameImplEnumerator extends Object implements Enumeration<String> {
  Vector<String> vector;
  
  int count;
  
  int limit;
  
  NameImplEnumerator(Vector<String> paramVector, int paramInt1, int paramInt2) {
    this.vector = paramVector;
    this.count = paramInt1;
    this.limit = paramInt2;
  }
  
  public boolean hasMoreElements() { return (this.count < this.limit); }
  
  public String nextElement() {
    if (this.count < this.limit)
      return (String)this.vector.elementAt(this.count++); 
    throw new NoSuchElementException("NameImplEnumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\NameImplEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */