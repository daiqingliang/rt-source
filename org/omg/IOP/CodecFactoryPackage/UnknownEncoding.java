package org.omg.IOP.CodecFactoryPackage;

import org.omg.CORBA.UserException;

public final class UnknownEncoding extends UserException {
  public UnknownEncoding() { super(UnknownEncodingHelper.id()); }
  
  public UnknownEncoding(String paramString) { super(UnknownEncodingHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\CodecFactoryPackage\UnknownEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */