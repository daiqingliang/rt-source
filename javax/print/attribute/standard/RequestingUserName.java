package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.TextSyntax;

public final class RequestingUserName extends TextSyntax implements PrintRequestAttribute {
  private static final long serialVersionUID = -2683049894310331454L;
  
  public RequestingUserName(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof RequestingUserName); }
  
  public final Class<? extends Attribute> getCategory() { return RequestingUserName.class; }
  
  public final String getName() { return "requesting-user-name"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\RequestingUserName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */