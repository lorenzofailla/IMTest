package com.apps.lore_f.domoticcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apps.lore_f.domoticcontroller.firebase.dataobjects.FileInCloudStorage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CloudStorageActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private static final String TAG = "CloudStorage";

    public RecyclerView storedFilesRecyclerView;
    public LinearLayoutManager linearLayoutManager;
    public FirebaseRecyclerAdapter<FileInCloudStorage, StoredFilesHolder> firebaseAdapter;

   private ValueEventListener valueEventListener = new ValueEventListener() {

       @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

           if (dataSnapshot.getValue()!=null) {

                   Log.i(TAG, dataSnapshot.getValue().toString());

           }

            // aggiorna l'adapter
            refreshAdapter();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_storage);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users/lorenzofailla/CloudStorage");
        storedFilesRecyclerView = (RecyclerView) findViewById(R.id.RWV___CLOUDSTORAGE___MAIN);

    }

    @Override
    protected void onPause(){

        super.onPause();

        // rimuove il ValueEventListener dal nodo
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    protected void onResume(){

        super.onResume();

        // aggiunge un ValueEventListener al nodo
        databaseReference.addValueEventListener(valueEventListener);

    }


    public static class StoredFilesHolder extends RecyclerView.ViewHolder {

        public TextView fileNameTXV;
        public TextView requestorTXV;
        public TextView sizeTXV;
        public TextView nOfDownloadsTXV;

        public ImageButton downloadBTN;
        public ImageButton deleteBTN;

        public StoredFilesHolder(View v) {
            super(v);
            fileNameTXV = (TextView) itemView.findViewById(R.id.TXV___STOREDFILE___NAME);
            requestorTXV = (TextView) itemView.findViewById(R.id.TXV___STOREDFILE___REQUESTOR_VALUE);
            sizeTXV = (TextView) itemView.findViewById(R.id.TXV___STOREDFILE___SIZE_VALUE);
            nOfDownloadsTXV = (TextView) itemView.findViewById(R.id.TXV___STOREDFILE___NOFDOWNLOADS_VALUE);

            downloadBTN = (ImageButton) itemView.findViewById(R.id.BTN___STOREDFILE___DOWNLOAD);
            deleteBTN = (ImageButton) itemView.findViewById(R.id.BTN___STOREDFILE___DELETE);

        }

    }

    private void refreshAdapter(){

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);

        firebaseAdapter = new FirebaseRecyclerAdapter<FileInCloudStorage, StoredFilesHolder>(
                FileInCloudStorage.class,
                R.layout.row_holder_stored_file_element,
                StoredFilesHolder.class,
                databaseReference) {

            @Override
            protected void populateViewHolder(StoredFilesHolder holder, final FileInCloudStorage cloudFile, int position) {

                holder.fileNameTXV.setText(cloudFile.getFileName());
                holder.requestorTXV.setText(cloudFile.getRequestor());
                holder.sizeTXV.setText(""+cloudFile.getSize());
                holder.nOfDownloadsTXV.setText(""+cloudFile.getnOfDownloads());

                holder.downloadBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // avvia la procedura di download del file richiesto
                        startCloudDownloadService(cloudFile);

                    }

                });

                holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                        askForFileDeletionConfirmation(cloudFile);

                    }
                });

            }

        };

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int mediaCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (mediaCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    storedFilesRecyclerView.scrollToPosition(positionStart);
                }

            }

        });

        storedFilesRecyclerView.setLayoutManager(linearLayoutManager);
        storedFilesRecyclerView.setAdapter(firebaseAdapter);

    }

    private void startCloudDownloadService(FileInCloudStorage f) {

        String[] param = {f.getFileName()};
        Intent intent = new Intent(this, DownloadFileFromCloud.class);
        intent.putExtra("__file_to_download", param);

        startService(intent);

    }

    private void deleteFileFromCloud(final FileInCloudStorage f) {

        // ottiene un riferimento alla posizione di storage sul cloud
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://domotic-28a5e.appspot.com/Users/lorenzofailla/uploads/"+f.getFileName());

        // avvia la procedura di eliminazione del file
        storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/lorenzofailla/CloudStorage");
                databaseReference.child(f.getItemID()).removeValue();

            }

        });

    }

    private void askForFileDeletionConfirmation(final FileInCloudStorage fileToBeDeleted){

        // rimuove il file selezionato dal cloud storage
        AlertDialog.Builder alertDialogBuilder= new AlertDialog.Builder(this)
                .setMessage(R.string.ALERTDIALOG_MESSAGE_CONFIRM_DELETE_FROM_CLOUD)

                .setPositiveButton(R.string.ALERTDIALOG_YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // elimina il file dal cloud
                        deleteFileFromCloud(fileToBeDeleted);
                    }
                })
                .setNegativeButton(R.string.ALERTDIALOG_NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // chiude la finestra di dialogo
                        dialogInterface.dismiss();
                    }
                })
                .setTitle(R.string.ALERTDIALOG_TITLE_CONFIRM_TORRENT_REMOVAL);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
