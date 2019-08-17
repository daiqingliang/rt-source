package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class DefinitionKind implements IDLEntity {
  public static final int _dk_none = 0;
  
  public static final int _dk_all = 1;
  
  public static final int _dk_Attribute = 2;
  
  public static final int _dk_Constant = 3;
  
  public static final int _dk_Exception = 4;
  
  public static final int _dk_Interface = 5;
  
  public static final int _dk_Module = 6;
  
  public static final int _dk_Operation = 7;
  
  public static final int _dk_Typedef = 8;
  
  public static final int _dk_Alias = 9;
  
  public static final int _dk_Struct = 10;
  
  public static final int _dk_Union = 11;
  
  public static final int _dk_Enum = 12;
  
  public static final int _dk_Primitive = 13;
  
  public static final int _dk_String = 14;
  
  public static final int _dk_Sequence = 15;
  
  public static final int _dk_Array = 16;
  
  public static final int _dk_Repository = 17;
  
  public static final int _dk_Wstring = 18;
  
  public static final int _dk_Fixed = 19;
  
  public static final int _dk_Value = 20;
  
  public static final int _dk_ValueBox = 21;
  
  public static final int _dk_ValueMember = 22;
  
  public static final int _dk_Native = 23;
  
  public static final int _dk_AbstractInterface = 24;
  
  public static final DefinitionKind dk_none = new DefinitionKind(0);
  
  public static final DefinitionKind dk_all = new DefinitionKind(1);
  
  public static final DefinitionKind dk_Attribute = new DefinitionKind(2);
  
  public static final DefinitionKind dk_Constant = new DefinitionKind(3);
  
  public static final DefinitionKind dk_Exception = new DefinitionKind(4);
  
  public static final DefinitionKind dk_Interface = new DefinitionKind(5);
  
  public static final DefinitionKind dk_Module = new DefinitionKind(6);
  
  public static final DefinitionKind dk_Operation = new DefinitionKind(7);
  
  public static final DefinitionKind dk_Typedef = new DefinitionKind(8);
  
  public static final DefinitionKind dk_Alias = new DefinitionKind(9);
  
  public static final DefinitionKind dk_Struct = new DefinitionKind(10);
  
  public static final DefinitionKind dk_Union = new DefinitionKind(11);
  
  public static final DefinitionKind dk_Enum = new DefinitionKind(12);
  
  public static final DefinitionKind dk_Primitive = new DefinitionKind(13);
  
  public static final DefinitionKind dk_String = new DefinitionKind(14);
  
  public static final DefinitionKind dk_Sequence = new DefinitionKind(15);
  
  public static final DefinitionKind dk_Array = new DefinitionKind(16);
  
  public static final DefinitionKind dk_Repository = new DefinitionKind(17);
  
  public static final DefinitionKind dk_Wstring = new DefinitionKind(18);
  
  public static final DefinitionKind dk_Fixed = new DefinitionKind(19);
  
  public static final DefinitionKind dk_Value = new DefinitionKind(20);
  
  public static final DefinitionKind dk_ValueBox = new DefinitionKind(21);
  
  public static final DefinitionKind dk_ValueMember = new DefinitionKind(22);
  
  public static final DefinitionKind dk_Native = new DefinitionKind(23);
  
  public static final DefinitionKind dk_AbstractInterface = new DefinitionKind(24);
  
  private int _value;
  
  public int value() { return this._value; }
  
  public static DefinitionKind from_int(int paramInt) {
    switch (paramInt) {
      case 0:
        return dk_none;
      case 1:
        return dk_all;
      case 2:
        return dk_Attribute;
      case 3:
        return dk_Constant;
      case 4:
        return dk_Exception;
      case 5:
        return dk_Interface;
      case 6:
        return dk_Module;
      case 7:
        return dk_Operation;
      case 8:
        return dk_Typedef;
      case 9:
        return dk_Alias;
      case 10:
        return dk_Struct;
      case 11:
        return dk_Union;
      case 12:
        return dk_Enum;
      case 13:
        return dk_Primitive;
      case 14:
        return dk_String;
      case 15:
        return dk_Sequence;
      case 16:
        return dk_Array;
      case 17:
        return dk_Repository;
      case 18:
        return dk_Wstring;
      case 19:
        return dk_Fixed;
      case 20:
        return dk_Value;
      case 21:
        return dk_ValueBox;
      case 22:
        return dk_ValueMember;
      case 23:
        return dk_Native;
    } 
    throw new BAD_PARAM();
  }
  
  protected DefinitionKind(int paramInt) { this._value = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\DefinitionKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */