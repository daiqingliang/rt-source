package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceInformation implements IDLEntity {
  public int[] service_options;
  
  public ServiceDetail[] service_details;
  
  public ServiceInformation() {}
  
  public ServiceInformation(int[] paramArrayOfInt, ServiceDetail[] paramArrayOfServiceDetail) {
    this.service_options = paramArrayOfInt;
    this.service_details = paramArrayOfServiceDetail;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\ServiceInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */