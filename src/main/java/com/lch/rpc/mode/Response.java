package com.lch.rpc.mode;

import lombok.Data;

@Data
public class Response {
    private int type;
    private String id;
    private Object result;
    private ErrorMsg error;
    private String version;

}
