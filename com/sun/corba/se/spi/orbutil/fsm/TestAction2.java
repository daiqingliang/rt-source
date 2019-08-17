package com.sun.corba.se.spi.orbutil.fsm;

class TestAction2 implements Action {
  private State oldState;
  
  private State newState;
  
  public void doIt(FSM paramFSM, Input paramInput) {
    System.out.println("TestAction2:");
    System.out.println("\toldState = " + this.oldState);
    System.out.println("\tnewState = " + this.newState);
    System.out.println("\tinput    = " + paramInput);
    if (this.oldState != paramFSM.getState())
      throw new Error("Unexpected old State " + paramFSM.getState()); 
  }
  
  public TestAction2(State paramState1, State paramState2) {
    this.oldState = paramState1;
    this.newState = paramState2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestAction2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */