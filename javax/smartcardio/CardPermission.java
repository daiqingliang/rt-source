package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permission;

public class CardPermission extends Permission {
  private static final long serialVersionUID = 7146787880530705613L;
  
  private static final int A_CONNECT = 1;
  
  private static final int A_EXCLUSIVE = 2;
  
  private static final int A_GET_BASIC_CHANNEL = 4;
  
  private static final int A_OPEN_LOGICAL_CHANNEL = 8;
  
  private static final int A_RESET = 16;
  
  private static final int A_TRANSMIT_CONTROL = 32;
  
  private static final int A_ALL = 63;
  
  private static final int[] ARRAY_MASKS = { 63, 1, 2, 4, 8, 16, 32 };
  
  private static final String S_CONNECT = "connect";
  
  private static final String S_EXCLUSIVE = "exclusive";
  
  private static final String S_GET_BASIC_CHANNEL = "getBasicChannel";
  
  private static final String S_OPEN_LOGICAL_CHANNEL = "openLogicalChannel";
  
  private static final String S_RESET = "reset";
  
  private static final String S_TRANSMIT_CONTROL = "transmitControl";
  
  private static final String S_ALL = "*";
  
  private static final String[] ARRAY_STRINGS = { "*", "connect", "exclusive", "getBasicChannel", "openLogicalChannel", "reset", "transmitControl" };
  
  private int mask;
  
  public CardPermission(String paramString1, String paramString2) {
    super(paramString1);
    if (paramString1 == null)
      throw new NullPointerException(); 
    this.mask = getMask(paramString2);
  }
  
  private static int getMask(String paramString) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("actions must not be empty"); 
    int i;
    for (i = 0; i < ARRAY_STRINGS.length; i++) {
      if (paramString == ARRAY_STRINGS[i])
        return ARRAY_MASKS[i]; 
    } 
    if (paramString.endsWith(","))
      throw new IllegalArgumentException("Invalid actions: '" + paramString + "'"); 
    i = 0;
    String[] arrayOfString = paramString.split(",");
    for (String str : arrayOfString) {
      byte b = 0;
      while (true) {
        if (b < ARRAY_STRINGS.length) {
          if (ARRAY_STRINGS[b].equalsIgnoreCase(str)) {
            i |= ARRAY_MASKS[b];
            break;
          } 
          b++;
          continue;
        } 
        throw new IllegalArgumentException("Invalid action: '" + str + "'");
      } 
    } 
    return i;
  }
  
  private static String getActions(int paramInt) {
    if (paramInt == 63)
      return "*"; 
    boolean bool = true;
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < ARRAY_MASKS.length; b++) {
      int i = ARRAY_MASKS[b];
      if ((paramInt & i) == i) {
        if (!bool) {
          stringBuilder.append(",");
        } else {
          bool = false;
        } 
        stringBuilder.append(ARRAY_STRINGS[b]);
      } 
    } 
    return stringBuilder.toString();
  }
  
  public String getActions() {
    if (this.actions == null)
      this.actions = getActions(this.mask); 
    return this.actions;
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof CardPermission))
      return false; 
    CardPermission cardPermission = (CardPermission)paramPermission;
    if ((this.mask & cardPermission.mask) != cardPermission.mask)
      return false; 
    String str = getName();
    return str.equals("*") ? true : (str.equals(cardPermission.getName()));
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CardPermission))
      return false; 
    CardPermission cardPermission = (CardPermission)paramObject;
    return (getName().equals(cardPermission.getName()) && this.mask == cardPermission.mask);
  }
  
  public int hashCode() { return getName().hashCode() + 31 * this.mask; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.actions == null)
      getActions(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.mask = getMask(this.actions);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\smartcardio\CardPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */