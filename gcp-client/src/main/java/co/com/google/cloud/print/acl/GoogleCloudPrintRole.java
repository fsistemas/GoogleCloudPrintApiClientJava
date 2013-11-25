package co.com.google.cloud.print.acl;

public enum GoogleCloudPrintRole {
	USER("USER"),
	MANAGER("MANAGER"),
	OWNER("OWNER");

	private String name;

	GoogleCloudPrintRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}