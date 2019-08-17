package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public class SOAPSEIModel extends AbstractSEIModelImpl {
  public SOAPSEIModel(WebServiceFeatureList paramWebServiceFeatureList) { super(paramWebServiceFeatureList); }
  
  protected void populateMaps() {
    byte b = 0;
    for (JavaMethodImpl javaMethodImpl : getJavaMethods()) {
      put(javaMethodImpl.getMethod(), javaMethodImpl);
      boolean bool = false;
      for (ParameterImpl parameterImpl : javaMethodImpl.getRequestParameters()) {
        ParameterBinding parameterBinding = parameterImpl.getBinding();
        if (parameterBinding.isBody()) {
          put(parameterImpl.getName(), javaMethodImpl);
          bool = true;
        } 
      } 
      if (!bool) {
        put(this.emptyBodyName, javaMethodImpl);
        b++;
      } 
    } 
    if (b > 1);
  }
  
  public Set<QName> getKnownHeaders() {
    HashSet hashSet = new HashSet();
    for (JavaMethodImpl javaMethodImpl : getJavaMethods()) {
      Iterator iterator = javaMethodImpl.getRequestParameters().iterator();
      fillHeaders(iterator, hashSet, WebParam.Mode.IN);
      iterator = javaMethodImpl.getResponseParameters().iterator();
      fillHeaders(iterator, hashSet, WebParam.Mode.OUT);
    } 
    return hashSet;
  }
  
  private void fillHeaders(Iterator<ParameterImpl> paramIterator, Set<QName> paramSet, WebParam.Mode paramMode) {
    while (paramIterator.hasNext()) {
      ParameterImpl parameterImpl = (ParameterImpl)paramIterator.next();
      ParameterBinding parameterBinding = (paramMode == WebParam.Mode.IN) ? parameterImpl.getInBinding() : parameterImpl.getOutBinding();
      QName qName = parameterImpl.getName();
      if (parameterBinding.isHeader() && !paramSet.contains(qName))
        paramSet.add(qName); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\SOAPSEIModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */