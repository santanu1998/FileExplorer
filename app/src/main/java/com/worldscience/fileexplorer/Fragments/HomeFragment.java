package com.worldscience.fileexplorer.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.worldscience.fileexplorer.FileAdapter;
import com.worldscience.fileexplorer.FileOpener;
import com.worldscience.fileexplorer.OnFileSelectedListener;
import com.worldscience.fileexplorer.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements OnFileSelectedListener {
    private FileAdapter fileAdapter;
    private List<File> fileList;
    String [] items = {"Details", "Rename", "Delete", "Share"};
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout linearImage = view.findViewById(R.id.linearImage);
        LinearLayout linearVideo = view.findViewById(R.id.linearVideo);
        LinearLayout linearMusic = view.findViewById(R.id.linearMusic);
        LinearLayout linearDocs = view.findViewById(R.id.linearDocs);
        LinearLayout linearDownloads = view.findViewById(R.id.linearDownload);
        LinearLayout linearApks = view.findViewById(R.id.linearApks);

        linearImage.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "image");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearVideo.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "video");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearMusic.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "music");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearDocs.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "docs");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearDownloads.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "downloads");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        linearApks.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "apk");
            CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
            catagorizedFragment.setArguments(args);

            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();
            }
        });

        runtimePermission();

        return view;

    }
    private void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions
                (Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displayFiles();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File [] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findFiles(singleFile));
                }
                else if (singleFile.getName().toLowerCase().endsWith(".jpeg") ||
                        singleFile.getName().toLowerCase().endsWith(".jpg") ||
                        singleFile.getName().toLowerCase().endsWith(".png") ||
                        singleFile.getName().toLowerCase().endsWith(".mp3") ||
                        singleFile.getName().toLowerCase().endsWith(".wav") ||
                        singleFile.getName().toLowerCase().endsWith(".mp4") ||
                        singleFile.getName().toLowerCase().endsWith(".pdf") ||
                        singleFile.getName().toLowerCase().endsWith(".doc") ||
                        singleFile.getName().toLowerCase().endsWith(".mkv") ||
                        singleFile.getName().toLowerCase().endsWith(".apk")) {
                    arrayList.add(singleFile);
                }
            }
        }

        arrayList.sort(Comparator.comparing(File::lastModified).reversed());

        return arrayList;
    }
    private void displayFiles() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_recents);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(Environment.getExternalStorageDirectory()));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
            internalFragment.setArguments(bundle);
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();
            }
        }
        else {
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options = optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();
            switch (selectedItem) {
                case "Details":
                    AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                    detailDialog.setTitle("Details:");
                    final TextView details = new TextView(getContext());
                    detailDialog.setView(details);
                    Date lastModified = new Date(file.lastModified());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                    String formattedDate = formatter.format(lastModified);

                    details.setText("File Name: " + file.getName() + "\n" +
                            "Size: " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                            "Path: " + file.getAbsolutePath() + "\n" +
                            "Last Modified: " +formattedDate);

                    detailDialog.setPositiveButton("OK", (dialogInterface, i12) -> optionDialog.cancel());

                    AlertDialog alertDialog_details = detailDialog.create();
                    alertDialog_details.show();
                    break;
                case "Rename":

                    AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                    renameDialog.setTitle("Rename File:");
                    final EditText name = new EditText(getContext());
                    renameDialog.setView(name);

                    renameDialog.setPositiveButton("OK", (dialogInterface, i13) -> {
                        String new_name = name.getEditableText().toString();
                        String extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                        File current = new File(file.getAbsolutePath());
                        File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extention);
                        if (current.renameTo(destination)) {
                            fileList.set(position, destination);
                            fileAdapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Renamed!!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Couldn't Rename!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    renameDialog.setNegativeButton("Cancel", (dialogInterface, i14) -> optionDialog.cancel());
                    AlertDialog alertDialog_rename = renameDialog.create();
                    alertDialog_rename.show();

                    break;
                case "Delete":
                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                    deleteDialog.setTitle("Delete" + file.getName() + "?");
                    deleteDialog.setPositiveButton("Yes", (dialogInterface, i15) -> {
                        boolean delete = file.delete();
                        fileList.remove(position);
                        fileAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("No", (dialogInterface, i1) -> optionDialog.cancel());
                    AlertDialog alertDialog_delete = deleteDialog.create();
                    alertDialog_delete.show();
                    break;
                case "Share":
                    String fileName= file.getName();
                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(share,"Share " + fileName));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + selectedItem);
            }
        });
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View myView = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView txtOptions = myView.findViewById(R.id.txtOption);
            ImageView imgOptions = myView.findViewById(R.id.imgOption);
            txtOptions.setText(items[i]);
            switch (items[i]) {
                case "Details":
                    imgOptions.setImageResource(R.drawable.ic_details);
                    break;
                case "Rename":
                    imgOptions.setImageResource(R.drawable.ic_rename);
                    break;
                case "Delete":
                    imgOptions.setImageResource(R.drawable.ic_delete);
                    break;
                case "Share":
                    imgOptions.setImageResource(R.drawable.ic_share);
                    break;
            }
            return myView;
        }
    }
}
