<!DOCTYPE html >

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html">
<h:head>

</h:head>
<h:body>
    <h2>Login</h2>
    <h:form id="login">
        <h:panelGrid columns="2">
            <h:outputLabel for="username" value="Username:"/>
            <h:inputText id="username" value="#{loginBean.username}" required="true"/>

            <h:outputLabel for="password" value="Password:"/>
            <h:inputSecret id="password" value="#{loginBean.password}" required="true" />

            <h:panelGroup/>
            <h:commandButton value="Login" actionListener="#{loginBean.doLogin}" />

        </h:panelGrid>
        <h:messages />
    </h:form>
    For this example, password is the same as username and any username will be accepted.
</h:body>
</html>
