import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.i("Device Admin: ", "Enabled FINDME");
        super.onEnabled(context, intent);
    }

    @Override
    public String onDisableRequested(Context context, Intent intent) {
        return "Admin disable requested";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.i("Device Admin: ", "Disabled");
        super.onDisabled(context, intent);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        Log.i("Device Admin: ", "Password changed");
        super.onPasswordChanged(context, intent);
    }

}
