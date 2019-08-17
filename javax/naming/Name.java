package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;

public interface Name extends Cloneable, Serializable, Comparable<Object> {
  public static final long serialVersionUID = -3617482732056931635L;
  
  Object clone();
  
  int compareTo(Object paramObject);
  
  int size();
  
  boolean isEmpty();
  
  Enumeration<String> getAll();
  
  String get(int paramInt);
  
  Name getPrefix(int paramInt);
  
  Name getSuffix(int paramInt);
  
  boolean startsWith(Name paramName);
  
  boolean endsWith(Name paramName);
  
  Name addAll(Name paramName) throws InvalidNameException;
  
  Name addAll(int paramInt, Name paramName) throws InvalidNameException;
  
  Name add(String paramString) throws InvalidNameException;
  
  Name add(int paramInt, String paramString) throws InvalidNameException;
  
  Object remove(int paramInt) throws InvalidNameException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\Name.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */