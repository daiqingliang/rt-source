package sun.rmi.transport;

import java.rmi.server.ObjID;

class ObjectEndpoint {
  private final ObjID id;
  
  private final Transport transport;
  
  ObjectEndpoint(ObjID paramObjID, Transport paramTransport) {
    if (paramObjID == null)
      throw new NullPointerException(); 
    assert paramTransport != null || paramObjID.equals(new ObjID(2));
    this.id = paramObjID;
    this.transport = paramTransport;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ObjectEndpoint) {
      ObjectEndpoint objectEndpoint = (ObjectEndpoint)paramObject;
      return (this.id.equals(objectEndpoint.id) && this.transport == objectEndpoint.transport);
    } 
    return false;
  }
  
  public int hashCode() { return this.id.hashCode() ^ ((this.transport != null) ? this.transport.hashCode() : 0); }
  
  public String toString() { return this.id.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\ObjectEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */