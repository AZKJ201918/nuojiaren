package com.shopping.controller;

import com.shopping.commons.constans.Constants;
import com.shopping.commons.exception.SuperMarketException;
import com.shopping.commons.resp.ApiResult;
import com.shopping.entity.Article;
import com.shopping.service.BrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@Slf4j
@Api(value = "品牌文化模块")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @ApiOperation(value = "品牌视频",notes = "品牌视频",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/video")
    public ApiResult video(Integer page){
        ApiResult<Object> result = new ApiResult<>();
        try {
            Map<String,Object> map=brandService.findVideoAndBanner(page);
            result.setMessage("查看视频成功");
            result.setData(map);
        }catch (SuperMarketException e){
            result.setMessage(e.getMessage());
            result.setCode(Constants.RESP_STATUS_BADREQUEST);
        }catch (Exception e){
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
    @ApiOperation(value = "品牌文章",notes = "品牌文章",httpMethod = "POST")
    @ApiImplicitParam
    @PostMapping("/article")
    public ApiResult article(Integer page){
        ApiResult<Object> result = new ApiResult<>();
        try {
            List<Article> articleList=brandService.findArticle(page);
            result.setData(articleList);
            result.setMessage("查看成功");
        }catch (Exception e){
            e.printStackTrace();
            result.setMessage("后台服务器异常");
            result.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
        }
        return result;
    }
}
