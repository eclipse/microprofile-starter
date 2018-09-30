<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:p="http://primefaces.org/ui>

<h:head>
    <title>Title</title>
</h:head>

<h:form>
    authenticated user : #{loggedInUser}  <br/>
    <p:commandButton actionListener="#{loginBean.logout}" value="Logout" ajax="false"/>

    <br/>

    Name : <p:inputText value="#{helloBean.name}" id="name"/>
    <p:commandButton value="Say hello" actionListener="#{helloBean.sayHello()}" id="greetingBtn"
        ajax="false"/>

    #{helloBean.helloText}

</h:form>

</html>