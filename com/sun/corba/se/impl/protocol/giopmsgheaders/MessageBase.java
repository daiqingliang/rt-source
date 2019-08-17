package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.impl.encoding.ByteBufferWithInfo;
import com.sun.corba.se.impl.encoding.CDRInputStream_1_0;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.AddressingDispositionException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.ReadTimeouts;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Principal;
import org.omg.CORBA.SystemException;
import org.omg.IOP.TaggedProfile;
import sun.corba.SharedSecrets;

public abstract class MessageBase implements Message {
  public byte[] giopHeader;
  
  private ByteBuffer byteBuffer;
  
  private int threadPoolToUse;
  
  byte encodingVersion = 0;
  
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.protocol");
  
  public static String typeToString(int paramInt) { return typeToString((byte)paramInt); }
  
  public static String typeToString(byte paramByte) {
    null = paramByte + "/";
    switch (paramByte) {
      case 0:
        return null + "GIOPRequest";
      case 1:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPReply";
      case 2:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPCancelRequest";
      case 3:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPLocateRequest";
      case 4:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPLocateReply";
      case 5:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPCloseConnection";
      case 6:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPMessageError";
      case 7:
        return SYNTHETIC_LOCAL_VARIABLE_1 + "GIOPFragment";
    } 
    return SYNTHETIC_LOCAL_VARIABLE_1 + "Unknown";
  }
  
  public static MessageBase readGIOPMessage(ORB paramORB, CorbaConnection paramCorbaConnection) { return (MessageBase)(null = readGIOPHeader(paramORB, paramCorbaConnection)).readGIOPBody(paramORB, paramCorbaConnection, null); }
  
