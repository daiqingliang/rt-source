package javax.security.auth.callback;

public class UnsupportedCallbackException extends Exception {
  private static final long serialVersionUID = -6873556327655666839L;
  
  private Callback callback;
  
  public UnsupportedCallbackException(Callback paramCallback) { this.callback = paramCallback; }
  
  public UnsupportedCallbackException(Callback paramCallback, String paramString) {
    super(paramString);
    this.callback = paramCallback;
  }
  
  public Callback getCallback() { return this.callback; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\UnsupportedCallbackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */