package com.vippygames.bianic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.consts.ReportsConsts;
import com.vippygames.bianic.db.DetailedReports.DetailedReportsDb;
import com.vippygames.bianic.db.DetailedReports.DetailedReportsRecord;
import com.vippygames.bianic.db.Reports.ReportsRecord;
import com.vippygames.bianic.utils.StringUtils;

import java.util.List;

public class DetailedReportActivity extends AppCompatActivity {
    private TableLayout detailedReportTable;
    // Define a counter to keep track of the number of rows added
    int counter = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed_report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.back) {
            handleActionBack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        // Get the table layout and the button from the XML file
        TableLayout table = findViewById(R.id.table);
        Button addRow = findViewById(R.id.add_row);

// Define some sample data for the table cells
        String[][] data = {
                {"Name", "Age", "Gender"},
                {"Alice", "25", "Female"},
                {"Bob", "30", "Male"},
                {"Charlie", "35", "Male"},
                {"David", "40", "Male"},
                {"Eve", "45", "Female"}
        };

// Define some colors for the table cells
        int[] colors = {
                Color.parseColor("#FFC107"), // Yellow
                Color.parseColor("#4CAF50"), // Green
                Color.parseColor("#F44336"), // Red
                Color.parseColor("#2196F3"), // Blue
                Color.parseColor("#9C27B0")  // Purple
        };

// Define a listener for the button click event
        addRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there are more rows to add
                if (true) {
                    // Create a new table row
                    TableRow row = new TableRow(DetailedReportActivity.this);

                    // Loop through the columns of the current row
                    for (int i = 0; i < 3; i++) {
                        // Create a new text view for the cell
                        TextView cell = new TextView(DetailedReportActivity.this);

                        // Set the text, text size, text color, background color, padding and gravity of the cell
                        cell.setText(data[0][i]);
                        cell.setTextSize(18);
                        cell.setTextColor(Color.WHITE);
                        cell.setBackgroundColor(colors[0]);
                        cell.setPadding(16, 16, 16, 16);
                        cell.setGravity(Gravity.CENTER);

                        // Add the cell to the row
                        row.addView(cell);
                    }

                    // Add the row to the table layout
                    table.addView(row);

                    // Increment the counter
                    counter++;
                }
            }
        });

//        detailedReportTable = findViewById(R.id.detailed_report_table);

//        loadDetailedReports();
    }

    private void handleActionBack() {
        finish();
    }

    private void loadDetailedReports() {
        String reportRecordUuid = getIntent().getStringExtra(ReportsConsts.REPORT_RECORD_UUID_INTENT_EXTRA);

        DetailedReportsDb db = new DetailedReportsDb(this);
        List<DetailedReportsRecord> records = db.loadRecords(db.getRecordsOrderedByTargetAllocationThenCoin(reportRecordUuid));

//        for (DetailedReportsRecord record : records) {
//            addDetailedReportRecordToUi(record);
//        }
    }

    private void addDetailedReportRecordToUi(DetailedReportsRecord record) {
        TableRow row = new TableRow(this);

        TextView a = new TextView(this);
        TextView b = new TextView(this);
        TextView c = new TextView(this);
        TextView d = new TextView(this);
        TextView e = new TextView(this);
        TextView f = new TextView(this);
        TextView g = new TextView(this);
        TextView h = new TextView(this);
        TextView i = new TextView(this);

//        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
//                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT
//                ));

        a.setText("Data A");
//        a.setLayoutParams(layoutParams);

        b.setText("Data A");
//        b.setLayoutParams(layoutParams);

        c.setText("Data A");
//        c.setLayoutParams(layoutParams);

        d.setText("Data A");
//        d.setLayoutParams(layoutParams);

        e.setText("Data A");
//        e.setLayoutParams(layoutParams);

        f.setText("Data A");
//        f.setLayoutParams(layoutParams);

        g.setText("Data A");
//        g.setLayoutParams(layoutParams);

        h.setText("Data A");
//        h.setLayoutParams(layoutParams);

        i.setText("Data A");
//        i.setLayoutParams(layoutParams);

        row.addView(a);
        row.addView(b);
        row.addView(c);
        row.addView(d);
        row.addView(e);
        row.addView(f);
        row.addView(g);
        row.addView(h);
        row.addView(i);

        detailedReportTable.addView(row);

//        View recordRoot = addEmptyRecord();
//
//        TextView tvRecordDbUuid = recordRoot.findViewById(R.id.record_db_uuid);
//        TextView tvCreatedAt = recordRoot.findViewById(R.id.tv_created_at);
//        TextView tvPassedThreshold = recordRoot.findViewById(R.id.tv_passed_threshold);
//        TextView tvDidNotPassThreshold = recordRoot.findViewById(R.id.tv_did_not_pass_threshold);
//        TextView tvTotalUsd = recordRoot.findViewById(R.id.tv_total_usd);
//        TextView tvThreshold = recordRoot.findViewById(R.id.tv_threshold);
//        TextView tvCoins = recordRoot.findViewById(R.id.tv_coins_count);
//        TextView tvHighestDeviationCoin = recordRoot.findViewById(R.id.tv_highest_deviation_coin);
//        TextView tvHighestDeviationPercent = recordRoot.findViewById(R.id.tv_highest_deviation_percent);
//        Button btnDetails = recordRoot.findViewById(R.id.btn_details);
//        Button btnClearReport = recordRoot.findViewById(R.id.btn_clear_report);
//
//        tvRecordDbUuid.setTag(record.getUuid());
//        tvCreatedAt.setText(record.getCreatedAt());
//
//        if (record.shouldRebalance()) {
//            tvPassedThreshold.setVisibility(View.VISIBLE);
//            tvDidNotPassThreshold.setVisibility(View.GONE);
//            recordRoot.setBackgroundColor(Color.rgb(162, 239, 165));
//        } else {
//            tvPassedThreshold.setVisibility(View.GONE);
//            tvDidNotPassThreshold.setVisibility(View.VISIBLE);
//            recordRoot.setBackgroundColor(Color.rgb(209, 219, 230));
//        }
//
//        StringUtils stringUtils = new StringUtils();
//        tvTotalUsd.setText(stringUtils.convertDoubleToString(record.getPortfolioUsdValue(), 1));
//        tvThreshold.setText(stringUtils.convertDoubleToString(record.getThresholdRebalancingPercent(), 3));
//        tvCoins.setText(String.valueOf(record.getCoinsCount()));
//        tvHighestDeviationCoin.setText(record.getHighestDeviationCoin());
//        tvHighestDeviationPercent.setText(stringUtils.convertDoubleToString(record.getHighestDeviationPercent(), 3));
//
//        btnDetails.setOnClickListener(this);
//        btnClearReport.setOnClickListener(this);
//
//        String recordTag = stringUtils.generateRandomString(ROOT_TAG_LENGTH);
//        String childrenTag = ROOT_TAG_PREFIX + recordTag;
//
//        recordRoot.setTag(recordTag);
//        btnDetails.setTag(childrenTag);
//        btnClearReport.setTag(childrenTag);
    }
}