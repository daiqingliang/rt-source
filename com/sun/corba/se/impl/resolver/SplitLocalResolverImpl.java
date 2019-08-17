package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.spi.orbutil.closure.Closure;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.Set;
import org.omg.CORBA.Object;

public class SplitLocalResolverImpl implements LocalResolver {
  private Resolver resolver;
  
  private LocalResolver localResolver;
  
  public SplitLocalResolverImpl(Resolver paramResolver, LocalResolver paramLocalResolver) {
    this.resolver = paramResolver;
    this.localResolver = paramLocalResolver;
  }
  
  public void register(String paramString, Closure paramClosure) { this.localResolver.register(paramString, paramClosure); }
  
  public Object resolve(String paramString) { return this.resolver.resolve(paramString); }
  
  public Set list() { return this.resolver.list(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\SplitLocalResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */