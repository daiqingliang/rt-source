package sun.security.tools.policytool;

class SerialPerm extends Perm {
  public SerialPerm() { super("SerializablePermission", "java.io.SerializablePermission", new String[] { "enableSubclassImplementation", "enableSubstitution" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\SerialPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */