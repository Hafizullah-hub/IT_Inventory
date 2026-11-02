package com.example.itinventory;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssetDetailActivity extends AppCompatActivity {

    private LinearLayout containerCategories;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvAssetType;

    // List of sensitive fields that should be masked
    private final List<String> secureFields = Arrays.asList(
            "product_key", "licence_key", "amount"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);

        containerCategories = findViewById(R.id.containerCategories);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        tvAssetType = findViewById(R.id.tvAssetType);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Handle back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get passed Asset data
        Asset asset = (Asset) getIntent().getSerializableExtra("asset_data");
        if (asset != null) {
            populateData(asset);
        } else {
            setupMockData();
        }
    }

    private void populateData(Asset asset) {
        // Set Header info
        String assetNo = "Unknown";
        String type = "Unknown";
        
        if (asset.getCoreDetails() != null) {
            assetNo = asset.getCoreDetails().getAssetNo() != null ? asset.getCoreDetails().getAssetNo() : assetNo;
            type = asset.getCoreDetails().getType() != null ? asset.getCoreDetails().getType() : type;
        } else if (asset.getDocumentId() != null) {
            assetNo = asset.getDocumentId();
        }
        
        collapsingToolbar.setTitle(assetNo);
        tvAssetType.setText(type);

        // 1. Core Details
        Map<String, String> coreDetails = new LinkedHashMap<>();
        if (asset.getCoreDetails() != null) {
            Asset.CoreDetails cd = asset.getCoreDetails();
            coreDetails.put("serial", cd.getSerial());
            coreDetails.put("manufacturer", cd.getManufacturer());
            coreDetails.put("model", cd.getModel());
            coreDetails.put("comp_name", cd.getCompName());
            coreDetails.put("ad", cd.getAd());
            coreDetails.put("box_locate", cd.getBoxLocate());
        }
        addCategoryCard("Core Details", coreDetails);

        // 2. Assignment
        Map<String, String> assignment = new LinkedHashMap<>();
        if (asset.getAssignment() != null) {
            Asset.Assignment as = asset.getAssignment();
            assignment.put("user", as.getUser());
            assignment.put("dept", as.getDept());
            assignment.put("company", as.getCompany());
            assignment.put("location", as.getLocation());
            assignment.put("last_use", as.getLastUse());
        }
        addCategoryCard("Assignment", assignment);

        // 3. Hardware
        Map<String, String> hardware = new LinkedHashMap<>();
        if (asset.getHardware() != null) {
            Asset.Hardware hw = asset.getHardware();
            hardware.put("processor", hw.getProcessor());
            hardware.put("ram", hw.getRam());
            hardware.put("hardisk", hw.getHardisk());
            hardware.put("additional_hardware", hw.getAdditionalHardware());
        }
        addCategoryCard("Hardware", hardware);

        // 4. Software
        Map<String, String> software = new LinkedHashMap<>();
        if (asset.getSoftware() != null) {
            Asset.Software sw = asset.getSoftware();
            if (sw.getOs() != null) {
                software.put("os_name", sw.getOs().getName());
                software.put("os_bit", sw.getOs().getBit());
                software.put("product_key", sw.getOs().getProductKey());
            }
            if (sw.getOffice() != null) {
                software.put("office_version", sw.getOffice().getVersion());
                software.put("licence_key", sw.getOffice().getLicenceKey());
            }
            if (sw.getAntivirus() != null) {
                software.put("antivirus_name", sw.getAntivirus().getName());
                software.put("antivirus_product_key", sw.getAntivirus().getProductKey());
                software.put("start_date", sw.getAntivirus().getStartDate());
                software.put("expiry_date", sw.getAntivirus().getExpiryDate());
                software.put("last_update", sw.getAntivirus().getLastUpdate());
            }
            software.put("other_app", sw.getOtherApp());
        }
        addCategoryCard("Software", software);

        // 5. Procurement
        Map<String, String> procurement = new LinkedHashMap<>();
        if (asset.getProcurement() != null) {
            Asset.Procurement pr = asset.getProcurement();
            procurement.put("supplier", pr.getSupplier());
            procurement.put("capex_no", pr.getCapexNo());
            procurement.put("po_no", pr.getPoNo());
            procurement.put("po_date", pr.getPoDate());
            procurement.put("invoice_no", pr.getInvoiceNo());
            procurement.put("amount", pr.getAmount() != null ? String.valueOf(pr.getAmount()) : null);
            procurement.put("warranty", pr.getWarranty());
        }
        addCategoryCard("Procurement", procurement);

        // 6. Metadata
        Map<String, String> metadata = new LinkedHashMap<>();
        if (asset.getMetadata() != null) {
            Asset.Metadata md = asset.getMetadata();
            metadata.put("remark1", md.getRemark1());
            metadata.put("remark2", md.getRemark2());
            metadata.put("file_seq", md.getFileSeq());
        }
        addCategoryCard("Metadata", metadata);
    }

    private void setupMockData() {
        // Set Header info
        collapsingToolbar.setTitle("IT-LAP-001");
        tvAssetType.setText("Laptop");

        // 1. Core Details (6 attributes)
        Map<String, String> coreDetails = new LinkedHashMap<>();
        coreDetails.put("serial", "SN123456789");
        coreDetails.put("manufacturer", "Dell");
        coreDetails.put("model", "Latitude 5420");
        coreDetails.put("comp_name", "IT-LAP-001");
        coreDetails.put("ad", "Yes");
        coreDetails.put("box_locate", "Shelf A");
        addCategoryCard("Core Details", coreDetails);

        // 2. Assignment (5 attributes)
        Map<String, String> assignment = new LinkedHashMap<>();
        assignment.put("user", "John Doe");
        assignment.put("dept", "IT Support");
        assignment.put("company", "George Kent (Malaysia) Berhad");
        assignment.put("location", "Puchong HQ");
        assignment.put("last_use", "2023-10-25");
        addCategoryCard("Assignment", assignment);

        // 3. Hardware (4 attributes)
        Map<String, String> hardware = new LinkedHashMap<>();
        hardware.put("processor", "Intel Core i7-1185G7");
        hardware.put("ram", "16GB DDR4");
        hardware.put("hardisk", "512GB NVMe SSD");
        hardware.put("additional_hardware", "Dell WD19 Docking Station");
        addCategoryCard("Hardware", hardware);

        // 4. Software (7 attributes - flattened)
        Map<String, String> software = new LinkedHashMap<>();
        software.put("os_name", "Windows 11 Pro");
        software.put("os_bit", "64-bit");
        software.put("product_key", "W269N-WFGWX-YVC9B-4J6C9-T83GX");
        software.put("office_version", "Microsoft Office 2021 Professional");
        software.put("licence_key", "NMMKJ-6RK4F-KMJVX-8D9MJ-6PBKP");
        software.put("antivirus_name", "Kaspersky Endpoint Security");
        software.put("other_app", "Adobe Acrobat Reader, AutoCAD LT");
        addCategoryCard("Software", software);

        // 5. Procurement (7 attributes)
        Map<String, String> procurement = new LinkedHashMap<>();
        procurement.put("supplier", "Tech Data Malaysia Sdn Bhd");
        procurement.put("capex_no", "CAPEX-2023-0145");
        procurement.put("po_no", "PO-4500012345");
        procurement.put("po_date", "2023-01-15");
        procurement.put("invoice_no", "INV-2023-9988");
        procurement.put("amount", "RM 4,500.00");
        procurement.put("warranty", "3 Years ProSupport");
        addCategoryCard("Procurement", procurement);

        // 6. Metadata (3 attributes)
        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put("remark1", "Assigned as primary workstation.");
        metadata.put("remark2", "Requires RAM upgrade next year.");
        metadata.put("file_seq", "SEQ-001-A");
        addCategoryCard("Metadata", metadata);
    }

    /**
     * Inflates a new MaterialCardView for a category and populates its fields.
     */
    private void addCategoryCard(String categoryTitle, Map<String, String> attributes) {
        View cardView = LayoutInflater.from(this).inflate(R.layout.layout_category_card, containerCategories, false);
        
        LinearLayout headerLayout = cardView.findViewById(R.id.headerLayout);
        TextView tvCategoryTitle = cardView.findViewById(R.id.tvCategoryTitle);
        ImageView ivToggleIcon = cardView.findViewById(R.id.ivToggleIcon);
        View divider = cardView.findViewById(R.id.divider);
        LinearLayout contentContainer = cardView.findViewById(R.id.contentContainer);

        tvCategoryTitle.setText(categoryTitle);

        // Populate detail rows inside the content container
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue() : "N/A";
            
            if (secureFields.contains(key)) {
                addSecureDetailRow(contentContainer, formatLabel(key), value);
            } else {
                addDetailRow(contentContainer, formatLabel(key), value);
            }
        }

        // Set up the expand/collapse (Accordion) logic
        headerLayout.setOnClickListener(v -> {
            boolean isExpanded = contentContainer.getVisibility() == View.VISIBLE;
            if (isExpanded) {
                contentContainer.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
                ivToggleIcon.setImageResource(R.drawable.ic_expand_more);
            } else {
                contentContainer.setVisibility(View.VISIBLE);
                divider.setVisibility(View.VISIBLE);
                ivToggleIcon.setImageResource(R.drawable.ic_expand_less);
            }
        });

        // Expand the first category by default (Core Details)
        if (categoryTitle.equals("Core Details")) {
            contentContainer.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            ivToggleIcon.setImageResource(R.drawable.ic_expand_less);
        }

        containerCategories.addView(cardView);
    }

    /**
     * Adds a standard label-value row to a category container.
     */
    private void addDetailRow(LinearLayout container, String label, String value) {
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_detail_row, container, false);
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);

        tvLabel.setText(label);
        tvValue.setText(value);

        container.addView(rowView);
    }

    /**
     * Adds a secure row with a "Tap to Reveal" eye toggle logic.
     */
    private void addSecureDetailRow(LinearLayout container, String label, String actualValue) {
        View rowView = LayoutInflater.from(this).inflate(R.layout.item_secure_detail_row, container, false);
        TextView tvLabel = rowView.findViewById(R.id.tvLabel);
        TextView tvValue = rowView.findViewById(R.id.tvValue);
        ImageView ivVisibilityToggle = rowView.findViewById(R.id.ivVisibilityToggle);

        tvLabel.setText(label);
        
        // Initial state: Masked
        final String maskedValue = "••••••••••••";
        tvValue.setText(maskedValue);
        
        // Toggle Logic
        final boolean[] isVisible = {false};
        
        ivVisibilityToggle.setOnClickListener(v -> {
            isVisible[0] = !isVisible[0];
            if (isVisible[0]) {
                tvValue.setText(actualValue);
                ivVisibilityToggle.setImageResource(R.drawable.ic_visibility);
            } else {
                tvValue.setText(maskedValue);
                ivVisibilityToggle.setImageResource(R.drawable.ic_visibility_off);
            }
        });

        container.addView(rowView);
    }

    /**
     * Helper to format raw keys (e.g., "po_no") into readable labels (e.g., "Po No").
     */
    private String formatLabel(String key) {
        String noUnderscore = key.replace("_", " ");
        String[] words = noUnderscore.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                builder.append(Character.toUpperCase(word.charAt(0)))
                       .append(word.substring(1).toLowerCase())
                       .append(" ");
            }
        }
        return builder.toString().trim();
    }
}
