package javax.management;

import java.io.Serializable;

public abstract class QueryEval implements Serializable {
  private static final long serialVersionUID = 2675899265640874796L;
  
  private static ThreadLocal<MBeanServer> server = new InheritableThreadLocal();
  
  public void setMBeanServer(MBeanServer paramMBeanServer) { server.set(paramMBeanServer); }
  
  public static MBeanServer getMBeanServer() { return (MBeanServer)server.get(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\QueryEval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */