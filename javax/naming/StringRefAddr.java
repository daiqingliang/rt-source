package javax.naming;

public class StringRefAddr extends RefAddr {
  private String contents;
  
  private static final long serialVersionUID = -8913762495138505527L;
  
  public StringRefAddr(String paramString1, String paramString2) {
    super(paramString1);
    this.contents = paramString2;
  }
  
  public Object getContent() { return this.contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\StringRefAddr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */