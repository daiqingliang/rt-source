package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Serializable;

public interface RepositoryIdStrings {
  String createForAnyType(Class paramClass);
  
  String createForJavaType(Serializable paramSerializable) throws TypeMismatchException;
  
  String createForJavaType(Class paramClass);
  
  String createSequenceRepID(Object paramObject);
  
  String createSequenceRepID(Class paramClass);
  
  RepositoryIdInterface getFromString(String paramString);
  
  String getClassDescValueRepId();
  
  String getWStringValueRepId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdStrings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */