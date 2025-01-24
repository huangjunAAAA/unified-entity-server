package com.zjht.unified.common.core.util;

import com.wukong.core.weblog.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * @Author: xxp
 * @Description:
 * @Date: Create in  2019/10/29 11:29
 */
public class MobileUtil {

    private static final Logger logger = LoggerFactory.getLogger(MobileUtil.class);

    /**
     * 手机号归属地查询工具类
     */

    private static String getSoapRequest(String mobileCode) {

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + " "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + " "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "\n"
                + "<soap:Body>" + "\n"
                + "<getMobileCodeInfo" + " " + "xmlns=\"http://WebXml.com.cn/\">" + "\n"
                + "<mobileCode>" + mobileCode + "</mobileCode>" + "\n"
                + "<userID></userID>" + "\n"
                + "</getMobileCodeInfo>" + "\n"
                + "</soap:Body>" + "\n"
                + "</soap:Envelope>"
        );
        return sb.toString();

    }

    private static InputStream getSoapInputStream(String mobileCode) {
        try {
            String soap = getSoapRequest(mobileCode);
            if (soap == null)
                return null;
            URL url = new URL("http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx");
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(soap.length()));
            conn.setRequestProperty("SOAPAction", "http://WebXml.com.cn/getMobileCodeInfo");
            OutputStream os = conn.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(soap);
            osw.flush();
            osw.close();
            osw.close();
            InputStream is = conn.getInputStream();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getMobileNoTrack(String mobileCode) {
        try {
            org.w3c.dom.Document document = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            InputStream is = getSoapInputStream(mobileCode);
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(is);
            NodeList nl = document.getElementsByTagName("getMobileCodeInfoResult");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nl.getLength(); i++) {
                org.w3c.dom.Node n = nl.item(i);
                if (n.getFirstChild().getNodeValue().equals("手机号码错误")) {
                    sb = new StringBuffer("#");
                    System.out.println("手机号码输入有误");
                    break;
                }
                sb.append(n.getFirstChild().getNodeValue() + "\n");
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询省份
     * @param Mobile
     * @return
     */
//    public static String getMobileAttribution(String Mobile) {
//        String str = "";
//        str = MobileUtil.getMobileNoTrack(Mobile);
//        if (str != null && !"".equals(str)) {
//            str = str.substring(str.indexOf("：") + 1);
//            String strArry[] = new String[]{};
//            strArry = str.split(" ");
//            str = strArry[0];
//        }
//        return str;
//    }

    public static String getMobileAttribution(String appcode,String Mobile) {
        String host = "http://plocn.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/plocn";// 【2】后缀
      //  String n = "18923499749"; // 【4】请求参数，详见文档描述
        String urlSend = host + path + "?n=" + Mobile ; // 【5】拼接请求链接
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
            // (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                logger.info("mobile area===="+json);
                Map<String,Object> jsonObject = (Map<String, Object>) JsonUtil.parse(json,Object.class);
                return jsonObject.get("province").toString();
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    logger.error("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    logger.error("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    logger.error("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    logger.error("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    logger.error("套餐包次数用完 ");
                } else if (httpCode == 403 && error.equals("Api Market Subscription quota exhausted")) {
                    logger.error("套餐包次数用完，请续购套餐");
                } else {
                    logger.error("参数名错误 或 其他错误");
                    logger.error(error);
                }
            }

        } catch (MalformedURLException e) {
            logger.error("URL格式错误");
        } catch (UnknownHostException e) {
            logger.error("URL地址错误");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return "没有此号码记录\n";
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    /**
     * 查询省份和城市
     * @param Mobile
     * @return
     */
    public static String[] getMobileArea(String Mobile) {
        String str = "";
        String strArry[] = new String[]{};
        str = MobileUtil.getMobileNoTrack(Mobile);
        if (str != null && !"".equals(str)) {
            str = str.substring(str.indexOf("：") + 1);
            strArry = str.split(" ");
            str = strArry[0];
        }
        return strArry;
    }

}
