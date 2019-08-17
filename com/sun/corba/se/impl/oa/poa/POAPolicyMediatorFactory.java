package com.sun.corba.se.impl.oa.poa;

abstract class POAPolicyMediatorFactory {
  static POAPolicyMediator create(Policies paramPolicies, POAImpl paramPOAImpl) {
    if (paramPolicies.retainServants()) {
      if (paramPolicies.useActiveMapOnly())
        return new POAPolicyMediatorImpl_R_AOM(paramPolicies, paramPOAImpl); 
      if (paramPolicies.useDefaultServant())
        return new POAPolicyMediatorImpl_R_UDS(paramPolicies, paramPOAImpl); 
      if (paramPolicies.useServantManager())
        return new POAPolicyMediatorImpl_R_USM(paramPolicies, paramPOAImpl); 
      throw paramPOAImpl.invocationWrapper().pmfCreateRetain();
    } 
    if (paramPolicies.useDefaultServant())
      return new POAPolicyMediatorImpl_NR_UDS(paramPolicies, paramPOAImpl); 
    if (paramPolicies.useServantManager())
      return new POAPolicyMediatorImpl_NR_USM(paramPolicies, paramPOAImpl); 
    throw paramPOAImpl.invocationWrapper().pmfCreateNonRetain();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\oa\poa\POAPolicyMediatorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */