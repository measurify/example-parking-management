package gestione_file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

public class GestioneFile {

    final static String TAG="Gestione file";

    public static String createDir(String nomeDirectory){
        String storeDir= Environment.getExternalStorageDirectory()+ nomeDirectory;
        File f= new File(storeDir);
        if(!f.exists()){
            f.mkdir();
            if(!f.mkdir()){
                Log.e(TAG, "Cannot create download directory");
                return  null;
            }
            else return storeDir;
        }
        else return storeDir;
    }

    public static String[] LocalInfo()
    {
        File f= new File(Environment.getExternalStorageDirectory()+ "/infoLocali.txt");
        return null;
        
    }

}
