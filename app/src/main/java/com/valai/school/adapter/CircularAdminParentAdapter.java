package com.valai.school.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.valai.school.R;
import com.valai.school.interfaces.GetFileDownload;
import com.valai.school.modal.GetAdminCircularParentPOJO;
import com.valai.school.modal.SignInPOJO;
import com.valai.school.utils.CommonUtils;
import com.valai.school.utils.ImageConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.valai.school.utils.AppConstants.FILE_ROOT_PATH_ASSIGNMENT;
import static com.valai.school.utils.AppConstants.FILE_ROOT_PATH_CIRCULAR;
import static com.valai.school.utils.AppConstants.ROOT_DOWNLOAD;
import static com.valai.school.utils.AppConstants.ROOT_GROUP_FOLDER;
import static com.valai.school.utils.CommonUtils.convertDateStringFormat2;
import static com.valai.school.utils.CommonUtils.splitTime;

public class CircularAdminParentAdapter extends RecyclerView.Adapter<CircularAdminParentAdapter.MyViewHolder> {

    private Context ctx;
    private List<GetAdminCircularParentPOJO.Datum> datumArrayList;
    private List<SignInPOJO.Datum> listSignInRes;
    private GetFileDownload circularFileDownload;
    private static final float BITMAP_SCALE = 0.7f;
    private static final int BLUR_RADIUS = 50;

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_circular)
        TextView text_circular;

        @BindView(R.id.text_date)
        TextView text_date;

        @BindView(R.id.text_time)
        TextView text_time;

        @BindView(R.id.image1)
        ImageView image1;

        @BindView(R.id.image_download)
        ImageView image_download;

        @BindView(R.id.image_downloaded)
        ImageView image_downloaded;

        @BindView(R.id.rl_download)
        RelativeLayout rl_download;

        @BindView(R.id.cardView)
        CardView cardView;

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        @BindView(R.id.tvFileName)
        TextView tvFileName;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public CircularAdminParentAdapter(Context ctx, List<GetAdminCircularParentPOJO.Datum> datumArrayList,
                                      List<SignInPOJO.Datum> listSignInRes, GetFileDownload circularFileDownload) {
        this.ctx = ctx;
        this.datumArrayList = datumArrayList;
        this.listSignInRes = listSignInRes;
        this.circularFileDownload = circularFileDownload;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circular_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.rl_download.setTag(position);
        holder.image_download.setTag(position);
        holder.image_downloaded.setTag(position);
        if (datumArrayList.get(position).getFileName() == null &&
                datumArrayList.get(position).getFileName().equals("") &&
                datumArrayList.get(position).getMessage() == null &&
                datumArrayList.get(position).getMessage().equals("")) {

            holder.cardView.setVisibility(View.GONE);

        } else {
            holder.cardView.setVisibility(View.VISIBLE);

            if (datumArrayList.get(position).getFileName() != null &&
                    !datumArrayList.get(position).getFileName().equals("") &&
                    datumArrayList.get(position).getGenfileName() != null &&
                    !datumArrayList.get(position).getGenfileName().equals("")) {

                holder.rl_download.setVisibility(View.VISIBLE);
                holder.tvFileName.setText(datumArrayList.get(position).getGenfileName());

                if (datumArrayList.get(position).getGenfileName() != null) {
                    String extension = datumArrayList.get(position).getGenfileName().substring(datumArrayList.get(position).getGenfileName()
                            .lastIndexOf("."));

                    //Log.e("extension", "extension>>" + extension);

                    switch (extension) {
                        case ".jpg":
                        case ".png":
                        case ".jpeg":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_XY);
                            if (!datumArrayList.get(position).isImageDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String imageURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                Log.e("imageURL", "imageURL>>" + imageURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(imageURL) // thumbnail url goes here
                                        .resize(200, 200)
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e("onSuccess", "image onSuccess");
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "image load");
                                            }
                                        });

                            } else {
                                //Log.e("else", "else image");
                                final String imageURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                Log.e("imageURL", "imageURL else>>" + imageURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(imageURL) // thumbnail url goes here
                                        .resize(200, 200)
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setImageDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(imageURL) // image url goes here
                                                        .resize(200, 200)
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "image load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }

                            break;
                        case ".pdf":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isPdfDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String pdfURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("pdfURL", "pdfURL>>" + pdfURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_pdf) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "pdf load");
                                            }
                                        });
                            } else {
                                //Log.e("else", "else pdf");
                                final String pdfURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("pdfURL", "pdfURL else>>" + pdfURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_pdf) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setPdfDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_pdf) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "pdf load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }

                            break;
                        case ".doc":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isDocDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String docURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("docURL", "docURL>>" + docURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_doc) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "doc load");
                                            }
                                        });
                            } else {
                                // Log.e("else", "else doc");
                                final String docURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("docURL", "docURL else>>" + docURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_doc) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setDocDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_doc) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "doc load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            break;
                        case ".txt":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isTxtDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String txtURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("txtURL", "txtURL>>" + txtURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_text) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "txt load");
                                            }
                                        });
                            } else {
                                //Log.e("else", "else txt");
                                final String txtURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                // Log.e("txtURL", "txtURL else>>" + txtURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_text) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setTxtDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_text) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "txt load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            break;
                        case ".xls":
                        case ".xlsx":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isExcelFileDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String xlsxURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("xlsxURL", "xlsxURL>>" + xlsxURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_excel) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "excel load");
                                            }
                                        });
                            } else {
                                //Log.e("else", "else excel");
                                final String xlsxURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("xlsxURL", "xlsxURL else>>" + xlsxURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_excel) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setExcelFileDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_excel) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "excel load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            break;
                        case ".csv":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isCsvFileDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String csvURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("csvURL", "csvURL>>" + csvURL);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_csv) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "excel load");
                                            }
                                        });
                            } else {
                                //Log.e("else", "else csvURL");
                                final String xlsxURL = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_CIRCULAR
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("csvURL", "csvURL else>>" + xlsxURL);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_csv) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setCsvFileDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_csv) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "csvURL load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            break;
                        case ".mp4":
                        case ".3gp":
                        case ".avi":
                        case ".flv":
                        case ".mov":
                        case ".m4a":
                        case ".mp3":
                        case ".wmv":
                        case ".ogg":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isAudioVideoDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String audioVideo = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_ASSIGNMENT
                                        + datumArrayList.get(position).getGenfileName();
                                Log.e("AudioVideo", "URL>>" + audioVideo);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.ic_movie_player) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "image load");
                                            }
                                        });

                            } else {
                                //Log.e("else", "else image");
                                final String audioVideo = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_ASSIGNMENT
                                        + datumArrayList.get(position).getGenfileName();
                                Log.e("AudioVideo", "URL>>" + audioVideo);

                                holder.image_download.setVisibility(View.GONE);
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);

                                Picasso.with(ctx)
                                        .load(R.drawable.ic_movie_player) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setAudioVideoDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.ic_movie_player) // audio url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                //Log.e("error", "audio load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }

                            break;

                        case ".gif":
                            holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            if (!datumArrayList.get(position).isGifFileDownloaded()) {
                                holder.image_downloaded.setClickable(false);
                                holder.image_downloaded.setEnabled(false);
                                final String gifUrl = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_ASSIGNMENT
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("gifUrl", "gifUrl>>" + gifUrl);
                                holder.image_download.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);
                                Picasso.with(ctx)
                                        .load(R.drawable.gif) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                Log.e("error", "excel load");
                                            }
                                        });
                            } else {
                                //Log.e("else", "else gifUrl");
                                holder.image_downloaded.setClickable(true);
                                holder.image_downloaded.setEnabled(true);
                                final String gifUrl = ROOT_DOWNLOAD
                                        + ROOT_GROUP_FOLDER
                                        + listSignInRes.get(0).getOrgId()
                                        + FILE_ROOT_PATH_ASSIGNMENT
                                        + datumArrayList.get(position).getGenfileName();
                                //Log.e("gifUrl", "gifUrl else>>" + gifUrl);

                                holder.image_download.setVisibility(View.GONE);

                                Picasso.with(ctx)
                                        .load(R.drawable.gif) // thumbnail url goes here
                                        .transform(blurTransformation)
                                        .error(R.drawable.error_image)
                                        .into(holder.image_downloaded, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                                datumArrayList.get(position).setGifFileDownloaded(true);
                                                Picasso.with(ctx)
                                                        .load(R.drawable.gif) // image url goes here
                                                        .into(holder.image_downloaded);
                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("error", "gifUrl load in");
                                                holder.image_download.setVisibility(View.GONE);
                                                holder.progressBar.setVisibility(View.GONE);
                                            }
                                        });
                            }
                            break;
                    }
                }

            } else {
                holder.rl_download.setVisibility(View.GONE);
            }

            if (datumArrayList.get(position).getMessage() != null && !datumArrayList.get(position).getMessage().equals("")) {
                holder.text_circular.setVisibility(View.VISIBLE);
                holder.text_circular.setText(CommonUtils.fromHtml(datumArrayList.get(position).getMessage()));
            } else {
                holder.text_circular.setText("");
                holder.text_circular.setVisibility(View.GONE);
            }

            String cir_date = convertDateStringFormat2(datumArrayList.get(position).getCirDate());
            String cir_time = splitTime(datumArrayList.get(position).getCirDate());
            holder.text_date.setText(ctx.getString(R.string.date_txt) + " " + cir_date);
            holder.text_time.setText(ctx.getString(R.string.time_txt) + " " + cir_time);
            Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.class_icon);
            Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
            holder.image1.setImageBitmap(circularBitmap);

            holder.image_downloaded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.e("pos", "pos>>" + Integer.parseInt(view.getTag().toString()));
                    // Log.e("FileName", "FileName>>" + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getFileName());
                    circularFileDownload.getFileDownloadCall("",
                            datumArrayList.get(Integer.parseInt(view.getTag().toString())).getFileName());
                }
            });

            holder.image_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    if (circularFileDownload.isInterNetConnected()) {
                        if (datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName() != null) {
                            String extension = datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName()
                                    .substring(datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName().lastIndexOf("."));
                            //Log.e("extension", "extension-in>>" + extension);

                            switch (extension) {
                                case ".jpg":
                                case ".png":
                                case ".jpeg":
                                    final String imageURL = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("imageURL", "imageURL-IN>>" + imageURL);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_XY);
                                    Picasso.with(ctx)
                                            .load(imageURL) // thumbnail url goes here
                                            .resize(200, 200)
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setImageDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(imageURL) // image url goes here
                                                            .resize(200, 200)
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "image load in");
                                                }
                                            });
                                    break;
                                case ".pdf":
                                    final String pdfURL = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("pdfURL", "pdfURL-IN>>" + pdfURL);

                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_pdf) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setPdfDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_pdf) // pdf url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "pdf load in");
                                                }
                                            });

                                    break;
                                case ".doc":
                                    final String docURL = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("docURL", "docURL-IN>>" + docURL);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_doc) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setDocDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_doc) // pdf url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "doc load in");
                                                }
                                            });
                                    break;
                                case ".txt":
                                    final String txtURL = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("txtURL", "txtURL>>" + txtURL);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_text) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setTxtDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_text) // pdf url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "txt load in");
                                                }
                                            });
                                    break;

                                case ".xls":
                                case ".xlsx":
                                    final String txtXlsx = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("txtXlsxURL", "txtXlsxURL>>" + txtXlsx);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_excel) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setExcelFileDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_excel) // pdf url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "txt load in");
                                                }
                                            });
                                    break;
                                case ".csv":
                                    final String csvFile = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("csvURL", "csvURL>>" + csvFile);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_csv) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setCsvFileDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_csv) // pdf url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "csv load in");
                                                }
                                            });
                                    break;

                                case ".mp4":
                                case ".3gp":
                                case ".avi":
                                case ".flv":
                                case ".mov":
                                case ".m4a":
                                case ".mp3":
                                case ".wmv":
                                case ".ogg":
                                    final String audioVideo = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_ASSIGNMENT
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("csvURL", "csvURL>>" + audioVideo);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.ic_movie_player) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setAudioVideoDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.ic_movie_player) // audio video url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "audio video load in");
                                                }
                                            });
                                    break;

                                case ".gif":
                                    final String gifFile = ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_ASSIGNMENT
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName();
                                    //Log.e("gifFile", "gifFile>>" + gifFile);
                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.image_download.setVisibility(View.VISIBLE);
                                    holder.image_downloaded.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Picasso.with(ctx)
                                            .load(R.drawable.gif) // thumbnail pdf url goes here
                                            .transform(blurTransformation)
                                            .error(R.drawable.error_image)
                                            .into(holder.image_downloaded, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.image_download.setVisibility(View.GONE);
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_downloaded.setClickable(true);
                                                    holder.image_downloaded.setEnabled(true);
                                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).setGifFileDownloaded(true);
                                                    Picasso.with(ctx)
                                                            .load(R.drawable.gif) // gifFile url goes here
                                                            .error(R.drawable.error_image)
                                                            .into(holder.image_downloaded);
                                                }

                                                @Override
                                                public void onError() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                    holder.image_download.setVisibility(View.GONE);
                                                    Log.e("error", "gifFile load in");
                                                }
                                            });
                                    break;
                            }
                            //fragmentListner.setCircularList(datumArrayList);
                            circularFileDownload.getFileDownloadCall(ROOT_DOWNLOAD
                                            + ROOT_GROUP_FOLDER
                                            + listSignInRes.get(0).getOrgId()
                                            + FILE_ROOT_PATH_CIRCULAR
                                            + datumArrayList.get(Integer.parseInt(view.getTag().toString())).getGenfileName(),
                                    datumArrayList.get(Integer.parseInt(view.getTag().toString())).getFileName());
                        }
                    } else {
                        circularFileDownload.showMessage();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datumArrayList.size();
    }

    private Transformation blurTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap blurred = fastBlur(source, BITMAP_SCALE, BLUR_RADIUS);
            source.recycle();
            return blurred;
        }

        @Override
        public String key() {
            return "blur()";
        }
    };

    @Nullable
    private Bitmap fastBlur(Bitmap sentBitmap, float scale, int radius) {

        int width = Math.round(sentBitmap.getWidth() * scale);
        int height = Math.round(sentBitmap.getHeight() * scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }
}