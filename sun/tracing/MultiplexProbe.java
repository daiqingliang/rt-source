package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

class MultiplexProbe extends ProbeSkeleton {
  private Set<Probe> probes = new HashSet();
  
  MultiplexProbe(Method paramMethod, Set<Provider> paramSet) {
    super(paramMethod.getParameterTypes());
    for (Provider provider : paramSet) {
      Probe probe = provider.getProbe(paramMethod);
      if (probe != null)
        this.probes.add(probe); 
    } 
  }
  
  public boolean isEnabled() {
    for (Probe probe : this.probes) {
      if (probe.isEnabled())
        return true; 
    } 
    return false;
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject) {
    for (Probe probe : this.probes) {
      try {
        ProbeSkeleton probeSkeleton = (ProbeSkeleton)probe;
        probeSkeleton.uncheckedTrigger(paramArrayOfObject);
      } catch (ClassCastException classCastException) {
        try {
          Method method = Probe.class.getMethod("trigger", new Class[] { Class.forName("[java.lang.Object") });
          method.invoke(probe, paramArrayOfObject);
        } catch (Exception exception) {
          assert false;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\MultiplexProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */