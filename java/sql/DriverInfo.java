package java.sql;

class DriverInfo {
  final Driver driver;
  
  DriverAction da;
  
  DriverInfo(Driver paramDriver, DriverAction paramDriverAction) {
    this.driver = paramDriver;
    this.da = paramDriverAction;
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof DriverInfo && this.driver == ((DriverInfo)paramObject).driver); }
  
  public int hashCode() { return this.driver.hashCode(); }
  
  public String toString() { return "driver[className=" + this.driver + "]"; }
  
  DriverAction action() { return this.da; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\DriverInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */