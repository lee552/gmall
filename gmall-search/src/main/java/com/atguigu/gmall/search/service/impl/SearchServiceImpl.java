package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import com.atguigu.gmall.search.vo.SearchResponseAttrVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;

    @Override
    public SearchResponse search(SearchParamVO searchParamVO) {
        // 构建查询条件
        String query = buildDslQuery(searchParamVO);
        Search action = new Search.Builder(query).addIndex("goods").addType("info").build();
        try {
            // 执行搜索，获取搜索结果集
            SearchResult result = this.jestClient.execute(action);

            // 解析结果集并响应
            System.out.println(result.toString());
            SearchResponse response = buildSearchResult(result);
            response.setPageNum(searchParamVO.getPageNum()); // 页码
            response.setPageSize(searchParamVO.getPageSize()); // 每页大小
            response.setTotal(result.getTotal()); // 总记录数

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SearchResponse buildSearchResult(SearchResult result) {
        SearchResponse response = new SearchResponse();

        // 1.解析查询的记录结果
        List<SearchResult.Hit<GoodsVO, Void>> hits = result.getHits(GoodsVO.class);
        List<GoodsVO> goodsVOList = hits.stream().map(hit -> {
            GoodsVO source = hit.source;
            // 获取高亮，并设置给记录
            source.setName(hit.highlight.get("name").get(0));
            return source;
        }).collect(Collectors.toList());
        response.setProducts(goodsVOList);

        // 2. 解析聚合结果集
        // 筛选属性
        MetricAggregation aggregations = result.getAggregations();
        // 属性子聚合
        ChildrenAggregation attrAgg = aggregations.getChildrenAggregation("attr_agg");
        // 属性id聚合
        TermsAggregation attrIdAgg = attrAgg.getTermsAggregation("attrId_agg");
        // 遍历id聚合桶
        List<SearchResponseAttrVO> attrVOs = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
            // 属性id
            searchResponseAttrVO.setProductAttributeId(Long.valueOf(bucket.getKeyAsString()));

            // 获取属性名子聚合
            TermsAggregation attrNameAgg = bucket.getTermsAggregation("attrName");
            // 从属性名子聚合中获取属性名
            searchResponseAttrVO.setName(attrNameAgg.getBuckets().get(0).getKeyAsString());

            // 获取属性值子聚合
            TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValue");
            List<String> values = attrValueAgg.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            // 设置属性值
            searchResponseAttrVO.setValue(values);

            return searchResponseAttrVO;
        }).collect(Collectors.toList());
        response.setAttrs(attrVOs);

        // 封装品牌 TODO
        SearchResponseAttrVO brand = new SearchResponseAttrVO();
        response.setBrand(brand);
        brand.setName("品牌"); // 品牌时 name-就是“品牌”
        // value：[{id:100,name:华为},{id:101,name:小米}]
        TermsAggregation brandIdAgg = aggregations.getTermsAggregation("brandId");
        // 获取品牌id聚合结果，并遍历桶
        List<String> brandValue = brandIdAgg.getBuckets().stream().map(b -> {
            Map<String, String> map = new HashMap<>();
            // 桶的key就是品牌的id
            map.put("id", b.getKeyAsString());
            // 子聚合得出品牌
            List<TermsAggregation.Entry> brandName = b.getTermsAggregation("brandName").getBuckets();
            if(!CollectionUtils.isEmpty(brandName)){
                map.put("name", brandName.get(0).getKeyAsString());
            }
            // 把map转化成json字符串
            return JSON.toJSONString(map);
        }).collect(Collectors.toList());
        brand.setValue(brandValue);

        // 封装分类，类似于品牌
        SearchResponseAttrVO category = new SearchResponseAttrVO();
        response.setCatelog(category);
        category.setName("分类");
        TermsAggregation categoryIdAgg = aggregations.getTermsAggregation("categoryId");
        List<String> categoryValue = categoryIdAgg.getBuckets().stream().map(b -> {
            Map<String, String> map = new HashMap<>();
            // 桶的key就是品牌的id
            map.put("id", b.getKeyAsString());
            // 子聚合得出品牌
            map.put("name", b.getTermsAggregation("categoryName").getBuckets().get(0).getKeyAsString());
            // 把map转化成json字符串
            return JSON.toJSONString(map);
        }).collect(Collectors.toList());
        category.setValue(categoryValue);

        return response;
    }

    private String buildDslQuery(SearchParamVO searchParamVO) {
        // 搜索条件构建器，辅助构建dsl语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 1.查询及过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        sourceBuilder.query(boolQueryBuilder);
        if (StringUtils.isNotBlank(searchParamVO.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", searchParamVO.getKeyword()).operator(Operator.AND));
        }
        // 属性过滤 2:win10-android-ios   3:4g	   4:5.5
        String[] props = searchParamVO.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                String[] attr = StringUtils.split(prop, ":");
                if (attr == null || attr.length != 2) {
                    continue;
                }
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.termQuery("attrValueList.productAttributeId", attr[0]));
                boolQuery.must(QueryBuilders.termsQuery("attrValueList.value", attr[1].split("-")));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrValueList", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            }
        }

        // 品牌过滤
        String[] brands = searchParamVO.getBrand();
        if (brands != null && brands.length > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brands));
        }

        // 分类过滤
        String[] catelog3 = searchParamVO.getCatelog3();
        if (catelog3 != null && catelog3.length > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId", catelog3));
        }

        // 价格范围过滤
        Integer priceFrom = searchParamVO.getPriceFrom();
        Integer priceTo = searchParamVO.getPriceTo();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        if (priceFrom != null) {
            rangeQueryBuilder.gte(priceFrom);
        }
        if (priceTo != null) {
            rangeQueryBuilder.lte(priceTo);
        }

        // 2.分页条件
        int start = (searchParamVO.getPageNum() - 1) * searchParamVO.getPageSize();
        sourceBuilder.from(start);
        sourceBuilder.size(searchParamVO.getPageSize());

        // 3.高亮
        if (searchParamVO.getKeyword() != null) {
            sourceBuilder.highlighter(new HighlightBuilder().field("name").preTags("<em style='color: red'>").postTags("<em>"));
        }

        // 4.排序 0：综合排序  1：销量  2：价格 service=1:asc
        String order = searchParamVO.getOrder();
        if (order != null) {
            String[] orderParams = StringUtils.split(order, ":");
            SortOrder sortOrder = StringUtils.equals("asc", orderParams[1]) ? SortOrder.ASC : SortOrder.DESC;
            if (orderParams != null && orderParams.length == 2) {
                switch (orderParams[0]) {
                    case "0":
                        sourceBuilder.sort("_source", sortOrder);
                    case "1":
                        sourceBuilder.sort("sale", sortOrder);
                    case "2":
                        sourceBuilder.sort("price", sortOrder);
                    default:
                        break;
                }
            }
        }

        // 5.属性聚合（嵌套聚合）
        NestedAggregationBuilder nestedAgg = AggregationBuilders.nested("attr_agg", "attrValueList");
        // 外层聚合出属性id，terms是聚合名称，field-聚合字段
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrId_agg").field("attrValueList.productAttributeId");
        nestedAgg.subAggregation(attrIdAgg); // 添加子聚合
        // 聚合出属性名
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrName").field("attrValueList.name"));
        // 聚合出属性值
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValue").field("attrValueList.value"));
        sourceBuilder.aggregation(nestedAgg);

        // 品牌聚合
        TermsAggregationBuilder BrandAgg = AggregationBuilders.terms("brandId").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandName").field("brandName"));
        sourceBuilder.aggregation(BrandAgg);

        // 分类聚合
        TermsAggregationBuilder categoryAgg = AggregationBuilders.terms("categoryId").field("productCategoryId")
                .subAggregation(AggregationBuilders.terms("categoryName").field("productCategoryName"));
        sourceBuilder.aggregation(categoryAgg);

        return sourceBuilder.toString();
    }

    public static void main(String[] args) {
        SearchParamVO searchParamVO = new SearchParamVO();
        searchParamVO.setKeyword("手机");
        searchParamVO.setBrand(new String[]{"8"});
        searchParamVO.setCatelog3(new String[]{"225"});
        searchParamVO.setOrder("1:desc");
        searchParamVO.setPriceFrom(2000);
        searchParamVO.setPriceTo(5000);
        System.out.println(new SearchServiceImpl().buildDslQuery(searchParamVO));
    }

}
