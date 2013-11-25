package co.com.google.cloud.print;

public enum GoogleCloudPrintPrinterStatus {
	ONLINE("ONLINE"),
	UNKNOWN("UNKNOWN"),
	OFFLINE("OFFLINE"),
	DORMANT("DORMANT");

	private String name;

	GoogleCloudPrintPrinterStatus(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}