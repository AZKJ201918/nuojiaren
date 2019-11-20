package com.shopping.service;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.entity.Article;

import java.util.List;
import java.util.Map;

public interface BrandService {
    Map<String,Object> findVideoAndBanner(Integer page) throws SuperMarketException;

    List<Article> findArticle(Integer page);
}
