package jdk.internal.org.objectweb.asm.util;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;

public interface Textifiable {
  void textify(StringBuffer paramStringBuffer, Map<Label, String> paramMap);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\Textifiable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */