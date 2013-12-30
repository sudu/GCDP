package com.me.GCDP.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.me.GCDP.util.env.NodeEnv;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: ifeng.com</p>
 * @author :huwq
 * @version 1.0
 * <p>--------------------------------------------------------------------</p>
 * <p>date                   author                    reason             </p>
 * <p>2013-1-17              huwq               create the class      </p>
 * <p>--------------------------------------------------------------------</p>
 */

public class GlobalVarModel implements TemplateDirectiveModel {
	
	//private static Log log = LogFactory.getLog(GlobalVarModel.class);

	@SuppressWarnings("rawtypes")
	@Override
	//<@env nodeId="123" var="123" />
	//<@include url="http://www.ifeng.com/test.html" cache_time="60" />
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {

		int nodeId = Integer.parseInt(params.get("nodeId").toString());
		String var = params.get("var").toString();
		
		Map<String,Object> map = NodeEnv.getNodeEnvInstance().getEnvByKey(nodeId, var);
		String result = null;
		if(map != null && map.containsKey("value")){
			result = map.get("value").toString();
		}
		//log.info(result)
		Writer out = env.getOut();
		out.write(result);
        if (body == null) {
        	body = new TemplateDirectiveBody() {
				
				@Override
				public void render(Writer arg0) throws TemplateException, IOException {
					
				}
			};
        }
        body.render(env.getOut());
	}

}
