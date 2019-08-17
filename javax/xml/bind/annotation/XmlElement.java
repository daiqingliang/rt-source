package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface XmlElement {
  String name() default "##default";
  
  boolean nillable() default false;
  
  boolean required() default false;
  
  String namespace() default "##default";
  
  String defaultValue() default "\000";
  
  Class type() default DEFAULT.class;
  
  public static final class DEFAULT {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\XmlElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */