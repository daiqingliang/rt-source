package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttributeDescription;
import com.sun.org.omg.CORBA.Initializer;
import com.sun.org.omg.CORBA.OperationDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IDLEntity;

public final class FullValueDescription implements IDLEntity {
  public String name = null;
  
  public String id = null;
  
  public boolean is_abstract = false;
  
  public boolean is_custom = false;
  
  public String defined_in = null;
  
  public String version = null;
  
  public OperationDescription[] operations = null;
  
  public AttributeDescription[] attributes = null;
  
  public ValueMember[] members = null;
  
  public Initializer[] initializers = null;
  
  public String[] supported_interfaces = null;
  
  public String[] abstract_base_values = null;
  
  public boolean is_truncatable = false;
  
  public String base_value = null;
  
  public TypeCode type = null;
  
  public FullValueDescription() {}
  
  public FullValueDescription(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, String paramString3, String paramString4, OperationDescription[] paramArrayOfOperationDescription, AttributeDescription[] paramArrayOfAttributeDescription, ValueMember[] paramArrayOfValueMember, Initializer[] paramArrayOfInitializer, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean3, String paramString5, TypeCode paramTypeCode) {
    this.name = paramString1;
    this.id = paramString2;
    this.is_abstract = paramBoolean1;
    this.is_custom = paramBoolean2;
    this.defined_in = paramString3;
    this.version = paramString4;
    this.operations = paramArrayOfOperationDescription;
    this.attributes = paramArrayOfAttributeDescription;
    this.members = paramArrayOfValueMember;
    this.initializers = paramArrayOfInitializer;
    this.supported_interfaces = paramArrayOfString1;
    this.abstract_base_values = paramArrayOfString2;
    this.is_truncatable = paramBoolean3;
    this.base_value = paramString5;
    this.type = paramTypeCode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\CORBA\ValueDefPackage\FullValueDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */