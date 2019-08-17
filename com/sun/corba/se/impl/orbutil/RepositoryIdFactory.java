package com.sun.corba.se.impl.orbutil;

public abstract class RepositoryIdFactory {
  private static final RepIdDelegator currentDelegator = new RepIdDelegator();
  
  public static RepositoryIdStrings getRepIdStringsFactory() { return currentDelegator; }
  
  public static RepositoryIdUtility getRepIdUtility() { return currentDelegator; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\RepositoryIdFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */