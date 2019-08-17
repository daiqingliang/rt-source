package javax.naming;

public class NamingException extends Exception {
  protected Name resolvedName = this.remainingName = null;
  
  protected Object resolvedObj = null;
  
  protected Name remainingName;
  
  protected Throwable rootException = null;
  
  private static final long serialVersionUID = -1299181962103167177L;
  
  public NamingException(String paramString) { super(paramString); }
  
  public NamingException() {}
  
  public Name getResolvedName() { return this.resolvedName; }
  
  public Name getRemainingName() { return this.remainingName; }
  
  public Object getResolvedObj() { return this.resolvedObj; }
  
  public String getExplanation() { return getMessage(); }
  
  public void setResolvedName(Name paramName) {
    if (paramName != null) {
      this.resolvedName = (Name)paramName.clone();
    } else {
      this.resolvedName = null;
    } 
  }
  
  public void setRemainingName(Name paramName) {
    if (paramName != null) {
      this.remainingName = (Name)paramName.clone();
    } else {
      this.remainingName = null;
    } 
  }
  
  public void setResolvedObj(Object paramObject) { this.resolvedObj = paramObject; }
  
  public void appendRemainingComponent(String paramString) {
    if (paramString != null)
      try {
        if (this.remainingName == null)
          this.remainingName = new CompositeName(); 
        this.remainingName.add(paramString);
      } catch (NamingException namingException) {
        throw new IllegalArgumentException(namingException.toString());
      }  
  }
  
  public void appendRemainingName(Name paramName) {
    if (paramName == null)
      return; 
    if (this.remainingName != null) {
      try {
        this.remainingName.addAll(paramName);
      } catch (NamingException namingException) {
        throw new IllegalArgumentException(namingException.toString());
      } 
    } else {
      this.remainingName = (Name)paramName.clone();
    } 
  }
  
  public Throwable getRootCause() { return this.rootException; }
  
  public void setRootCause(Throwable paramThrowable) {
    if (paramThrowable != this)
      this.rootException = paramThrowable; 
  }
  
  public Throwable getCause() { return getRootCause(); }
  
  public Throwable initCause(Throwable paramThrowable) {
    super.initCause(paramThrowable);
    setRootCause(paramThrowable);
    return this;
  }
  
  public String toString() {
    String str = super.toString();
    if (this.rootException != null)
      str = str + " [Root exception is " + this.rootException + "]"; 
    if (this.remainingName != null)
      str = str + "; remaining name '" + this.remainingName + "'"; 
    return str;
  }
  
  public String toString(boolean paramBoolean) { return (!paramBoolean || this.resolvedObj == null) ? toString() : (toString() + "; resolved object " + this.resolvedObj); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\NamingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */