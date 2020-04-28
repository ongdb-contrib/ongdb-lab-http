package data.lab.ongdb.remote.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.io.File;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.remote.http.HttpReqeust
 * @Description: TODO
 * @date 2020/4/28 15:20
 */
public class HttpReqeust {

    public static String httpGetforString(String url) throws Exception {
        return httpGetforString(url, (String) null,
                (String) null);
    }

    /**
     * get请求URL
     *
     * @param url
     * @param cookie
     * @param userAgent
     * @throws Exception
     */
    public static String httpGetforString(String url, String cookie,
                                          String userAgent) throws Exception {
        return HttpRequestUtil.httpGetforString(url, cookie, userAgent, null);
        // //responseBody = responseBody.replaceAll("\\p{Cntrl}", "\r\n");
        // if(responseBody.contains("result") &&
        // responseBody.contains("errorCode") &&
        // appContext.containsProperty("user.uid")){
        // try {
        // Result res = Result.parse(new
        // ByteArrayInputStream(responseBody.getBytes()));
        // if(res.getErrorCode() == 0){
        // appContext.Logout();
        // appContext.getUnLoginHandler().sendEmptyMessage(1);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // return new ByteArrayInputStream(responseBody.getBytes());
    }


    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws Exception
     */
    public static String httpPostforString(String url, Map<String, Object> params,
                                           Map<String, File> files) throws Exception {
        return httpPostforString(url, (String) null,
                (String) null, params,
                files);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param files
     * @throws Exception
     */
    public static String httpPostforFile(String url,
                                         Map<String, File> files) throws Exception {
        return httpPostforString(url, (String) null,
                (String) null, null,
                files);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @throws Exception
     */
    public static String httpPostforString(String url, Map<String, Object> params) throws Exception {
        return httpPostforString(url, (String) null,
                (String) null, params,
                (Map<String, File>) null);
    }

    /**
     * 公用post方法
     *
     * @param url
     * @throws Exception
     */
    public static String httpPostforString(String url) throws Exception {
        return httpPostforString(url, (String) null,
                (String) null, (Map<String, Object>) null,
                (Map<String, File>) null);
    }

    /**
     * 公用post方法:文件上传方法
     *
     * @param url
     * @param params
     * @param files
     * @throws Exception
     */
    public static String httpPostforString(String url, String cookie,
                                           String userAgent, Map<String, Object> params,
                                           Map<String, File> files) throws Exception {
        return HttpRequestUtil.httpPostforString(url, cookie, userAgent, params, files);
        // responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        // if(responseBody.contains("result") &&
        // responseBody.contains("errorCode") &&
        // appContext.containsProperty("user.uid")){
        // try {
        // Result res = Result.parse(new
        // ByteArrayInputStream(responseBody.getBytes()));
        // if(res.getErrorCode() == 0){
        // appContext.Logout();
        // appContext.getUnLoginHandler().sendEmptyMessage(1);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // return new ByteArrayInputStream(responseBody.getBytes());
    }


    /**
     * 公用post方法:文件上传方法
     *
     * @param url
     * @param cookie
     * @param userAgent
     * @param files
     * @throws Exception
     */
    public static String httpPostforString(String url, String cookie,
                                           String userAgent,
                                           Map<String, File> files) throws Exception {
        return HttpRequestUtil.httpPostforString(url, cookie, userAgent, files);
        // responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
        // if(responseBody.contains("result") &&
        // responseBody.contains("errorCode") &&
        // appContext.containsProperty("user.uid")){
        // try {
        // Result res = Result.parse(new
        // ByteArrayInputStream(responseBody.getBytes()));
        // if(res.getErrorCode() == 0){
        // appContext.Logout();
        // appContext.getUnLoginHandler().sendEmptyMessage(1);
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        // return new ByteArrayInputStream(responseBody.getBytes());
    }

}
