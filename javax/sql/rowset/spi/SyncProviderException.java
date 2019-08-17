package javax.sql.rowset.spi;

import com.sun.rowset.internal.SyncResolverImpl;
import java.sql.SQLException;

public class SyncProviderException extends SQLException {
  private SyncResolver syncResolver = null;
  
  static final long serialVersionUID = -939908523620640692L;
  
  public SyncProviderException() {}
  
  public SyncProviderException(String paramString) { super(paramString); }
  
  public SyncProviderException(SyncResolver paramSyncResolver) {
    if (paramSyncResolver == null)
      throw new IllegalArgumentException("Cannot instantiate a SyncProviderException with a null SyncResolver object"); 
    this.syncResolver = paramSyncResolver;
  }
  
  public SyncResolver getSyncResolver() {
    if (this.syncResolver != null)
      return this.syncResolver; 
    try {
      this.syncResolver = new SyncResolverImpl();
    } catch (SQLException sQLException) {}
    return this.syncResolver;
  }
  
  public void setSyncResolver(SyncResolver paramSyncResolver) {
    if (paramSyncResolver == null)
      throw new IllegalArgumentException("Cannot set a null SyncResolver object"); 
    this.syncResolver = paramSyncResolver;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\SyncProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */