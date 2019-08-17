package sun.tracing.dtrace;

class Activation {
  private SystemResource resource;
  
  private int referenceCount;
  
  Activation(String paramString, DTraceProvider[] paramArrayOfDTraceProvider) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      RuntimePermission runtimePermission = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
      securityManager.checkPermission(runtimePermission);
    } 
    this.referenceCount = paramArrayOfDTraceProvider.length;
    for (DTraceProvider dTraceProvider : paramArrayOfDTraceProvider)
      dTraceProvider.setActivation(this); 
    this.resource = new SystemResource(this, JVM.activate(paramString, paramArrayOfDTraceProvider));
  }
  
  void disposeProvider(DTraceProvider paramDTraceProvider) {
    if (--this.referenceCount == 0)
      this.resource.dispose(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\dtrace\Activation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */