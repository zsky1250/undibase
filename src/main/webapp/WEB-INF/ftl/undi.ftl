<#ftl strip_whitespace=true>

<#--
 *
 * Macro to show login username.
 -->
<#macro username>${undiMacroRequestContext.getUsername()}</#macro>

<#--
 *
 * Macro to show user name.
 -->
<#macro name>${undiMacroRequestContext.getName()}</#macro>