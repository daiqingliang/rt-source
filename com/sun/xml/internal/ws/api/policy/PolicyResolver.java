package com.sun.xml.internal.ws.api.policy;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.ws.WebServiceException;

public interface PolicyResolver {
  PolicyMap resolve(ServerContext paramServerContext) throws WebServiceException;
  
  PolicyMap resolve(ClientContext paramClientContext) throws WebServiceException;
  
  public static class ClientContext {
    private PolicyMap policyMap;
    
    private Container container;
    
    public ClientContext(@Nullable PolicyMap param1PolicyMap, Container param1Container) {
      this.policyMap = param1PolicyMap;
      this.container = param1Container;
    }
    
    @Nullable
    public PolicyMap getPolicyMap() { return this.policyMap; }
    
    public Container getContainer() { return this.container; }
  }
  
  public static class ServerContext {
    private final PolicyMap policyMap;
    
    private final Class endpointClass;
    
    private final Container container;
    
    private final boolean hasWsdl;
    
    private final Collection<PolicyMapMutator> mutators;
    
    public ServerContext(@Nullable PolicyMap param1PolicyMap, Container param1Container, Class param1Class, PolicyMapMutator... param1VarArgs) {
      this.policyMap = param1PolicyMap;
      this.endpointClass = param1Class;
      this.container = param1Container;
      this.hasWsdl = true;
      this.mutators = Arrays.asList(param1VarArgs);
    }
    
    public ServerContext(@Nullable PolicyMap param1PolicyMap, Container param1Container, Class param1Class, boolean param1Boolean, PolicyMapMutator... param1VarArgs) {
      this.policyMap = param1PolicyMap;
      this.endpointClass = param1Class;
      this.container = param1Container;
      this.hasWsdl = param1Boolean;
      this.mutators = Arrays.asList(param1VarArgs);
    }
    
    @Nullable
    public PolicyMap getPolicyMap() { return this.policyMap; }
    
    @Nullable
    public Class getEndpointClass() { return this.endpointClass; }
    
    public Container getContainer() { return this.container; }
    
    public boolean hasWsdl() { return this.hasWsdl; }
    
    public Collection<PolicyMapMutator> getMutators() { return this.mutators; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\PolicyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */