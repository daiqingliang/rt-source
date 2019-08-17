package sun.tracing;

class NullProbe extends ProbeSkeleton {
  public NullProbe(Class<?>[] paramArrayOfClass) { super(paramArrayOfClass); }
  
  public boolean isEnabled() { return false; }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\NullProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */