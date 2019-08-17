package com.sun.corba.se.impl.util;

import java.util.Hashtable;

public class RepositoryIdCache extends Hashtable {
  private RepositoryIdPool pool = new RepositoryIdPool();
  
  public RepositoryIdCache() { this.pool.setCaches(this); }
  
  public final RepositoryId getId(String paramString) {
    RepositoryId repositoryId = (RepositoryId)get(paramString);
    if (repositoryId != null)
      return repositoryId; 
    repositoryId = new RepositoryId(paramString);
    put(paramString, repositoryId);
    return repositoryId;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\RepositoryIdCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */