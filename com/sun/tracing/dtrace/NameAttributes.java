package com.sun.tracing.dtrace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface NameAttributes {
  Attributes value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\dtrace\NameAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */