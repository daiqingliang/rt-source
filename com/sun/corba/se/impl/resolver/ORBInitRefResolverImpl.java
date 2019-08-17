package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.StringPair;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Object;

public class ORBInitRefResolverImpl implements Resolver {
  Operation urlHandler;
  
  Map orbInitRefTable;
  
  public ORBInitRefResolverImpl(Operation paramOperation, StringPair[] paramArrayOfStringPair) {
    this.urlHandler = paramOperation;
    this.orbInitRefTable = new HashMap();
    for (byte b = 0; b < paramArrayOfStringPair.length; b++) {
      StringPair stringPair = paramArrayOfStringPair[b];
      this.orbInitRefTable.put(stringPair.getFirst(), stringPair.getSecond());
    } 
  }
  
  public Object resolve(String paramString) {
    String str = (String)this.orbInitRefTable.get(paramString);
    return (str == null) ? null : (Object)this.urlHandler.operate(str);
  }
  
  public Set list() { return this.orbInitRefTable.keySet(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\ORBInitRefResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */