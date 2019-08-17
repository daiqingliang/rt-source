package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.impl.ior.IORTemplateImpl;
import com.sun.corba.se.impl.ior.IORTemplateListImpl;
import com.sun.corba.se.impl.ior.ObjectIdImpl;
import com.sun.corba.se.impl.ior.ObjectKeyFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectKeyImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceFactoryImpl;
import com.sun.corba.se.impl.ior.ObjectReferenceProducerBase;
import com.sun.corba.se.impl.ior.ObjectReferenceTemplateImpl;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public class IORFactories {
  public static ObjectId makeObjectId(byte[] paramArrayOfByte) { return new ObjectIdImpl(paramArrayOfByte); }
  
  public static ObjectKey makeObjectKey(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId) { return new ObjectKeyImpl(paramObjectKeyTemplate, paramObjectId); }
  
  public static IOR makeIOR(ORB paramORB, String paramString) { return new IORImpl(paramORB, paramString); }
  
  public static IOR makeIOR(ORB paramORB) { return new IORImpl(paramORB); }
  
  public static IOR makeIOR(InputStream paramInputStream) { return new IORImpl(paramInputStream); }
  
  public static IORTemplate makeIORTemplate(ObjectKeyTemplate paramObjectKeyTemplate) { return new IORTemplateImpl(paramObjectKeyTemplate); }
  
  public static IORTemplate makeIORTemplate(InputStream paramInputStream) { return new IORTemplateImpl(paramInputStream); }
  
  public static IORTemplateList makeIORTemplateList() { return new IORTemplateListImpl(); }
  
  public static IORTemplateList makeIORTemplateList(InputStream paramInputStream) { return new IORTemplateListImpl(paramInputStream); }
  
  public static IORFactory getIORFactory(ObjectReferenceTemplate paramObjectReferenceTemplate) {
    if (paramObjectReferenceTemplate instanceof ObjectReferenceTemplateImpl) {
      ObjectReferenceTemplateImpl objectReferenceTemplateImpl = (ObjectReferenceTemplateImpl)paramObjectReferenceTemplate;
      return objectReferenceTemplateImpl.getIORFactory();
    } 
    throw new BAD_PARAM();
  }
  
  public static IORTemplateList getIORTemplateList(ObjectReferenceFactory paramObjectReferenceFactory) {
    if (paramObjectReferenceFactory instanceof ObjectReferenceProducerBase) {
      ObjectReferenceProducerBase objectReferenceProducerBase = (ObjectReferenceProducerBase)paramObjectReferenceFactory;
      return objectReferenceProducerBase.getIORTemplateList();
    } 
    throw new BAD_PARAM();
  }
  
  public static ObjectReferenceTemplate makeObjectReferenceTemplate(ORB paramORB, IORTemplate paramIORTemplate) { return new ObjectReferenceTemplateImpl(paramORB, paramIORTemplate); }
  
  public static ObjectReferenceFactory makeObjectReferenceFactory(ORB paramORB, IORTemplateList paramIORTemplateList) { return new ObjectReferenceFactoryImpl(paramORB, paramIORTemplateList); }
  
  public static ObjectKeyFactory makeObjectKeyFactory(ORB paramORB) { return new ObjectKeyFactoryImpl(paramORB); }
  
  public static IOR getIOR(Object paramObject) { return ORBUtility.getIOR(paramObject); }
  
  public static Object makeObjectReference(IOR paramIOR) { return ORBUtility.makeObjectReference(paramIOR); }
  
  public static void registerValueFactories(ORB paramORB) {
    ValueFactory valueFactory = new ValueFactory() {
        public Serializable read_value(InputStream param1InputStream) { return new ObjectReferenceTemplateImpl(param1InputStream); }
      };
    paramORB.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceTemplateImpl:1.0", valueFactory);
    valueFactory = new ValueFactory() {
        public Serializable read_value(InputStream param1InputStream) { return new ObjectReferenceFactoryImpl(param1InputStream); }
      };
    paramORB.register_value_factory("IDL:com/sun/corba/se/impl/ior/ObjectReferenceFactoryImpl:1.0", valueFactory);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\IORFactories.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */