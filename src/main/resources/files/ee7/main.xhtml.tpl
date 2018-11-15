<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">

<h:head>
    <title>Title</title>
</h:head>

<h:form>
    authenticated user : #{loggedInUser}  <br/>
    <h:commandButton actionListener="#{loginBean.logout}" value="Logout"/>

    <br/>

    Name : <h:inputText value="#{helloBean.name}" id="name"/>
    <h:commandButton value="Say hello" actionListener="#{helloBean.sayHello()}" id="greetingBtn"/>

    #{helloBean.helloText}

</h:form>

</html>