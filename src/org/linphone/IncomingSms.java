package org.linphone;

import org.linphone.mediastream.Log;
import org.linphone.setup.SetupActivity;
import org.linphone.setup.SetupFragmentsEnum;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import com.sivvar.AppUtil;

@SuppressLint("DefaultLocale")
public class IncomingSms extends BroadcastReceiver {

	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
             
            if (bundle != null) {
                 
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                 
                for (int i = 0; i < pdusObj.length; i++) {
                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
 
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                    
                    if (SetupActivity.instance() != null 
                    		&& message.toLowerCase().contains("sivvar registration code")
                    		&& SetupFragmentsEnum.WIZARD_CONFIRM == SetupActivity.instance().getCurrentFragmentEnum()) {
                    	String regCode = AppUtil.getRegistrationCodeFrom(message);
                    	SetupActivity.instance().subscribeRegCode(regCode);
                    }
                     
                } // end for loop
              } // bundle is null
 
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
             
        }
	}

}
