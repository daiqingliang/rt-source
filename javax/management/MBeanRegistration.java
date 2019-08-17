package javax.management;

public interface MBeanRegistration {
  ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception;
  
  void postRegister(Boolean paramBoolean);
  
  void preDeregister() throws Exception;
  
  void postDeregister() throws Exception;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanRegistration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */