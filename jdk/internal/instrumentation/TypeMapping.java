package jdk.internal.instrumentation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(TypeMappings.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeMapping {
  String from();
  
  String to();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\TypeMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */