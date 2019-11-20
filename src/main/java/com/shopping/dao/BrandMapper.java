package com.shopping.dao;

import com.shopping.entity.Article;
import com.shopping.entity.Video;
import com.shopping.entity.VideoBanner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrandMapper {
    @Select("select url from videobanner order by createtime DESC")
    List<VideoBanner> selectBanner();
    @Select("select videourl from video order by createtime DESC limit #{start},10")
    List<Video> selectVideo(Integer start);
    @Select("select title,pictrue,articleurl,author from article order by createtime DESC limit #{start},10")
    List<Article> selectArticle(Integer start);
}
