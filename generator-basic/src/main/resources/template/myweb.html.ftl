<!DOCTYPE html>
<html>
    <head>
        <title> 我的官网</title>
    </head>
    <body>
    <h1>Welcome to 我的官网</h1>
    <ul>
    <#-- Loop rendering the navigation bar -->
        <#list menuItems as item>
            <li><a href="${item.url}">${item.label}</a></li>
        </#list>
    </ul>
    <#-- Bottom copyright information (commented section, will not be output) -->
    <footer>
        ${currentYear} 我的网站
    </footer>
    </body>
</html>
