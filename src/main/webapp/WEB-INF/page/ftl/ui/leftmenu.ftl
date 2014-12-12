<#--
left导航标签：用于显示left menu。递归实现, 可显示多级.
-->
<#macro leftmenu>
	<#list LeftMenu?if_exists as menu>
		<#if menu.leaf>
			<@sec.authorize url="${menu.href}">
				<a href="${menu.href}" class="list-group-item"> <i
					class="fa-left-panel-icon ${menu.iconClass}"></i><span class="list-text">${menu.name}</span></a>
			</@sec.authorize>
		<#elseif !menu.leaf >
			<div class="list-divider">${menu.name}</div>
			<#assign LeftMenu=menu.subMenuItems.menuItem />
			<@p.leftmenu />
		</#if>
	</#list>
</#macro>
