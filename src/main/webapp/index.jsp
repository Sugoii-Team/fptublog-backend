<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html itemscope itemtype="http://schema.org/Article">
<head>
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <meta name="google-signin-client_id" content="412413988682-r9de2fogj5hfnsd72vh5eqqdpsjhc1jo.apps.googleusercontent.com">
</head>
<body>

<div class="g-signin2" data-onsuccess="onSignIn"></div>
<script>
    function onSignIn(googleUser) {
        const id_token = googleUser.getAuthResponse().id_token;

        const xhr = new XMLHttpRequest();
        xhr.open('POST', 'http://localhost:8080/fptu-blog/api/auth');
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onload = function() {
            console.log('Signed in as: ' + xhr.responseText);
        };
        xhr.send('id_token=' + id_token);
    }
</script>

</body>
</html>
