package com.calvinliu.deadpoolsoundboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class EventHandlerClass {

    public static final String LOG_TAG = "EVENTHANDLER";

    private static MediaPlayer mp;

    public static void startMediaPlayer(View view, Integer soundID){
        try{

            if (soundID != null){

                if(mp != null){
                    mp.reset();
                }

                mp = MediaPlayer.create(view.getContext(), soundID);
                mp.setVolume(1,1);
                mp.start();
            }

        } catch (Exception e){
            Log.e(LOG_TAG, "Fail to initialize MediaPlayer: " + e.getMessage());
        }
    }

    public static void releaseMediaPlayer(){

        if (mp != null){

            mp.release();
            mp = null;
        }
    }

    private static boolean storagePermissionGranted(Context context){

        return (ContextCompat
                .checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean settingsPermissionGranted(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            return Settings.System.canWrite(context);
        }
        return true;
    }

    public static void popupmanager(final View view, final SoundObject soundObject){

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.longclick, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_send || item.getItemId() == R.id.action_ringtone){

                    if (!storagePermissionGranted(view.getContext())){

                        Toast.makeText(view.getContext(), R.string.perm_write_storage_error, Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    }

                    final String fileName = soundObject.getItemName() + ".mp3";

                    File storage = Environment.getExternalStorageDirectory();
                    File directory = new File(storage.getAbsolutePath() + "/dp_soundboard/");
                    directory.mkdirs();

                    final File file = new File(directory, fileName);

                    InputStream in = view.getContext().getResources().openRawResource(soundObject.getItemId());

                    try {

                        OutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];

                        int len;
                        while ((len = in.read(buffer, 0, buffer.length)) != -1){

                            out.write(buffer, 0, len);
                        }

                        in.close();
                        out.close();

                    } catch (Exception e){

                        Log.e(LOG_TAG, "Failed to save file: " + e.getMessage());
                    }

                    if (item.getItemId() == R.id.action_send){

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){

                            final String AUTHORITY = view.getContext().getPackageName() + ".fileprovider";

                            Uri contentUri = FileProvider.getUriForFile(view.getContext(), AUTHORITY, file);

                            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            shareIntent.setType("audio/mp3");
                            view.getContext().startActivity(Intent.createChooser(shareIntent, "Share sound via..."));

                        } else{

                            final Intent shareIntent = new Intent(Intent.ACTION_SEND);

                            Uri fileUri = Uri.parse(file.getAbsolutePath());

                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                            shareIntent.setType("audio/mp3");
                            view.getContext().startActivity(Intent.createChooser(shareIntent, "Share sound via..."));

                        }
                    }

                    if (item.getItemId() == R.id.action_ringtone){

                        if (!settingsPermissionGranted(view.getContext())){

                            Toast.makeText(view.getContext(), R.string.perm_write_settings_error, Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_HOLO_LIGHT);
                        builder.setTitle("Save as...");
                        builder.setItems(new CharSequence[]{"Ringtone", "Notification", "Alarm"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which){

                                    case 0:
                                        changeSystemAudio(view, fileName, file, 1);
                                        break;
                                    case 1:
                                        changeSystemAudio(view, fileName, file, 2);
                                        break;
                                    case 2:
                                        changeSystemAudio(view, fileName, file, 3);
                                        break;
                                }
                            }
                        });

                        builder.create();
                        builder.show();

                    }
                }

                return true;
            }
        });

        popup.show();
    }

    private static void changeSystemAudio(View view, String fileName, File file, int action) {

        try {

            ContentValues values = new ContentValues();

            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");

            switch (action) {

                case 1:
                    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                    break;
                case 2:
                    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                    break;
                case 3:
                    values.put(MediaStore.Audio.Media.IS_ALARM, true);
                    break;
                default:
            }

            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
            view.getContext().getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);
            Uri finalUri = view.getContext().getContentResolver().insert(uri, values);

            switch (action) {

                case 1:
                    RingtoneManager.setActualDefaultRingtoneUri(view.getContext(), RingtoneManager.TYPE_RINGTONE, finalUri);
                    break;
                case 2:
                    RingtoneManager.setActualDefaultRingtoneUri(view.getContext(), RingtoneManager.TYPE_NOTIFICATION, finalUri);
                    break;
                case 3:
                    RingtoneManager.setActualDefaultRingtoneUri(view.getContext(), RingtoneManager.TYPE_ALARM, finalUri);
                    break;
                default:
            }
        } catch (Exception e){

            Log.e(LOG_TAG, "Failed to save as system audio: " + e.getMessage());
        }
    }

}
