package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PreDestroy {}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\PreDestroy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */