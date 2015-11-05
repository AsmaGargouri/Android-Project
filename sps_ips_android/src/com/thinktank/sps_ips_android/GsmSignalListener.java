package com.thinktank.sps_ips_android;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.widget.Toast;
 
public class GsmSignalListener extends PhoneStateListener
{
  @Override
  public void onSignalStrengthsChanged(SignalStrength signalStrength)
  {
     super.onSignalStrengthsChanged(signalStrength);   
     }

};