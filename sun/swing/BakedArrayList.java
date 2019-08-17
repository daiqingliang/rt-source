package sun.swing;

import java.util.ArrayList;
import java.util.List;

public class BakedArrayList extends ArrayList {
  private int _hashCode;
  
  public BakedArrayList(int paramInt) { super(paramInt); }
  
  public BakedArrayList(List paramList) {
    this(paramList.size());
    byte b = 0;
    int i = paramList.size();
    while (b < i) {
      add(paramList.get(b));
      b++;
    } 
    cacheHashCode();
  }
  
  public void cacheHashCode() {
    this._hashCode = 1;
    for (int i = size() - 1; i >= 0; i--)
      this._hashCode = 31 * this._hashCode + get(i).hashCode(); 
  }
  
  public int hashCode() { return this._hashCode; }
  
  public boolean equals(Object paramObject) {
    BakedArrayList bakedArrayList = (BakedArrayList)paramObject;
    int i = size();
    if (bakedArrayList.size() != i)
      return false; 
    while (i-- > 0) {
      if (!get(i).equals(bakedArrayList.get(i)))
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\BakedArrayList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */