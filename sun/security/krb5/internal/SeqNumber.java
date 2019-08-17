package sun.security.krb5.internal;

public interface SeqNumber {
  void randInit();
  
  void init(int paramInt);
  
  int current();
  
  int next();
  
  int step();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\SeqNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */