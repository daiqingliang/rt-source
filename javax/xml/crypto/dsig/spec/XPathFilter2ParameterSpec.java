package javax.xml.crypto.dsig.spec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class XPathFilter2ParameterSpec implements TransformParameterSpec {
  private final List<XPathType> xPathList;
  
  public XPathFilter2ParameterSpec(List paramList) {
    if (paramList == null)
      throw new NullPointerException("xPathList cannot be null"); 
    ArrayList arrayList1 = new ArrayList(paramList);
    if (arrayList1.isEmpty())
      throw new IllegalArgumentException("xPathList cannot be empty"); 
    int i = arrayList1.size();
    for (byte b = 0; b < i; b++) {
      if (!(arrayList1.get(b) instanceof XPathType))
        throw new ClassCastException("xPathList[" + b + "] is not a valid type"); 
    } 
    ArrayList arrayList2 = arrayList1;
    this.xPathList = Collections.unmodifiableList(arrayList2);
  }
  
  public List getXPathList() { return this.xPathList; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\XPathFilter2ParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */