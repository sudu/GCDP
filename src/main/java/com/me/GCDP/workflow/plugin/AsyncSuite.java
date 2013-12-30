package com.me.GCDP.workflow.plugin;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.w3c.dom.Element;

import com.me.GCDP.workflow.CmppProcessContext;
import com.ifeng.common.conf.ConfigRoot;
import com.ifeng.common.plugin.core.abst.AbstSuite;
import com.ifeng.common.plugin.core.itf.IntfPlugin;
import com.ifeng.common.workflow.ProcessContext;
import com.me.GCDP.script.ScriptThreadLocal;


/**
 * AsyncSuite借助CountDownLatch来实现工作流两个Activity之间并发执行的多个分支
 * 之间的异步执行。
 * @author xiongfeng
 *
 */
public class AsyncSuite extends AbstSuite{

	private StringBuffer subThreadLog = new StringBuffer();
	
	@Override
	public Object doExecute(Object context) {
		int asyncPluginNum = this.stepModules.size();
		CountDownLatch doneSignal = new CountDownLatch(asyncPluginNum);
		ExecutorService exec = Executors.newFixedThreadPool(asyncPluginNum);
		for (int i = 0; i < asyncPluginNum; i++) {
			exec.execute(new AsyncWorker(doneSignal, i, this.stepModules.get(i), context));
		}
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			//关闭线程池
			exec.shutdown();
			ScriptThreadLocal.appendOutput(subThreadLog.toString());
		}
		return Boolean.TRUE;
	}

	private static final String ACCEPTED_TAGS = "async-plugin" ;

	
	protected void configSubPlugin(ConfigRoot configRoot, Element itemEle) {

		if (ACCEPTED_TAGS.equals(itemEle.getTagName())) {
		    super.configSubPlugin(configRoot, itemEle);
		} else {
		    throw new java.lang.IllegalArgumentException("'" + ACCEPTED_TAGS + "' required.");
		}
	}
	
	/**
	 * 私有内部类,每一个该类的实例代表fork/join节点中的一个小的分支
	 * @author xiongfeng
	 *
	 */
	@SuppressWarnings("unused")
	private class AsyncWorker implements Runnable {

		private final CountDownLatch doneSignal;
		private final int id;
		private IntfPlugin plugin;
		private Object context;
		public AsyncWorker(CountDownLatch doneSignal, int id, 
				IntfPlugin plugin, Object context) {
			this.doneSignal = doneSignal;
			this.id = id;
			this.plugin = plugin;
			this.context = context;
		}
		
		@Override
		public void run() {
			String ids[] = new String[3];
        	ids[0] = ((CmppProcessContext)context).getNodeid() + "";
        	ids[1] = "process";
        	ids[2] = ((CmppProcessContext)context).getProcessDefinitionName();
        	ScriptThreadLocal.setScriptIDS(ids);
        	ScriptThreadLocal.setInstanceId(((CmppProcessContext)context).getId());
        	ScriptThreadLocal.setOutput(true);
			if (isSuccess(plugin.execute(context)) == Boolean.TRUE) {
				doneSignal.countDown();
			}else {
				//如果该分支执行出错，那么设置整个异步活动的next为null
				((ProcessContext)context).setNextActivity(null);
				doneSignal.countDown();
			}
			subThreadLog.append(ScriptThreadLocal.getOutputString());
			ScriptThreadLocal.removeScriptIds();
			ScriptThreadLocal.removeInstanceId();
		}
		
	}
}