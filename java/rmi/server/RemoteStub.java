package java.rmi.server;

@Deprecated
public abstract class RemoteStub extends RemoteObject {
  private static final long serialVersionUID = -1585587260594494182L;
  
  protected RemoteStub() {}
  
  protected RemoteStub(RemoteRef paramRemoteRef) { super(paramRemoteRef); }
  
  @Deprecated
  protected static void setRef(RemoteStub paramRemoteStub, RemoteRef paramRemoteRef) { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RemoteStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */