package com.abilix.brain.data;

import java.util.List;

public class ApkVersionInfo {

    /**
     * code : false
     * message :
     * data : [{"packageName":"stm32","verCode":33685585},{"packageName":"com.abilix.brainset","verCode":81},{"packageName":"com.abilix.explainer","verCode":46},{"packageName":"com.abilix.volumecontrol","verCode":8},{"packageName":"com.abilix.learn.rtspserver","verCode":14},{"packageName":"com.abilix.control","verCode":1316},{"packageName":"com.abilix.brain","verCode":10174},{"packageName":"com.abilix.updateonlinetest","verCode":139}]
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * packageName : stm32
         * verCode : 33685585
         */

        private String packageName;
        private int verCode;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getVerCode() {
            return verCode;
        }

        public void setVerCode(int verCode) {
            this.verCode = verCode;
        }
    }
}
