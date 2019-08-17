package com.sun.org.glassfish.external.statistics;

public interface Statistic {
  String getName();
  
  String getUnit();
  
  String getDescription();
  
  long getStartTime();
  
  long getLastSampleTime();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\statistics\Statistic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */