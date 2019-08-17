package sun.reflect;

import java.lang.reflect.Modifier;

public class FieldInfo {
  private String name;
  
  private String signature;
  
  private int modifiers;
  
  private int slot;
  
  public String name() { return this.name; }
  
  public String signature() { return this.signature; }
  
  public int modifiers() { return this.modifiers; }
  
  public int slot() { return this.slot; }
  
  public boolean isPublic() { return Modifier.isPublic(modifiers()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\FieldInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */