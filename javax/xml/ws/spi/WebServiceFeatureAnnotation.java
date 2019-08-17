package javax.xml.ws.spi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.WebServiceFeature;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServiceFeatureAnnotation {
  String id();
  
  Class<? extends WebServiceFeature> bean();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\spi\WebServiceFeatureAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */