<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayMessage=!messagesPerField.existsError('username','password')
    displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??;
    section>

  <#if section = "header">
    Chào mừng bạn trở lại
  <#elseif section = "form">

    <#if realm.password>
      <form id="kc-form-login" action="${url.loginAction}" method="post"
            onsubmit="document.getElementById('kc-login').disabled=true; return true;">
        <div class="gs-fields">

        <#-- Username / Email -->
        <#if !usernameHidden??>
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
            <input id="username" name="username" type="text"
                   class="gs-input <#if messagesPerField.existsError('username','password')>gs-input--error</#if>"
                   value="${(login.username!'')}"
                   placeholder="Ví dụ: customer@gymstore.local"
                   autofocus autocomplete="username"
                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
            <#if messagesPerField.existsError('username','password')>
              <span class="gs-field-error">
                <i class="fas fa-circle-exclamation"></i>
                ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
              </span>
            </#if>
          </div>
        </#if>

        <#-- Password -->
        <div class="gs-form-group">
          <div class="gs-label-row">
            <label class="gs-label" for="password">
              <i class="fas fa-lock"></i> Mật khẩu
            </label>
            <#if realm.resetPasswordAllowed>
              <a href="${url.loginResetCredentialsUrl}" class="gs-forgot-link">
                Quên mật khẩu?
              </a>
            </#if>
          </div>
          <div class="gs-input-group">
            <input id="password" name="password" type="password"
                   class="gs-input <#if messagesPerField.existsError('username','password')>gs-input--error</#if>"
                   placeholder="Nhập mật khẩu..."
                   autocomplete="current-password"
                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"/>
            <button type="button" class="gs-btn-eye"
                    aria-label="${msg('showPassword')}"
                    aria-controls="password"
                    data-password-toggle
                    data-icon-show="fas fa-eye"
                    data-icon-hide="fas fa-eye-slash">
              <i class="fas fa-eye"></i>
            </button>
          </div>
          <#if usernameHidden?? && messagesPerField.existsError('username','password')>
            <span class="gs-field-error">
              <i class="fas fa-circle-exclamation"></i>
              ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
            </span>
          </#if>
        </div>

        <#-- Remember me -->
        <#if realm.rememberMe && !usernameHidden??>
          <div class="gs-form-options">
            <label class="gs-checkbox-label">
              <input id="rememberMe" name="rememberMe" type="checkbox"
                     <#if login.rememberMe??>checked</#if>/>
              <span>Ghi nhớ đăng nhập</span>
            </label>
          </div>
        </#if>

        <input type="hidden" name="credentialId"
               <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>

        <button id="kc-login" type="submit" class="gs-btn gs-btn--primary gs-btn--block">
          <i class="fas fa-sign-in-alt"></i> Đăng nhập
        </button>

        </div><!-- /.gs-fields -->
      </form>
    </#if>

    <script type="module" src="${url.resourcesPath}/js/passwordVisibility.js"></script>

  <#elseif section = "info">
    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
      <div class="gs-signup">
        Chưa có tài khoản?
        <a href="${url.registrationUrl}">Đăng ký ngay</a>
      </div>
    </#if>

  <#elseif section = "socialProviders">
    <#if realm.password && social.providers??>
      <div class="gs-social">
        <div class="gs-divider"><span>hoặc đăng nhập với</span></div>
        <ul class="gs-social__list">
          <#list social.providers as p>
            <li>
              <a id="social-${p.alias}" class="gs-social__btn" href="${p.loginUrl}">
                <#if p.iconClasses?has_content>
                  <i class="${p.iconClasses!}"></i>
                </#if>
                <span>${p.displayName!}</span>
              </a>
            </li>
          </#list>
        </ul>
      </div>
    </#if>
  </#if>

</@layout.registrationLayout>
