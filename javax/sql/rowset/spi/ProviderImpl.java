package javax.sql.rowset.spi;

import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;

class ProviderImpl extends SyncProvider {
  private String className = null;
  
  private String vendorName = null;
  
  private String ver = null;
  
  private int index;
  
  public void setClassname(String paramString) { this.className = paramString; }
  
  public String getClassname() { return this.className; }
  
  public void setVendor(String paramString) { this.vendorName = paramString; }
  
  public String getVendor() { return this.vendorName; }
  
  public void setVersion(String paramString) { this.ver = paramString; }
  
  public String getVersion() { return this.ver; }
  
  public void setIndex(int paramInt) { this.index = paramInt; }
  
  public int getIndex() { return this.index; }
  
  public int getDataSourceLock() {
    int i = 0;
    try {
      i = SyncFactory.getInstance(this.className).getDataSourceLock();
    } catch (SyncFactoryException syncFactoryException) {
      throw new SyncProviderException(syncFactoryException.getMessage());
    } 
    return i;
  }
  
  public int getProviderGrade() {
    int i = 0;
    try {
      i = SyncFactory.getInstance(this.className).getProviderGrade();
    } catch (SyncFactoryException syncFactoryException) {}
    return i;
  }
  
  public String getProviderID() { return this.className; }
  
  public RowSetReader getRowSetReader() {
    RowSetReader rowSetReader = null;
    try {
      rowSetReader = SyncFactory.getInstance(this.className).getRowSetReader();
    } catch (SyncFactoryException syncFactoryException) {}
    return rowSetReader;
  }
  
  public RowSetWriter getRowSetWriter() {
    RowSetWriter rowSetWriter = null;
    try {
      rowSetWriter = SyncFactory.getInstance(this.className).getRowSetWriter();
    } catch (SyncFactoryException syncFactoryException) {}
    return rowSetWriter;
  }
  
  public void setDataSourceLock(int paramInt) {
    try {
      SyncFactory.getInstance(this.className).setDataSourceLock(paramInt);
    } catch (SyncFactoryException syncFactoryException) {
      throw new SyncProviderException(syncFactoryException.getMessage());
    } 
  }
  
  public int supportsUpdatableView() {
    int i = 0;
    try {
      i = SyncFactory.getInstance(this.className).supportsUpdatableView();
    } catch (SyncFactoryException syncFactoryException) {}
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\spi\ProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */