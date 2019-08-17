package sun.security.tools.policytool;

class Perm {
  public final String CLASS;
  
  public final String FULL_CLASS;
  
  public final String[] TARGETS;
  
  public final String[] ACTIONS;
  
  public Perm(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2) {
    this.CLASS = paramString1;
    this.FULL_CLASS = paramString2;
    this.TARGETS = paramArrayOfString1;
    this.ACTIONS = paramArrayOfString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\Perm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */