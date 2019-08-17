package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.OutputStreamFactory;

public class GenericTaggedProfile extends GenericIdentifiable implements TaggedProfile {
  private ORB orb;
  
  public GenericTaggedProfile(int paramInt, InputStream paramInputStream) {
    super(paramInt, paramInputStream);
    this.orb = (ORB)paramInputStream.orb();
  }
  
  public GenericTaggedProfile(ORB paramORB, int paramInt, byte[] paramArrayOfByte) {
    super(paramInt, paramArrayOfByte);
    this.orb = paramORB;
  }
  
  public TaggedProfileTemplate getTaggedProfileTemplate() { return null; }
  
  public ObjectId getObjectId() { return null; }
  
  public ObjectKeyTemplate getObjectKeyTemplate() { return null; }
  
  public ObjectKey getObjectKey() { return null; }
  
  public boolean isEquivalent(TaggedProfile paramTaggedProfile) { return equals(paramTaggedProfile); }
  
  public void makeImmutable() {}
  
  public boolean isLocal() { return false; }
  
  public TaggedProfile getIOPProfile() {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream(this.orb);
    write(encapsOutputStream);
    InputStream inputStream = (InputStream)encapsOutputStream.create_input_stream();
    return TaggedProfileHelper.read(inputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\GenericTaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */