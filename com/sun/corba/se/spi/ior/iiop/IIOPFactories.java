package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.impl.ior.iiop.AlternateIIOPAddressComponentImpl;
import com.sun.corba.se.impl.ior.iiop.CodeSetsComponentImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPAddressImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileImpl;
import com.sun.corba.se.impl.ior.iiop.IIOPProfileTemplateImpl;
import com.sun.corba.se.impl.ior.iiop.JavaCodebaseComponentImpl;
import com.sun.corba.se.impl.ior.iiop.JavaSerializationComponent;
import com.sun.corba.se.impl.ior.iiop.MaxStreamFormatVersionComponentImpl;
import com.sun.corba.se.impl.ior.iiop.ORBTypeComponentImpl;
import com.sun.corba.se.impl.ior.iiop.RequestPartitioningComponentImpl;
import com.sun.corba.se.spi.ior.EncapsulationFactoryBase;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfile;

public abstract class IIOPFactories {
  public static IdentifiableFactory makeRequestPartitioningComponentFactory() { return new EncapsulationFactoryBase(1398099457) {
        public Identifiable readContents(InputStream param1InputStream) {
          int i = param1InputStream.read_ulong();
          return new RequestPartitioningComponentImpl(i);
        }
      }; }
  
  public static RequestPartitioningComponent makeRequestPartitioningComponent(int paramInt) { return new RequestPartitioningComponentImpl(paramInt); }
  
  public static IdentifiableFactory makeAlternateIIOPAddressComponentFactory() { return new EncapsulationFactoryBase(3) {
        public Identifiable readContents(InputStream param1InputStream) {
          IIOPAddressImpl iIOPAddressImpl = new IIOPAddressImpl(param1InputStream);
          return new AlternateIIOPAddressComponentImpl(iIOPAddressImpl);
        }
      }; }
  
  public static AlternateIIOPAddressComponent makeAlternateIIOPAddressComponent(IIOPAddress paramIIOPAddress) { return new AlternateIIOPAddressComponentImpl(paramIIOPAddress); }
  
  public static IdentifiableFactory makeCodeSetsComponentFactory() { return new EncapsulationFactoryBase(1) {
        public Identifiable readContents(InputStream param1InputStream) { return new CodeSetsComponentImpl(param1InputStream); }
      }; }
  
  public static CodeSetsComponent makeCodeSetsComponent(ORB paramORB) { return new CodeSetsComponentImpl(paramORB); }
  
  public static IdentifiableFactory makeJavaCodebaseComponentFactory() { return new EncapsulationFactoryBase(25) {
        public Identifiable readContents(InputStream param1InputStream) {
          String str = param1InputStream.read_string();
          return new JavaCodebaseComponentImpl(str);
        }
      }; }
  
  public static JavaCodebaseComponent makeJavaCodebaseComponent(String paramString) { return new JavaCodebaseComponentImpl(paramString); }
  
  public static IdentifiableFactory makeORBTypeComponentFactory() { return new EncapsulationFactoryBase(0) {
        public Identifiable readContents(InputStream param1InputStream) {
          int i = param1InputStream.read_ulong();
          return new ORBTypeComponentImpl(i);
        }
      }; }
  
  public static ORBTypeComponent makeORBTypeComponent(int paramInt) { return new ORBTypeComponentImpl(paramInt); }
  
  public static IdentifiableFactory makeMaxStreamFormatVersionComponentFactory() { return new EncapsulationFactoryBase(38) {
        public Identifiable readContents(InputStream param1InputStream) {
          byte b = param1InputStream.read_octet();
          return new MaxStreamFormatVersionComponentImpl(b);
        }
      }; }
  
  public static MaxStreamFormatVersionComponent makeMaxStreamFormatVersionComponent() { return new MaxStreamFormatVersionComponentImpl(); }
  
  public static IdentifiableFactory makeJavaSerializationComponentFactory() { return new EncapsulationFactoryBase(1398099458) {
        public Identifiable readContents(InputStream param1InputStream) {
          byte b = param1InputStream.read_octet();
          return new JavaSerializationComponent(b);
        }
      }; }
  
  public static JavaSerializationComponent makeJavaSerializationComponent() { return JavaSerializationComponent.singleton(); }
  
  public static IdentifiableFactory makeIIOPProfileFactory() { return new EncapsulationFactoryBase(0) {
        public Identifiable readContents(InputStream param1InputStream) { return new IIOPProfileImpl(param1InputStream); }
      }; }
  
  public static IIOPProfile makeIIOPProfile(ORB paramORB, ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId, IIOPProfileTemplate paramIIOPProfileTemplate) { return new IIOPProfileImpl(paramORB, paramObjectKeyTemplate, paramObjectId, paramIIOPProfileTemplate); }
  
  public static IIOPProfile makeIIOPProfile(ORB paramORB, TaggedProfile paramTaggedProfile) { return new IIOPProfileImpl(paramORB, paramTaggedProfile); }
  
  public static IdentifiableFactory makeIIOPProfileTemplateFactory() { return new EncapsulationFactoryBase(0) {
        public Identifiable readContents(InputStream param1InputStream) { return new IIOPProfileTemplateImpl(param1InputStream); }
      }; }
  
  public static IIOPProfileTemplate makeIIOPProfileTemplate(ORB paramORB, GIOPVersion paramGIOPVersion, IIOPAddress paramIIOPAddress) { return new IIOPProfileTemplateImpl(paramORB, paramGIOPVersion, paramIIOPAddress); }
  
  public static IIOPAddress makeIIOPAddress(ORB paramORB, String paramString, int paramInt) { return new IIOPAddressImpl(paramORB, paramString, paramInt); }
  
  public static IIOPAddress makeIIOPAddress(InputStream paramInputStream) { return new IIOPAddressImpl(paramInputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\iiop\IIOPFactories.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */