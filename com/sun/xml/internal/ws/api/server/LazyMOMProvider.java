package com.sun.xml.internal.ws.api.server;

import java.util.HashSet;
import java.util.Set;

public static enum LazyMOMProvider {
  INSTANCE;
  
  private final Set<WSEndpointScopeChangeListener> endpointsWaitingForMOM = new HashSet();
  
  private final Set<DefaultScopeChangeListener> listeners = new HashSet();
  
  public void initMOMForScope(Scope paramScope) {
    if (this.scope == Scope.GLASSFISH_JMX || (paramScope == Scope.STANDALONE && (this.scope == Scope.GLASSFISH_JMX || this.scope == Scope.GLASSFISH_NO_JMX)) || this.scope == paramScope)
      return; 
    this.scope = paramScope;
    fireScopeChanged();
  }
  
  private void fireScopeChanged() {
    for (ScopeChangeListener scopeChangeListener : this.endpointsWaitingForMOM)
      scopeChangeListener.scopeChanged(this.scope); 
    for (ScopeChangeListener scopeChangeListener : this.listeners)
      scopeChangeListener.scopeChanged(this.scope); 
  }
  
  public void registerListener(DefaultScopeChangeListener paramDefaultScopeChangeListener) {
    this.listeners.add(paramDefaultScopeChangeListener);
    if (!isProviderInDefaultScope())
      paramDefaultScopeChangeListener.scopeChanged(this.scope); 
  }
  
  private boolean isProviderInDefaultScope() { return (this.scope == Scope.STANDALONE); }
  
  public Scope getScope() { return this.scope; }
  
  public void registerEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener) {
    this.endpointsWaitingForMOM.add(paramWSEndpointScopeChangeListener);
    if (!isProviderInDefaultScope())
      paramWSEndpointScopeChangeListener.scopeChanged(this.scope); 
  }
  
  public void unregisterEndpoint(WSEndpointScopeChangeListener paramWSEndpointScopeChangeListener) { this.endpointsWaitingForMOM.remove(paramWSEndpointScopeChangeListener); }
  
  public static interface DefaultScopeChangeListener extends ScopeChangeListener {}
  
  public enum Scope {
    STANDALONE, GLASSFISH_NO_JMX, GLASSFISH_JMX;
  }
  
  public static interface ScopeChangeListener {
    void scopeChanged(LazyMOMProvider.Scope param1Scope);
  }
  
  public static interface WSEndpointScopeChangeListener extends ScopeChangeListener {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\LazyMOMProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */