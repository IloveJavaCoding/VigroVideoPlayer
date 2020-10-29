package com.nepalese.vigrovideoplayer.data.bean;

import com.nepalese.virgosdk.Util.ConvertUtil;

import java.io.Serializable;

/**
 * @author nepalese on 2020/10/27 16:03
 * @usage
 */
public class BaseBean implements Serializable {
    public BaseBean(){

    }

    public String toJson(){
        return ConvertUtil.toJson(this);
    }
}
