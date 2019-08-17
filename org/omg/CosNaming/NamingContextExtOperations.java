package org.omg.CosNaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public interface NamingContextExtOperations extends NamingContextOperations {
  String to_string(NameComponent[] paramArrayOfNameComponent) throws InvalidName;
  
  NameComponent[] to_name(String paramString) throws InvalidName;
  
  String to_url(String paramString1, String paramString2) throws InvalidAddress, InvalidName;
  
  Object resolve_str(String paramString) throws NotFound, CannotProceed, InvalidName;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextExtOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */