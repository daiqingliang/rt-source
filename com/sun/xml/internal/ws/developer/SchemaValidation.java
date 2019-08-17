package com.sun.xml.internal.ws.developer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
@WebServiceFeatureAnnotation(id = "http://jax-ws.dev.java.net/features/schema-validation", bean = SchemaValidationFeature.class)
public @interface SchemaValidation {
  Class<? extends ValidationErrorHandler> handler() default com.sun.xml.internal.ws.server.DraconianValidationErrorHandler.class;
  
  boolean inbound() default true;
  
  boolean outbound() default true;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\SchemaValidation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */