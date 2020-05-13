package com.yozosoft.fileserver.common.utils;

/**
 * <pre>
 * 用于方法的返回值
 * 很多方法除了要返回成功与否，还要返回错误信息和数据
 * isSuccess()得到操作是否成功
 * getMessage()得到错误信息
 * getData()数据
 * </pre>
 */
public interface IResult<T> {

    /**
     * 是否成功
     */
    boolean isSuccess();

    /**
     * 得到消息
     */
    String getMessage();

    /**
     * 得到数据
     */
    T getData();

    void setData(T obj);

}
