package name.njbartlett.eclipse.dynext.contribute;

public class ContributionProcessingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ContributionProcessingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContributionProcessingException(String message) {
		super(message);
	}

	public ContributionProcessingException(Throwable cause) {
		super(cause);
	}

}
