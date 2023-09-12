
import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Parcelable;

import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import android.nfc.NdefMessage;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.app.PendingIntent;

public class NfcUtils {
    public static NdefMessage[] getNdefMessages(Intent intent) {
        NdefMessage[] messages = null;
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
            }
        }

        return messages;
    }
}