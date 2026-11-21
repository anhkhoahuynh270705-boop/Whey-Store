<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html lang="<#if locale?? && locale.currentLanguageTag??>${locale.currentLanguageTag}<#else>vi</#if>"><head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <meta name="robots" content="noindex, nofollow"/>
  <title>${msg("loginTitle",(realm.displayName!'GymStore'))}</title>

  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous"/>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>

  <#if properties.styles?has_content>
    <#list properties.styles?split(' ') as style>
      <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
    </#list>
  </#if>

  <#if authenticationSession??>
    <script type="module">
      import { checkCookiesAndSetTimer } from "${url.resourcesPath}/js/authChecker.js";
      checkCookiesAndSetTimer(
        "${authenticationSession.authSessionId}",
        "${authenticationSession.tabId}",
        "${url.ssoLoginInOtherTabsUrl?no_esc}"
      );
    </script>
  </#if>
</head>

<body class="gs-body">
<div class="gs-page">

  <div class="gs-card">

    <!-- ── Brand header ── -->
    <div class="gs-brand">
      <span class="gs-brand__icon"><i class="fas fa-dumbbell"></i></span>
      <span class="gs-brand__name">GYM<span class="gs-brand__accent">STORE</span></span>
    </div>

    <!-- ── Page title ── -->
    <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
      <h1 class="gs-title"><#nested "header"></h1>
    <#else>
      <#nested "show-username">
      <div class="gs-attempted-user">
        <span>${auth.attemptedUsername}</span>
        <a href="${url.loginRestartFlowUrl}" class="gs-restart-link" title="${msg('restartLoginTooltip')}">
          <i class="fas fa-rotate-left"></i>
        </a>
      </div>
    </#if>

    <!-- ── Flash message ── -->
    <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
      <div class="gs-alert gs-alert--${message.type}">
        <#if message.type = 'error'><i class="fas fa-circle-exclamation"></i></#if>
        <#if message.type = 'success'><i class="fas fa-circle-check"></i></#if>
        <#if message.type = 'warning'><i class="fas fa-triangle-exclamation"></i></#if>
        <#if message.type = 'info'><i class="fas fa-circle-info"></i></#if>
        <span>${kcSanitize(message.summary)?no_esc}</span>
      </div>
    </#if>

    <!-- ── Main form slot ── -->
    <#nested "form">

    <!-- ── Try another way ── -->
    <#if auth?has_content && auth.showTryAnotherWayLink()>
      <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post" class="gs-try-another">
        <input type="hidden" name="tryAnotherWay" value="on"/>
        <a href="#" onclick="document.forms['kc-select-try-another-way-form'].submit();return false;">
          ${msg("doTryAnotherWay")}
        </a>
      </form>
    </#if>

    <!-- ── Social providers slot ── -->
    <#nested "socialProviders">

    <!-- ── Info / register link slot ── -->
    <#if displayInfo>
      <div class="gs-info">
        <#nested "info">
      </div>
    </#if>

  </div><!-- /.gs-card -->

</div><!-- /.gs-page -->
</body>
</html>
</#macro>
