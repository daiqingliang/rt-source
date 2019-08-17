package javax.naming.event;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public interface EventDirContext extends EventContext, DirContext {
  void addNamingListener(Name paramName, String paramString, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException;
  
  void addNamingListener(String paramString1, String paramString2, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException;
  
  void addNamingListener(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException;
  
  void addNamingListener(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls, NamingListener paramNamingListener) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\event\EventDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */