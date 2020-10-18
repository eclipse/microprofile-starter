package [# th:text="${java_package}"/].graphql.db;

@SuppressWarnings("serial")
public class UnknownTeamException extends Exception {

    public UnknownTeamException(String message) {
        super(message);
    }
}