package javax.management.openmbean;

import java.util.Collection;

public interface CompositeData {
  CompositeType getCompositeType();
  
  Object get(String paramString);
  
  Object[] getAll(String[] paramArrayOfString);
  
  boolean containsKey(String paramString);
  
  boolean containsValue(Object paramObject);
  
  Collection<?> values();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\openmbean\CompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */