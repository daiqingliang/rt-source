package com.sun.tracing.dtrace;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Attributes {
  StabilityLevel name() default StabilityLevel.PRIVATE;
  
  StabilityLevel data() default StabilityLevel.PRIVATE;
  
  DependencyClass dependency() default DependencyClass.UNKNOWN;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\dtrace\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */