package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
  String name() default "";
  
  String lookup() default "";
  
  Class<?> type() default Object.class;
  
  AuthenticationType authenticationType() default AuthenticationType.CONTAINER;
  
  boolean shareable() default true;
  
  String mappedName() default "";
  
  String description() default "";
  
  public enum AuthenticationType {
    CONTAINER, APPLICATION;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */