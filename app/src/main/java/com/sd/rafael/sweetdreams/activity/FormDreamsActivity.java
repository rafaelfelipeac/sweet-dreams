package com.sd.rafael.sweetdreams.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.sd.rafael.sweetdreams.DAO.DreamDAO;
import com.sd.rafael.sweetdreams.R;
import com.sd.rafael.sweetdreams.helper.FormDreamsHelper;
import com.sd.rafael.sweetdreams.models.Dream;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FormDreamsActivity extends BaseActivity  {

    private FormDreamsHelper helper;
    private Dream dream;
    private TagView tagGroup;
    private ScrollView sv;
    private ActionBar toolbar;

    private Button btnPlay;
    private Button btnStop;
    private Button btnRecord;
    private String audioFilePath;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    public static final int RequestPermissionCode = 1;

    static final int DIALOG_DATE_ID = 2;
    int yearX;
    int monthX;
    int dayX;

    public void recordAudio(View view) {
        isRecording = true;
        btnStop.setEnabled(true);
        btnPlay.setEnabled(false);
        btnRecord.setEnabled(false);

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void stopAudio(View view) {
        btnStop.setEnabled(false);
        btnPlay.setEnabled(true);

        if(isRecording) {
            try {
                btnRecord.setEnabled(false);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            mediaPlayer.release();
            mediaPlayer = null;
            btnRecord.setEnabled(true);
        }
    }

    public void playAudio(View view) {
        btnPlay.setEnabled(false);
        btnRecord.setEnabled(false);
        btnStop.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //btnPlay.setEnabled(true);
        //btnStop.setEnabled(true);
    }

    private DatePickerDialog.OnDateSetListener dpickerListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    yearX = year;
                    monthX = month + 1;
                    dayX = dayOfMonth;

                    TextView date = (TextView) findViewById(R.id.form_dreams_date);
                    date.setText(dayX + "/" + monthX + "/" + yearX);
                }
            };

    public void showDatePickerDialog() {
        Button btnSetDate = (Button) findViewById(R.id.form_dreams_btnSetDate);

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE_ID);
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_DATE_ID) {
            DatePickerDialog dpd;

            if(dream == null)
                dpd = new DatePickerDialog(this, dpickerListener, yearX, monthX, dayX);
            else
                dpd = new DatePickerDialog(this, dpickerListener, dream.getYear(), dream.getMonth() - 1, dream.getDay());
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());

            return dpd;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_dreams);

        final Calendar cal = Calendar.getInstance();

        toolbar = getSupportActionBar();

        btnPlay = (Button) findViewById(R.id.form_dreams_btnPlay);
        btnStop = (Button) findViewById(R.id.form_dreams_btnStop);
        btnRecord = (Button) findViewById(R.id.form_dreams_btnRecord);



        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()) {
                    playAudio(view);
                }
                else {
                    requestPermission();
                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAudio(view);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAudio(view);
            }
        });

        btnPlay.setEnabled(false);
        btnStop.setEnabled(false);

        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio.3gp";

        sv = (ScrollView) findViewById(R.id.form_dreams);
        tagGroup = (TagView) findViewById(R.id.tag_group_form);

        yearX = (cal.get(Calendar.YEAR));
        monthX = (cal.get(Calendar.MONTH));
        dayX = (cal.get(Calendar.DAY_OF_MONTH));

        showDatePickerDialog();

        TextView date = (TextView) findViewById(R.id.form_dreams_date);
        date.setText(dayX + "/" + (monthX+1) + "/" + yearX);

        helper = new FormDreamsHelper(this);

        Intent intent = getIntent();
        dream = (Dream) intent.getSerializableExtra("dream");

        if(dream != null) {
            helper.makeDream(dream);
            toolbar.setTitle(R.string.form_edit_activity);
        }
        else
            toolbar.setTitle(R.string.form_activity);

        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, com.cunoraz.tagview.Tag tag, final int position) {
                view.remove(position);
            }
        });

        Button btnNewTag = (Button) findViewById(R.id.form_dreams_btnNewTag);
        btnNewTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newTag = (EditText) findViewById(R.id.form_dreams_tag);

                if(!newTag.getText().toString().isEmpty() && newTag.getText().toString().trim().length() > 0) {
                    com.cunoraz.tagview.Tag tag = new Tag(newTag.getText().toString());
                    tag.radius = 10f;
                    tag.layoutColor = Color.rgb(95, 170, 223);
                    tag.isDeletable = true;
                    newTag.setText("");
                    tagGroup.addTag(tag);
                }
                else
                    Snackbar.make(sv, R.string.form_dreams_empty_tag, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(FormDreamsActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_confirm, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Dream dream = helper.getDream();
        Intent intentDream;
        switch (item.getItemId()) {
            case R.id.menu_form_confirm:
                if(emptyDream(2)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FormDreamsActivity.this);
                    alert.setMessage(R.string.form_dreams_save).setCancelable(false)
                            .setPositiveButton(R.string.form_dreams_ok, null);
                    alert.show();
                }
                else {
                    DreamDAO dao = new DreamDAO(FormDreamsActivity.this);

                    if(dream.getId() != null)
                        dao.Update(dream);
                    else {
                        dao.Insert(dream);

                        List<Dream> lst = dao.Read();
                        dream = lst.get(lst.size()-1);
                    }

                    dao.close();
                    intentDream = new Intent(FormDreamsActivity.this, DreamsActivity.class);
                    intentDream.putExtra("dream", dream);
                    startActivity(intentDream);

                    finish();
                }
                break;
            case android.R.id.home:
                DreamDAO dao = new DreamDAO(this);
                List<Dream> dreams = dao.Read();
                Dream originalDream = new Dream();
                if(this.dream != null) {
                    for(Dream d : dreams) {
                        if(d.getId().equals(this.dream.getId()))
                            originalDream = d;
                    }
                }

                if(emptyDream(1) || ((compareDreams(originalDream, dream)))) {
                    if(dream.getId() == null)
                        intentDream = new Intent(FormDreamsActivity.this, MainNavDrawerActivity.class);
                    else
                        intentDream = new Intent(FormDreamsActivity.this, DreamsActivity.class);

                    intentDream.putExtra("dream", originalDream);
                    startActivity(intentDream);
                    finish();
                }
                else
                    showDialogExitSave();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogExitSave() {
        final Intent intentA = new Intent(FormDreamsActivity.this, MainNavDrawerActivity.class);
        AlertDialog.Builder alert = new AlertDialog.Builder(FormDreamsActivity.this);
        alert.setMessage(R.string.form_dreams_without_save).setCancelable(false)
                .setNegativeButton(R.string.form_dreams_cancel, null)
                .setPositiveButton(R.string.form_dreams_exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intentA);
                    }
                });
        alert.show();
    }

    public boolean emptyDream(int n) {
        Dream dream = helper.getDream();

        switch(n) {
            case 1:
                return (dream.getTitle().trim().length() == 0 && dream.getDescription().trim().length() == 0);
            case 2:
                return (dream.getTitle().trim().length() == 0 || dream.getDescription().trim().length() == 0);

        }
        return false;
    }

    public boolean compareDreams(Dream dream, Dream dream2) {
        if(dream.getTitle() != null) {

            return dream.getTitle().equals(dream2.getTitle()) &&
                    dream.getDescription().equals(dream2.getDescription()) &&
                    dream.getTags().equals(dream2.getTags());
        }
        return false;
    }

    public boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int record = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return write == PackageManager.PERMISSION_GRANTED && record == PackageManager.PERMISSION_GRANTED;
    }
}