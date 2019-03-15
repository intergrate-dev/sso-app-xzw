package com.founder.sso;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.founder.sso.util.MD5Util;

//@Component
public class SignFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("============================== SignFilter doFilter enter ... , url " + ((HttpServletRequest)req).getRequestURI());
		HttpServletRequest request = (HttpServletRequest) req;
		request.setCharacterEncoding("utf-8");
		if(request.getRequestURI().contains("/api/uploadPortrait")){
			chain.doFilter(req, resp);
			return ;
		}
		if(request.getRequestURI().contains("/api/syn")){
			chain.doFilter(req, resp);
			return ;
		}
		if(request.getRequestURI().equals("/sso-app/api/findHead")){
			chain.doFilter(req, resp);
			return ;
		}
		if(request.getRequestURI().equals("/sso-app/api/delete")){
			chain.doFilter(req, resp);
			return ;
		}
		System.out.println(request.getRequestURI());
		Enumeration<String> headers = request.getHeaderNames();
		while(headers.hasMoreElements()){
			String h = headers.nextElement();
			System.out.println(h+":"+request.getHeader(h));
		}
		String sign = request.getHeader("program-sign");
		String devid = request.getHeader("devid");
		String version = request.getHeader("version");
		String time = request.getHeader("timestamp");
		long t = time==null?0:Long.valueOf(time);
		String token = request.getHeader("token");
		String random = request.getHeader("random");
		String params = request.getHeader("program-params");
		boolean valid = params==null?false:params.matches("^[a-zA-Z_]+(,[a-zA-Z_]+)*$");
		if(sign==null||devid==null||version==null||random==null||token==null||!valid||t<1451577600000l){
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"arguments are not enough");
		}
		StringBuffer buffer = new StringBuffer("devid=").append(devid).append("&random=").append(random)
				.append("&timestamp=").append(time).append("&token=").append(token).append("&version=").append(version);
		StringBuffer sBuffer = new StringBuffer();
		boolean paramsValid = true;
		for(String p:params.split(",")){
			String v = request.getParameter(p);
			if(v==null){
				paramsValid = false;
				break ;
			}
			sBuffer.append(p).append("=").append(v).append("&");
		}
		if(paramsValid){
			sBuffer.append("secret=").append(MD5Util.md5(buffer.toString()));
			System.out.println("+++++++++++++++++++  sBuffer secret:"+buffer.toString() + " ++++++++++++++++++++");
			System.out.println("========================= md5 before :"+sBuffer.toString() + " ========================");
			String md5 = MD5Util.md5(sBuffer.toString());
			System.out.println("+++++++++++++++++  sBuffer md5 end "+md5 + " +++++++++++++++++++++++++++++");
			if(!sign.equals(md5))
				paramsValid = false;
		}
		System.out.println("******************************************** paramsValid:"+paramsValid);
		if(!paramsValid){
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "sign is not invalid.");
		}


		System.out.println("============================== SignFilter doFilter enter ... , end " + ((HttpServletRequest)req).getRequestURI());
		chain.doFilter(req, resp);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
