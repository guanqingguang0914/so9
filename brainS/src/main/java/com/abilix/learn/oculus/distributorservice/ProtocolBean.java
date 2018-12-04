package com.abilix.learn.oculus.distributorservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guozm on 2018/1/30.
 */

public class ProtocolBean implements Parcelable {

    private int orderId;
    private String content;
    private byte[] protocolData;

    public ProtocolBean() {
    }

    protected ProtocolBean(Parcel in) {
        orderId = in.readInt();
        content = in.readString();
        protocolData = in.createByteArray();
    }

    public static final Creator<ProtocolBean> CREATOR = new Creator<ProtocolBean>() {
        @Override
        public ProtocolBean createFromParcel(Parcel in) {
            return new ProtocolBean(in);
        }

        @Override
        public ProtocolBean[] newArray(int size) {
            return new ProtocolBean[size];
        }
    };

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public byte[] getProtocolData() {
        return protocolData;
    }

    public void setProtocolData(byte[] protocolData) {
        this.protocolData = protocolData;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(orderId);
        parcel.writeString(content);
        parcel.writeByteArray(protocolData);
    }

    public void readFromParcel(Parcel parcel) {
        orderId = parcel.readInt();
        content = parcel.readString();
        protocolData = parcel.createByteArray();
    }
}
