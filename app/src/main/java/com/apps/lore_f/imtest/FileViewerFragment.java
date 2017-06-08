package com.apps.lore_f.imtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FileViewerFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    List<FileInfo> fileInfoList;
    String rawDirData;
    String currentDirName;

    TextView currentDirectoryTextView;
    ListView currentDirectoryListView;

    FileListAdapter.FileListAdapterListener fileListAdapterListener = new FileListAdapter.FileListAdapterListener() {
        @Override
        public void onItemSelected(FileInfo fileInfo) {

            if(fileViewerFragmentListener!=null) fileViewerFragmentListener.onItemSelected(fileInfo);

        }
    };

    public FileViewerFragment() {

    }

    interface FileViewerFragmentListener{

        public void onItemSelected(FileInfo fileInfo);

    }

    FileViewerFragmentListener fileViewerFragmentListener;

    public void setFileViewerFragmentListener(FileViewerFragmentListener listener){

        fileViewerFragmentListener=listener;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* recupera gli extra dall'intent */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fileviewer_list, container, false);

        currentDirectoryTextView = (TextView) view.findViewById(R.id.TXV___FILELIST___FOLDERNAME);
        currentDirectoryListView = (ListView) view.findViewById(R.id.LVW___FILELIST___MAIN);

        updateContent();

        return view;

    }


    public void updateContent(){

        if (rawDirData!=null) {

            currentDirectoryTextView.setText(currentDirName);

            fileInfoList = refreshFilesList();

            FileListAdapter fileListAdapter = new FileListAdapter(getContext(), R.layout.fragment_fileviewer, fileInfoList);
            fileListAdapter.setFileListAdapterListener(fileListAdapterListener);
            currentDirectoryListView.setAdapter(fileListAdapter);

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(FileInfo item);
    }

    private List<FileInfo> refreshFilesList(){

        List<FileInfo> tmpFilesInfos = new ArrayList<>();
        String[] tmpFileStringLines = rawDirData.split("\n");

        /* procedura di generazione */
        for (int i=1; i<tmpFileStringLines.length; i++){

            tmpFilesInfos.add(new FileInfo(currentDirName,tmpFileStringLines[i]));

        }

        return tmpFilesInfos;

    }

}