<#--
top导航标签：用于显示top menu。
-->
<#macro topmenu>
	<#list TopMenu?if_exists as menu>
		<@sec.authorize url="${menu.href}">
			<li class="vdivider"></li>
			<li>
				<a href="${menu.href}"><i class="fa fa-lg ${menu.iconClass}"></i>${menu.name}</a>
			</li>
		</@sec.authorize>
	</#list>
</#macro>

