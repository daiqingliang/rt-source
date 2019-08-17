package sun.rmi.transport.tcp;

class MultiplexConnectionInfo {
  int id;
  
  MultiplexInputStream in = null;
  
  MultiplexOutputStream out = null;
  
  boolean closed = false;
  
  MultiplexConnectionInfo(int paramInt) { this.id = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\tcp\MultiplexConnectionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */