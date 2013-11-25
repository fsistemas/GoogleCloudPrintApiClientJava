package co.com.google.cloud.print.dto;

public class GetPrinterInputDto {
	private String printerId;
	private String useCdd;
	private String extraFields;
	private String printerConnectionStatus;

	public String getPrinterId() {
		return printerId;
	}
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
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
	public String getPrinterConnectionStatus() {
		return printerConnectionStatus;
	}
	public void setPrinterConnectionStatus(String printerConnectionStatus) {
		this.printerConnectionStatus = printerConnectionStatus;
	}
}