package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;

public final class CannotProceed extends UserException {
  public NamingContext cxt = null;
  
  public NameComponent[] rest_of_name = null;
  
  public CannotProceed() { super(CannotProceedHelper.id()); }
  
  public CannotProceed(NamingContext paramNamingContext, NameComponent[] paramArrayOfNameComponent) {
    super(CannotProceedHelper.id());
    this.cxt = paramNamingContext;
    this.rest_of_name = paramArrayOfNameComponent;
  }
  
  public CannotProceed(String paramString, NamingContext paramNamingContext, NameComponent[] paramArrayOfNameComponent) {
    super(CannotProceedHelper.id() + "  " + paramString);
    this.cxt = paramNamingContext;
    this.rest_of_name = paramArrayOfNameComponent;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextPackage\CannotProceed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */