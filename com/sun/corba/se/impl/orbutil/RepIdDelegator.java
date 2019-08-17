package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.Serializable;
import java.net.MalformedURLException;

public final class RepIdDelegator implements RepositoryIdStrings, RepositoryIdUtility, RepositoryIdInterface {
  private final RepositoryId delegate;
  
  public String createForAnyType(Class paramClass) { return RepositoryId.createForAnyType(paramClass); }
  
  public String createForJavaType(Serializable paramSerializable) throws TypeMismatchException { return RepositoryId.createForJavaType(paramSerializable); }
  
  public String createForJavaType(Class paramClass) { return RepositoryId.createForJavaType(paramClass); }
  
  public String createSequenceRepID(Object paramObject) { return RepositoryId.createSequenceRepID(paramObject); }
  
  public String createSequenceRepID(Class paramClass) { return RepositoryId.createSequenceRepID(paramClass); }
  
  public RepositoryIdInterface getFromString(String paramString) { return new RepIdDelegator(RepositoryId.cache.getId(paramString)); }
  
  public boolean isChunkedEncoding(int paramInt) { return RepositoryId.isChunkedEncoding(paramInt); }
  
  public boolean isCodeBasePresent(int paramInt) { return RepositoryId.isCodeBasePresent(paramInt); }
  
  public String getClassDescValueRepId() { return RepositoryId.kClassDescValueRepID; }
  
  public String getWStringValueRepId() { return "IDL:omg.org/CORBA/WStringValue:1.0"; }
  
  public int getTypeInfo(int paramInt) { return RepositoryId.getTypeInfo(paramInt); }
  
  public int getStandardRMIChunkedNoRepStrId() { return RepositoryId.kPreComputed_StandardRMIChunked_NoRep; }
  
  public int getCodeBaseRMIChunkedNoRepStrId() { return RepositoryId.kPreComputed_CodeBaseRMIChunked_NoRep; }
  
  public int getStandardRMIChunkedId() { return RepositoryId.kPreComputed_StandardRMIChunked; }
  
  public int getCodeBaseRMIChunkedId() { return RepositoryId.kPreComputed_CodeBaseRMIChunked; }
  
  public int getStandardRMIUnchunkedId() { return RepositoryId.kPreComputed_StandardRMIUnchunked; }
  
  public int getCodeBaseRMIUnchunkedId() { return RepositoryId.kPreComputed_CodeBaseRMIUnchunked; }
  
  public int getStandardRMIUnchunkedNoRepStrId() { return RepositoryId.kPreComputed_StandardRMIUnchunked_NoRep; }
  
  public int getCodeBaseRMIUnchunkedNoRepStrId() { return RepositoryId.kPreComputed_CodeBaseRMIUnchunked_NoRep; }
  
  public Class getClassFromType() throws ClassNotFoundException { return this.delegate.getClassFromType(); }
  
  public Class getClassFromType(String paramString) throws ClassNotFoundException, MalformedURLException { return this.delegate.getClassFromType(paramString); }
  
  public Class getClassFromType(Class paramClass, String paramString) throws ClassNotFoundException, MalformedURLException { return this.delegate.getClassFromType(paramClass, paramString); }
  
  public String getClassName() { return this.delegate.getClassName(); }
  
  public RepIdDelegator() { this(null); }
  
  private RepIdDelegator(RepositoryId paramRepositoryId) { this.delegate = paramRepositoryId; }
  
  public String toString() { return (this.delegate != null) ? this.delegate.toString() : getClass().getName(); }
  
  public boolean equals(Object paramObject) { return (this.delegate != null) ? this.delegate.equals(paramObject) : super.equals(paramObject); }
  
  public int hashCode() { return (this.delegate != null) ? this.delegate.hashCode() : super.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\RepIdDelegator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */