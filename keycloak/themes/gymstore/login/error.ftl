<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>

  <#if section = "header">
    Đã xảy ra lỗi
  <#elseif section = "form">
    <div class="gs-error-box">
      <div class="gs-error-box__icon">
        <i class="fas fa-triangle-exclamation"></i>
      </div>
      <p class="gs-error-box__msg">${kcSanitize(message.summary)?no_esc}</p>
      <#if !skipLink??>
        <#if client?? && client.baseUrl?has_content>
          <a href="${client.baseUrl}" class="gs-btn gs-btn--secondary">
            <i class="fas fa-arrow-left"></i> Quay lại ứng dụng
          </a>
        </#if>
      </#if>
    </div>
  </#if>

</@layout.registrationLayout>
