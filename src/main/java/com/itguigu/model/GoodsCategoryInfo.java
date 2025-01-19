package com.itguigu.model;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/12/26 19:21
 * 市场商品less简短信息
 * 用于映射获取市场藏品集合的响应体参数
 **/
public class GoodsCategoryInfo {
    @JSONField(name = "value")
    private String categoryId;
    @JSONField(name = "name")
    private String categoryName;
    @JSONField(name = "artworkUid")
    private String goodsId;//商品id
    @JSONField(name = "artworkName")
    private String goodsName;//商品名称
    @JSONField(name = "count")
    private Integer fluxNum;//商品流通量

    //用于从csv文件导入数据
    public static GoodsCategoryInfo fromString(String str) {
        // 简单解析字符串
        String[] parts = str.replace("GoodsCategoryInfo{", "").replace("}", "").split(", ");
        GoodsCategoryInfo info = new GoodsCategoryInfo();
        for (String part : parts) {
            String[] keyValue = part.split("=");
            switch (keyValue[0]) {
                case "categoryId":
                    info.setCategoryId(keyValue[1]);
                    break;
                case "categoryName":
                    info.setCategoryName(keyValue[1]);
                    break;
                case "goodsId":
                    info.setGoodsId(keyValue[1]);
                    break;
                case "goodsName":
                    info.setGoodsName(keyValue[1]);
                    break;
                case "fluxNum":
                    info.setFluxNum(Integer.parseInt(keyValue[1]));
                    break;
            }
        }
        return info;
    }

    public GoodsCategoryInfo(String categoryId, String categoryName, String goodsId, String goodsName, Integer fluxNum) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.fluxNum = fluxNum;
    }

    public GoodsCategoryInfo() {
    }

    @Override
    public String toString() {
        return "GoodsCategoryInfo{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", fluxNum=" + fluxNum +
                '}';
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getFluxNum() {
        return fluxNum;
    }

    public void setFluxNum(Integer fluxNum) {
        this.fluxNum = fluxNum;
    }
}
