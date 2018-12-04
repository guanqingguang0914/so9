// DistributorBack.aidl
package com.abilix.learn.oculus.distributorservice;
import com.abilix.learn.oculus.distributorservice.ProtocolBean;

// Declare any non-default types here with import statements

interface DistributorBack {
    void disCallBack(inout ProtocolBean protocolbean);
 }
