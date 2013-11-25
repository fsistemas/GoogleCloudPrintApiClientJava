package co.com.google.cloud.print.dto;

public class SearchPrinterInputDto {
	private String q;
	private String type;
	private String connectionStatus;
	private String useCdd;
	private String extraFields;

	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getConnectionStatus() {
		return connectionStatus;
	}
	public void setConnectionStatus(String connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
	public String getUseCdd() {
		return useCdd;
	}
	public void setUseCdd(String useCdd) {
		this.useCdd = useCdd;
	}
	public String getExtraFields() {
		return extraFields;
	}
	public void setExtraFields(String extraFields) {
		this.extraFields = extraFields;
	}
}