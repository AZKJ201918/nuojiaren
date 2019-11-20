package com.shopping.commons.constans;


public class Constants {

    public static final int RESP_STATUS_OK = 200;

    public static final int RESP_STATUS_NOAUTH = 401;

    public static final int RESP_STATUS_INTERNAL_ERROR = 500;

    public static final int RESP_STATUS_BADREQUEST = 400;

    public static final int RESP_STATUS_BADREQUEST1 = 407;



//    public static final String WEXING_STATUS_APPID="wxf74c67f67043acf4";
//
//
//    public static  final  String WEIXING_STATUS_APPSECRET="100d3ea626d739417d3289c9c112e06e";
//
//
//    public static  final  String WEIXING_ACCESS_TOKEN="95eb61ae89ee48f792dd6f1db5bc9bbe";


    public static final String WEXING_STATUS_APPID="wxea595309e07cd5e5";


    public static  final  String WEIXING_STATUS_APPSECRET="330cf75cf393773f7eaaf1daa11fd2a3";


    public static  final  String WEIXING_ACCESS_TOKEN="c8713f6753554dc68e41f9fcdc21d02d";



    /***七牛keys start****/
    public static final String QINIU_ACCESS_KEY="GLt1Gr-awMVuVmnSntaXKplLEhIIe6BnzCTtyXzF";

    public static final String QINIU_SECRET_KEY="eIsMWbGo3VxfG5wtlfmcnj-f8xEQ3ZI53bVvfcFc";

    public static final String QINIU_HEAD_IMG_BUCKET_NAME="whazkj";

    public static final String QINIU_HEAD_IMG_BUCKET_URL="http://q0jf94eoj.bkt.clouddn.com";

    /***七牛keys end****/


    /**百度云推送 start**/
    public static final String BAIDU_YUN_PUSH_API_KEY="eqoZtUW4ZgeTPMiAwiaF6u9Z";

    public static final String BAIDU_YUN_PUSH_SECRET_KEY="t5w5u3VFpprpnPy9qAnBjkyGVzAZzrE2";

    public static final String CHANNEL_REST_URL = "api.push.baidu.com";
    /**百度云推送end**/



    /**用户token**/
    public static final String REQUEST_TOKEN_HEADER = "x-auth-token";
    /**用户session***/
    public static final String REQUEST_USER_SESSION = "current-user";
    /**客户端版本**/
    public static final String REQUEST_VERSION_KEY = "version";


    /**用户注册分布式锁路径***/
    public static final String USER_REGISTER_DISTRIBUTE_LOCK_PATH = "/user_reg";

    public static final String USER_REGISTER_DISTRIBUTE_LOCK_PATH1 = "/user_reg1";

    public static final String USER_REGISTER_DISTRIBUTE_LOCK_PATH2 = "/user_reg2";

    public static final String CACHE_PRODUCT_CATEGORY = "product:category";
    public static final String CACHE_PRODUCT_DETAIL = "product:detail";
    public static final String CACHE_PRODUCT_COMMODITY = "product:commodity";

}
