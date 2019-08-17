package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class DuplicateName extends UserException {
  public String name = null;
  
  public DuplicateName() { super(DuplicateNameHelper.id()); }
  
  public DuplicateName(String paramString) {
    super(DuplicateNameHelper.id());
    this.name = paramString;
  }
  
  public DuplicateName(String paramString1, String paramString2) {
    super(DuplicateNameHelper.id() + "  " + paramString1);
    this.name = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\PortableInterceptor\ORBInitInfoPackage\DuplicateName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */