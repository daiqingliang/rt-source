package com.sun.xml.internal.ws.api;

import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceFeature;

public class WebServiceFeatureFactory {
  public static WSFeatureList getWSFeatureList(Iterable<Annotation> paramIterable) {
    WebServiceFeatureList webServiceFeatureList = new WebServiceFeatureList();
    webServiceFeatureList.parseAnnotations(paramIterable);
    return webServiceFeatureList;
  }
  
  public static WebServiceFeature getWebServiceFeature(Annotation paramAnnotation) { return WebServiceFeatureList.getFeature(paramAnnotation); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\WebServiceFeatureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */