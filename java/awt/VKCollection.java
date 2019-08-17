package java.awt;

import java.util.HashMap;
import java.util.Map;

class VKCollection {
  Map<Integer, String> code2name = new HashMap();
  
  Map<String, Integer> name2code = new HashMap();
  
  public void put(String paramString, Integer paramInteger) {
    assert paramString != null && paramInteger != null;
    assert findName(paramInteger) == null;
    assert findCode(paramString) == null;
    this.code2name.put(paramInteger, paramString);
    this.name2code.put(paramString, paramInteger);
  }
  
  public Integer findCode(String paramString) {
    assert paramString != null;
    return (Integer)this.name2code.get(paramString);
  }
  
  public String findName(Integer paramInteger) {
    assert paramInteger != null;
    return (String)this.code2name.get(paramInteger);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\VKCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */