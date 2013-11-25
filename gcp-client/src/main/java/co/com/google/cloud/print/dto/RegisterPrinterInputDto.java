package co.com.google.cloud.print.dto;

public class RegisterPrinterInputDto {
	private String printer;
	private String defaultDisplayName;
	private String proxy;
	private String description;
	private String uuid;
	private String manufacturer;
	private String model;
	private String gcpVersion;
	private String setupUrl;
	private String supportUrl;
	private String updateUrl;
	private String firmware;
	private String localSettings;
	private String semanticState;
	private String useCdd;
	private String capabilities;
	private String defaults;
	private String capsHash;
	private String tag;
	private String data;

	@Deprecated
	private String contentTypes;

	public String getPrinter() {
		return printer;
	}
	public void setPrinter(String printer) {
		this.printer = printer;
	}
	public String getDefaultDisplayName() {
		return defaultDisplayName;
	}
	public void setDefaultDisplayName(String defaultDisplayName) {
		this.defaultDisplayName = defaultDisplayName;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getGcpVersion() {
		return gcpVersion;
	}
	public void setGcpVersion(String gcpVersion) {
		this.gcpVersion = gcpVersion;
	}
	public String getSetupUrl() {
		return setupUrl;
	}
	public void setSetupUrl(String setupUrl) {
		this.setupUrl = setupUrl;
	}
	public String getSupportUrl() {
		return supportUrl;
	}
	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}
	public String getUpdateUrl() {
		return updateUrl;
	}
	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public String getLocalSettings() {
		return localSettings;
	}
	public void setLocalSettings(String localSettings) {
		this.localSettings = localSettings;
	}
	public String getSemanticState() {
		return semanticState;
	}
	public void setSemanticState(String semanticState) {
		this.semanticState = semanticState;
	}
	public String getUseCdd() {
		return useCdd;
	}
	public void setUseCdd(String useCdd) {
		this.useCdd = useCdd;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public String getDefaults() {
		return defaults;
	}
	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	public String getCapsHash() {
		return capsHash;
	}
	public void setCapsHash(String capsHash) {
		this.capsHash = capsHash;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	@Deprecated
	public String getContentTypes() {
		return contentTypes;
	}

	@Deprecated
	public void setContentTypes(String contentTypes) {
		this.contentTypes = contentTypes;
	}
}