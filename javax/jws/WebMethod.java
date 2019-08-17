package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WebMethod {
  String operationName() default "";
  
  String action() default "";
  
  boolean exclude() default false;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\jws\WebMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */