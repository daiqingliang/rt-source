package javax.tools;

import java.util.Locale;

public interface Diagnostic<S> {
  public static final long NOPOS = -1L;
  
  Kind getKind();
  
  S getSource();
  
  long getPosition();
  
  long getStartPosition();
  
  long getEndPosition();
  
  long getLineNumber();
  
  long getColumnNumber();
  
  String getCode();
  
  String getMessage(Locale paramLocale);
  
  public enum Kind {
    ERROR, WARNING, MANDATORY_WARNING, NOTE, OTHER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\Diagnostic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */