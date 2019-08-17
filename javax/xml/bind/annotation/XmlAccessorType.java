package javax.xml.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
public @interface XmlAccessorType {
  XmlAccessType value() default XmlAccessType.PUBLIC_MEMBER;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\XmlAccessorType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */