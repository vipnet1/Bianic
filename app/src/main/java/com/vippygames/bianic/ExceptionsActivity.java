package com.vippygames.bianic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vippygames.bianic.consts.ExceptionHandleConsts;
import com.vippygames.bianic.consts.NotificationConsts;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogDb;
import com.vippygames.bianic.db.exceptions_log.ExceptionsLogRecord;
import com.vippygames.bianic.utils.StringUtils;

import java.util.List;

public class ExceptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        int viewId = view.getId();

        if (viewId == R.id.btn_clear_exception) {
            handleActionClearException(recordRoot);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exceptions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.redirect_main) {
            handleActionRedirectMain();
            return true;
        } else if (itemId == R.id.refresh) {
            handleActionRefresh();
            return true;
        } else if (itemId == R.id.clear_all_exceptions) {
            handleActionClearAllExceptions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exceptions);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dynamicLinearLayout = findViewById(R.id.layout_dynamic_exceptions);

        loadExceptions();
    }

    private void loadExceptions() {
        ExceptionsLogDb db = new ExceptionsLogDb(this);
        List<ExceptionsLogRecord> records = db.loadRecords(db.getRecordsOrderedByCreatedAt());

        for (ExceptionsLogRecord record : records) {
            addExceptionRecordToUi(record);
        }
    }

    private void addExceptionRecordToUi(ExceptionsLogRecord record) {
        View recordRoot = addEmptyRecord();

        TextView tvRecordDbId = recordRoot.findViewById(R.id.record_db_id);
        TextView tvCreatedAt = recordRoot.findViewById(R.id.tv_created_at);
        TextView tvSeverity = recordRoot.findViewById(R.id.tv_severity);
        TextView tvMessage = recordRoot.findViewById(R.id.tv_message);
        Button btnClearException = recordRoot.findViewById(R.id.btn_clear_exception);

        String severity = record.getSeverity();
        if (severity.equals(ExceptionHandleConsts.SEVERITY_NORMAL)) {
            recordRoot.setBackgroundColor(Color.rgb(244, 236, 102));
        } else if (severity.equals(ExceptionHandleConsts.SEVERITY_CRITICAL)) {
            recordRoot.setBackgroundColor(Color.rgb(244, 164, 102));
        } else {
            recordRoot.setBackgroundColor(Color.rgb(244, 107, 102));
        }

        StringUtils stringUtils = new StringUtils();
        tvRecordDbId.setTag(record.getId());
        tvCreatedAt.setText(stringUtils.convertUtcToLocalTime(record.getCreatedAt()));
        tvSeverity.setText(severity);
        tvMessage.setText(record.getMessage());

        btnClearException.setOnClickListener(this);

        String recordTag = stringUtils.generateRandomString(ROOT_TAG_LENGTH);
        String childrenTag = ROOT_TAG_PREFIX + recordTag;

        recordRoot.setTag(recordTag);
        btnClearException.setTag(childrenTag);
    }

    private View addEmptyRecord() {
        View rootView = layoutInflater.inflate(R.layout.exception_record, null);
        dynamicLinearLayout.addView(rootView);
        return rootView;
    }

    private View getRecordRoot(View view) {
        String rootTag = ((String) view.getTag()).substring(ROOT_TAG_PREFIX.length());
        return dynamicLinearLayout.findViewWithTag(rootTag);
    }

    private void handleActionRedirectMain() {
        if (!getIntent().getBooleanExtra(NotificationConsts.LAUNCHED_FROM_NOTIFICATION_EXTRA, false)) {
            finish();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    private void handleActionRefresh() {
        dynamicLinearLayout.removeAllViews();
        loadExceptions();
    }

    private void handleActionClearAllExceptions() {
        dynamicLinearLayout.removeAllViews();

        ExceptionsLogDb db = new ExceptionsLogDb(this);
        db.clearAllExceptionsFromDb();
    }

    private void handleActionClearException(View recordRoot) {
        TextView recordDbId = recordRoot.findViewById(R.id.record_db_id);
        dynamicLinearLayout.removeView(recordRoot);

        String id = recordDbId.getTag().toString();
        ExceptionsLogDb db = new ExceptionsLogDb(this);
        db.clearExceptionFromDb(id);
    }
}