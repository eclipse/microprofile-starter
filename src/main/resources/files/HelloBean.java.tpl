package [# th:text="${java_package}"/].view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 */
@RequestScoped
@Named  // Default helloBean
public class HelloBean {

    private String helloText;

    private String name;

    public void sayHello() {
        helloText = "Welcome "+name;
        name = null;
    }

    public String getHelloText() {
        return helloText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
