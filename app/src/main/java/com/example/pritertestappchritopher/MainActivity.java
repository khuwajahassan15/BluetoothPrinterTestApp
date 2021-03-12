package com.example.pritertestappchritopher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pritertestappchritopher.Print.PrintPic;
import com.example.pritertestappchritopher.Print.PrinterCommands;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends BaseActivity   {
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    int printstat;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private static OutputStream btoutputstream;
    ProgressDialog printingProgressBar;
    Dialog blutoothDevicesDialog;
    boolean isInApp=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printingProgressBar=new ProgressDialog(this);
        printingProgressBar.setMessage("Printing...");
        
        findViewById(R.id.printBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                   // dialogBoxForConnecctingBTPrinter();
                }
                else
                {
                    if(mBluetoothSocket!=null)
                    {
                        showToast("Already Connected");

                        try {
                            sendData();
                        } catch (IOException e) {
                            Log.e("SendDataError",e.toString());
                            e.printStackTrace();
                        }


                    }
                    else
                    {
                        dialogBoxForConnecctingBTPrinter();
                    }
                }
            }
        });

    }
    //Printing Stuff
    private void initials(){
        ProgressBar tv_prgbar=blutoothDevicesDialog.findViewById(R.id.printerProgress);
        tv_prgbar.setVisibility(View.VISIBLE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        ListView t_blueDeviceListView=blutoothDevicesDialog.findViewById(R.id.blueDeviceListView);
        t_blueDeviceListView.setAdapter(mPairedDevicesArrayAdapter);
        t_blueDeviceListView.setOnItemClickListener(mDeviceClickListener);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName()+"\n"+mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
        tv_prgbar.setVisibility(View.GONE);
    }
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {
            TextView tv_status=blutoothDevicesDialog.findViewById(R.id.tv_status);
            ProgressBar tv_prgbar=blutoothDevicesDialog.findViewById(R.id.printerProgress);
            try {
                tv_prgbar.setVisibility(View.VISIBLE);
                tv_status.setText("Device Status:Connecting....");
                mBluetoothAdapter.cancelDiscovery();
                String mDeviceInfo = ((TextView) mView).getText().toString();
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Code here will run in UI thread
                        TextView tv_status=blutoothDevicesDialog.findViewById(R.id.tv_status);
                        ProgressBar tv_prgbar=blutoothDevicesDialog.findViewById(R.id.printerProgress);

                        try {

                            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                            mBluetoothAdapter.cancelDiscovery();
                            mBluetoothSocket.connect();
                            tv_status.setText("Device Status:Connected");
                            //controlLay(1);
                            tv_prgbar.setVisibility(View.GONE);
                            blutoothDevicesDialog.dismiss();
                        } catch (IOException eConnectException) {
                            tv_status.setText("Device Status:Try Again");
                            tv_prgbar.setVisibility(View.GONE);
                            Log.e("ConnectError",eConnectException.toString());
                            closeSocket(mBluetoothSocket);
                            //controlLay(0);
                        }

                    }
                });


            } catch (Exception ex) {
                Log.e("ConnectError",ex.toString());

            }
        }
    };
    private void dialogBoxForConnecctingBTPrinter() {
        int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        blutoothDevicesDialog = new Dialog(this);
        blutoothDevicesDialog.setContentView(R.layout.blutoothdevicelistdialoglayout);
        Objects.requireNonNull(blutoothDevicesDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        blutoothDevicesDialog.getWindow().setLayout((int) (width / 1.1f), (int) (height / 1.3));
//        dialog.getWindow().setLayout(500, 500);
        blutoothDevicesDialog.setCancelable(false);
        //init dialog views
        final ImageView ivBack = blutoothDevicesDialog.findViewById(R.id.iv_back);
        final Button scanDevices=blutoothDevicesDialog.findViewById(R.id.btn_scanDevices);
        TextView tv_status=blutoothDevicesDialog.findViewById(R.id.tv_status);
        ListView blueDeviceListView=blutoothDevicesDialog.findViewById(R.id.blueDeviceListView);
        initials();
        scanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                initials();

            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blutoothDevicesDialog.dismiss();
            }
        });

        blutoothDevicesDialog.show();

    }
    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d("", "SocketClosed");
        } catch (IOException ex) {
            Log.d("", "CouldNotCloseSocket");
        }
    }
    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v("", "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }
    void sendData() throws IOException {
        try {
            btoutputstream = mBluetoothSocket.getOutputStream();
            // the text typed by the user
            InvoiceForPrint recInvoiceForPrint=new InvoiceForPrint();
            recInvoiceForPrint.setMsatoshi(823239211);
            recInvoiceForPrint.setPaid_at(123123213);
            recInvoiceForPrint.setPayment_preimage("a9cbcbedf78499b4aa034f0b2f4114e765547df82b1943f9dbd905e8c3031be2");
            recInvoiceForPrint.setPurchasedItems("\n Apple \n Banan \n Coke");
            recInvoiceForPrint.setTax("10");
            DecimalFormat precision = new DecimalFormat("0.00");
            if(recInvoiceForPrint!=null) {
                final String paidAt =    getDateFromUTCTimestamp(recInvoiceForPrint.getPaid_at(),AppConstants.OUTPUT_DATE_FORMATE);
                final String amount =    excatFigure(round((mSatoshoToBtc(recInvoiceForPrint.getMsatoshi())),9))+" BTC/ $"+precision.format(round(getUsdFromBtc(mSatoshoToBtc(recInvoiceForPrint.getMsatoshi())),2))+" USD";
                final  String des   =    recInvoiceForPrint.getPurchasedItems();
                final String Tax    =    recInvoiceForPrint.getTax();
                final Bitmap bitmap=getBitMapFromHex(recInvoiceForPrint.getPayment_preimage());
                printingProgressBar.show();
                printingProgressBar.setCancelable(false);
                printingProgressBar.setCanceledOnTouchOutside(false);
                Thread t = new Thread() {
                    public void run() {
                        try {
                            // This is printer specific code you can comment ==== > Start
                            btoutputstream.write(PrinterCommands.reset);
                            btoutputstream.write(PrinterCommands.INIT);
                            btoutputstream.write("\n\n".getBytes());
                            btoutputstream.write("Amount:".getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write(PrinterCommands.ESC_ALIGN_CENTER);
                            btoutputstream.write(amount.getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write("Paid at:".getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write(paidAt.getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write("Tax:".getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write(paidAt.getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write("Description:".getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write(des.getBytes());
                            btoutputstream.write("\n".getBytes());
                            btoutputstream.write("Payment Hash:".getBytes());
//                            btoutputstream.write("Image Here!!!:".getBytes());
                            printNewLine();
                            if(bitmap!=null){
                                Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                                new ByteArrayOutputStream();
                                PrintPic printPic = PrintPic.getInstance();
                                printPic.init(bMapScaled);
                                byte[] bitmapdata = printPic.printDraw();
//                                btoutputstream.write("Image Here!!!:".getBytes());
                                btoutputstream.write(PrinterCommands.print);
                                btoutputstream.write(bitmapdata);
                                btoutputstream.write(PrinterCommands.print);
                                btoutputstream.write("\n\n".getBytes());
                            }

                            btoutputstream.write("\n\n".getBytes());
                            Thread.sleep(1000);
                            printingProgressBar.dismiss();


                        } catch (Exception e) {
                            Log.e("PrintError", "Exe ", e);

                        }

                    }
                };
                t.start();
            }
            else {
                String paidAt="\n\n\n\n\n\n\nNot Data Found\n\n\n\n\n\n\n";

                btoutputstream.write(paidAt.getBytes());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showToast(String already_connected) {
        Toast.makeText(this,already_connected,Toast.LENGTH_LONG).show();
    }
    protected void printNewLine() {
        try {
            btoutputstream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isInApp){
         }

    }
    @Override
    public void onDestroy () {
//        handler.removeCallbacks(runnable);
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    ListPairedDevices();
                    initials();
                } else {
                    Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;


        }

    }
}