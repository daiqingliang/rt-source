package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class TransientNameService {
  private Object theInitialNamingContext;
  
  public TransientNameService(ORB paramORB) throws INITIALIZE { initialize(paramORB, "NameService"); }
  
  public TransientNameService(ORB paramORB, String paramString) throws INITIALIZE { initialize(paramORB, paramString); }
  
  private void initialize(ORB paramORB, String paramString) throws INITIALIZE {
    NamingSystemException namingSystemException = NamingSystemException.get(paramORB, "naming");
    try {
      POA pOA1 = (POA)paramORB.resolve_initial_references("RootPOA");
      pOA1.the_POAManager().activate();
      byte b = 0;
      Policy[] arrayOfPolicy = new Policy[3];
      arrayOfPolicy[b++] = pOA1.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
      arrayOfPolicy[b++] = pOA1.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
      arrayOfPolicy[b++] = pOA1.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
      POA pOA2 = pOA1.create_POA("TNameService", null, arrayOfPolicy);
      pOA2.the_POAManager().activate();
      TransientNamingContext transientNamingContext = new TransientNamingContext(paramORB, null, pOA2);
      byte[] arrayOfByte = pOA2.activate_object(transientNamingContext);
      transientNamingContext.localRoot = pOA2.id_to_reference(arrayOfByte);
      this.theInitialNamingContext = transientNamingContext.localRoot;
      paramORB.register_initial_reference(paramString, this.theInitialNamingContext);
    } catch (SystemException systemException) {
      throw namingSystemException.transNsCannotCreateInitialNcSys(systemException);
    } catch (Exception exception) {
      throw namingSystemException.transNsCannotCreateInitialNc(exception);
    } 
  }
  
  public Object initialNamingContext() { return this.theInitialNamingContext; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNameService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */