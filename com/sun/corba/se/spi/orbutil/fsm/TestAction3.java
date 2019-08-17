package com.sun.corba.se.spi.orbutil.fsm;

class TestAction3 implements Action {
  private State oldState;
  
  private Input label;
  
  public void doIt(FSM paramFSM, Input paramInput) {
    System.out.println("TestAction1:");
    System.out.println("\tlabel    = " + this.label);
    System.out.println("\toldState = " + this.oldState);
    if (this.label != paramInput)
      throw new Error("Unexcepted Input " + paramInput); 
  }
  
  public TestAction3(State paramState, Input paramInput) {
    this.oldState = paramState;
    this.label = paramInput;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */