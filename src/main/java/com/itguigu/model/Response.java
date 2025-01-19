package com.itguigu.model;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/9 19:47
 **/
public class Response {
    private Integer code = -1314520;//默认响应状态码
    private String msg;//响应体

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 快速构建实体对象
     *
     * @param code
     * @param msg
     * @return
     */
    public static Response build(Integer code, String msg) {
        Response response = new Response();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }
}
