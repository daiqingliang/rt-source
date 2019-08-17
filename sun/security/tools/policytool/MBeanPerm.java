package sun.security.tools.policytool;

class MBeanPerm extends Perm {
  public MBeanPerm() { super("MBeanPermission", "javax.management.MBeanPermission", new String[0], new String[] { 
          "addNotificationListener", "getAttribute", "getClassLoader", "getClassLoaderFor", "getClassLoaderRepository", "getDomains", "getMBeanInfo", "getObjectInstance", "instantiate", "invoke", 
          "isInstanceOf", "queryMBeans", "queryNames", "registerMBean", "removeNotificationListener", "setAttribute", "unregisterMBean" }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\MBeanPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */