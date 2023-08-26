package com.example.binancerebalancinghelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.binancerebalancinghelper.consts.ExceptionHandleConsts;
import com.example.binancerebalancinghelper.sqlite.SqliteDbHelper;
import com.example.binancerebalancinghelper.sqlite.consts.ExceptionsLogTableConsts;
import com.example.binancerebalancinghelper.utils.StringUtils;

public class ExceptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String ROOT_TAG_PREFIX = "root_tag_";
    private static final int ROOT_TAG_LENGTH = 10;

    private LayoutInflater layoutInflater;
    private LinearLayout dynamicLinearLayout;

    @Override
    public void onClick(View view) {
        View recordRoot = getRecordRoot(view);
        handleActionClearException(recordRoot);
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

        if(itemId == R.id.redirect_main) {
            handleActionRedirectMain();
            return true;
        }
        else if(itemId == R.id.refresh) {
            handleActionRefresh();
            return true;
        }
        else if(itemId == R.id.clear_all_exceptions) {
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
        Cursor exceptions = getExceptionsFromDb();

        int idColumnIndex = exceptions.getColumnIndex(ExceptionsLogTableConsts.ID_COLUMN);
        int createdAtColumnIndex = exceptions.getColumnIndex(ExceptionsLogTableConsts.CREATED_AT_COLUMN);
        int severityColumnIndex = exceptions.getColumnIndex(ExceptionsLogTableConsts.SEVERITY_COLUMN);
        int messageColumnIndex = exceptions.getColumnIndex(ExceptionsLogTableConsts.MESSAGE_COLUMN);

        while (exceptions.moveToNext()) {
            int id = exceptions.getInt(idColumnIndex);
            String createdAt = exceptions.getString(createdAtColumnIndex);
            String severity = exceptions.getString(severityColumnIndex);
            String message = exceptions.getString(messageColumnIndex);

            addExceptionRecord(id, createdAt, severity, message);
        }

        exceptions.close();
    }

    private Cursor getExceptionsFromDb() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(this);

        return sqLiteDatabase.rawQuery("" +
                "SELECT * FROM " + ExceptionsLogTableConsts.TABLE_NAME
                + " ORDER BY " + ExceptionsLogTableConsts.CREATED_AT_COLUMN + " DESC",
                null);
    }

    private void addExceptionRecord(int id, String createdAt, String severity, String message) {
        View recordRoot = addEmptyRecord();

        TextView tvRecordDbId = recordRoot.findViewById(R.id.record_db_id);
        TextView tvCreatedAt = recordRoot.findViewById(R.id.tv_created_at);
        TextView tvSeverity = recordRoot.findViewById(R.id.tv_severity);
        TextView tvMessage = recordRoot.findViewById(R.id.tv_message);
        Button btnClearException = recordRoot.findViewById(R.id.btn_clear_exception);

        if(severity.equals(ExceptionHandleConsts.SEVERITY_NORMAL)) {
            recordRoot.setBackgroundColor(Color.rgb(244, 236, 102));
        }
        else if(severity.equals(ExceptionHandleConsts.SEVERITY_CRITICAL)) {
            recordRoot.setBackgroundColor(Color.rgb(244, 164, 102));
        }
        else {
            recordRoot.setBackgroundColor(Color.rgb(244, 107, 102));
        }

        tvRecordDbId.setTag(id);
        tvCreatedAt.setText(createdAt);
        tvSeverity.setText(severity);
        tvMessage.setText(message);

        btnClearException.setOnClickListener(this);

        StringUtils stringUtils = new StringUtils();
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
        String rootTag = ((String)view.getTag()).substring(ROOT_TAG_PREFIX.length());
        return dynamicLinearLayout.findViewWithTag(rootTag);
    }

    private void handleActionRedirectMain() {
        finish();
    }

    private void handleActionRefresh() {
        dynamicLinearLayout.removeAllViews();
        loadExceptions();
    }

    private void handleActionClearAllExceptions() {
        dynamicLinearLayout.removeAllViews();
        clearAllExceptionsFromDb();
    }

    private void handleActionClearException(View recordRoot) {
        TextView recordDbId = recordRoot.findViewById(R.id.record_db_id);
        dynamicLinearLayout.removeView(recordRoot);

        String id = recordDbId.getTag().toString();
        clearExceptionFromDb(id);
    }

    private void clearAllExceptionsFromDb() {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(this);
        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, null, null);
    }

    private void clearExceptionFromDb(String exceptionId) {
        SQLiteDatabase sqLiteDatabase = SqliteDbHelper.getWriteableDatabaseInstance(this);
        sqLiteDatabase.delete(ExceptionsLogTableConsts.TABLE_NAME, "id=" + exceptionId, null);
    }
}