package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExcC14NParameterSpec implements C14NMethodParameterSpec {
  private List<String> preList;
  
  public static final String DEFAULT = "#default";
  
  public ExcC14NParameterSpec() { this.preList = Collections.emptyList(); }
  
  public ExcC14NParameterSpec(List paramList) {
    if (paramList == null)
      throw new NullPointerException("prefixList cannot be null"); 
    ArrayList arrayList1 = new ArrayList(paramList);
    byte b = 0;
    int i = arrayList1.size();
    while (b < i) {
      if (!(arrayList1.get(b) instanceof String))
        throw new ClassCastException("not a String"); 
      b++;
    } 
    ArrayList arrayList2 = arrayList1;
    this.preList = Collections.unmodifiableList(arrayList2);
  }
  
  public List getPrefixList() { return this.preList; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\ExcC14NParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */