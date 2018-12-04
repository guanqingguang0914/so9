// DisAppBack.aidl
package com.abilix.learn.oculus.distributorservice;
import com.abilix.learn.oculus.distributorservice.ProtocolBean;

// Declare any non-default types here with import statements

interface DisAppBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
  void disAppCallBack(inout ProtocolBean protocolbean);
}
