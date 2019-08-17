package javax.management;

public interface PersistentMBean {
  void load() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
  
  void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\PersistentMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */