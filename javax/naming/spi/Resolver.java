package javax.naming.spi;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface Resolver {
  ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass) throws NamingException;
  
  ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\Resolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */