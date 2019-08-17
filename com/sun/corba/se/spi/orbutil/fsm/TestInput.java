package com.sun.corba.se.spi.orbutil.fsm;

class TestInput {
  Input value;
  
  String msg;
  
  TestInput(Input paramInput, String paramString) {
    this.value = paramInput;
    this.msg = paramString;
  }
  
  public String toString() { return "Input " + this.value + " : " + this.msg; }
  
  public Input getInput() { return this.value; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\TestInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */