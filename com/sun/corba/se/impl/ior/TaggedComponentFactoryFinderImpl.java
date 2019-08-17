package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.TaggedComponent;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedComponentHelper;
import sun.corba.OutputStreamFactory;

public class TaggedComponentFactoryFinderImpl extends IdentifiableFactoryFinderBase implements TaggedComponentFactoryFinder {
  public TaggedComponentFactoryFinderImpl(ORB paramORB) { super(paramORB); }
  
  public Identifiable handleMissingFactory(int paramInt, InputStream paramInputStream) { return new GenericTaggedComponent(paramInt, paramInputStream); }
  
  public TaggedComponent create(ORB paramORB, TaggedComponent paramTaggedComponent) {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramORB);
    TaggedComponentHelper.write(encapsOutputStream, paramTaggedComponent);
    InputStream inputStream = (InputStream)encapsOutputStream.create_input_stream();
    inputStream.read_ulong();
    return (TaggedComponent)create(paramTaggedComponent.tag, inputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\TaggedComponentFactoryFinderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */