package com.joe.robot.tbk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 搜索结果
 *
 * @author joe
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
    //搜索是否有结果，如果有结果那么该值为true，否则为false
    private boolean success;
    //搜索到的商品
    private Goods goods;
    //转换的链接
    private Link link;
}
