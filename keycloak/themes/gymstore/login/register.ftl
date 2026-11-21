<#import "template.ftl" as layout>
<@layout.registrationLayout
    displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm');
    section>

  <#if section = "header">
    Tạo tài khoản GymStore
  <#elseif section = "form">
    <form id="kc-register-form" action="${url.registrationAction}" method="post">
      <div class="gs-fields">

      <#-- Tên & Họ trên cùng hàng -->
      <div class="gs-form-row">
        <div class="gs-form-group">
          <label class="gs-label" for="firstName">
            <i class="fas fa-user"></i> Tên
          </label>
          <input type="text" id="firstName" name="firstName" class="gs-input <#if messagesPerField.existsError('firstName')>gs-input--error</#if>"
                 value="${(register.formData.firstName!'')}"
                 placeholder="Tên của bạn"
                 aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"/>
          <#if messagesPerField.existsError('firstName')>
            <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('firstName'))?no_esc}</span>
          </#if>
        </div>

        <div class="gs-form-group">
          <label class="gs-label" for="lastName">
            <i class="fas fa-user"></i> Họ
          </label>
          <input type="text" id="lastName" name="lastName" class="gs-input <#if messagesPerField.existsError('lastName')>gs-input--error</#if>"
                 value="${(register.formData.lastName!'')}"
                 placeholder="Họ của bạn"
                 aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"/>
          <#if messagesPerField.existsError('lastName')>
            <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('lastName'))?no_esc}</span>
          </#if>
        </div>
      </div>

      <#-- Email -->
      <div class="gs-form-group">
        <label class="gs-label" for="email">
          <i class="fas fa-envelope"></i> Địa chỉ Email
        </label>
        <input type="email" id="email" name="email" class="gs-input <#if messagesPerField.existsError('email')>gs-input--error</#if>"
               value="${(register.formData.email!'')}"
               placeholder="Ví dụ: nguyenvan@gmail.com"
               autocomplete="email"
               aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"/>
        <#if messagesPerField.existsError('email')>
          <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('email'))?no_esc}</span>
        </#if>
      </div>

      <#-- Username (nếu realm không dùng email làm username) -->
      <#if !realm.registrationEmailAsUsername>
        <div class="gs-form-group">
          <label class="gs-label" for="username">
            <i class="fas fa-at"></i> Tên đăng nhập
          </label>
          <input type="text" id="username" name="username" class="gs-input <#if messagesPerField.existsError('username')>gs-input--error</#if>"
                 value="${(register.formData.username!'')}"
                 autocomplete="username"
                 aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"/>
          <#if messagesPerField.existsError('username')>
            <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('username'))?no_esc}</span>
          </#if>
        </div>
      </#if>

      <#-- Password & Confirm trên cùng hàng -->
      <#if passwordRequired??>
        <div class="gs-form-row">
          <div class="gs-form-group">
            <label class="gs-label" for="password">
              <i class="fas fa-lock"></i> Mật khẩu
            </label>
            <div class="gs-input-group">
              <input type="password" id="password" name="password"
                     class="gs-input <#if messagesPerField.existsError('password','password-confirm')>gs-input--error</#if>"
                     placeholder="Tối thiểu 8 ký tự"
                     autocomplete="new-password"
                     aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"/>
              <button type="button" class="gs-btn-eye"
                      data-password-toggle
                      aria-controls="password"
                      data-icon-show="fas fa-eye"
                      data-icon-hide="fas fa-eye-slash">
                <i class="fas fa-eye"></i>
              </button>
            </div>
            <#if messagesPerField.existsError('password')>
              <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('password'))?no_esc}</span>
            </#if>
          </div>

          <div class="gs-form-group">
            <label class="gs-label" for="password-confirm">
              <i class="fas fa-shield-halved"></i> Xác nhận mật khẩu
            </label>
            <div class="gs-input-group">
              <input type="password" id="password-confirm" name="password-confirm"
                     class="gs-input <#if messagesPerField.existsError('password-confirm')>gs-input--error</#if>"
                     placeholder="Nhập lại mật khẩu"
                     aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"/>
              <button type="button" class="gs-btn-eye"
                      data-password-toggle
                      aria-controls="password-confirm"
                      data-icon-show="fas fa-eye"
                      data-icon-hide="fas fa-eye-slash">
                <i class="fas fa-eye"></i>
              </button>
            </div>
            <#if messagesPerField.existsError('password-confirm')>
              <span class="gs-field-error"><i class="fas fa-circle-exclamation"></i> ${kcSanitize(messagesPerField.get('password-confirm'))?no_esc}</span>
            </#if>
          </div>
        </div>
      </#if>

      <#-- Recaptcha -->
      <#if recaptchaRequired??>
        <div class="gs-form-group">
          <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
        </div>
      </#if>

      <#-- Submit -->
      <button type="submit" class="gs-btn gs-btn--primary gs-btn--block" style="margin-top:8px;">
        <i class="fas fa-user-check"></i> Tạo tài khoản
      </button>

      </div><!-- /.gs-fields -->

      <div class="gs-signup">
        Đã có tài khoản?
        <a href="${url.loginUrl}">Đăng nhập</a>
      </div>

    </form>
    <script type="module" src="${url.resourcesPath}/js/passwordVisibility.js"></script>
  </#if>

</@layout.registrationLayout>
