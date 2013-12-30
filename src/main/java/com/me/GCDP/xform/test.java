package com.me.GCDP.xform;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.dom4j.DocumentException;

import com.me.json.JSONException;
public class test {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws JSONException 
	 * @throws DocumentException 
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
//		JsonFormConfig conf = FormHelper.getConfig(69);
//		System.out.println(conf.getFormDesignConfig());
//		System.out.println(conf.getDbDefine().toString());
//		System.out.println(conf.getListDesignConfig());
//		System.out.println(conf.getListRuntimeConfig());
//		//System.out.println(FormHelper.getFormListConfig());
//		Map<String,Object> context = new HashMap<String, Object>();
//		Map<String,Object> a = new HashMap<String, Object>();
//		a.put("a", "aa");
//		a.put("b", "bb");
//		context.putAll(a);
//		System.out.println(context);
//		Map<String,Object> b = new HashMap<String, Object>();
//		b.put("b", "bbb");
//		b.put("c", "ccc");
//		context.putAll(b);
//		String tStr="aaaa";
//		System.out.println(FreeMarkerHelper.process2(tStr, context));
//		Pattern tagP = Pattern.compile("\\{#(([^#]+?))#\\}");
//		Pattern funcP = Pattern.compile("([\\s\\S]*)\\((([\\s\\S]*))\\)");
//		String template ="sfsdf{#render_idx(id:123,name:hello)#}sdfsdf{#sda(x:0,y:1)#}sdfsdf";
//		Matcher m = tagP.matcher(template);
//		StringWriter sw = new StringWriter();
//		while(m.find())
//		{
//			System.out.println(m.group());
//			String funcStr = m.group(1);
//			Matcher mt = funcP.matcher(funcStr);
//			while(mt.find())
//			{
//				System.out.println(mt.group()+":["+ mt.group(1)+":{"+mt.group(2)+"}]");
//				template = template.replace(mt.group(),"[aa]");
//			}
//		}
//		Pattern tagP = Pattern.compile("(?<=(\\{#renderdata))[\\s\\S]*(?=(#\\}))");
//		Matcher m =tagP.matcher("{#renderdata{\"aa\":\"sfsfs#{()}[]\",\"bb\":\"$$$$$$\"}#}");
//		while(m.find())
//		{
			//String incContent = HttpUtil.get("http://finance.ifeng.com/ssi-special-footer.shtml","").get("content").toString();
			//System.out.print(incContent);
			int a =6;
			int b =2;
			System.out.print(a&b);
//		}

//		Map<String,Object> mp = new HashMap<String,Object>();
//		
//		mp.put("aa", "sfsfs#{()}[]");
//		mp.put("bb", "$$$$$$");
//		JSONObject js = new JSONObject(mp);
//		System.out.println(js.toString());
//		String xhtml =readFile("d:\\xhtml.txt");
//		//System.out.println(xhtml);
//		Document doc= DocumentHelper.parseText(xhtml);
//		Element root = doc.getRootElement();
//		for( Iterator i = root.elementIterator(); i.hasNext();)
//		{
//			Element e = (Element) i.next();
//			
//			if(e.attribute("cmpp")!=null)
//			{
//				System.out.println(e.asXML());
//				xhtml = xhtml.replace(e.asXML(), "{#aaa()#}");
//			}
//		}
//		System.out.println(xhtml);
		//System.out.println(doc.getStringValue());
	}
   public static String readFile(String fileName)
   {
	  		String path=fileName;
	  		String content="";//content保存文件内容，
	  		BufferedReader reader=null;//定义BufferedReader
	
	try{
		reader=new BufferedReader(new FileReader(path));
		
		//按行读取文件并加入到content中。
		//当readLine方法返回null时表示文件读取完毕。
		String line;
		while((line=reader.readLine())!=null){
		content+=line+"\r\n";
	}
	}catch(IOException e){
		e.printStackTrace();
	}finally{
	
	//最后要在finally中将reader对象关闭
	if(reader!=null){
	try{
	reader.close();
	}catch(IOException e){
	e.printStackTrace();
	}
	}
	}
	return content;
   }
}
