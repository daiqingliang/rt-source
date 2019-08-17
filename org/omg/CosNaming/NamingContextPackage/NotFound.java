package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;

public final class NotFound extends UserException {
  public NotFoundReason why = null;
  
  public NameComponent[] rest_of_name = null;
  
  public NotFound() { super(NotFoundHelper.id()); }
  
  public NotFound(NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent) {
    super(NotFoundHelper.id());
    this.why = paramNotFoundReason;
    this.rest_of_name = paramArrayOfNameComponent;
  }
  
  public NotFound(String paramString, NotFoundReason paramNotFoundReason, NameComponent[] paramArrayOfNameComponent) {
    super(NotFoundHelper.id() + "  " + paramString);
    this.why = paramNotFoundReason;
    this.rest_of_name = paramArrayOfNameComponent;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFound.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */