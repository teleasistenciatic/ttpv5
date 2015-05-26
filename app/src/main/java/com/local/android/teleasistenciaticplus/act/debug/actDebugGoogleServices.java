package com.local.android.teleasistenciaticplus.act.debug;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.local.android.teleasistenciaticplus.R;

public class actDebugGoogleServices extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_debug_google_services);

        ////////////////////////////////////////////////////////
        // Comprobación de que los Google Services están funcionando
        ////////////////////////////////////////////////////////

        EditText googleServiceTextIsActivated = (EditText) findViewById(R.id.edit_string_google_services_output);

        int googleServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if ( googleServices == ConnectionResult.SUCCESS ) {
            googleServiceTextIsActivated.setText("Google Services ONLINE");
        } else {
            googleServiceTextIsActivated.setText("Google Services OFFLINE");
        }
    }

}
