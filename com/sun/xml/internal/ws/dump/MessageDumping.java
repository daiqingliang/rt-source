package com.sun.xml.internal.ws.dump;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WebServiceFeatureAnnotation(id = "com.sun.xml.internal.ws.messagedump.MessageDumpingFeature", bean = MessageDumpingFeature.class)
public @interface MessageDumping {
  boolean enabled() default true;
  
  String messageLoggingRoot() default "com.sun.xml.internal.ws.messagedump";
  
  String messageLoggingLevel() default "FINE";
  
  boolean storeMessages() default false;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */