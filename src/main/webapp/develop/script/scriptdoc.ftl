<#if pdoc?exists>
  <div class='helpContainer'>
	<div class="clsTitle">${(pdoc.classDoc?html)?default("")}</div>
	<div class="h_body">
		<div><b>【作者】 </b> ${pdoc.author?default("")}</div>
		<div><b>【示例】 </b> 
			<div class="h_body">${pdoc.example?default("")}</div>
		</div>
	</div>	
		<div class="methods">
		<div class="head">方法列表:</div>
		<#if pdoc.mlist?exists>
		<ul  class="h_body">
		<#list pdoc.mlist as m>
		 <li class="methodItem">
			<div class="methodTitle">${(m.methodName?html)?default("")}</div>
			<div  class="h_body">
				<div>${(m.methodDoc?html)?default("")}</div>
				<div><b>【参数列表】 </b></div>
				<#if m.paramsDoc?exists>
				<ul  class="paramList">
				<#list m.paramsDoc as paramDoc>
				<li>${paramDoc}</li> 
		
				</#list>
				</ul>
				</#if>
				<div><b>【返回值】 </b>${m.returnDoc}</div>
				<div><b>【异常信息】 </b>${m.exceptionInfo}</div>
			</div>
		 </li>
		</#list>
		</ul>
		</#if>
		</div>
  </div>
</#if>


