package com.itguigu.model;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/9 19:43
 * 市场商品详情信息
 * 用于映射下单接口的请求体参数
 **/

public class GoodsInfo {
    @JSONField(name = "accountArtworkUid")
    private String goodsId;
    @JSONField(name = "artworkName")
    private String goodsName;
    @JSONField(name = "publishPrice")
    private Long price;

    public GoodsInfo(String goodsId, String goodsName, Long price) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
    }

    public GoodsInfo() {
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
