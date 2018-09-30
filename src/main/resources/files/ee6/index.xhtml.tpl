<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html" xmlns:f="http://java.sun.com/jsf/core">

<h:head>
    <title>Title</title>

</h:head>

<h:body>

    <h:form id="frm">
        Name : <h:inputText value="#{helloBean.name}" id="name"/>
        <h:commandButton value="Say hello" actionListener="#{helloBean.sayHello()}" id="greetingBtn"/>

    </h:form>
    #{helloBean.helloText}


</h:body>

</html>
