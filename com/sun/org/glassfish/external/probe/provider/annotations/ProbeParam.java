package com.sun.org.glassfish.external.probe.provider.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ProbeParam {
  String value();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\probe\provider\annotations\ProbeParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */