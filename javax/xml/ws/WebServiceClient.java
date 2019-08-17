package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceClient {
  String name() default "";
  
  String targetNamespace() default "";
  
  String wsdlLocation() default "";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\WebServiceClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */