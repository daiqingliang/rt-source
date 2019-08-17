package sun.security.tools.keytool;

import java.util.Objects;

class Pair<A, B> extends Object {
  public final A fst;
  
  public final B snd;
  
  public Pair(A paramA, B paramB) {
    this.fst = paramA;
    this.snd = paramB;
  }
  
  public String toString() { return "Pair[" + this.fst + "," + this.snd + "]"; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Pair && Objects.equals(this.fst, ((Pair)paramObject).fst) && Objects.equals(this.snd, ((Pair)paramObject).snd)); }
  
  public int hashCode() { return (this.fst == null) ? ((this.snd == null) ? 0 : (this.snd.hashCode() + 1)) : ((this.snd == null) ? (this.fst.hashCode() + 2) : (this.fst.hashCode() * 17 + this.snd.hashCode())); }
  
  public static <A, B> Pair<A, B> of(A paramA, B paramB) { return new Pair(paramA, paramB); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\keytool\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */