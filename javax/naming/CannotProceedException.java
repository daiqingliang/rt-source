package javax.naming;

import java.util.Hashtable;

public class CannotProceedException extends NamingException {
  protected Name remainingNewName = null;
  
  protected Hashtable<?, ?> environment = null;
  
  protected Name altName = null;
  
  protected Context altNameCtx = null;
  
  private static final long serialVersionUID = 1219724816191576813L;
  
  public CannotProceedException(String paramString) { super(paramString); }
  
  public CannotProceedException() {}
  
  public Hashtable<?, ?> getEnvironment() { return this.environment; }
  
  public void setEnvironment(Hashtable<?, ?> paramHashtable) { this.environment = paramHashtable; }
  
  public Name getRemainingNewName() { return this.remainingNewName; }
  
  public void setRemainingNewName(Name paramName) {
    if (paramName != null) {
      this.remainingNewName = (Name)paramName.clone();
    } else {
      this.remainingNewName = null;
    } 
  }
  
  public Name getAltName() { return this.altName; }
  
  public void setAltName(Name paramName) { this.altName = paramName; }
  
  public Context getAltNameCtx() { return this.altNameCtx; }
  
  public void setAltNameCtx(Context paramContext) { this.altNameCtx = paramContext; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\CannotProceedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */