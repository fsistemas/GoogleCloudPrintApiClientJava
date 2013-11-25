package co.com.google.cloud.print;

public enum GoogleCloutPrintPrinterType {
	GOOGLE("GOOGLE"),
	HP("HP"),
	DRIVE("DRIVE"),
	FEDEX("FEDEX"),
	ANDROID_CHROME_SNAPSHOT("ANDROID_CHROME_SNAPSHOT"),
	IOS_CHROME_SNAPSHOT("IOS_CHROME_SNAPSHOT");

	private String name;

	GoogleCloutPrintPrinterType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}