  public static MessageBase readGIOPHeader(ORB paramORB, CorbaConnection paramCorbaConnection) {
    FragmentMessage_1_2 fragmentMessage_1_2 = null;
    ReadTimeouts readTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
    ByteBuffer byteBuffer1 = null;
    try {
      byteBuffer1 = paramCorbaConnection.read(12, 0, 12, readTimeouts.get_max_giop_header_time_to_wait());
    } catch (IOException iOException) {
      throw wrapper.ioexceptionWhenReadingConnection(iOException);
    } 
    if (paramORB.giopDebugFlag) {
      dprint(".readGIOPHeader: " + typeToString(byteBuffer1.get(7)));
      dprint(".readGIOPHeader: GIOP header is: ");
      ByteBuffer byteBuffer2 = byteBuffer1.asReadOnlyBuffer();
      byteBuffer2.position(0).limit(12);
      ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(paramORB, byteBuffer2);
      byteBufferWithInfo.buflen = 12;
      CDRInputStream_1_0.printBuffer(byteBufferWithInfo);
    } 
    byte b1 = byteBuffer1.get(0) << 24 & 0xFF000000;
    byte b2 = byteBuffer1.get(1) << 16 & 0xFF0000;
    byte b3 = byteBuffer1.get(2) << 8 & 0xFF00;
    byte b4 = byteBuffer1.get(3) << 0 & 0xFF;
    byte b5 = b1 | b2 | b3 | b4;
    if (b5 != 1195986768)
      throw wrapper.giopMagicError(CompletionStatus.COMPLETED_MAYBE); 
    byte b6 = 0;
    if (byteBuffer1.get(4) == 13 && byteBuffer1.get(5) <= 1 && byteBuffer1.get(5) > 0 && paramORB.getORBData().isJavaSerializationEnabled()) {
      b6 = byteBuffer1.get(5);
      byteBuffer1.put(4, (byte)1);
      byteBuffer1.put(5, (byte)2);
    } 
    GIOPVersion gIOPVersion = paramORB.getORBData().getGIOPVersion();
    if (paramORB.giopDebugFlag) {
      dprint(".readGIOPHeader: Message GIOP version: " + byteBuffer1.get(4) + '.' + byteBuffer1.get(5));
      dprint(".readGIOPHeader: ORB Max GIOP Version: " + gIOPVersion);
    } 
    if ((byteBuffer1.get(4) > gIOPVersion.getMajor() || (byteBuffer1.get(4) == gIOPVersion.getMajor() && byteBuffer1.get(5) > gIOPVersion.getMinor())) && byteBuffer1.get(7) != 6)
      throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE); 
    AreFragmentsAllowed(byteBuffer1.get(4), byteBuffer1.get(5), byteBuffer1.get(6), byteBuffer1.get(7));
    switch (byteBuffer1.get(7)) {
      case 0:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating RequestMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          fragmentMessage_1_2 = new RequestMessage_1_0(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          RequestMessage_1_1 requestMessage_1_1 = new RequestMessage_1_1(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          RequestMessage_1_2 requestMessage_1_2 = new RequestMessage_1_2(paramORB);
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 3:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating LocateRequestMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          LocateRequestMessage_1_0 locateRequestMessage_1_0 = new LocateRequestMessage_1_0(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          LocateRequestMessage_1_1 locateRequestMessage_1_1 = new LocateRequestMessage_1_1(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          LocateRequestMessage_1_2 locateRequestMessage_1_2 = new LocateRequestMessage_1_2(paramORB);
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 2:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating CancelRequestMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          CancelRequestMessage_1_0 cancelRequestMessage_1_0 = new CancelRequestMessage_1_0();
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          CancelRequestMessage_1_1 cancelRequestMessage_1_1 = new CancelRequestMessage_1_1();
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          CancelRequestMessage_1_2 cancelRequestMessage_1_2 = new CancelRequestMessage_1_2();
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 1:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating ReplyMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          ReplyMessage_1_0 replyMessage_1_0 = new ReplyMessage_1_0(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          ReplyMessage_1_1 replyMessage_1_1 = new ReplyMessage_1_1(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          ReplyMessage_1_2 replyMessage_1_2 = new ReplyMessage_1_2(paramORB);
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 4:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating LocateReplyMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          LocateReplyMessage_1_0 locateReplyMessage_1_0 = new LocateReplyMessage_1_0(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          LocateReplyMessage_1_1 locateReplyMessage_1_1 = new LocateReplyMessage_1_1(paramORB);
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          LocateReplyMessage_1_2 locateReplyMessage_1_2 = new LocateReplyMessage_1_2(paramORB);
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 5:
      case 6:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating Message for CloseConnection or MessageError"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
          Message_1_0 message_1_0 = new Message_1_0();
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          Message_1_1 message_1_1 = new Message_1_1();
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          Message_1_1 message_1_1 = new Message_1_1();
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      case 7:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: creating FragmentMessage"); 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0)
          break; 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 1) {
          FragmentMessage_1_1 fragmentMessage_1_1 = new FragmentMessage_1_1();
          break;
        } 
        if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 2) {
          fragmentMessage_1_2 = new FragmentMessage_1_2();
          break;
        } 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
      default:
        if (paramORB.giopDebugFlag)
          dprint(".readGIOPHeader: UNKNOWN MESSAGE TYPE: " + byteBuffer1.get(7)); 
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
    } 
    if (byteBuffer1.get(4) == 1 && byteBuffer1.get(5) == 0) {
      Message_1_0 message_1_0 = (Message_1_0)fragmentMessage_1_2;
      message_1_0.magic = b5;
      message_1_0.GIOP_version = new GIOPVersion(byteBuffer1.get(4), byteBuffer1.get(5));
      message_1_0.byte_order = (byteBuffer1.get(6) == 1);
      fragmentMessage_1_2.threadPoolToUse = 0;
      message_1_0.message_type = byteBuffer1.get(7);
      message_1_0.message_size = readSize(byteBuffer1.get(8), byteBuffer1.get(9), byteBuffer1.get(10), byteBuffer1.get(11), message_1_0.isLittleEndian()) + 12;
    } else {
      Message_1_1 message_1_1 = (Message_1_1)fragmentMessage_1_2;
      message_1_1.magic = b5;
      message_1_1.GIOP_version = new GIOPVersion(byteBuffer1.get(4), byteBuffer1.get(5));
      message_1_1.flags = (byte)(byteBuffer1.get(6) & 0x3);
      fragmentMessage_1_2.threadPoolToUse = byteBuffer1.get(6) >>> 2 & 0x3F;
      message_1_1.message_type = byteBuffer1.get(7);
      message_1_1.message_size = readSize(byteBuffer1.get(8), byteBuffer1.get(9), byteBuffer1.get(10), byteBuffer1.get(11), message_1_1.isLittleEndian()) + 12;
    } 
    if (paramORB.giopDebugFlag) {
      dprint(".readGIOPHeader: header construction complete.");
      ByteBuffer byteBuffer2 = byteBuffer1.asReadOnlyBuffer();
      byte[] arrayOfByte = new byte[12];
      byteBuffer2.position(0).limit(12);
      byteBuffer2.get(arrayOfByte, 0, arrayOfByte.length);
      fragmentMessage_1_2.giopHeader = arrayOfByte;
    } 
    fragmentMessage_1_2.setByteBuffer(byteBuffer1);
    fragmentMessage_1_2.setEncodingVersion(b6);
    return fragmentMessage_1_2;
  }
  
  public static Message readGIOPBody(ORB paramORB, CorbaConnection paramCorbaConnection, Message paramMessage) {
    ReadTimeouts readTimeouts = paramORB.getORBData().getTransportTCPReadTimeouts();
    ByteBuffer byteBuffer1 = paramMessage.getByteBuffer();
    byteBuffer1.position(12);
    int i = paramMessage.getSize() - 12;
    try {
      byteBuffer1 = paramCorbaConnection.read(byteBuffer1, 12, i, readTimeouts.get_max_time_to_wait());
    } catch (IOException iOException) {
      throw wrapper.ioexceptionWhenReadingConnection(iOException);
    } 
    paramMessage.setByteBuffer(byteBuffer1);
    if (paramORB.giopDebugFlag) {
      dprint(".readGIOPBody: received message:");
      ByteBuffer byteBuffer2 = byteBuffer1.asReadOnlyBuffer();
      byteBuffer2.position(0).limit(paramMessage.getSize());
      ByteBufferWithInfo byteBufferWithInfo = new ByteBufferWithInfo(paramORB, byteBuffer2);
      CDRInputStream_1_0.printBuffer(byteBufferWithInfo);
    } 
    return paramMessage;
  }
  
  private static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, byte[] paramArrayOfByte, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new RequestMessage_1_0(paramORB, paramServiceContexts, paramInt, paramBoolean, paramArrayOfByte, paramString, paramPrincipal); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new RequestMessage_1_1(paramORB, paramServiceContexts, paramInt, paramBoolean, new byte[] { 0, 0, 0 }, paramArrayOfByte, paramString, paramPrincipal); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      byte b = 3;
      if (paramBoolean) {
        b = 3;
      } else {
        b = 0;
      } 
      TargetAddress targetAddress = new TargetAddress();
      targetAddress.object_key(paramArrayOfByte);
      RequestMessage_1_2 requestMessage_1_2 = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, targetAddress, paramString, paramServiceContexts);
      requestMessage_1_2.setEncodingVersion(paramByte);
      return requestMessage_1_2;
    } 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static RequestMessage createRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, boolean paramBoolean, IOR paramIOR, short paramShort, String paramString, ServiceContexts paramServiceContexts, Principal paramPrincipal) {
    RequestMessage requestMessage = null;
    IIOPProfile iIOPProfile = paramIOR.getProfile();
    if (paramShort == 0) {
      iIOPProfile = paramIOR.getProfile();
      ObjectKey objectKey = iIOPProfile.getObjectKey();
      byte[] arrayOfByte = objectKey.getBytes(paramORB);
      requestMessage = createRequest(paramORB, paramGIOPVersion, paramByte, paramInt, paramBoolean, arrayOfByte, paramString, paramServiceContexts, paramPrincipal);
    } else {
      if (!paramGIOPVersion.equals(GIOPVersion.V1_2))
        throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE); 
      byte b = 3;
      if (paramBoolean) {
        b = 3;
      } else {
        b = 0;
      } 
      TargetAddress targetAddress = new TargetAddress();
      if (paramShort == 1) {
        iIOPProfile = paramIOR.getProfile();
        targetAddress.profile(iIOPProfile.getIOPProfile());
      } else if (paramShort == 2) {
        IORAddressingInfo iORAddressingInfo = new IORAddressingInfo(0, paramIOR.getIOPIOR());
        targetAddress.ior(iORAddressingInfo);
      } else {
        throw wrapper.illegalTargetAddressDisposition(CompletionStatus.COMPLETED_NO);
      } 
      requestMessage = new RequestMessage_1_2(paramORB, paramInt, b, new byte[] { 0, 0, 0 }, targetAddress, paramString, paramServiceContexts);
      requestMessage.setEncodingVersion(paramByte);
    } 
    if (paramGIOPVersion.supportsIORIIOPProfileComponents()) {
      int i = 0;
      IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)iIOPProfile.getTaggedProfileTemplate();
      Iterator iterator = iIOPProfileTemplate.iteratorById(1398099457);
      if (iterator.hasNext())
        i = ((RequestPartitioningComponent)iterator.next()).getRequestPartitioningId(); 
      if (i < 0 || i > 63)
        throw wrapper.invalidRequestPartitioningId(new Integer(i), new Integer(0), new Integer(63)); 
      requestMessage.setThreadPoolToUse(i);
    } 
    return requestMessage;
  }
  
  public static ReplyMessage createReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, ServiceContexts paramServiceContexts, IOR paramIOR) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new ReplyMessage_1_0(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new ReplyMessage_1_1(paramORB, paramServiceContexts, paramInt1, paramInt2, paramIOR); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      ReplyMessage_1_2 replyMessage_1_2 = new ReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramServiceContexts, paramIOR);
      replyMessage_1_2.setEncodingVersion(paramByte);
      return replyMessage_1_2;
    } 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static LocateRequestMessage createLocateRequest(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt, byte[] paramArrayOfByte) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new LocateRequestMessage_1_0(paramORB, paramInt, paramArrayOfByte); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new LocateRequestMessage_1_1(paramORB, paramInt, paramArrayOfByte); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      TargetAddress targetAddress = new TargetAddress();
      targetAddress.object_key(paramArrayOfByte);
      LocateRequestMessage_1_2 locateRequestMessage_1_2 = new LocateRequestMessage_1_2(paramORB, paramInt, targetAddress);
      locateRequestMessage_1_2.setEncodingVersion(paramByte);
      return locateRequestMessage_1_2;
    } 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static LocateReplyMessage createLocateReply(ORB paramORB, GIOPVersion paramGIOPVersion, byte paramByte, int paramInt1, int paramInt2, IOR paramIOR) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new LocateReplyMessage_1_0(paramORB, paramInt1, paramInt2, paramIOR); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new LocateReplyMessage_1_1(paramORB, paramInt1, paramInt2, paramIOR); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2)) {
      LocateReplyMessage_1_2 locateReplyMessage_1_2 = new LocateReplyMessage_1_2(paramORB, paramInt1, paramInt2, paramIOR);
      locateReplyMessage_1_2.setEncodingVersion(paramByte);
      return locateReplyMessage_1_2;
    } 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static CancelRequestMessage createCancelRequest(GIOPVersion paramGIOPVersion, int paramInt) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new CancelRequestMessage_1_0(paramInt); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new CancelRequestMessage_1_1(paramInt); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
      return new CancelRequestMessage_1_2(paramInt); 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static Message createCloseConnection(GIOPVersion paramGIOPVersion) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new Message_1_0(1195986768, false, (byte)5, 0); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)5, 0); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
      return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)5, 0); 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static Message createMessageError(GIOPVersion paramGIOPVersion) {
    if (paramGIOPVersion.equals(GIOPVersion.V1_0))
      return new Message_1_0(1195986768, false, (byte)6, 0); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_1))
      return new Message_1_1(1195986768, GIOPVersion.V1_1, (byte)0, (byte)6, 0); 
    if (paramGIOPVersion.equals(GIOPVersion.V1_2))
      return new Message_1_1(1195986768, GIOPVersion.V1_2, (byte)0, (byte)6, 0); 
    throw wrapper.giopVersionError(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static FragmentMessage createFragmentMessage(GIOPVersion paramGIOPVersion) { return null; }
  
  public static int getRequestId(Message paramMessage) {
    switch (paramMessage.getType()) {
      case 0:
        return ((RequestMessage)paramMessage).getRequestId();
      case 1:
        return ((ReplyMessage)paramMessage).getRequestId();
      case 3:
        return ((LocateRequestMessage)paramMessage).getRequestId();
      case 4:
        return ((LocateReplyMessage)paramMessage).getRequestId();
      case 2:
        return ((CancelRequestMessage)paramMessage).getRequestId();
      case 7:
        return ((FragmentMessage)paramMessage).getRequestId();
    } 
    throw wrapper.illegalGiopMsgType(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public static void setFlag(ByteBuffer paramByteBuffer, int paramInt) {
    byte b = paramByteBuffer.get(6);
    b = (byte)(b | paramInt);
    paramByteBuffer.put(6, b);
  }
  
  public static void clearFlag(byte[] paramArrayOfByte, int paramInt) { paramArrayOfByte[6] = (byte)(paramArrayOfByte[6] & (0xFF ^ paramInt)); }
  
  private static void AreFragmentsAllowed(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4) {
    if (paramByte1 == 1 && paramByte2 == 0 && paramByte4 == 7)
      throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE); 
    if ((paramByte3 & 0x2) == 2)
      switch (paramByte4) {
        case 2:
        case 5:
        case 6:
          throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE);
        case 3:
        case 4:
          if (paramByte1 == 1 && paramByte2 == 1)
            throw wrapper.fragmentationDisallowed(CompletionStatus.COMPLETED_MAYBE); 
          break;
      }  
  }
  
  static ObjectKey extractObjectKey(byte[] paramArrayOfByte, ORB paramORB) {
    try {
      if (paramArrayOfByte != null) {
        ObjectKey objectKey = paramORB.getObjectKeyFactory().create(paramArrayOfByte);
        if (objectKey != null)
          return objectKey; 
      } 
    } catch (Exception exception) {}
    throw wrapper.invalidObjectKey();
  }
  
  static ObjectKey extractObjectKey(TargetAddress paramTargetAddress, ORB paramORB) {
    short s1 = paramORB.getORBData().getGIOPTargetAddressPreference();
    short s2 = paramTargetAddress.discriminator();
    switch (s1) {
      case 0:
        if (s2 != 0)
          throw new AddressingDispositionException((short)0); 
        break;
      case 1:
        if (s2 != 1)
          throw new AddressingDispositionException((short)1); 
        break;
      case 2:
        if (s2 != 2)
          throw new AddressingDispositionException((short)2); 
        break;
      case 3:
        break;
      default:
        throw wrapper.orbTargetAddrPreferenceInExtractObjectkeyInvalid();
    } 
    try {
      IORAddressingInfo iORAddressingInfo;
      TaggedProfile taggedProfile;
      IIOPProfile iIOPProfile;
      byte[] arrayOfByte;
      switch (s2) {
        case 0:
          arrayOfByte = paramTargetAddress.object_key();
          if (arrayOfByte != null) {
            ObjectKey objectKey = paramORB.getObjectKeyFactory().create(arrayOfByte);
            if (objectKey != null)
              return objectKey; 
          } 
          break;
        case 1:
          iIOPProfile = null;
          taggedProfile = paramTargetAddress.profile();
          if (taggedProfile != null) {
            iIOPProfile = IIOPFactories.makeIIOPProfile(paramORB, taggedProfile);
            ObjectKey objectKey = iIOPProfile.getObjectKey();
            if (objectKey != null)
              return objectKey; 
          } 
          break;
        case 2:
          iORAddressingInfo = paramTargetAddress.ior();
          if (iORAddressingInfo != null) {
            taggedProfile = iORAddressingInfo.ior.profiles[iORAddressingInfo.selected_profile_index];
            iIOPProfile = IIOPFactories.makeIIOPProfile(paramORB, taggedProfile);
            ObjectKey objectKey = iIOPProfile.getObjectKey();
            if (objectKey != null)
              return objectKey; 
          } 
          break;
      } 
    } catch (Exception exception) {}
    throw wrapper.invalidObjectKey();
  }
  
  private static int readSize(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, boolean paramBoolean) {
    byte b4;
    byte b3;
    byte b2;
    byte b1;
    if (!paramBoolean) {
      b1 = paramByte1 << 24 & 0xFF000000;
      b2 = paramByte2 << 16 & 0xFF0000;
      b3 = paramByte3 << 8 & 0xFF00;
      b4 = paramByte4 << 0 & 0xFF;
    } else {
      b1 = paramByte4 << 24 & 0xFF000000;
      b2 = paramByte3 << 16 & 0xFF0000;
      b3 = paramByte2 << 8 & 0xFF00;
      b4 = paramByte1 << 0 & 0xFF;
    } 
    return b1 | b2 | b3 | b4;
  }
  
  static void nullCheck(Object paramObject) {
    if (paramObject == null)
      throw wrapper.nullNotAllowed(); 
  }
  
  static SystemException getSystemException(String paramString1, int paramInt, CompletionStatus paramCompletionStatus, String paramString2, ORBUtilSystemException paramORBUtilSystemException) {
    SystemException systemException = null;
    try {
      Class clazz = SharedSecrets.getJavaCorbaAccess().loadClass(paramString1);
      if (paramString2 == null) {
        systemException = (SystemException)clazz.newInstance();
      } else {
        Class[] arrayOfClass = { String.class };
        Constructor constructor = clazz.getConstructor(arrayOfClass);
        Object[] arrayOfObject = { paramString2 };
        systemException = (SystemException)constructor.newInstance(arrayOfObject);
      } 
    } catch (Exception exception) {
      throw paramORBUtilSystemException.badSystemExceptionInReply(CompletionStatus.COMPLETED_MAYBE, exception);
    } 
    systemException.minor = paramInt;
    systemException.completed = paramCompletionStatus;
    return systemException;
  }
  
  public void callback(MessageHandler paramMessageHandler) throws IOException { paramMessageHandler.handleInput(this); }
  
  public ByteBuffer getByteBuffer() { return this.byteBuffer; }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer) { this.byteBuffer = paramByteBuffer; }
  
  public int getThreadPoolToUse() { return this.threadPoolToUse; }
  
  public byte getEncodingVersion() { return this.encodingVersion; }
  
  public void setEncodingVersion(byte paramByte) { this.encodingVersion = paramByte; }
  
  private static void dprint(String paramString) { ORBUtility.dprint("MessageBase", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\MessageBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */