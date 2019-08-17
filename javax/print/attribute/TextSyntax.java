package javax.print.attribute;

import java.io.Serializable;
import java.util.Locale;

public abstract class TextSyntax implements Serializable, Cloneable {
  private static final long serialVersionUID = -8130648736378144102L;
  
  private String value;
  
  private Locale locale;
  
  protected TextSyntax(String paramString, Locale paramLocale) {
    this.value = verify(paramString);
    this.locale = verify(paramLocale);
  }
  
  private static String verify(String paramString) {
    if (paramString == null)
      throw new NullPointerException(" value is null"); 
    return paramString;
  }
  
  private static Locale verify(Locale paramLocale) { return (paramLocale == null) ? Locale.getDefault() : paramLocale; }
  
  public String getValue() { return this.value; }
  
  public Locale getLocale() { return this.locale; }
  
  public int hashCode() { return this.value.hashCode() ^ this.locale.hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof TextSyntax && this.value.equals(((TextSyntax)paramObject).value) && this.locale.equals(((TextSyntax)paramObject).locale)); }
  
  public String toString() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\TextSyntax.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */