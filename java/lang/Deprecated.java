package java.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PACKAGE, ElementType.PARAMETER, ElementType.TYPE})
public @interface Deprecated {}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Deprecated.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */