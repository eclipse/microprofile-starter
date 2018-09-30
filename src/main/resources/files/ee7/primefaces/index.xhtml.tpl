<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:p="http://primefaces.org/ui">

<h:head>
    <title>Title</title>

</h:head>

<h:body>

    <h:form id="frm">
        Name : <h:inputText value="#{helloBean.name}" id="name"/>
        <p:commandButton value="Say hello" actionListener="#{helloBean.sayHello()}" id="greetingBtn"
            ajax="false"/>

    </h:form>
    #{helloBean.helloText}


</h:body>

</html>
