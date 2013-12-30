package com.me.GCDP.util;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

import com.me.GCDP.util.property.CmppConfig;

public class ImageCompressUtil {

	private static final Log log = LogFactory.getLog(ImageCompressUtil.class);
	
	private static String[] allowedTypes;	// 允许压缩的图片类型（jpeg）
	private static String maxFilesize;			// 最大文件大小（307200）
	private static String maxQuality;			// 最大压缩质量（80）
	
	static {
		try{
			allowedTypes = CmppConfig.getKey("image.compress.allowedTypes").split(",");
			maxFilesize = CmppConfig.getKey("image.compress.maxFilesize");
			maxQuality = CmppConfig.getKey("image.compress.maxQuality");
			
			if(null == allowedTypes || null == maxFilesize || null == maxQuality){
				throw new IllegalArgumentException("image.compress.allowedTypes或者image.compress.maxFilesize或者image.compress.maxQuality还没配置。");
			}
		}catch(Throwable t){
			log.error("ImageCompressUtil 初始化配置参数异常："+t.getMessage());
		}
	}
	
	/**
	 * 判断是否需要压缩<br>（压缩条件：allowedTypes满足 && 文件大小>maxFilesize && 文件压缩质量>maxQuality）
	 * <br>（allowedTypes，maxFilesize，maxQuality采用默认配置）
	 * @param file
	 * @return
	 */
	public static boolean isNeedCompress(final File file){
		return isNeedCompress(file, allowedTypes, maxFilesize, maxQuality);
	}
	
	/**
	 * 判断是否需要压缩<br>（压缩条件：allowedTypes满足 && 文件大小>maxFilesize && 文件压缩质量>maxQuality）
	 * <br>（allowedTypes，maxFilesize，maxQuality为null时，则采用默认配置）
	 * @param file 源文件
	 * @param allowedTypes 允许压缩的图片文件类型(jpeg,gif,png,bmp)
	 * @param maxFilesize 文件最大字节数
	 * @param maxQuality 最大压缩质量（0~100）
	 * @return
	 */
	public static boolean isNeedCompress(final File file, String[] allowedTypes, String maxFilesize, String maxQuality){
		if(!file.exists() || file.isDirectory() || file.length() == 0){
			return false;
		}
		long start = System.currentTimeMillis();
		IMOperation imoperation = new IMOperation();
		imoperation.verbose();
		imoperation.addImage(new String[] { file.getAbsolutePath() });
		try {
			IdentifyCmd identifycmd = new IdentifyCmd();
			ArrayListOutputConsumer arraylistoutputconsumer = new ArrayListOutputConsumer();
			identifycmd.setOutputConsumer(arraylistoutputconsumer);
			identifycmd.run(imoperation, new Object[0]);
			String format = null;
			int quality = 0;
			Iterator<String> iterator = arraylistoutputconsumer.getOutput().iterator();
			String s;
			while(iterator.hasNext()){
				s = iterator.next().trim();
				if(s.startsWith("Format")){
					format = s.split(": ", 2)[1].substring(0, 4).toLowerCase();
				}
				if(s.startsWith("Quality") || s.indexOf("quality") != -1){
					quality = Integer.parseInt(s.split(": ", 2)[1]);
				}
				if(null != format && 0 != quality){
					break;
				}
			}
			log.info("ImageCompressUtil & isNeedCompress(): format="+format+" | quality="+quality+" | runTime=" + (System.currentTimeMillis() - start) + "ms");
			if(null == format || 0 == quality){
				throw new Exception("获取不到图片的格式或者压缩质量。");
			}
			if(allowedTypes == null){
				allowedTypes = ImageCompressUtil.allowedTypes;
			}
			if(maxFilesize == null){
				maxFilesize = ImageCompressUtil.maxFilesize;
			}
			if(maxQuality == null){
				maxQuality = ImageCompressUtil.maxQuality;
			}
			for(int i=0; i<allowedTypes.length; i++){
				if( format.startsWith(allowedTypes[i])
						&& file.length() > Long.parseLong(maxFilesize)
						&& quality > Integer.parseInt(maxQuality)){
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			log.error("ImageCompressUtil & isNeedCompress():"+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 压缩图片质量（将文件压缩质量设置为maxQuality，仅对jpg格式图片操作有效，非jpg格式图片相当于copy操作）
	 * @param srcPath 源文件绝对路径
	 * @param destPath 压缩文件绝对路径（不存在时，会创建压缩文件；存在时，压缩文件覆盖）<br>（必须指定有效扩展名，否则linux下报无法识别格式错误）
	 * @throws Exception
	 */
	public static void compressQuality(String srcPath, String destPath) throws Exception{
		compressQuality(srcPath, destPath, maxQuality);
	}
	
	/**
	 * 压缩图片质量（仅对jpg格式图片操作有效，非jpg格式图片相当于copy操作）
	 * @param srcPath 源文件绝对路径
	 * @param destPath 压缩文件绝对路径（不存在时，会创建压缩文件；存在时，压缩文件覆盖）<br>（必须指定有效扩展名，否则linux下报无法识别格式错误）
	 * @throws Exception
	 */
	public static void compressQuality(String srcPath, String destPath, String quality) throws Exception{

		try{
			long start = System.currentTimeMillis();
			IMOperation op = new IMOperation();
			op.addRawArgs("-quality" ,  quality);
			op.addImage(new String[] {
					srcPath, destPath
		    });
			
			ConvertCmd cmd = new ConvertCmd();
			cmd.run(op,new Object[0]);
			log.info("ImageCompressUtil & compressQuality(): runTime=" + (System.currentTimeMillis() - start) + "ms");
		} catch (Exception e){
			log.error("ImageCompressUtil & compressQuality():"+e.getMessage());
			throw e;
		}
	}
}
