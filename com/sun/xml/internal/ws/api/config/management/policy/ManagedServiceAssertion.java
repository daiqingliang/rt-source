package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedServiceAssertion extends ManagementAssertion {
  public static final QName MANAGED_SERVICE_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedService");
  
  private static final QName COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementations");
  
  private static final QName COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementation");
  
  private static final QName CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfiguratorImplementation");
  
  private static final QName CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigSaverImplementation");
  
  private static final QName CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigReaderImplementation");
  
  private static final QName CLASS_NAME_ATTRIBUTE_QNAME = new QName("className");
  
  private static final QName ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME = new QName("endpointDisposeDelay");
  
  private static final Logger LOGGER = Logger.getLogger(ManagedServiceAssertion.class);
  
  public static ManagedServiceAssertion getAssertion(WSEndpoint paramWSEndpoint) throws WebServiceException {
    LOGGER.entering(new Object[] { paramWSEndpoint });
    PolicyMap policyMap = paramWSEndpoint.getPolicyMap();
    ManagedServiceAssertion managedServiceAssertion = (ManagedServiceAssertion)ManagementAssertion.getAssertion(MANAGED_SERVICE_QNAME, policyMap, paramWSEndpoint.getServiceName(), paramWSEndpoint.getPortName(), ManagedServiceAssertion.class);
    LOGGER.exiting(managedServiceAssertion);
    return managedServiceAssertion;
  }
  
  public ManagedServiceAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection) throws AssertionCreationException { super(MANAGED_SERVICE_QNAME, paramAssertionData, paramCollection); }
  
  public boolean isManagementEnabled() {
    String str = getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
    boolean bool = true;
    if (str != null)
      if (str.trim().toLowerCase().equals("on")) {
        bool = true;
      } else {
        bool = Boolean.parseBoolean(str);
      }  
    return bool;
  }
  
  public long getEndpointDisposeDelay(long paramLong) throws WebServiceException {
    long l = paramLong;
    String str = getAttributeValue(ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME);
    if (str != null)
      try {
        l = Long.parseLong(str);
      } catch (NumberFormatException numberFormatException) {
        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(str), numberFormatException));
      }  
    return l;
  }
  
  public Collection<ImplementationRecord> getCommunicationServerImplementations() {
    LinkedList linkedList = new LinkedList();
    Iterator iterator = getParametersIterator();
    while (iterator.hasNext()) {
      PolicyAssertion policyAssertion = (PolicyAssertion)iterator.next();
      if (COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME.equals(policyAssertion.getName())) {
        Iterator iterator1 = policyAssertion.getParametersIterator();
        if (!iterator1.hasNext())
          throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1005_EXPECTED_COMMUNICATION_CHILD())); 
        while (iterator1.hasNext()) {
          PolicyAssertion policyAssertion1 = (PolicyAssertion)iterator1.next();
          if (COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME.equals(policyAssertion1.getName())) {
            linkedList.add(getImplementation(policyAssertion1));
            continue;
          } 
          throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(ManagementMessages.WSM_1004_EXPECTED_XML_TAG(COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME, policyAssertion1.getName())));
        } 
      } 
    } 
    return linkedList;
  }
  
  public ImplementationRecord getConfiguratorImplementation() { return findImplementation(CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME); }
  
  public ImplementationRecord getConfigSaverImplementation() { return findImplementation(CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME); }
  
  public ImplementationRecord getConfigReaderImplementation() { return findImplementation(CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME); }
  
  private ImplementationRecord findImplementation(QName paramQName) {
    Iterator iterator = getParametersIterator();
    while (iterator.hasNext()) {
      PolicyAssertion policyAssertion = (PolicyAssertion)iterator.next();
      if (paramQName.equals(policyAssertion.getName()))
        return getImplementation(policyAssertion); 
    } 
    return null;
  }
  
  private ImplementationRecord getImplementation(PolicyAssertion paramPolicyAssertion) {
    String str = paramPolicyAssertion.getAttributeValue(CLASS_NAME_ATTRIBUTE_QNAME);
    HashMap hashMap = new HashMap();
    Iterator iterator = paramPolicyAssertion.getParametersIterator();
    LinkedList linkedList = new LinkedList();
    while (iterator.hasNext()) {
      PolicyAssertion policyAssertion = (PolicyAssertion)iterator.next();
      QName qName = policyAssertion.getName();
      if (policyAssertion.hasParameters()) {
        HashMap hashMap1 = new HashMap();
        Iterator iterator1 = policyAssertion.getParametersIterator();
        while (iterator1.hasNext()) {
          PolicyAssertion policyAssertion1 = (PolicyAssertion)iterator1.next();
          String str2 = policyAssertion1.getValue();
          if (str2 != null)
            str2 = str2.trim(); 
          hashMap1.put(policyAssertion1.getName(), str2);
        } 
        linkedList.add(new NestedParameters(qName, hashMap1, null));
        continue;
      } 
      String str1 = policyAssertion.getValue();
      if (str1 != null)
        str1 = str1.trim(); 
      hashMap.put(qName, str1);
    } 
    return new ImplementationRecord(str, hashMap, linkedList);
  }
  
  public static class ImplementationRecord {
    private final String implementation;
    
    private final Map<QName, String> parameters;
    
    private final Collection<ManagedServiceAssertion.NestedParameters> nestedParameters;
    
    protected ImplementationRecord(String param1String, Map<QName, String> param1Map, Collection<ManagedServiceAssertion.NestedParameters> param1Collection) {
      this.implementation = param1String;
      this.parameters = param1Map;
      this.nestedParameters = param1Collection;
    }
    
    public String getImplementation() { return this.implementation; }
    
    public Map<QName, String> getParameters() { return this.parameters; }
    
    public Collection<ManagedServiceAssertion.NestedParameters> getNestedParameters() { return this.nestedParameters; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      ImplementationRecord implementationRecord = (ImplementationRecord)param1Object;
      return ((this.implementation == null) ? (implementationRecord.implementation != null) : !this.implementation.equals(implementationRecord.implementation)) ? false : ((this.parameters != implementationRecord.parameters && (this.parameters == null || !this.parameters.equals(implementationRecord.parameters))) ? false : (!(this.nestedParameters != implementationRecord.nestedParameters && (this.nestedParameters == null || !this.nestedParameters.equals(implementationRecord.nestedParameters)))));
    }
    
    public int hashCode() {
      null = 3;
      null = 53 * null + ((this.implementation != null) ? this.implementation.hashCode() : 0);
      null = 53 * null + ((this.parameters != null) ? this.parameters.hashCode() : 0);
      return 53 * null + ((this.nestedParameters != null) ? this.nestedParameters.hashCode() : 0);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder("ImplementationRecord: ");
      stringBuilder.append("implementation = \"").append(this.implementation).append("\", ");
      stringBuilder.append("parameters = \"").append(this.parameters).append("\", ");
      stringBuilder.append("nested parameters = \"").append(this.nestedParameters).append("\"");
      return stringBuilder.toString();
    }
  }
  
  public static class NestedParameters {
    private final QName name;
    
    private final Map<QName, String> parameters;
    
    private NestedParameters(QName param1QName, Map<QName, String> param1Map) {
      this.name = param1QName;
      this.parameters = param1Map;
    }
    
    public QName getName() { return this.name; }
    
    public Map<QName, String> getParameters() { return this.parameters; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      NestedParameters nestedParameters = (NestedParameters)param1Object;
      return ((this.name == null) ? (nestedParameters.name != null) : !this.name.equals(nestedParameters.name)) ? false : (!(this.parameters != nestedParameters.parameters && (this.parameters == null || !this.parameters.equals(nestedParameters.parameters))));
    }
    
    public int hashCode() {
      null = 5;
      null = 59 * null + ((this.name != null) ? this.name.hashCode() : 0);
      return 59 * null + ((this.parameters != null) ? this.parameters.hashCode() : 0);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder("NestedParameters: ");
      stringBuilder.append("name = \"").append(this.name).append("\", ");
      stringBuilder.append("parameters = \"").append(this.parameters).append("\"");
      return stringBuilder.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\config\management\policy\ManagedServiceAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */