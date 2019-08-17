package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.omg.CORBA.Object;

public class LocalResolverImpl implements LocalResolver {
  Map nameToClosure = new HashMap();
  
  public Object resolve(String paramString) {
    Closure closure = (Closure)this.nameToClosure.get(paramString);
    return (closure == null) ? null : (Object)closure.evaluate();
  }
  
  public Set list() { return this.nameToClosure.keySet(); }
  
  public void register(String paramString, Closure paramClosure) { this.nameToClosure.put(paramString, paramClosure); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\LocalResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */