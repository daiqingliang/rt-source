package com.sun.xml.internal.ws.binding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class FeatureListUtil {
  @NotNull
  public static WebServiceFeatureList mergeList(WebServiceFeatureList... paramVarArgs) {
    WebServiceFeatureList webServiceFeatureList = new WebServiceFeatureList();
    for (WebServiceFeatureList webServiceFeatureList1 : paramVarArgs)
      webServiceFeatureList.addAll(webServiceFeatureList1); 
    return webServiceFeatureList;
  }
  
  @Nullable
  public static <F extends WebServiceFeature> F mergeFeature(@NotNull Class<F> paramClass, @Nullable WebServiceFeatureList paramWebServiceFeatureList1, @Nullable WebServiceFeatureList paramWebServiceFeatureList2) throws WebServiceException {
    WebServiceFeature webServiceFeature1 = (paramWebServiceFeatureList1 != null) ? paramWebServiceFeatureList1.get(paramClass) : null;
    WebServiceFeature webServiceFeature2 = (paramWebServiceFeatureList2 != null) ? paramWebServiceFeatureList2.get(paramClass) : null;
    if (webServiceFeature1 == null)
      return (F)webServiceFeature2; 
    if (webServiceFeature2 == null)
      return (F)webServiceFeature1; 
    if (webServiceFeature1.equals(webServiceFeature2))
      return (F)webServiceFeature1; 
    throw new WebServiceException(webServiceFeature1 + ", " + webServiceFeature2);
  }
  
  public static boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @Nullable WebServiceFeatureList paramWebServiceFeatureList1, @Nullable WebServiceFeatureList paramWebServiceFeatureList2) throws WebServiceException {
    WebServiceFeature webServiceFeature = mergeFeature(paramClass, paramWebServiceFeatureList1, paramWebServiceFeatureList2);
    return (webServiceFeature != null && webServiceFeature.isEnabled());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\binding\FeatureListUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */