package java.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Native {}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\annotation\Native.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */