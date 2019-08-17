package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.Object;

public class CompositeResolverImpl implements Resolver {
  private Resolver first;
  
  private Resolver second;
  
  public CompositeResolverImpl(Resolver paramResolver1, Resolver paramResolver2) {
    this.first = paramResolver1;
    this.second = paramResolver2;
  }
  
  public Object resolve(String paramString) {
    Object object = this.first.resolve(paramString);
    if (object == null)
      object = this.second.resolve(paramString); 
    return object;
  }
  
  public Set list() {
    HashSet hashSet = new HashSet();
    hashSet.addAll(this.first.list());
    hashSet.addAll(this.second.list());
    return hashSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\CompositeResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */