package javax.xml.ws;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWrapper {
  String localName() default "";
  
  String targetNamespace() default "";
  
  String className() default "";
  
  String partName() default "";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\ResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */