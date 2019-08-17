package javax.naming.directory;

import javax.naming.Binding;

public class SearchResult extends Binding {
  private Attributes attrs;
  
  private static final long serialVersionUID = -9158063327699723172L;
  
  public SearchResult(String paramString, Object paramObject, Attributes paramAttributes) {
    super(paramString, paramObject);
    this.attrs = paramAttributes;
  }
  
  public SearchResult(String paramString, Object paramObject, Attributes paramAttributes, boolean paramBoolean) {
    super(paramString, paramObject, paramBoolean);
    this.attrs = paramAttributes;
  }
  
  public SearchResult(String paramString1, String paramString2, Object paramObject, Attributes paramAttributes) {
    super(paramString1, paramString2, paramObject);
    this.attrs = paramAttributes;
  }
  
  public SearchResult(String paramString1, String paramString2, Object paramObject, Attributes paramAttributes, boolean paramBoolean) {
    super(paramString1, paramString2, paramObject, paramBoolean);
    this.attrs = paramAttributes;
  }
  
  public Attributes getAttributes() { return this.attrs; }
  
  public void setAttributes(Attributes paramAttributes) { this.attrs = paramAttributes; }
  
  public String toString() { return super.toString() + ":" + getAttributes(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\SearchResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */