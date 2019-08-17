package com.sun.corba.se.spi.orbutil.fsm;

public interface Guard {
  Result evaluate(FSM paramFSM, Input paramInput);
  
  public static final class Complement extends GuardBase {
    private Guard guard;
    
    public Complement(GuardBase param1GuardBase) {
      super("not(" + param1GuardBase.getName() + ")");
      this.guard = param1GuardBase;
    }
    
    public Guard.Result evaluate(FSM param1FSM, Input param1Input) { return this.guard.evaluate(param1FSM, param1Input).complement(); }
  }
  
  public static final class Result {
    private String name;
    
    public static final Result ENABLED = new Result("ENABLED");
    
    public static final Result DISABLED = new Result("DISABLED");
    
    public static final Result DEFERED = new Result("DEFERED");
    
    private Result(String param1String) { this.name = param1String; }
    
    public static Result convert(boolean param1Boolean) { return param1Boolean ? ENABLED : DISABLED; }
    
    public Result complement() { return (this == ENABLED) ? DISABLED : ((this == DISABLED) ? ENABLED : DEFERED); }
    
    public String toString() { return "Guard.Result[" + this.name + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\orbutil\fsm\Guard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */