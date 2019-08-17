package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Map;

public class SimpleRemapper extends Remapper {
  private final Map<String, String> mapping;
  
  public SimpleRemapper(Map<String, String> paramMap) { this.mapping = paramMap; }
  
  public SimpleRemapper(String paramString1, String paramString2) { this.mapping = Collections.singletonMap(paramString1, paramString2); }
  
  public String mapMethodName(String paramString1, String paramString2, String paramString3) {
    String str = map(paramString1 + '.' + paramString2 + paramString3);
    return (str == null) ? paramString2 : str;
  }
  
  public String mapFieldName(String paramString1, String paramString2, String paramString3) {
    String str = map(paramString1 + '.' + paramString2);
    return (str == null) ? paramString2 : str;
  }
  
  public String map(String paramString) { return (String)this.mapping.get(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\SimpleRemapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */