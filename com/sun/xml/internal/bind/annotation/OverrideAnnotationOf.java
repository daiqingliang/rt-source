package com.sun.xml.internal.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OverrideAnnotationOf {
  String value() default "content";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\annotation\OverrideAnnotationOf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */