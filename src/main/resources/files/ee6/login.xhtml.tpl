<!DOCTYPE html >

<html xmlns="http://www.w3.org/1999/xhtml" xmlns:h="http://java.sun.com/jsf/html"
      xmlns:p="http://primefaces.org/ui">
<h:head>

</h:head>
<h:body>
    <h2>Login</h2>
    <h:form id="login">
        <h:panelGrid columns="2">
            <p:outputLabel for="username" value="Username:"/>
            <p:inputText id="username" value="#{loginBean.username}" required="true"/>

            <p:outputLabel for="password" value="Password:"/>
            <p:password id="password" value="#{loginBean.password}" required="true" feedback="" />

            <h:panelGroup/>
            <p:commandButton value="Login" actionListener="#{loginBean.doLogin}" update="@form" process="@form"/>

        </h:panelGrid>
        <p:messages />
    </h:form>
    For this example, password is the same as username and any username will be accepted.
</h:body>
</html>
