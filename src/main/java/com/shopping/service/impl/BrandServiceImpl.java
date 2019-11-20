package com.shopping.service.impl;

import com.shopping.commons.exception.SuperMarketException;
import com.shopping.dao.BrandMapper;
import com.shopping.entity.Article;
import com.shopping.entity.Video;
import com.shopping.entity.VideoBanner;
import com.shopping.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    @Override
    public Map<String, Object> findVideoAndBanner(Integer page) throws SuperMarketException {
        Map<String, Object> map = new HashMap<>();
        //List<VideoBanner>bannerList=brandMapper.selectBanner();
        Integer start=(page-1)*10;
        List<Video> videoList=brandMapper.selectVideo(start);
        if (videoList==null){
            throw new SuperMarketException("没有找到视频资源");
        }
        map.put("videoList",videoList);
        return map;
    }

    @Override
    public List<Article> findArticle(Integer page) {
        Integer start=(page-1)*10;
        return brandMapper.selectArticle(start);
    }
}
