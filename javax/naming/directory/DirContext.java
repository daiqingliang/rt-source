package javax.naming.directory;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public interface DirContext extends Context {
  public static final int ADD_ATTRIBUTE = 1;
  
  public static final int REPLACE_ATTRIBUTE = 2;
  
  public static final int REMOVE_ATTRIBUTE = 3;
  
  Attributes getAttributes(Name paramName) throws NamingException;
  
  Attributes getAttributes(String paramString) throws NamingException;
  
  Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException;
  
  Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException;
  
  void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException;
  
  void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException;
  
  void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException;
  
  void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException;
  
  void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException;
  
  void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException;
  
  void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException;
  
  void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException;
  
  DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException;
  
  DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException;
  
  DirContext getSchema(Name paramName) throws NamingException;
  
  DirContext getSchema(String paramString) throws NamingException;
  
  DirContext getSchemaClassDefinition(Name paramName) throws NamingException;
  
  DirContext getSchemaClassDefinition(String paramString) throws NamingException;
  
  NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException;
  
  NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException;
  
  NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException;
  
  NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException;
  
  NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException;
  
  NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException;
  
  NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException;
  
  NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\DirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */