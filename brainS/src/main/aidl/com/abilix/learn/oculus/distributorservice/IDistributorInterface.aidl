// IDistributorInterface.aidl
package com.abilix.learn.oculus.distributorservice;
import com.abilix.learn.oculus.distributorservice.DistributorBack;
import com.abilix.learn.oculus.distributorservice.ProtocolBean;
import com.abilix.learn.oculus.distributorservice.DisAppBack;

// Declare any non-default types here with import statements

interface IDistributorInterface {
    void handAction(inout ProtocolBean protocolbean);//brain下发的执行动作状态
    void setDisBack(DistributorBack discallback);//brain分发器回调

     void handAppAction(inout ProtocolBean protocolbean);//app传给brain的执行动作状态
     void setAppDisBack(DisAppBack appCallBack);//app 分发器回调
    }
