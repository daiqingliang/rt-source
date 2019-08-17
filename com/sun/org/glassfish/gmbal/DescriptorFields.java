package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DescriptorFields {
  String[] value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\gmbal\DescriptorFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */