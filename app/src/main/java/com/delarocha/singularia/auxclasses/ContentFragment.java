package com.delarocha.singularia.auxclasses;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.delarocha.singularia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;

/**
 * Created by jmata on 04/10/2018.
 */

public class ContentFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_FRAG_INDEX = "mFragIndex";

    private static final String TAG = "ContentFragment";
    private static final String ARG_imgList = "img_list";
    private String imgStr,textoProm;
    private static Context ctx;
    private DocumentReference dRef;
    public List<Map<String,Object>> mapList;
    private int fragIndex;
    private FirebaseFirestore firestoreDb;
    private TextView promoText;
    private List<Integer> imageIdList;
    private List<String> imageURLList;
    private List imgIndex = new ArrayList();
    int i = 0;

    public ContentFragment(){
        imageIdList = new ArrayList<>();
        imageURLList = new ArrayList<>();
        this.firestoreDb = FirebaseFirestore.getInstance();
        //getImagenesPromo();
        //imageIdList.add(R.drawable.promo1);
        //imageIdList.add(R.drawable.promo2);
        //imageIdList.add(R.drawable.promo3);
        //imageIdList.add(R.drawable.promo4);
        //imageIdList.add(R.drawable.promo5);
        //imageIdList.add(R.drawable.photo_demo_five);
        //imageIdList.add(R.drawable.photo_demo_six);
    }

    public String getParam1(){
        //return mParam1;
        return "";
    }

    public void getImagenesPromo(){
        mapList = new ArrayList<>();
        /*
        try{

            dRef = firestoreDb.collection(FIRESTORE_IMGS_COLLECTION).document("raUbC777aaBtw4iKyinKK");
            dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot snapshot = task.getResult();
                        imageURLList.add(snapshot.get("imgPromo0").toString());
                        imageURLList.add(snapshot.get("imgPromo1").toString());
                        imageURLList.add(snapshot.get("imgPromo2").toString());
                        imageURLList.add(snapshot.get("imgPromo3").toString());
                        imageURLList.add(snapshot.get("imgPromo4").toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
        */
        try{
            firestoreDb.collection(Tools.FIRESTORE_IMGS_COLLECTION)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mapList.add(document.getData());
                                }
                                setImageList();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

   /* public static ContentFragment newInstance(String param1, List<Integer> imgResourceList){

        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,param1);
        args.putIntegerArrayList(ARG_imgList, (ArrayList<Integer>) imgResourceList);
        fragment.setArguments(args);

        return fragment;
    }*/

    public static ContentFragment newInstance(Context context, String imgStr,String textoProm, int indice){

        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString("img_str",imgStr);
        args.putString("texto",textoProm);
        args.putInt(ARG_FRAG_INDEX, indice);
        ctx = context;
        //args.putIntegerArrayList(ARG_imgList, (ArrayList<Integer>) imgResourceList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //i++;
        if(getArguments()!=null){
            imgStr = getArguments().getString("img_str");
            textoProm = getArguments().getString("texto");
            fragIndex = getArguments().getInt(ARG_FRAG_INDEX);
            //imgIndex.add(i);
            //imageIdList = getArguments().getIntegerArrayList(ARG_imgList);
            //imageIdList.add(R.drawable.photo_demo_one);
            //imageIdList.add(R.drawable.photo_demo_two);
            //imageIdList.add(R.drawable.photo_demo_three);
            //imageIdList.add(R.drawable.photo_demo_four);
            //imageIdList.add(R.drawable.photo_demo_five);
            //imageIdList.add(R.drawable.photo_demo_six);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        //TextView text = (TextView) view.findViewById(R.id.text);
        ImageView img = (ImageView)view.findViewById(R.id.img_promo);

        //if(imageURLList.size()!=0){
            Glide.with(ctx).load(imgStr).apply(new RequestOptions().centerCrop()).into(img);
            //Picasso.with(ctx).load(imgStr).fit().into(img);
        //}else{
            //img.setImageResource(imageIdList.get(fragIndex));
        //}


        /*for(int drawable: imageIdList){
            img.setImageResource(drawable);
        }*/
        //img.setImageResource(imageIdList.get(fragIndex));
        //text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        //text.setText(textoProm);
        return view;
    }

    public void setImageList(){

        for(Map<String, Object> map: mapList){
            String imgUrl0 = map.get("imgPromo0").toString();
            String imgUrl1 = map.get("imgPromo1").toString();
            String imgUrl2 = map.get("imgPromo2").toString();
            String imgUrl3 = map.get("imgPromo3").toString();
            String imgUrl4 = map.get("imgPromo4").toString();

            imageURLList.add(imgUrl0);
            imageURLList.add(imgUrl1);
            imageURLList.add(imgUrl2);
            imageURLList.add(imgUrl3);
            imageURLList.add(imgUrl4);
        }
    }
}
