<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayInfo=true
    displayMessage=!messagesPerField.existsError('username');
    section>

  <#if section = "header">
    Khôi phục mật khẩu
  <#elseif section = "form">
    <p class="gs-hint">
      <i class="fas fa-circle-info"></i>
      Nhập email đã đăng ký — chúng tôi sẽ gửi link đặt lại mật khẩu cho bạn.
    </p>
    <form id="kc-reset-password-form" action="${url.loginAction}" method="post">
      <div class="gs-fields">
      <div class="gs-form-group">
        <label class="gs-label" for="username">
          <i class="fas fa-envelope"></i>
          <#if !realm.loginWithEmailAllowed>
            Tên tài khoản
          <#elseif !realm.registrationEmailAsUsername>
            Email hoặc tên tài khoản
          <#else>
            Địa chỉ Email
          </#if>
        </label>
        <input type="text" id="username" name="username"
               class="gs-input <#if messagesPerField.existsError('username')>gs-input--error</#if>"
               placeholder="Nhập địa chỉ email của bạn"
               autofocus value="${(auth.attemptedUsername!'')}"
               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
        <#if messagesPerField.existsError('username')>
          <span class="gs-field-error">
            <i class="fas fa-circle-exclamation"></i>
            ${kcSanitize(messagesPerField.get('username'))?no_esc}
          </span>
        </#if>
      </div>

      <button type="submit" class="gs-btn gs-btn--primary gs-btn--block">
        <i class="fas fa-paper-plane"></i> Gửi link khôi phục
      </button>

      </div><!-- /.gs-fields -->

      <div class="gs-signup">
        <a href="${url.loginUrl}">
          <i class="fas fa-arrow-left"></i> Quay lại đăng nhập
        </a>
      </div>
    </form>
  </#if>

</@layout.registrationLayout>
