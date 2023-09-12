package com.urovo.sdk.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.urovo.sdk.R;

import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import android.os.Parcelable;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.app.PendingIntent;

import java.nio.charset.Charset;
import java.io.*;

// public class BeamActivity extends BaseActivity implements View.OnClickListener {

//     private String driver = "";
//     private TextView nfcTagContent;

//     private PendingIntent pendingIntent;
//     private NfcAdapter nfcAdapter;

//     @Override
//     protected void onCreate(Bundle savedInstanceState) {
//         super.onCreate(savedInstanceState);
//         setContentView(R.layout.activity_beam);
//         nfcTagContent = findViewById(R.id.nfcTagContent);
//         nfcTagContent.setText("Loaded successfully");
//     }

//     @Override
//     public void onClick(View view) {

//         boolean status = false;
//         int iRet = -1;
//         try {
//             switch (view.getId()) {
//                 case R.id.btnsearchCard:
//                     outputText("searchCard");
//                     // startSearchCard();
//                     break;
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     @Override
//     protected void onResume() {
//         super.onResume();
//         // Check if the app is launched due to an NFC tag discovery
//         Intent intent = getIntent();
//         if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
//             // Handle the NFC tag discovery
//             processNfcTag(intent);
//         }
//     }

//     private void processNfcTag(Intent intent) {
//         // Extract the NFC tag information
//         Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

//         // Check if the tag supports Mifare Ultralight technology
//         MifareUltralight mifareUltralightTag = MifareUltralight.get(tag);
//         if (mifareUltralightTag != null) {
//             try {
//                 // Connect to the tag
//                 mifareUltralightTag.connect();

//                 // Read data from the tag (example: reading page 4)
//                 byte[] data = mifareUltralightTag.readPages(4);

//                 // Process and display the tag's content
//                 String tagContent = new String(data);
//                 nfcTagContent.setText(tagContent);
//             } catch (Exception e) {
//                 // Handle exceptions
//                 e.printStackTrace();
//             } finally {
//                 // Disconnect from the tag
//                 try {
//                     mifareUltralightTag.close();
//                 } catch (Exception e) {
//                     // Handle exceptions
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }

// }
// import androidx.appcompat.app.AppCompatActivity;

// This activity handles the ACTION_TECH_DISCOVERED intent for NfcA tags
public class BeamActivity extends BaseActivity/* extends AppCompatActivity */ {
    // The tag object that was discovered
    private Tag tag;
    // The NfcA object that represents the tag's communication
    private NfcA nfcA;

    private TextView textView;
    // The ISO 14443-A command for reading a block of data
    private static final byte[] READ_COMMAND = { (byte) 0x30, (byte) 0x00 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        TextView textView = findViewById(R.id.nfcTagContent);
        // Get the intent that started this activity
        Intent intent = getIntent();
        // Check if the intent has a tag extra
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            // Get the tag extra from the intent
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // Get an instance of NfcA for the tag
            nfcA = NfcA.get(tag);
            // Try to read the first block of data from the tag
            readBlock(0);
        }
    }

    // A method that reads a block of data from the tag using the NfcA object
    private void readBlock(int block) {
        // Check if the block number is valid (0-255)
        if (block < 0 || block > 255) {
            // outputText("Invalid block number");

            return;
        }
        // Update the block number in the read command
        READ_COMMAND[1] = (byte) block;
        // Try to connect to the tag and send the read command
        try {
            // Connect to the tag
            nfcA.connect();
            // Send the read command and get the response
            byte[] response = nfcA.transceive(READ_COMMAND);
            // Check if the response is valid (16 bytes)
            if (response.length == 16) {
                // Convert the response to a hex string
                String hex = bytesToHex(response);
                // Display the hex string in a text view
                textView.setText(hex);
            } else {
                textView.setText("Invalid response");
            }
            // Close the connection to the tag
            nfcA.close();
        } catch (IOException e) {
            textView.setText("IO error");
        }
    }

    // A helper method that converts a byte array to a hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}