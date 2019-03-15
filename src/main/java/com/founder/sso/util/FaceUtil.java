package com.founder.sso.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.founder.sso.entity.User;

public class FaceUtil {
	
	public static String default_avatarSmall =  "/static/images/face/default_avatarSmall.jpg";
	public static String default_avatarMiddle =  "/static/images/face/default_avatarMiddle.jpg";
	public static String default_avatarLarge =  "/static/images/face/default_avatarLarge.jpg";
	// public static String faceRootPath = (System.getenv("FACE_PATH") != null && System.getenv("FACE_PATH") != "") ? System.getenv("FACE_PATH"):SystemConfigHolder.getConfig("face_path");
	
	//小头像50*50
    public static final int avatarSmallSize = 50;
    //中头像100*100
    public static final int avatarMiddleSize = 100;
    //大头像322*322
    public static final int avatarLargeSize = 322;
	
	/**
	 * 根据url，把图片下载到本地，然后生成大中小三种尺寸的头像
	 * @param strUrl
	 */
	/*public static Map<String,String> writeFile(String strUrl){
		URL url = null;
		String folderAfter = getFolderAfter();
		String fileNameAhead = getFileNameAhead();
		String avatar = faceRootPath + folderAfter + fileNameAhead + ".jpg";
		String avatarSmall = folderAfter + fileNameAhead + "_avatarSmall.jpg";
		String avatarMiddle = folderAfter + fileNameAhead + "_avatarMiddle.jpg";
		String avatarLarge = folderAfter + fileNameAhead + "_avatarLarge.jpg";
		Map<String,String> map = new HashMap<String,String>();
		OutputStream os = null;
		try {
			url = new URL(strUrl);
			InputStream is = null;
			is = url.openStream();
			File f = new File(faceRootPath + folderAfter);
			if(!f.exists()){
				f.mkdirs();
			}
			os = new FileOutputStream(avatar);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while((bytesRead = is.read(buffer,0,8192))!=-1){
				os.write(buffer,0,bytesRead);
			}
			Small_pic mypic = new Small_pic();
			mypic.t_pic(avatar, faceRootPath + avatarSmall, FaceUtil.avatarSmallSize, FaceUtil.avatarSmallSize, true);
			mypic.t_pic(avatar, faceRootPath + avatarMiddle, FaceUtil.avatarMiddleSize, FaceUtil.avatarMiddleSize, true);
			mypic.t_pic(avatar, faceRootPath + avatarLarge, FaceUtil.avatarLargeSize, FaceUtil.avatarLargeSize, true);

			map.put("avatarSmall", avatarSmall);
			map.put("avatarMiddle", avatarMiddle);
			map.put("avatarLarge", avatarLarge);
		}catch(Exception e){
			e.printStackTrace();
			map.put("avatarSmall", default_avatarSmall);
			map.put("avatarMiddle", default_avatarMiddle);
			map.put("avatarLarge", default_avatarLarge);
		}finally {
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}*/
	
	 /**
     * 得到图片的后半部分路径
     */
    public static String getFolderAfter() {
		return "/content/face/" + DateUtil.getCurDate() + "/";
	}
    /**
     * 得到时间的字符串
     */
    public static String getFileNameAhead() {
    	return new Date().getTime() + "";
    }
}
