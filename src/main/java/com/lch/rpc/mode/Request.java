package com.lch.rpc.mode;

import lombok.Data;

@Data
public class Request {
    private int type;
    private String id;
    private String method;
    private Object[] params;
    private Class<?>[] paramsType;
    private String version;
}
