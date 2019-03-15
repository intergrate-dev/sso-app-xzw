package com.founder.sso.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 缩略图类
 * 
 * @param mapping
 * @param form
 * @param request
 * @param response
 * @return ActionForward
 * @author  本java类能将jpg图片文件，进行等比或非等比的大小转换 具体使用方法
 *         s_pic(大图片路径,生成小图片路径,大图片文件名,生成小图片文名,生成小图片宽度,生成小图片高度,是否等比缩放(默认为true))
 */

public class Small_pic
{
	String InputDir; // 输入图路径

	String OutputDir; // 输出图路径

	String InputFileName; // 输入图文件名

	String OutputFileName; // 输出图文件名

	String srcFileName;

	String etcFileName;

	int OutputWidth = 80; // 默认输出图片宽

	int OutputHeight = 80; // 默认输出图片高

	int rate = 0;

	boolean proportion = true; // 是否等比缩放标记(默认为等比缩放)

	private String EFileName;

	public Small_pic()
	{
		// 初始化变量
		InputDir = "";
		OutputDir = "";
		InputFileName = "";
		OutputFileName = "";
		srcFileName = "";
		etcFileName = "";
		OutputWidth = 80;
		OutputHeight = 80;
		rate = 0;
	}

	public void setInputDir(String InputDir)
	{
		this.InputDir = InputDir;
	}

	public void setOutputDir(String OutputDir)
	{
		this.OutputDir = OutputDir;
	}

	public void setInputFileName(String InputFileName)
	{
		this.InputFileName = InputFileName;
	}

	public void setOutputFileName(String OutputFileName)
	{
		this.OutputFileName = OutputFileName;
	}

	public void setOutputWidth(int OutputWidth)
	{
		this.OutputWidth = OutputWidth;
	}

	public void setOutputHeight(int OutputHeight)
	{
		this.OutputHeight = OutputHeight;
	}

	public void setW_H(int width, int height)
	{
		this.OutputWidth = width;
		this.OutputHeight = height;

	}

	public String t_pic(String srcFileName, String eFileName, int width,
			int height, boolean gp)
	{

		return jpgTset(srcFileName, eFileName, width, height);

	}


	public String getSrcFileName()
	{
		return srcFileName;
	}

	public String getEtcFileName()
	{
		return etcFileName;
	}

	public String getEFileName()
	{
		return EFileName;
	}

	public void setSrcFileName(String srcFileName)
	{
		this.srcFileName = srcFileName;
	}

	public void setEtcFileName(String etcFileName)
	{
		this.etcFileName = etcFileName;
	}

	public void setEFileName(String EFileName)
	{
		this.EFileName = EFileName;
	}

	public String jpgTset(String srcImg, String etcImg, int wideth, int height)
	{

		File _file = new File(srcImg); // 读入文件
		Image src = null;
		FileOutputStream out = null;
		try
		{
			src = javax.imageio.ImageIO.read(_file); // 构造Image对象

			// int wideth=src.getWidth(null); //得到源图宽
			// int height=src.getHeight(null); //得到源图长
			BufferedImage tag = new BufferedImage(wideth, height,
					BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(src, 0, 0, wideth, height, null); // 绘制缩小后的图
			out = new FileOutputStream(etcImg); // 输出到文件流
			// File file = new File("D:\\newFile.jpg");
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//			// JPEGImageEncoder encoder = JPEGCodec.c
//			encoder.encode(tag); // 近JPEG编码
			ImageIO.write(tag, "jpg", out);
		}
		catch (IOException ex)
		{
		}
		finally
		{
			if (out != null) {
				try
				{
					out.close();
				}
				catch (IOException ex1)
				{
				}
			}
		}
		return etcImg;
	}

}
