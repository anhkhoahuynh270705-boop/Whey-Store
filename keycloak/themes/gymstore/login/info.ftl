<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

  <#if section = "header">
    <#if messageHeader??>
      ${messageHeader}
    <#else>
      Thông báo
    </#if>
  <#elseif section = "form">
    <div class="gs-info-box">
      <div class="gs-info-box__icon">
        <i class="fas fa-circle-check"></i>
      </div>
      <p class="gs-info-box__msg">
        ${message.summary}
        <#if requiredActions??>
          <#list requiredActions>: <strong><#items as a>${kcSanitize(msg("requiredAction.${a}"))?no_esc}<#sep>, </#items></strong></#list>
        </#if>
      </p>
      <#if !skipLink??>
        <#if pageRedirectUri?has_content>
          <a href="${pageRedirectUri}" class="gs-btn gs-btn--primary">
            ${kcSanitize(msg("backToApplication"))?no_esc}
          </a>
        <#elseif actionUri?has_content>
          <a href="${actionUri}" class="gs-btn gs-btn--primary">
            ${kcSanitize(msg("proceedWithAction"))?no_esc}
          </a>
        <#elseif (client.baseUrl)?has_content>
          <a href="${client.baseUrl}" class="gs-btn gs-btn--secondary">
            <i class="fas fa-arrow-left"></i> Quay lại ứng dụng
          </a>
        </#if>
      </#if>
    </div>
  </#if>

</@layout.registrationLayout>
