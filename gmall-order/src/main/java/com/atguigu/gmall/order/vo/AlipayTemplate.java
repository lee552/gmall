package com.atguigu.gmall.order.vo;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id;

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCRYgsZwRczjE/2BQezISCRWufaWA9BwnbBccJeWMmWSTVeIThBnjR3keEFtF41gA1hSmDstwHRcxHHUhr0qws/9jlQ0HbqJ3VIiLOauaLiUD4W+G31P0t7eJgaFCv9zFDFM+ZB/qYGVWnaWA7UT9gEB0Xln578opFzAzNdOo9wexx4dLbGM28iuYG9GvXFs9SZM5R9fJruI6AobtnTXmFxFsZcaLjascqd9KcLm0wysf+N7N5xZIqHEa0ip16+G++iyOA6BkHBXJeBP2y6pdH/e/5tx6e4/nZF1/USACHnfC6tYw+K0K0we+4KJYHE+KLOY5EDbUMp1g0uYf2sPF97AgMBAAECggEAY+BGHgBQDyLCwBH+kyob9J/ODmtWt+d/rC+ZxzUZ8DwFBNCvlCy6gLZgL4oU+o4pIwB1C8pB3oE94n/ect2a0es2vvUn7H8FDLqVIKONagqUTTWIXOaJ1Z4oaCoZerdFeeTIt+fYaRt+p/i831Cm50WT2EdLcuXAJJI7VD3p0uxHsuvjgg1ITTdstlh4ab4SOgSVPV3b9fQGtVnPYpAsXYTaCw5KbxmocFh3iFSGe4w2Ot8K+BbG9u+XtbuBWUn5T5NwFBzBe70s6f//nB8innKyJvWRoh5tQwcn5kQh4jY8btQ8MFDZv0Prf8xIBfxM51bvooxtcdFn9c/ucmY/2QKBgQDCt1j5XPLiQq4x1VRyAvvz04lPTmgsEWYFdF74+M/42GIJz9yT4fB8ojx38sQ+m5Pj9pQ5ktO5qnr+uqUQW61rWQshpuceH5CO13idnC+sOJ2zHeCwGEwiJwVSATHIEHUqapHQtuJ0Ho1WMCEJCX+c76gUSg9I9VcKlwsrwYrfPQKBgQC/I9M2ddp9lqJPOuRg++NY97Vih5CEMnfhB/kKC+s3MVhQOF7I2F4B2ofqlni05mHHJfAS8wOQex3GzBwI6J0GUVJwxqStQiewK9aYdvK++I/hjgdiqZVcrk/XvPo5Hu/2ResJdMIjnzJKtLCzd41FdtD2mybEOgSgVJkCUGClFwKBgQCf9d2bJZfG9r6B4942t2riWOKRizkbEsP8MSigYk8AAEsyAlgUCOX2jZUdeQts34R0HyqPzaYv/fSC9TQLJzVlXcY+RA3T+lxYbH0DBb9IPU6YSjA+mgdZezLstlx+Hb8TNG9LxiJtAHhd2LYa5Xm3pE4xNNIvvpVd9s3PiXUbhQKBgGlq58WEkGFtn80UzPpUUhoyXB6gZ+sRLp601/xbgHnrRCrSPCyHcKmTDh0qqh4TZeABI3w//TpFPLH0xo1/RYNDfLVoSVCpSNxYAT4LdGwxCKG8xIP8xbzdAnoY5uR0FKJ+A1guxhgBaCpdp4sDp62lDzDDd0kIOp72qYDmNbc3AoGAeGwiz7QMZhIrHNQoWPqAZcLfjpXroUMeB9sE+KZ5X9/BInsuJeYozngWzSUfktiPgOlXHEAhd/TIVxSOUzb5ywl1PCLndFV9eqcnx/rshTn+uBluNiXHYLRucAU6k9jlPt2PlSEFPeT5Vp0+yMOjmVqGVrO9zYbvnMnaphX+tOY=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QnBRkKwM0xWkyqQKO7hq3nQziVS/bffxClrNfI20w22qDFVtvQTuyDkXHC4Dbvf9Q78eMKhMdo4Hl86pn5ygD+pIjKKj5gLW3+wAgVTB7WUaQ3U0zrVs5P1CnLzREcj3QCaYSfHqRjrCux0BqpzfNxsojXiX1r2BFZpg/JAAvViBBb5uSpdtml588PueAaQm5s7gqiGVgGv1i+ASWXGWJNCanMH67jL6NDST1/AQYqEjp49eoUTWcBvNpEnBicm1As72yvRTIvScC0SMhwm8IH3Fl47RyfA5ASGepC/Xm+0HBv8sFATq448UAFpSCWLlyj4kNl2T9uwJvZBObtO5QIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type;

    // 字符编码格式
    private  String charset;

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl;

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
