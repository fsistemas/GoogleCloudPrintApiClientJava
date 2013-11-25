package co.com.google.cloud.print.acl;

public enum GoogleCloudPrintType {
	USER("USER"),
	GROUP("GROUP"),
	DOMAIN("DOMAIN");

	private String name;

	GoogleCloudPrintType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}