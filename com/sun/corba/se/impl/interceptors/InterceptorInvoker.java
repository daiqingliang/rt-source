package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public class InterceptorInvoker {
  private ORB orb;
  
  private InterceptorList interceptorList;
  
  private boolean enabled = false;
  
  private PICurrent current;
  
  InterceptorInvoker(ORB paramORB, InterceptorList paramInterceptorList, PICurrent paramPICurrent) {
    this.orb = paramORB;
    this.interceptorList = paramInterceptorList;
    this.enabled = false;
    this.current = paramPICurrent;
  }
  
  void setEnabled(boolean paramBoolean) { this.enabled = paramBoolean; }
  
  void objectAdapterCreated(ObjectAdapter paramObjectAdapter) {
    if (this.enabled) {
      IORInfoImpl iORInfoImpl = new IORInfoImpl(paramObjectAdapter);
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])this.interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      int j;
      for (j = i - 1; j >= 0; j--) {
        IORInterceptor iORInterceptor = arrayOfIORInterceptor[j];
        try {
          iORInterceptor.establish_components(iORInfoImpl);
        } catch (Exception exception) {}
      } 
      iORInfoImpl.makeStateEstablished();
      for (j = i - 1; j >= 0; j--) {
        IORInterceptor iORInterceptor = arrayOfIORInterceptor[j];
        if (iORInterceptor instanceof IORInterceptor_3_0) {
          IORInterceptor_3_0 iORInterceptor_3_0 = (IORInterceptor_3_0)iORInterceptor;
          iORInterceptor_3_0.components_established(iORInfoImpl);
        } 
      } 
      iORInfoImpl.makeStateDone();
    } 
  }
  
  void adapterManagerStateChanged(int paramInt, short paramShort) {
    if (this.enabled) {
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])this.interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      for (int j = i - 1; j >= 0; j--) {
        try {
          IORInterceptor iORInterceptor = arrayOfIORInterceptor[j];
          if (iORInterceptor instanceof IORInterceptor_3_0) {
            IORInterceptor_3_0 iORInterceptor_3_0 = (IORInterceptor_3_0)iORInterceptor;
            iORInterceptor_3_0.adapter_manager_state_changed(paramInt, paramShort);
          } 
        } catch (Exception exception) {}
      } 
    } 
  }
  
  void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort) {
    if (this.enabled) {
      IORInterceptor[] arrayOfIORInterceptor = (IORInterceptor[])this.interceptorList.getInterceptors(2);
      int i = arrayOfIORInterceptor.length;
      for (int j = i - 1; j >= 0; j--) {
        try {
          IORInterceptor iORInterceptor = arrayOfIORInterceptor[j];
          if (iORInterceptor instanceof IORInterceptor_3_0) {
            IORInterceptor_3_0 iORInterceptor_3_0 = (IORInterceptor_3_0)iORInterceptor;
            iORInterceptor_3_0.adapter_state_changed(paramArrayOfObjectReferenceTemplate, paramShort);
          } 
        } catch (Exception exception) {}
      } 
    } 
  }
  
  void invokeClientInterceptorStartingPoint(ClientRequestInfoImpl paramClientRequestInfoImpl) {
    if (this.enabled)
      try {
        this.current.pushSlotTable();
        paramClientRequestInfoImpl.setPICurrentPushed(true);
        paramClientRequestInfoImpl;
        paramClientRequestInfoImpl.setCurrentExecutionPoint(0);
        ClientRequestInterceptor[] arrayOfClientRequestInterceptor = (ClientRequestInterceptor[])this.interceptorList.getInterceptors(0);
        int i = arrayOfClientRequestInterceptor.length;
        int j = i;
        boolean bool = true;
        for (int k = 0; bool && k < i; k++) {
          try {
            arrayOfClientRequestInterceptor[k].send_request(paramClientRequestInfoImpl);
          } catch (ForwardRequest forwardRequest) {
            j = k;
            paramClientRequestInfoImpl.setForwardRequest(forwardRequest);
            paramClientRequestInfoImpl.setEndingPointCall(2);
            paramClientRequestInfoImpl.setReplyStatus((short)3);
            updateClientRequestDispatcherForward(paramClientRequestInfoImpl);
            bool = false;
          } catch (SystemException systemException) {
            j = k;
            paramClientRequestInfoImpl.setEndingPointCall(1);
            paramClientRequestInfoImpl.setReplyStatus((short)1);
            paramClientRequestInfoImpl.setException(systemException);
            bool = false;
          } 
        } 
        paramClientRequestInfoImpl.setFlowStackIndex(j);
      } finally {
        this.current.resetSlotTable();
      }  
  }
  
  void invokeClientInterceptorEndingPoint(ClientRequestInfoImpl paramClientRequestInfoImpl) {
    if (this.enabled)
      try {
        paramClientRequestInfoImpl;
        paramClientRequestInfoImpl.setCurrentExecutionPoint(2);
        ClientRequestInterceptor[] arrayOfClientRequestInterceptor = (ClientRequestInterceptor[])this.interceptorList.getInterceptors(0);
        int i = paramClientRequestInfoImpl.getFlowStackIndex();
        int j = paramClientRequestInfoImpl.getEndingPointCall();
        if (j == 0 && paramClientRequestInfoImpl.getIsOneWay()) {
          j = 2;
          paramClientRequestInfoImpl.setEndingPointCall(j);
        } 
        for (int k = i - 1; k >= 0; k--) {
          try {
            switch (j) {
              case 0:
                arrayOfClientRequestInterceptor[k].receive_reply(paramClientRequestInfoImpl);
                break;
              case 1:
                arrayOfClientRequestInterceptor[k].receive_exception(paramClientRequestInfoImpl);
                break;
              case 2:
                arrayOfClientRequestInterceptor[k].receive_other(paramClientRequestInfoImpl);
                break;
            } 
          } catch (ForwardRequest forwardRequest) {
            j = 2;
            paramClientRequestInfoImpl.setEndingPointCall(j);
            paramClientRequestInfoImpl.setReplyStatus((short)3);
            paramClientRequestInfoImpl.setForwardRequest(forwardRequest);
            updateClientRequestDispatcherForward(paramClientRequestInfoImpl);
          } catch (SystemException systemException) {
            j = 1;
            paramClientRequestInfoImpl.setEndingPointCall(j);
            paramClientRequestInfoImpl.setReplyStatus((short)1);
            paramClientRequestInfoImpl.setException(systemException);
          } 
        } 
      } finally {
        if (paramClientRequestInfoImpl != null && paramClientRequestInfoImpl.isPICurrentPushed())
          this.current.popSlotTable(); 
      }  
  }
  
  void invokeServerInterceptorStartingPoint(ServerRequestInfoImpl paramServerRequestInfoImpl) {
    if (this.enabled)
      try {
        this.current.pushSlotTable();
        paramServerRequestInfoImpl.setSlotTable(this.current.getSlotTable());
        this.current.pushSlotTable();
        paramServerRequestInfoImpl;
        paramServerRequestInfoImpl.setCurrentExecutionPoint(0);
        ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
        int i = arrayOfServerRequestInterceptor.length;
        int j = i;
        boolean bool = true;
        for (int k = 0; bool && k < i; k++) {
          try {
            arrayOfServerRequestInterceptor[k].receive_request_service_contexts(paramServerRequestInfoImpl);
          } catch (ForwardRequest forwardRequest) {
            j = k;
            paramServerRequestInfoImpl.setForwardRequest(forwardRequest);
            paramServerRequestInfoImpl.setIntermediatePointCall(1);
            paramServerRequestInfoImpl.setEndingPointCall(2);
            paramServerRequestInfoImpl.setReplyStatus((short)3);
            bool = false;
          } catch (SystemException systemException) {
            j = k;
            paramServerRequestInfoImpl.setException(systemException);
            paramServerRequestInfoImpl.setIntermediatePointCall(1);
            paramServerRequestInfoImpl.setEndingPointCall(1);
            paramServerRequestInfoImpl.setReplyStatus((short)1);
            bool = false;
          } 
        } 
        paramServerRequestInfoImpl.setFlowStackIndex(j);
      } finally {
        this.current.popSlotTable();
      }  
  }
  
  void invokeServerInterceptorIntermediatePoint(ServerRequestInfoImpl paramServerRequestInfoImpl) {
    int i = paramServerRequestInfoImpl.getIntermediatePointCall();
    if (this.enabled && i != 1) {
      paramServerRequestInfoImpl;
      paramServerRequestInfoImpl.setCurrentExecutionPoint(1);
      ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
      int j = arrayOfServerRequestInterceptor.length;
      for (byte b = 0; b < j; b++) {
        try {
          arrayOfServerRequestInterceptor[b].receive_request(paramServerRequestInfoImpl);
        } catch (ForwardRequest forwardRequest) {
          paramServerRequestInfoImpl.setForwardRequest(forwardRequest);
          paramServerRequestInfoImpl.setEndingPointCall(2);
          paramServerRequestInfoImpl.setReplyStatus((short)3);
          break;
        } catch (SystemException systemException) {
          paramServerRequestInfoImpl.setException(systemException);
          paramServerRequestInfoImpl.setEndingPointCall(1);
          paramServerRequestInfoImpl.setReplyStatus((short)1);
          break;
        } 
      } 
    } 
  }
  
  void invokeServerInterceptorEndingPoint(ServerRequestInfoImpl paramServerRequestInfoImpl) {
    if (this.enabled)
      try {
        ServerRequestInterceptor[] arrayOfServerRequestInterceptor = (ServerRequestInterceptor[])this.interceptorList.getInterceptors(1);
        int i = paramServerRequestInfoImpl.getFlowStackIndex();
        int j = paramServerRequestInfoImpl.getEndingPointCall();
        for (int k = i - 1; k >= 0; k--) {
          try {
            switch (j) {
              case 0:
                arrayOfServerRequestInterceptor[k].send_reply(paramServerRequestInfoImpl);
                break;
              case 1:
                arrayOfServerRequestInterceptor[k].send_exception(paramServerRequestInfoImpl);
                break;
              case 2:
                arrayOfServerRequestInterceptor[k].send_other(paramServerRequestInfoImpl);
                break;
            } 
          } catch (ForwardRequest forwardRequest) {
            j = 2;
            paramServerRequestInfoImpl.setEndingPointCall(j);
            paramServerRequestInfoImpl.setForwardRequest(forwardRequest);
            paramServerRequestInfoImpl.setReplyStatus((short)3);
            paramServerRequestInfoImpl.setForwardRequestRaisedInEnding();
          } catch (SystemException systemException) {
            j = 1;
            paramServerRequestInfoImpl.setEndingPointCall(j);
            paramServerRequestInfoImpl.setException(systemException);
            paramServerRequestInfoImpl.setReplyStatus((short)1);
          } 
        } 
        paramServerRequestInfoImpl.setAlreadyExecuted(true);
      } finally {
        this.current.popSlotTable();
      }  
  }
  
  private void updateClientRequestDispatcherForward(ClientRequestInfoImpl paramClientRequestInfoImpl) {
    ForwardRequest forwardRequest = paramClientRequestInfoImpl.getForwardRequestException();
    if (forwardRequest != null) {
      Object object = forwardRequest.forward;
      IOR iOR = ORBUtility.getIOR(object);
      paramClientRequestInfoImpl.setLocatedIOR(iOR);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\InterceptorInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */