package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.addressing.policy.AddressingFeatureConfigurator;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.MtomFeatureConfigurator;
import com.sun.xml.internal.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class PolicyUtil {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
  
  private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS = new LinkedList();
  
  public static <T> void addServiceProviders(Collection<T> paramCollection, Class<T> paramClass) {
    Iterator iterator = ServiceFinder.find(paramClass).iterator();
    while (iterator.hasNext())
      paramCollection.add(iterator.next()); 
  }
  
  public static void configureModel(WSDLModel paramWSDLModel, PolicyMap paramPolicyMap) throws PolicyException {
    LOGGER.entering(new Object[] { paramWSDLModel, paramPolicyMap });
    for (WSDLService wSDLService : paramWSDLModel.getServices().values()) {
      for (WSDLPort wSDLPort : wSDLService.getPorts()) {
        Collection collection = getPortScopedFeatures(paramPolicyMap, wSDLService.getName(), wSDLPort.getName());
        for (WebServiceFeature webServiceFeature : collection) {
          wSDLPort.addFeature(webServiceFeature);
          wSDLPort.getBinding().addFeature(webServiceFeature);
        } 
      } 
    } 
    LOGGER.exiting();
  }
  
  public static Collection<WebServiceFeature> getPortScopedFeatures(PolicyMap paramPolicyMap, QName paramQName1, QName paramQName2) {
    LOGGER.entering(new Object[] { paramPolicyMap, paramQName1, paramQName2 });
    ArrayList arrayList = new ArrayList();
    try {
      PolicyMapKey policyMapKey = PolicyMap.createWsdlEndpointScopeKey(paramQName1, paramQName2);
      for (PolicyFeatureConfigurator policyFeatureConfigurator : CONFIGURATORS) {
        Collection collection = policyFeatureConfigurator.getFeatures(policyMapKey, paramPolicyMap);
        if (collection != null)
          arrayList.addAll(collection); 
      } 
    } catch (PolicyException policyException) {
      throw new WebServiceException(policyException);
    } 
    LOGGER.exiting(arrayList);
    return arrayList;
  }
  
  static  {
    CONFIGURATORS.add(new AddressingFeatureConfigurator());
    CONFIGURATORS.add(new MtomFeatureConfigurator());
    CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
    CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());
    addServiceProviders(CONFIGURATORS, PolicyFeatureConfigurator.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */