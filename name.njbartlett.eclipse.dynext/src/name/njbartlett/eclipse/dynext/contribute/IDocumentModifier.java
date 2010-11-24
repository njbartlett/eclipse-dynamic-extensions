package name.njbartlett.eclipse.dynext.contribute;

import org.jdom.Document;

public interface IDocumentModifier {
	void modify(Document document) throws ContributionProcessingException;
}
