package javax.naming.directory;

import java.io.Serializable;

public class SearchControls implements Serializable {
  public static final int OBJECT_SCOPE = 0;
  
  public static final int ONELEVEL_SCOPE = 1;
  
  public static final int SUBTREE_SCOPE = 2;
  
  private int searchScope = 1;
  
  private int timeLimit = 0;
  
  private boolean derefLink;
  
  private boolean returnObj;
  
  private long countLimit;
  
  private String[] attributesToReturn;
  
  private static final long serialVersionUID = -2480540967773454797L;
  
  public SearchControls() {
    this.countLimit = 0L;
    this.derefLink = false;
    this.returnObj = false;
    this.attributesToReturn = null;
  }
  
  public SearchControls(int paramInt1, long paramLong, int paramInt2, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2) {
    this.derefLink = paramBoolean2;
    this.returnObj = paramBoolean1;
    this.countLimit = paramLong;
    this.attributesToReturn = paramArrayOfString;
  }
  
  public int getSearchScope() { return this.searchScope; }
  
  public int getTimeLimit() { return this.timeLimit; }
  
  public boolean getDerefLinkFlag() { return this.derefLink; }
  
  public boolean getReturningObjFlag() { return this.returnObj; }
  
  public long getCountLimit() { return this.countLimit; }
  
  public String[] getReturningAttributes() { return this.attributesToReturn; }
  
  public void setSearchScope(int paramInt) { this.searchScope = paramInt; }
  
  public void setTimeLimit(int paramInt) { this.timeLimit = paramInt; }
  
  public void setDerefLinkFlag(boolean paramBoolean) { this.derefLink = paramBoolean; }
  
  public void setReturningObjFlag(boolean paramBoolean) { this.returnObj = paramBoolean; }
  
  public void setCountLimit(long paramLong) { this.countLimit = paramLong; }
  
  public void setReturningAttributes(String[] paramArrayOfString) { this.attributesToReturn = paramArrayOfString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\SearchControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */