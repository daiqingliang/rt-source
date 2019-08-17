package org.omg.CosNaming;

import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public interface NamingContextOperations {
  void bind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  void rebind(NameComponent[] paramArrayOfNameComponent, Object paramObject) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound;
  
  Object resolve(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName;
  
  void unbind(NameComponent[] paramArrayOfNameComponent) throws NotFound, CannotProceed, InvalidName;
  
  void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder);
  
  NamingContext new_context();
  
  NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent) throws NotFound, AlreadyBound, CannotProceed, InvalidName;
  
  void destroy() throws NotEmpty;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\NamingContextOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */