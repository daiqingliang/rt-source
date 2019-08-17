package com.sun.corba.se.impl.javax.rmi.CORBA;

class KeepAlive extends Thread {
  boolean quit = false;
  
  public KeepAlive() { setDaemon(false); }
  
  public void run() {
    while (!this.quit) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  public void quit() {
    this.quit = true;
    notifyAll();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\KeepAlive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */