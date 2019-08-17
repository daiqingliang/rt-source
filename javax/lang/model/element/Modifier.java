package javax.lang.model.element;

import java.util.Locale;

public static enum Modifier {
  PUBLIC, PROTECTED, PRIVATE, ABSTRACT, DEFAULT, STATIC, FINAL, TRANSIENT, VOLATILE, SYNCHRONIZED, NATIVE, STRICTFP;
  
  public String toString() { return name().toLowerCase(Locale.US); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\Modifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */