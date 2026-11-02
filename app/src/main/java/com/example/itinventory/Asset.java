package com.example.itinventory;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

/**
 * Main Asset entity mapped to Firebase Cloud Firestore.
 * 
 * To query by nested fields in Firestore, use dot notation.
 * Example:
 * db.collection("assets").whereEqualTo("assignment.dept", "IT")
 */
public class Asset implements Serializable {

    @DocumentId
    private String documentId; // Acts as the ASSET NO if it's the document key

    @PropertyName("core_details")
    private CoreDetails coreDetails;
    private Assignment assignment;
    private Hardware hardware;
    private Software software;
    private Procurement procurement;
    private Metadata metadata;

    // Required empty constructor for Firestore deserialization
    public Asset() {}

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    @PropertyName("core_details")
    public CoreDetails getCoreDetails() { return coreDetails; }
    @PropertyName("core_details")
    public void setCoreDetails(CoreDetails coreDetails) { this.coreDetails = coreDetails; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public Hardware getHardware() { return hardware; }
    public void setHardware(Hardware hardware) { this.hardware = hardware; }

    public Software getSoftware() { return software; }
    public void setSoftware(Software software) { this.software = software; }

    public Procurement getProcurement() { return procurement; }
    public void setProcurement(Procurement procurement) { this.procurement = procurement; }

    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }

    // --- NESTED CLASSES ---

    public static class CoreDetails implements Serializable {
        @PropertyName("asset_no")
        private String assetNo;
        private String serial;
        private String type;
        private String manufacturer;
        private String model;
        @PropertyName("comp_name")
        private String compName;
        private String ad;
        @PropertyName("box_locate")
        private String boxLocate;

        public CoreDetails() {}

        @PropertyName("asset_no")
        public String getAssetNo() { return assetNo; }
        @PropertyName("asset_no")
        public void setAssetNo(String assetNo) { this.assetNo = assetNo; }

        public String getSerial() { return serial; }
        public void setSerial(String serial) { this.serial = serial; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getManufacturer() { return manufacturer; }
        public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        @PropertyName("comp_name")
        public String getCompName() { return compName; }
        @PropertyName("comp_name")
        public void setCompName(String compName) { this.compName = compName; }

        public String getAd() { return ad; }
        public void setAd(String ad) { this.ad = ad; }

        @PropertyName("box_locate")
        public String getBoxLocate() { return boxLocate; }
        @PropertyName("box_locate")
        public void setBoxLocate(String boxLocate) { this.boxLocate = boxLocate; }
    }

    public static class Assignment implements Serializable {
        private String user;
        private String dept;
        private String company;
        private String location;
        @PropertyName("last_use")
        private String lastUse;

        public Assignment() {}

        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }

        public String getDept() { return dept; }
        public void setDept(String dept) { this.dept = dept; }

        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        @PropertyName("last_use")
        public String getLastUse() { return lastUse; }
        @PropertyName("last_use")
        public void setLastUse(String lastUse) { this.lastUse = lastUse; }
    }

    public static class Hardware implements Serializable {
        private String processor;
        private String ram;
        private String hardisk;
        @PropertyName("additional_hardware")
        private String additionalHardware;

        public Hardware() {}

        public String getProcessor() { return processor; }
        public void setProcessor(String processor) { this.processor = processor; }

        public String getRam() { return ram; }
        public void setRam(String ram) { this.ram = ram; }

        public String getHardisk() { return hardisk; }
        public void setHardisk(String hardisk) { this.hardisk = hardisk; }

        @PropertyName("additional_hardware")
        public String getAdditionalHardware() { return additionalHardware; }
        @PropertyName("additional_hardware")
        public void setAdditionalHardware(String additionalHardware) { this.additionalHardware = additionalHardware; }
    }

    public static class Software implements Serializable {
        private OS os;
        private Office office;
        private Antivirus antivirus;
        @PropertyName("other_app")
        private String otherApp;

        public Software() {}

        public OS getOs() { return os; }
        public void setOs(OS os) { this.os = os; }

        public Office getOffice() { return office; }
        public void setOffice(Office office) { this.office = office; }

        public Antivirus getAntivirus() { return antivirus; }
        public void setAntivirus(Antivirus antivirus) { this.antivirus = antivirus; }

        @PropertyName("other_app")
        public String getOtherApp() { return otherApp; }
        @PropertyName("other_app")
        public void setOtherApp(String otherApp) { this.otherApp = otherApp; }

        public static class OS implements Serializable {
            private String name;
            private String bit;
            @PropertyName("product_key")
            private String productKey;

            public OS() {}

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getBit() { return bit; }
            public void setBit(String bit) { this.bit = bit; }

            @PropertyName("product_key")
            public String getProductKey() { return productKey; }
            @PropertyName("product_key")
            public void setProductKey(String productKey) { this.productKey = productKey; }
        }

        public static class Office implements Serializable {
            private String version;
            @PropertyName("licence_key")
            private String licenceKey;

            public Office() {}

            public String getVersion() { return version; }
            public void setVersion(String version) { this.version = version; }

            @PropertyName("licence_key")
            public String getLicenceKey() { return licenceKey; }
            @PropertyName("licence_key")
            public void setLicenceKey(String licenceKey) { this.licenceKey = licenceKey; }
        }

        public static class Antivirus implements Serializable {
            private String name;
            @PropertyName("product_key")
            private String productKey;
            @PropertyName("start_date")
            private String startDate;
            @PropertyName("expiry_date")
            private String expiryDate;
            @PropertyName("last_update")
            private String lastUpdate;

            public Antivirus() {}

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            @PropertyName("product_key")
            public String getProductKey() { return productKey; }
            @PropertyName("product_key")
            public void setProductKey(String productKey) { this.productKey = productKey; }

            @PropertyName("start_date")
            public String getStartDate() { return startDate; }
            @PropertyName("start_date")
            public void setStartDate(String startDate) { this.startDate = startDate; }

            @PropertyName("expiry_date")
            public String getExpiryDate() { return expiryDate; }
            @PropertyName("expiry_date")
            public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

            @PropertyName("last_update")
            public String getLastUpdate() { return lastUpdate; }
            @PropertyName("last_update")
            public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
        }
    }

    public static class Procurement implements Serializable {
        private String supplier;
        @PropertyName("capex_no")
        private String capexNo;
        @PropertyName("po_no")
        private String poNo;
        @PropertyName("po_date")
        private String poDate;
        @PropertyName("invoice_no")
        private String invoiceNo;
        private Double amount;
        private String warranty;

        public Procurement() {}

        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }

        @PropertyName("capex_no")
        public String getCapexNo() { return capexNo; }
        @PropertyName("capex_no")
        public void setCapexNo(String capexNo) { this.capexNo = capexNo; }

        @PropertyName("po_no")
        public String getPoNo() { return poNo; }
        @PropertyName("po_no")
        public void setPoNo(String poNo) { this.poNo = poNo; }

        @PropertyName("po_date")
        public String getPoDate() { return poDate; }
        @PropertyName("po_date")
        public void setPoDate(String poDate) { this.poDate = poDate; }

        @PropertyName("invoice_no")
        public String getInvoiceNo() { return invoiceNo; }
        @PropertyName("invoice_no")
        public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getWarranty() { return warranty; }
        public void setWarranty(String warranty) { this.warranty = warranty; }
    }

    public static class Metadata implements Serializable {
        private String remark1;
        private String remark2;
        @PropertyName("file_seq")
        private String fileSeq;

        public Metadata() {}

        public String getRemark1() { return remark1; }
        public void setRemark1(String remark1) { this.remark1 = remark1; }

        public String getRemark2() { return remark2; }
        public void setRemark2(String remark2) { this.remark2 = remark2; }

        @PropertyName("file_seq")
        public String getFileSeq() { return fileSeq; }
        @PropertyName("file_seq")
        public void setFileSeq(String fileSeq) { this.fileSeq = fileSeq; }
    }
}
