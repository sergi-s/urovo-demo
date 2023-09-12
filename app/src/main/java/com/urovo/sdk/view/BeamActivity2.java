package com.urovo.sdk.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.urovo.sdk.R;

import android.nfc.NfcAdapter;
import android.nfc.tech.NfcA;
import android.os.Parcelable;
import android.content.Intent;
import android.app.PendingIntent;
import java.nio.charset.Charset;
import java.io.*;
import android.nfc.Tag;

public class BeamActivity2 extends BaseActivity {
    private TextView textView;
    private Button listenButton;
    private Tag tag;
    private NfcA nfcA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam2);

        textView = findViewById(R.id.nfcTagContent);
        listenButton = findViewById(R.id.listenButton);
        initView();

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click here, initiate NFC reading when the button is clicked
                readAllBlocks();
            }
        });

        // Get the intent that started this activity
        Intent intent = getIntent();
        // Check if the intent has a tag extra
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            // Get the tag extra from the intent
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            // Get an instance of NfcA for the tag
            nfcA = NfcA.get(tag);
        }
    }

    private void readAllBlocks() {
        StringBuilder tagData = new StringBuilder();

        // Iterate through all possible blocks (0-255)
        for (int block = 0; block <= 255; block++) {
            // Read the current block
            String blockData = readBlock(block);
            tagData.append("Block ").append(block).append(": ").append(blockData).append("\n");
        }

        // Display all block data in the TextView
        textView.setText(tagData.toString());
    }

    private String readBlock(int block) {
        if (block < 0 || block > 255) {
            return "Invalid block number";
        }

        byte[] READ_COMMAND = { (byte) 0x30, (byte) block };
        try {
            nfcA.connect();
            byte[] response = nfcA.transceive(READ_COMMAND);
            nfcA.close();

            if (response.length == 16) {
                return bytesToHex(response);
            } else {
                return "Invalid response";
            }
        } catch (IOException e) {
            return "IO error";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
