package com.sun.corba.se.spi.ior;

import org.omg.IOP.TaggedProfile;

public interface TaggedProfile extends Identifiable, MakeImmutable {
  TaggedProfileTemplate getTaggedProfileTemplate();
  
  ObjectId getObjectId();
  
  ObjectKeyTemplate getObjectKeyTemplate();
  
  ObjectKey getObjectKey();
  
  boolean isEquivalent(TaggedProfile paramTaggedProfile);
  
  TaggedProfile getIOPProfile();
  
  boolean isLocal();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\TaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */