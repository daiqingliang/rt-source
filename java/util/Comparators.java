package java.util;

import java.io.Serializable;

class Comparators {
  private Comparators() { throw new AssertionError("no instances"); }
  
  enum NaturalOrderComparator implements Comparator<Comparable<Object>> {
    INSTANCE;
    
    public int compare(Comparable<Object> param1Comparable1, Comparable<Object> param1Comparable2) { return param1Comparable1.compareTo(param1Comparable2); }
    
    public Comparator<Comparable<Object>> reversed() { return Comparator.reverseOrder(); }
  }
  
  static final class NullComparator<T> extends Object implements Comparator<T>, Serializable {
    private static final long serialVersionUID = -7569533591570686392L;
    
    private final boolean nullFirst;
    
    private final Comparator<T> real;
    
    NullComparator(boolean param1Boolean, Comparator<? super T> param1Comparator) {
      this.nullFirst = param1Boolean;
      this.real = param1Comparator;
    }
    
    public int compare(T param1T1, T param1T2) { return (param1T1 == null) ? ((param1T2 == null) ? 0 : (this.nullFirst ? -1 : 1)) : ((param1T2 == null) ? (this.nullFirst ? 1 : -1) : ((this.real == null) ? 0 : this.real.compare(param1T1, param1T2))); }
    
    public Comparator<T> thenComparing(Comparator<? super T> param1Comparator) {
      Objects.requireNonNull(param1Comparator);
      return new NullComparator(this.nullFirst, (this.real == null) ? param1Comparator : this.real.thenComparing(param1Comparator));
    }
    
    public Comparator<T> reversed() { return new NullComparator(!this.nullFirst, (this.real == null) ? null : this.real.reversed()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Comparators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */