package javax.naming.ldap;

public class BasicControl implements Control {
  protected String id;
  
  protected boolean criticality = false;
  
  protected byte[] value = null;
  
  private static final long serialVersionUID = -4233907508771791687L;
  
  public BasicControl(String paramString) { this.id = paramString; }
  
  public BasicControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte) {
    this.id = paramString;
    this.criticality = paramBoolean;
    this.value = paramArrayOfByte;
  }
  
  public String getID() { return this.id; }
  
  public boolean isCritical() { return this.criticality; }
  
  public byte[] getEncodedValue() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\BasicControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */