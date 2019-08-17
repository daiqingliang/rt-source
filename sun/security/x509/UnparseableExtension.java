package sun.security.x509;

import java.lang.reflect.Field;
import sun.misc.HexDumpEncoder;

class UnparseableExtension extends Extension {
  private String name = "";
  
  private Throwable why;
  
  public UnparseableExtension(Extension paramExtension, Throwable paramThrowable) {
    super(paramExtension);
    try {
      Class clazz = OIDMap.getClass(paramExtension.getExtensionId());
      if (clazz != null) {
        Field field = clazz.getDeclaredField("NAME");
        this.name = (String)field.get(null) + " ";
      } 
    } catch (Exception exception) {}
    this.why = paramThrowable;
  }
  
  public String toString() { return super.toString() + "Unparseable " + this.name + "extension due to\n" + this.why + "\n\n" + (new HexDumpEncoder()).encodeBuffer(getExtensionValue()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\UnparseableExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */