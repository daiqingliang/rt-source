package com.sun.xml.internal.txw2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface XmlElement {
  String value() default "";
  
  String ns() default "##default";
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\annotation\XmlElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */