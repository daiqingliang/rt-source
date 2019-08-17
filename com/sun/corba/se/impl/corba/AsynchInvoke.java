package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;

public class AsynchInvoke implements Runnable {
  private RequestImpl _req;
  
  private ORB _orb;
  
  private boolean _notifyORB;
  
  public AsynchInvoke(ORB paramORB, RequestImpl paramRequestImpl, boolean paramBoolean) {
    this._orb = paramORB;
    this._req = paramRequestImpl;
    this._notifyORB = paramBoolean;
  }
  
  public void run() {
    this._req.doInvocation();
    synchronized (this._req) {
      this._req.gotResponse = true;
      this._req.notify();
    } 
    if (this._notifyORB == true)
      this._orb.notifyORB(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\AsynchInvoke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */