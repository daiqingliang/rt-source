package com.sun.corba.se.spi.orbutil.fsm;

public class FSMTest {
  public static final State STATE1 = new StateImpl("1");
  
  public static final State STATE2 = new StateImpl("2");
  
  public static final State STATE3 = new StateImpl("3");
  
  public static final State STATE4 = new StateImpl("4");
  
  public static final Input INPUT1 = new InputImpl("1");
  
  public static final Input INPUT2 = new InputImpl("2");
  
  public static final Input INPUT3 = new InputImpl("3");
  
  public static final Input INPUT4 = new InputImpl("4");
  
  private Guard counterGuard = new Guard() {
      public Result evaluate(FSM param1FSM, Input param1Input) {
        MyFSM myFSM = (MyFSM)param1FSM;
        return Result.convert((myFSM.counter < 3));
      }
    };
  
  private static void add1(StateEngine paramStateEngine, State paramState1, Input paramInput, State paramState2) { paramStateEngine.add(paramState1, paramInput, new TestAction1(paramState1, paramInput, paramState2), paramState2); }
  
  private static void add2(StateEngine paramStateEngine, State paramState1, State paramState2) { paramStateEngine.setDefault(paramState1, new TestAction2(paramState1, paramState2), paramState2); }
  
  public static void main(String[] paramArrayOfString) {
    TestAction3 testAction3 = new TestAction3(STATE3, INPUT1);
    StateEngine stateEngine = StateEngineFactory.create();
    add1(stateEngine, STATE1, INPUT1, STATE1);
    add2(stateEngine, STATE1, STATE2);
    add1(stateEngine, STATE2, INPUT1, STATE2);
    add1(stateEngine, STATE2, INPUT2, STATE2);
    add1(stateEngine, STATE2, INPUT3, STATE1);
    add1(stateEngine, STATE2, INPUT4, STATE3);
    stateEngine.add(STATE3, INPUT1, testAction3, STATE3);
    stateEngine.add(STATE3, INPUT1, testAction3, STATE4);
    add1(stateEngine, STATE3, INPUT2, STATE1);
    add1(stateEngine, STATE3, INPUT3, STATE2);
    add1(stateEngine, STATE3, INPUT4, STATE2);
    MyFSM myFSM = new MyFSM(stateEngine);
    TestInput testInput1 = new TestInput(INPUT1, "1.1");
    TestInput testInput2 = new TestInput(INPUT1, "1.2");
    TestInput testInput3 = new TestInput(INPUT2, "2.1");
    TestInput testInput4 = new TestInput(INPUT2, "2.2");
    TestInput testInput5 = new TestInput(INPUT3, "3.1");
    TestInput testInput6 = new TestInput(INPUT3, "3.2");
    TestInput testInput7 = new TestInput(INPUT3, "3.3");
    TestInput testInput8 = new TestInput(INPUT4, "4.1");
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput2.getInput());
    myFSM.doIt(testInput8.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput4.getInput());
    myFSM.doIt(testInput5.getInput());
    myFSM.doIt(testInput7.getInput());
    myFSM.doIt(testInput8.getInput());
    myFSM.doIt(testInput8.getInput());
    myFSM.doIt(testInput8.getInput());
    myFSM.doIt(testInput4.getInput());
    myFSM.doIt(testInput6.getInput());
    myFSM.doIt(testInput8.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput2.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput1.getInput());
    myFSM.doIt(testInput1.getInput());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\FSMTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */