package neostudy.deal.exceptions;

public class ApplicationAlreadyApprovedException extends RuntimeException {
    public ApplicationAlreadyApprovedException() {
    }

    public ApplicationAlreadyApprovedException(String message) {
        super(message);
    }
}
