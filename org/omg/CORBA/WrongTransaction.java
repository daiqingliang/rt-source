package org.omg.CORBA;

public final class WrongTransaction extends UserException {
  public WrongTransaction() { super(WrongTransactionHelper.id()); }
  
  public WrongTransaction(String paramString) { super(WrongTransactionHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\WrongTransaction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */