package javax.naming;

public class LinkException extends NamingException {
  protected Name linkResolvedName = null;
  
  protected Object linkResolvedObj = null;
  
  protected Name linkRemainingName = null;
  
  protected String linkExplanation = null;
  
  private static final long serialVersionUID = -7967662604076777712L;
  
  public LinkException(String paramString) { super(paramString); }
  
  public LinkException() {}
  
  public Name getLinkResolvedName() { return this.linkResolvedName; }
  
  public Name getLinkRemainingName() { return this.linkRemainingName; }
  
  public Object getLinkResolvedObj() { return this.linkResolvedObj; }
  
  public String getLinkExplanation() { return this.linkExplanation; }
  
  public void setLinkExplanation(String paramString) { this.linkExplanation = paramString; }
  
  public void setLinkResolvedName(Name paramName) {
    if (paramName != null) {
      this.linkResolvedName = (Name)paramName.clone();
    } else {
      this.linkResolvedName = null;
    } 
  }
  
  public void setLinkRemainingName(Name paramName) {
    if (paramName != null) {
      this.linkRemainingName = (Name)paramName.clone();
    } else {
      this.linkRemainingName = null;
    } 
  }
  
  public void setLinkResolvedObj(Object paramObject) { this.linkResolvedObj = paramObject; }
  
  public String toString() { return super.toString() + "; Link Remaining Name: '" + this.linkRemainingName + "'"; }
  
  public String toString(boolean paramBoolean) { return (!paramBoolean || this.linkResolvedObj == null) ? toString() : (toString() + "; Link Resolved Object: " + this.linkResolvedObj); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\LinkException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */