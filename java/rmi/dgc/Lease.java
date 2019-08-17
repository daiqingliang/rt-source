package java.rmi.dgc;

import java.io.Serializable;

public final class Lease implements Serializable {
  private VMID vmid;
  
  private long value;
  
  private static final long serialVersionUID = -5713411624328831948L;
  
  public Lease(VMID paramVMID, long paramLong) {
    this.vmid = paramVMID;
    this.value = paramLong;
  }
  
  public VMID getVMID() { return this.vmid; }
  
  public long getValue() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\dgc\Lease.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */