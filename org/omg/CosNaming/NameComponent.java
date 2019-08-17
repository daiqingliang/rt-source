package org.omg.CosNaming;

import org.omg.CORBA.portable.IDLEntity;

public final class NameComponent implements IDLEntity {
  public String id = null;
  
  public String kind = null;
  
  public NameComponent() {}
  
  public NameComponent(String paramString1, String paramString2) {
    this.id = paramString1;
    this.kind = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NameComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */