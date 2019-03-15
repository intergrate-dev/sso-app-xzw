package com.founder.sso.test;

/**
 * Created by yuan-pc on 2018/11/7.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.exception.FacebookGraphException;
import com.restfb.exception.FacebookJsonMappingException;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.exception.FacebookResponseStatusException;
import com.restfb.types.User;

public class FacebookTest {
    public static void main(String[] args) {
        SLF4JBridgeHandler.install();

        SocketAddress sa = new InetSocketAddress("192.168.10.130", 808);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);


        // 通过OAUTH认证获取
        String accessToken = "EAAEwcXmnjdkBAH2l55jN6AU9EYp3yTdb576pEFCL6Kd0shdAPx1jy0A7Veyat5uXThdkKMB2jJuMdCPh6nvrDobanvQZBxZBtFFHQmFSmLnnM1EDoiPf2D4X7Rqwl2OpW3MvIBbkrOkc5p9gZCXh2y13uq0nuMue8C6XJBUScFk4J6pPWYiXAeWTAKZACRxHhcZBJMg1kuPLB2xugPZBfl91jEeS2g5tYZD";
        try {

            // 方式一：直接调用API
            // method_1(accessToken);
            methodByProxy(accessToken);

            // 方式二：使用restfb
            // method_2(accessToken);
            // 一次返回所有好友信息，需要自己封装返回类型
            //<T> T = facebookClient.fetchObjects(fbids, Class<T> objectType, Parameter.with("locale", "zh_CN"));

        } catch (FacebookJsonMappingException e) {
            // Looks like this API method didn't really return a list of users
            e.printStackTrace();
        } catch (FacebookNetworkException e) {
            // An error occurred at the network level
            int code = e.getHttpStatusCode();
            System.out.println("API returned HTTP status code " + e.getHttpStatusCode());
        } catch (FacebookOAuthException e) {  // Authentication failed - bad access token?
            e.printStackTrace();
        } catch (FacebookGraphException e) {  // The Graph API returned a specific error
            e.printStackTrace();
            System.out.println("Call failed. API says: " + e.getErrorMessage());

        } catch (FacebookResponseStatusException e) {
            // Old-style Facebook error response.
            // The Graph API only throws these when FQL calls fail.
            // You'll see this exception more if you use the Old REST API
            // via LegacyFacebookClient.
            if (e.getErrorCode() == 200) {
                System.out.println("Permission denied!");
            }
        } catch (FacebookException e) {
            // This is the catchall handler for any kind of Facebook exception
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Make sure to tear down when you're done using the bridge
            SLF4JBridgeHandler.uninstall();
        }
    }

    private static void methodByProxy(String accessToken) {
        /*System.setProperty("http.proxyHost", "isasrvhk.hold.founder.com");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.nonProxyHosts", "172.19.32.35");*/

        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        // 获取用户FB信息
        String urlStr = "https://graph.facebook.com/me?";
        URL url = null;
        Proxy proxy = null;
        try {
            urlStr += "scope=email&fields=id,name,first_name,last_name,email,birthday,picture&access_token=" + accessToken;// + "&locale=zh_CN";
            // urlStr += "access_token=" + accessToken + "&locale=zh_CN";
            SocketAddress sa = new InetSocketAddress("isasrvhk.hold.founder.com", 8080);
            proxy = new Proxy(Proxy.Type.HTTP, sa);

            url = new URL(urlStr);
            url.openConnection(proxy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while((line=br.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private static void method_2(String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
        User user = facebookClient.fetchObject("me", User.class, Parameter.with("locale", "zh_CN"));
        System.out.println(user.getEmail());
        System.out.println(user);

        System.out.println("=====================================");
        // (只能取到名字和ID)
        Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class, Parameter.with("locale", "zh_CN"));
        List<String> fbids = new ArrayList<String>();
        for (User friend : myFriends.getData()) {
            // 通过FBID去获取好友详细信息
            user = facebookClient.fetchObject(friend.getId(), User.class);
            System.out.println(user);
            fbids.add(friend.getId());
        }
    }

    private static void method_1(String accessToken) throws IOException {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter("http.protocol.content-charset","UTF-8");
        // 获取用户FB信息
        String urlStr = "https://graph.facebook.com/me?";
        // urlStr = getString(urlStr);
        GetMethod getMethod = new GetMethod(urlStr);
        NameValuePair[] accessData = { new NameValuePair("access_token",accessToken), new NameValuePair("locale","zh_CN") };

        getMethod.setQueryString(accessData);
        httpClient.executeMethod(getMethod);
        byte[] responseBody = getMethod.getResponseBody();
        String responseStr = new String(responseBody, "utf-8");
        if(StringUtils.isNotEmpty(responseStr)) {
            System.out.println(responseStr);
        }

        System.out.println("=====================================");
        // 获取用户FB好友信息(只能取到名字和ID)
        /*url = "https://graph.facebook.com/me/friends?";
        getMethod = new GetMethod(url);
        getMethod.setQueryString(accessData);

        httpClient.executeMethod(getMethod);
        responseBody = getMethod.getResponseBody();
        responseStr = new String(responseBody, "utf-8");
        if(StringUtils.isNotEmpty(responseStr)) {
            System.out.println(responseStr);
        }

        System.out.println("+++++++++++++++++++++++++++++++++++++");*/
    }

    private static String getString(String urlStr) {
        URL url = null;
        try {
            SocketAddress sa = new InetSocketAddress("isasrvhk.hold.founder.com", 8080);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);

            url = new URL(urlStr);
            url.openConnection(proxy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url.toString();
    }
}
