package com.sun.corba.se.spi.orbutil.fsm;

class TestAction1 implements Action {
  private State oldState;
  
  private Input label;
  
  private State newState;
  
  public void doIt(FSM paramFSM, Input paramInput) {
    System.out.println("TestAction1:");
    System.out.println("\tlabel    = " + this.label);
    System.out.println("\toldState = " + this.oldState);
    System.out.println("\tnewState = " + this.newState);
    if (this.label != paramInput)
      throw new Error("Unexcepted Input " + paramInput); 
    if (this.oldState != paramFSM.getState())
      throw new Error("Unexpected old State " + paramFSM.getState()); 
  }
  
  public TestAction1(State paramState1, Input paramInput, State paramState2) {
    this.oldState = paramState1;
    this.newState = paramState2;
    this.label = paramInput;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */