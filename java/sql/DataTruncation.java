package java.sql;

public class DataTruncation extends SQLWarning {
  private int index;
  
  private boolean parameter;
  
  private boolean read;
  
  private int dataSize;
  
  private int transferSize;
  
  private static final long serialVersionUID = 6464298989504059473L;
  
  public DataTruncation(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3) {
    super("Data truncation", (paramBoolean2 == true) ? "01004" : "22001");
    this.index = paramInt1;
    this.parameter = paramBoolean1;
    this.read = paramBoolean2;
    this.dataSize = paramInt2;
    this.transferSize = paramInt3;
  }
  
  public DataTruncation(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3, Throwable paramThrowable) {
    super("Data truncation", (paramBoolean2 == true) ? "01004" : "22001", paramThrowable);
    this.index = paramInt1;
    this.parameter = paramBoolean1;
    this.read = paramBoolean2;
    this.dataSize = paramInt2;
    this.transferSize = paramInt3;
  }
  
  public int getIndex() { return this.index; }
  
  public boolean getParameter() { return this.parameter; }
  
  public boolean getRead() { return this.read; }
  
  public int getDataSize() { return this.dataSize; }
  
  public int getTransferSize() { return this.transferSize; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\DataTruncation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */