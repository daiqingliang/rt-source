package javax.jws.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SOAPBinding {
  Style style() default Style.DOCUMENT;
  
  Use use() default Use.LITERAL;
  
  ParameterStyle parameterStyle() default ParameterStyle.WRAPPED;
  
  public enum ParameterStyle {
    BARE, WRAPPED;
  }
  
  public enum Style {
    DOCUMENT, RPC;
  }
  
  public enum Use {
    LITERAL, ENCODED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\jws\soap\SOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */