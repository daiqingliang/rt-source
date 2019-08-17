package com.sun.corba.se.spi.copyobject;

import com.sun.corba.se.impl.copyobject.FallbackObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.JavaStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ORBStreamObjectCopierImpl;
import com.sun.corba.se.impl.copyobject.ReferenceObjectCopierImpl;
import com.sun.corba.se.spi.orb.ORB;

public abstract class CopyobjectDefaults {
  private static final ObjectCopier referenceObjectCopier = new ReferenceObjectCopierImpl();
  
  private static ObjectCopierFactory referenceObjectCopierFactory = new ObjectCopierFactory() {
      public ObjectCopier make() { return referenceObjectCopier; }
    };
  
  public static ObjectCopierFactory makeORBStreamObjectCopierFactory(final ORB orb) { return new ObjectCopierFactory() {
        public ObjectCopier make() { return new ORBStreamObjectCopierImpl(orb); }
      }; }
  
  public static ObjectCopierFactory makeJavaStreamObjectCopierFactory(final ORB orb) { return new ObjectCopierFactory() {
        public ObjectCopier make() { return new JavaStreamObjectCopierImpl(orb); }
      }; }
  
  public static ObjectCopierFactory getReferenceObjectCopierFactory() { return referenceObjectCopierFactory; }
  
  public static ObjectCopierFactory makeFallbackObjectCopierFactory(final ObjectCopierFactory f1, final ObjectCopierFactory f2) { return new ObjectCopierFactory() {
        public ObjectCopier make() {
          ObjectCopier objectCopier1 = f1.make();
          ObjectCopier objectCopier2 = f2.make();
          return new FallbackObjectCopierImpl(objectCopier1, objectCopier2);
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\copyobject\CopyobjectDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */