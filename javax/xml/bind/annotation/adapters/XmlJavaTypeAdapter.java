package javax.xml.bind.annotation.adapters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface XmlJavaTypeAdapter {
  Class<? extends XmlAdapter> value();
  
  Class type() default DEFAULT.class;
  
  public static final class DEFAULT {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\adapters\XmlJavaTypeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */