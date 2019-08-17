package javax.security.auth.callback;

import java.io.Serializable;
import java.util.Locale;

public class LanguageCallback implements Callback, Serializable {
  private static final long serialVersionUID = 2019050433478903213L;
  
  private Locale locale;
  
  public void setLocale(Locale paramLocale) { this.locale = paramLocale; }
  
  public Locale getLocale() { return this.locale; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\callback\LanguageCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */