package name.njbartlett.eclipse.dynext.scr;

class ExtensionFactoryAddress {
	private final String contributionXmlPath;
	private final String factoryId;

	public ExtensionFactoryAddress(String contributionXmlPath, String factoryId) {
		this.contributionXmlPath = contributionXmlPath;
		this.factoryId = factoryId;
	}
	
	public String getContributionXmlPath() {
		return contributionXmlPath;
	}
	
	public String getFactoryId() {
		return factoryId;
	}

	@Override
	public String toString() {
		return "DynamicExtensionFactoryReference [contributionXmlPath="
				+ contributionXmlPath + ", factoryId=" + factoryId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((contributionXmlPath == null) ? 0 : contributionXmlPath
						.hashCode());
		result = prime * result
				+ ((factoryId == null) ? 0 : factoryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtensionFactoryAddress other = (ExtensionFactoryAddress) obj;
		if (contributionXmlPath == null) {
			if (other.contributionXmlPath != null)
				return false;
		} else if (!contributionXmlPath.equals(other.contributionXmlPath))
			return false;
		if (factoryId == null) {
			if (other.factoryId != null)
				return false;
		} else if (!factoryId.equals(other.factoryId))
			return false;
		return true;
	}
	
	
	
}
