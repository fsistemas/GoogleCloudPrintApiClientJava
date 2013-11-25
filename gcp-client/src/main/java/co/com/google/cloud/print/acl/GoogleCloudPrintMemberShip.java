package co.com.google.cloud.print.acl;

public enum GoogleCloudPrintMemberShip {
	NONE("NONE"),
	MEMBER("MEMBER"),
	MANAGER("MANAGER");

	private String name;

	GoogleCloudPrintMemberShip(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}