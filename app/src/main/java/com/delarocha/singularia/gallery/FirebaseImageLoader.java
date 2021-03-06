package com.delarocha.singularia.gallery;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import io.grpc.Context;

public class FirebaseImageLoader implements ModelLoader<StorageReference, InputStream> {

    public static final String TAG = "FireImageLoader";



    public static class Factory implements ModelLoaderFactory<StorageReference, InputStream>{

        @NonNull
        @Override
        public ModelLoader<StorageReference, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new FirebaseImageLoader();
        }

        @Override
        public void teardown() {

        }
    }


    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull StorageReference storageReference, int width, int height, @NonNull Options options) {
        return new LoadData<>(new FirebaseStorageKey(storageReference),new FirebaseStorageFetcher(storageReference));
    }

    @Override
    public boolean handles(@NonNull StorageReference storageReference) {
        return true;
    }

    private static class FirebaseStorageKey implements Key{

        private StorageReference mRef;

        public FirebaseStorageKey(StorageReference ref){
            mRef = ref;
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            messageDigest.update(mRef.getPath().getBytes(Charset.defaultCharset()));
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            FirebaseStorageKey key = (FirebaseStorageKey) obj;

            return mRef.equals(key.mRef);
            //return super.equals(obj);
        }
    }

    private static class FirebaseStorageFetcher implements DataFetcher<InputStream>{

        private StorageReference mRef;
        private StreamDownloadTask mStreamTask;
        private InputStream mInputStream;

        public FirebaseStorageFetcher(StorageReference ref) {
            mRef = ref;
        }

        @Override
        public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super InputStream> callback) {
            mStreamTask = mRef.getStream();
            mStreamTask.addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                    mInputStream = taskSnapshot.getStream();
                    callback.onDataReady(mInputStream);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onLoadFailed(e);
                }
            });
        }

        @Override
        public void cleanup() {
            //Close stream if possible
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                    mInputStream = null;
                } catch (IOException e) {
                    Log.w(TAG, "Could not close stream", e);
                }
            }
        }

        @Override
        public void cancel() {
            // Cancel task if possible
            if (mStreamTask != null && mStreamTask.isInProgress()) {
                mStreamTask.cancel();
            }
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }
    }
}
