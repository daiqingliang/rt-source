package jdk.internal.instrumentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstrumentationTarget {
  String value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\InstrumentationTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */