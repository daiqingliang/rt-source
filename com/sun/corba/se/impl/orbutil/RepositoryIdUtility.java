package com.sun.corba.se.impl.orbutil;

public interface RepositoryIdUtility {
  public static final int NO_TYPE_INFO = 0;
  
  public static final int SINGLE_REP_TYPE_INFO = 2;
  
  public static final int PARTIAL_LIST_TYPE_INFO = 6;
  
  boolean isChunkedEncoding(int paramInt);
  
  boolean isCodeBasePresent(int paramInt);
  
  int getTypeInfo(int paramInt);
  
  int getStandardRMIChunkedNoRepStrId();
  
  int getCodeBaseRMIChunkedNoRepStrId();
  
  int getStandardRMIChunkedId();
  
  int getCodeBaseRMIChunkedId();
  
  int getStandardRMIUnchunkedId();
  
  int getCodeBaseRMIUnchunkedId();
  
  int getStandardRMIUnchunkedNoRepStrId();
  
  int getCodeBaseRMIUnchunkedNoRepStrId();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */