package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface XmlNs {
  String prefix();
  
  String namespaceURI();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\XmlNs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */