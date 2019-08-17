package sun.security.tools.policytool;

class MBeanSvrPerm extends Perm {
  public MBeanSvrPerm() { super("MBeanServerPermission", "javax.management.MBeanServerPermission", new String[] { "createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\MBeanSvrPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */