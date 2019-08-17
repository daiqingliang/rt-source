package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;

public class ORBDefaultInitRefResolverImpl implements Resolver {
  Operation urlHandler;
  
  String orbDefaultInitRef;
  
  public ORBDefaultInitRefResolverImpl(Operation paramOperation, String paramString) {
    this.urlHandler = paramOperation;
    this.orbDefaultInitRef = paramString;
  }
  
  public Object resolve(String paramString) {
    String str;
    if (this.orbDefaultInitRef == null)
      return null; 
    if (this.orbDefaultInitRef.startsWith("corbaloc:")) {
      str = this.orbDefaultInitRef + "/" + paramString;
    } else {
      str = this.orbDefaultInitRef + "#" + paramString;
    } 
    return (Object)this.urlHandler.operate(str);
  }
  
  public Set list() { return new HashSet(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\ORBDefaultInitRefResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */