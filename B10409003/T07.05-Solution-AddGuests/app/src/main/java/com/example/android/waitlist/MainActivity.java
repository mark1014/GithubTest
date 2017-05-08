package com.example.android.waitlist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private String orderby ;
    private RadioButton maleRadiobutton;
    private RadioButton femaleRadiobutton;
    private EditText mNameeditText;
    private EditText mAgeeditText;

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private Text mNewPartySizeText;

    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        orderby = WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP;
        RecyclerView waitlistRecyclerView;

        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);

        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);

        mDb = dbHelper.getWritableDatabase();

        Cursor cursor = getAllGuests();

        mAdapter = new GuestListAdapter(this, cursor);

        waitlistRecyclerView.setAdapter(mAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int)viewHolder.itemView.getTag();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
            }
        }).attachToRecyclerView(waitlistRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_by_id:
                orderby = WaitlistContract.WaitlistEntry._ID;
                break;
            case R.id.sort_by_name:
                orderby = WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME;
               break;
            case R.id.sort_by_gender:
                orderby = WaitlistContract.WaitlistEntry.COLUMN_GENDER;
                break;
            case R.id.sort_by_age:
                orderby = WaitlistContract.WaitlistEntry.COLUMN_AGE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mAdapter.swapCursor(getAllGuests());
        return super.onOptionsItemSelected(item);
    }

    public void input (View bn){
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View v = li.inflate(R.layout.jump, null);
        AlertDialog.Builder alertdiaglogbuilder = new AlertDialog.Builder(MainActivity.this);
        alertdiaglogbuilder.setView(v);
        maleRadiobutton = (RadioButton) v.findViewById(R.id.male_radio_button);
        femaleRadiobutton = (RadioButton) v.findViewById(R.id.female_radio_button);
        mNameeditText = (EditText) v.findViewById(R.id.name_edit_text);
        mAgeeditText = (EditText) v.findViewById(R.id.age_edit_text);
        alertdiaglogbuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (mNameeditText.getText().length() == 0 ||
                                        mAgeeditText.getText().length() == 0) {
                                    return;
                                }
                                String name = mNameeditText.getText().toString();
                                int ismale =(maleRadiobutton.isChecked())? 0:1;
                                int age = 1;
                                try {
                                    age = Integer.parseInt(mAgeeditText.getText().toString());
                                } catch (NumberFormatException ex) {
                                    Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
                                }
                                addNewGuest(name ,ismale , age );
                                mAdapter.swapCursor(getAllGuests());

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertdiaglogbuilder.create();

        alertDialog.show();
    }

    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderby
        );
    }

    private long addNewGuest(String name, int gender, int age) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_AGE, age);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GENDER, gender);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }

    private boolean removeGuest(int id) {
        return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
    }


}