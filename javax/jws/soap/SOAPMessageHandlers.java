package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Deprecated
public @interface SOAPMessageHandlers {
  SOAPMessageHandler[] value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\jws\soap\SOAPMessageHandlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */