package javax.jws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface WebParam {
  String name() default "";
  
  String partName() default "";
  
  String targetNamespace() default "";
  
  Mode mode() default Mode.IN;
  
  boolean header() default false;
  
  public enum Mode {
    IN, OUT, INOUT;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\jws\WebParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */