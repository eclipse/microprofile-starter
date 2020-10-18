package [# th:text="${java_package}"/].graphql.model;

@SuppressWarnings("serial")
public class UnknownCharacterException extends Exception {

    public UnknownCharacterException(String message) {
        super(message);
    }

}
