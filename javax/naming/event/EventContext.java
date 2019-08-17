package javax.naming.event;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;

public interface EventContext extends Context {
  public static final int OBJECT_SCOPE = 0;
  
  public static final int ONELEVEL_SCOPE = 1;
  
  public static final int SUBTREE_SCOPE = 2;
  
  void addNamingListener(Name paramName, int paramInt, NamingListener paramNamingListener) throws NamingException;
  
  void addNamingListener(String paramString, int paramInt, NamingListener paramNamingListener) throws NamingException;
  
  void removeNamingListener(NamingListener paramNamingListener) throws NamingException;
  
  boolean targetMustExist() throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\event\EventContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */