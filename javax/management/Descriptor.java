package javax.management;

import java.io.Serializable;

public interface Descriptor extends Serializable, Cloneable {
  Object getFieldValue(String paramString) throws RuntimeOperationsException;
  
  void setField(String paramString, Object paramObject) throws RuntimeOperationsException;
  
  String[] getFields();
  
  String[] getFieldNames();
  
  Object[] getFieldValues(String... paramVarArgs);
  
  void removeField(String paramString);
  
  void setFields(String[] paramArrayOfString, Object[] paramArrayOfObject) throws RuntimeOperationsException;
  
  Object clone() throws RuntimeOperationsException;
  
  boolean isValid() throws RuntimeOperationsException;
  
  boolean equals(Object paramObject);
  
  int hashCode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\Descriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */