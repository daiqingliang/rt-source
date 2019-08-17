package com.sun.corba.se.impl.transport;

import com.sun.corba.se.spi.transport.ReadTimeouts;

public class ReadTCPTimeoutsImpl implements ReadTimeouts {
  private int initial_time_to_wait;
  
  private int max_time_to_wait;
  
  private int max_giop_header_time_to_wait;
  
  private double backoff_factor;
  
  public ReadTCPTimeoutsImpl(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.initial_time_to_wait = paramInt1;
    this.max_time_to_wait = paramInt2;
    this.max_giop_header_time_to_wait = paramInt3;
    this.backoff_factor = 1.0D + paramInt4 / 100.0D;
  }
  
  public int get_initial_time_to_wait() { return this.initial_time_to_wait; }
  
  public int get_max_time_to_wait() { return this.max_time_to_wait; }
  
  public double get_backoff_factor() { return this.backoff_factor; }
  
  public int get_max_giop_header_time_to_wait() { return this.max_giop_header_time_to_wait; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\ReadTCPTimeoutsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */