package javax.naming.ldap;

public class SortKey {
  private String attrID;
  
  private boolean reverseOrder = false;
  
  private String matchingRuleID = null;
  
  public SortKey(String paramString) { this.attrID = paramString; }
  
  public SortKey(String paramString1, boolean paramBoolean, String paramString2) {
    this.attrID = paramString1;
    this.reverseOrder = !paramBoolean;
    this.matchingRuleID = paramString2;
  }
  
  public String getAttributeID() { return this.attrID; }
  
  public boolean isAscending() { return !this.reverseOrder; }
  
  public String getMatchingRuleID() { return this.matchingRuleID; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\SortKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */