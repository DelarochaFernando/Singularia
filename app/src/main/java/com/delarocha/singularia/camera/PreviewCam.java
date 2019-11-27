package com.delarocha.singularia.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

//import Surface;

public class PreviewCam extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    public Camera camera;
    Context mContext;
    int setOrientation;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    boolean sp_lock_device_rotation;
    public DisplayMetrics metrics;
    public WindowManager wmanager;

    public PreviewCam(Context context) {
        super(context);
        this.mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setOrientation = checkOrientation();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = preferences.edit();
        metrics = new DisplayMetrics();
        wmanager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

    }

    public int checkOrientation()
    {
        try
        {
            return getResources().getConfiguration().orientation;
        }
        catch(Exception ex)
        {
            Log.e("PreviewBack","Error " + ex.getMessage());
            return 1;
        }
    }

    public boolean getLockDeviceRotation()
    {
        try
        {
            return preferences.getBoolean("sp_lock_device_rotation", false);
        }
        catch(Exception ex)
        {
            return false;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        Log.d("PREVIEW", "startPreview");

        // camera = Camera.open();
        //Camera.Parameters p = camera.getParameters();

        try
        {
            if(camera!=null)
            {
                camera.release();
            }
            camera = Camera.open();
        }
        catch(Exception ex)
        {
            Log.v("PREVIEW", "Error ex = " + ex.getMessage());
        }

        try
        {
            camera.setPreviewDisplay(surfaceHolder);

            //	1	Portrait
            //	2	Landscape

            wmanager.getDefaultDisplay().getMetrics(metrics);
            int density = metrics.densityDpi;

            if(setOrientation==Configuration.ORIENTATION_PORTRAIT)
            {
                if(density == metrics.DENSITY_MEDIUM){
                    camera.setDisplayOrientation(270);
                }else if(density == metrics.DENSITY_XXHIGH){
                    camera.setDisplayOrientation(90);
                }else{
                    camera.setDisplayOrientation(90);
                }

            } else {
                camera.setDisplayOrientation(0);
            }

            /*
            if(Integer.parseInt(Build.VERSION.SDK)==8){
                setDisplayOrientationMethod(camera,90);
            }else{

                //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                if(setOrientation==Configuration.ORIENTATION_PORTRAIT)
                {
                    p.set("orientation", "portrait");
                    p.set("rotation", 90);
                }
                //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                if(setOrientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    p.set("orientation", "landscape");
                    p.set("rotation", 90);
                }
            }*/

            //camera.setDisplayOrientation(90);
//        camera.autoFocus(new AutoFocusCallback()
//        {
//           // @Override
//            public void onAutoFocus(boolean success, Camera camera) {
//                // TODO Auto-generated method stub
//              //  Preview.this.invalidate();
//            }
//        });

            //
            Camera.Parameters p = camera.getParameters();
            // Obtener la mejor resolución del dispositivo Android (Relación Calidad/Peso)
            // Rango óptimo
    	/*
    	         width / height
    	         600       480      Mínimo requerido
    	         800       600
    	         1200      1200     Máximo requerido

    	 */
            List<Camera.Size> listaResoluciones           =   p.getSupportedPictureSizes();

            Integer count  =   0;
            for(int i=0; i<listaResoluciones.size();i++)
            {
                Integer width   =   0;
                Integer height  =   0;


                width   =   listaResoluciones.get(i).width;
                height  =   listaResoluciones.get(i).height;


                if(width>=600 && width<=1200)
                {

                    count++;
                    Log.d("***listaResoluciones***","WIDTH = " + width + " x HEIGHT = " + height + " ; Count = " + count);
                }

                //  Log.d("***listaResoluciones***","WIDTH = " + width + " x HEIGHT = " + height + " ; Count = " + count);
            }



            Integer[][] WH  =   new Integer [count][2];

            Integer countWH    =   0;

            for(int i=0; i<listaResoluciones.size();i++)
            {
                Integer width   =   0;
                Integer height  =   0;


                width   =   listaResoluciones.get(i).width;
                height  =   listaResoluciones.get(i).height;



                if(width>=600 && width<=1200)
                {
                    WH[countWH][0]       =   listaResoluciones.get(i).width;
                    WH[countWH][1]       =   listaResoluciones.get(i).height;
                    countWH++;
                }

            }




            int b, j, t,n,t2;

            n  =   WH.length-1;


            do
            {
                b = 0;
                for(j=0; j<n; j++)
                {
                    if(WH[j][0] > WH[j+1][0])
                    {

                        t = WH[j][0];
                        t2 =WH[j][1];

                        WH[j][0] = WH[j+1][0];
                        WH[j][1] = WH[j+1][1];

                        WH[j+1][0] = t;
                        WH[j+1][1] = t2;


                        b++;
                    }
                }
                n--;
            }
            while(b > 0);


            for(int i=0;i< WH.length;i++)
            {

                Log.d("WH","Valor = " +WH[i][0].toString() + ","+WH[i][1].toString() );

            }

            Integer defaultW=800;
            Integer defaultH=600;

            if(WH.length>0)
            {
                defaultW    =   WH[0][0];
                defaultH    =   WH[0][1];
            }

            p.set("jpeg-quality", 100);

            if(camera.getParameters().getSupportedPictureFormats() != null)
            {
                p.setPictureFormat(PixelFormat.JPEG);
            }

//        if(camera.getParameters().getSupportedSceneModes() != null)
//        {
//            p.setSceneMode(Parameters.SCENE_MODE_PORTRAIT);
//        }

//        if(camera.getParameters().getSupportedWhiteBalance()!= null)
//        {
//            p.setWhiteBalance(Parameters.WHITE_BALANCE_DAYLIGHT);
//        }


            //int id_camera_resolution = getCameraResolution();
            int id_camera_resolution = 1;
            Log.v("PreviewWDCamera","Resolution id_camera_resolution = " + id_camera_resolution);

            if(id_camera_resolution==1)
            {
                int[] get_resolution = new int[2];
                //get_resolution = getResolution(listaResoluciones);
                get_resolution = getBestResolution(listaResoluciones);

                defaultW = get_resolution[0];
                defaultH = get_resolution[1];

//            defaultW = 2048;
//            defaultH = 1536;
            }



            p.setPictureSize(defaultW, defaultH);
            Log.d("setPictureSize","Width = " + defaultW + "; Height = " + defaultH);

            camera.setParameters(p);

            camera.setPreviewCallback(new Camera.PreviewCallback()
            {
                // Called for each frame previewed
                public void onPreviewFrame(byte[] data, Camera camera)
                {
                    PreviewCam.this.invalidate();
                }
            });


            camera.setPreviewCallback(new Camera.PreviewCallback()
            {
                // Called for each frame previewed
                public void onPreviewFrame(byte[] data, Camera camera)
                {
                    PreviewCam.this.invalidate();
                }
            });



        }
        catch (IOException e)
        {
            Log.v("PREVIEW", "Error IOException = " + e.getMessage());
            camera.release();
            camera = null;
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            Log.v("PREVIEW", "Error Exception = " + ex.getMessage());
        }
    }

    public void setCameraResolution(String id)
    {
        try
        {
            Log.i("setCameraResolution","id = " + id);
            int camera_resolution = 0; // 0 - Normal; 1 - High

            if
            (
                    id.equals("1")  ||	//	Estado de Cuenta
                            id.equals("5")  ||	//	Firma del trabajador
                            id.equals("8")  ||	//	Comprobante de domicilio
                            id.equals("9")  ||	//	ID Menor de edad
                            id.equals("12") ||	//	Solicitud
                            id.equals("13") ||	//	Rendimiento
                            id.equals("14") 	//	Contrato
            )
            {
                camera_resolution = 1;
            }
            else
            {
                camera_resolution = 0;
            }

            camera_resolution = 1;

            editor = preferences.edit();
            editor.putInt("store_camera_resolution", camera_resolution);
            editor.commit();
        }
        catch(Exception ex)
        {

        }
    }

    public int[] getBestResolution(List<Camera.Size> list)
    {
        String TAG = "getBestResolution";
        int[] set_width_and_height = new int[2];

        List<Camera.Size> listDeviceResolutiones = list;
        int countGetResolutions = listDeviceResolutiones.size();
        int index;

        try
        {
            Log.i(TAG,"countGetResolutions =" + countGetResolutions);
            for(int i=0; i<countGetResolutions ; i++)
            {
                Log.i(TAG,"width = " + listDeviceResolutiones.get(i).width+" - height = "+listDeviceResolutiones.get(i).height);
            }

            if(countGetResolutions==1)
            {
                set_width_and_height[0] = listDeviceResolutiones.get(0).width;
                set_width_and_height[1] = listDeviceResolutiones.get(0).height;

                return set_width_and_height;
            }
            else
            {
                //	Buscar por la resolución ideal, en base a la obtenida en el Galaxy S4
                //	width  = 2048
                //	height = 1536

                boolean flag_ideal 	= false;
                int id_selected_ideal = 0;

                for(int i=0;i<countGetResolutions;i++)
                {
                    if
                    (
                            listDeviceResolutiones.get(i).width  == 2048
                                    &&
                                    listDeviceResolutiones.get(i).height == 1536
                    )
                    {
                        flag_ideal 		= true;
                        id_selected_ideal = i;
                    }
                }

                Log.i(TAG,"flag_ideal 		=" + flag_ideal);
                Log.i(TAG,"id_selected_ideal  =" + id_selected_ideal);

                if(flag_ideal)
                {
                    set_width_and_height[0] = listDeviceResolutiones.get(id_selected_ideal).width;
                    set_width_and_height[1] = listDeviceResolutiones.get(id_selected_ideal).height;

                    Log.i(TAG,"width  ideal = " + set_width_and_height[0]);
                    Log.i(TAG,"height ideal = " + set_width_and_height[1]);

                    return set_width_and_height;
                }
                else
                {
                    //	Buscar por una resolución mayor a 1500 y menor a 2500 (width)
                    boolean flag_normal 	 = false;
                    int id_selected_normal = 0;

                    for(int i=0;i<countGetResolutions;i++)
                    {
                        if
                        (
                                listDeviceResolutiones.get(i).width > 1500
                                        &&
                                        listDeviceResolutiones.get(i).width <= 2500
                        )
                        {
                            flag_normal 			= true;
                            id_selected_normal 	= i;
                        }
                    }

                    if(flag_normal)
                    {
                        set_width_and_height[0] = listDeviceResolutiones.get(id_selected_normal).width;
                        set_width_and_height[1] = listDeviceResolutiones.get(id_selected_normal).height;

                        Log.i(TAG,"width  normal = " + set_width_and_height[0]);
                        Log.i(TAG,"height normal = " + set_width_and_height[1]);

                        return set_width_and_height;
                    }
                    else
                    {
                        //	Retornar la primer resolución disponible, no garantiza que las fotografías esten en alta resolución
                        //	Una resolución menor a 1500 producirá fotografías en baja resolución
                        //	Una resolución mayor a 2500 provocará problemas de memoria en algunos dispositivos Android
                        set_width_and_height[0] = listDeviceResolutiones.get(0).width;
                        set_width_and_height[1] = listDeviceResolutiones.get(0).height;

                        Log.i(TAG,"width  no en rango = " + set_width_and_height[0]);
                        Log.i(TAG,"height no en rango = " + set_width_and_height[1]);
                        return set_width_and_height;
                    }
                }
            }

        }
        catch(Exception ex)
        {
            set_width_and_height[0] = listDeviceResolutiones.get(0).width;
            set_width_and_height[1] = listDeviceResolutiones.get(0).height;

            return set_width_and_height;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        setOrientation = checkOrientation();
        Log.i("PreviewBack","setOrientation " + setOrientation);

        Log.i("PreviewBack","sp_lock_device_rotation 1 " + sp_lock_device_rotation);
        sp_lock_device_rotation = getLockDeviceRotation();
        Log.i("PreviewBack","sp_lock_device_rotation 2" + sp_lock_device_rotation);

        if(!sp_lock_device_rotation)
        {
            if (camera != null) {
                camera.startPreview();
            }
        }

        // camera.startPreview();

        //camera.startPreview();
//    camera.autoFocus(new AutoFocusCallback() {
//
//        @Override
//        public void onAutoFocus(boolean success, Camera camera) {
//            // TODO Auto-generated method stub
//
//        }
//    });
        Log.d("PREVIEW", "startPreview");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(camera != null)
        {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
            Log.d("PREVIEW", "Surface destroyed");
        }
    }

    /*protected void setDisplayOrientationMethod(Camera camera, int angle){
        Method downPolymorphic;
        try{
            downPolymorphic =  camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if(downPolymorphic!=null){
                downPolymorphic.invoke(camera,new Object[]{angle});
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
