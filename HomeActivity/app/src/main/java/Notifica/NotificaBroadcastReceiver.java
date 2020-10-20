package Notifica;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.parkingapp.homeactivity.R;

import static Notifica.Notifica.CHANNEL_ID;
import static Notifica.Notifica.NOTIFICATION_ID;

public class NotificaBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Notifica notifica = new Notifica();
        notifica.createNotificationChannel(context, NotificationManager.IMPORTANCE_DEFAULT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(intent.getStringExtra("titolo"))
                .setContentText(intent.getStringExtra("messaggio"))
                .setSmallIcon(R.drawable.logo_app)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //  Random rand = new Random(); //Genero un id a caso per la notifica
        int id=intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, builder.build());
        Log.i("Notifica Broadcast", "notifica creata");
    }
}
