package javax.xml.ws.soap;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WebServiceFeatureAnnotation(id = "http://www.w3.org/2004/08/soap/features/http-optimization", bean = MTOMFeature.class)
public @interface MTOM {
  boolean enabled() default true;
  
  int threshold() default 0;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\soap\MTOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */