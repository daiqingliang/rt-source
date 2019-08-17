package sun.security.x509;

import java.util.Comparator;

class AVAComparator extends Object implements Comparator<AVA> {
  private static final Comparator<AVA> INSTANCE = new AVAComparator();
  
  static Comparator<AVA> getInstance() { return INSTANCE; }
  
  public int compare(AVA paramAVA1, AVA paramAVA2) {
    boolean bool1 = paramAVA1.hasRFC2253Keyword();
    boolean bool2 = paramAVA2.hasRFC2253Keyword();
    return (bool1 == bool2) ? paramAVA1.toRFC2253CanonicalString().compareTo(paramAVA2.toRFC2253CanonicalString()) : (bool1 ? -1 : 1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\AVAComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */