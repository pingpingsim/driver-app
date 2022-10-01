package com.pingu.driverapp.util;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.pingu.driverapp.R;
import com.pingu.driverapp.data.model.LatLng;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class ParcelHelper {

    private static LatLng getLatLngFromAddress(final Context context, final String strAddress) {
        try {
            Geocoder coder = new Geocoder(context);
            List<Address> address;
            LatLng latLng = null;

            try {
                address = coder.getFromLocationName(strAddress, 5);
                if (address == null) {
                    return null;
                }

                Address location = address.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());

            } catch (IOException ex) {
                return null;
            }

            return latLng;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void openMap(final Context context, String address) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final View view = LayoutInflater.from(context).inflate(R.layout.dialog_gps_layout, null);
            builder.setView(view);

            final AlertDialog dialog = builder.create();
            dialog.show();

            view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            view.findViewById(R.id.panel_gps_google_map).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openGoogleMapForCoordinate(context, address);
                    dialog.dismiss();
                }
            });

            view.findViewById(R.id.panel_gps_waze).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openWazeForCoordinate(context, address);
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            Toast.makeText(context, context.getString(R.string.gps_open_navigation_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private static void openGoogleMapForCoordinate(final Context context, final String address) {
        try {
            final LatLng latLngForAddress = getLatLngFromAddress(context, address);

            String map = "";
            if (latLngForAddress != null) {
                map = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", latLngForAddress.getLatitude(), latLngForAddress.getLongitude());

            } else {
                map = "http://maps.google.com/maps?q=" + address;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.gps_google_map_not_installed), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(context, context.getString(R.string.gps_open_navigation_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private static void openWazeForCoordinate(final Context context, final String address) {
        try {
            final LatLng latLngForAddress = getLatLngFromAddress(context, address);

            if (latLngForAddress != null) {
                String mapRequest = "https://waze.com/ul?ll=" + latLngForAddress.getLatitude() + "," + latLngForAddress.getLongitude() + "&navigate=yes";
                Uri gmmIntentUri = Uri.parse(mapRequest);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.waze");
                context.startActivity(mapIntent);
            } else {
                Toast.makeText(context, context.getString(R.string.gps_address_not_recognised), Toast.LENGTH_SHORT).show();
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getString(R.string.gps_waze_not_installed), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(context, context.getString(R.string.gps_open_navigation_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public static void callNumber(final Context context, final String contactNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber));
        context.startActivity(intent);
    }

    public static void openWhatsApp(final String number, final String message, final Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            } else {
                Toast.makeText(context, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    public static boolean validateParcelIdFormat(final String parcelId) {
        if (!TextUtils.isEmpty(parcelId)) {
            if (parcelId.trim().length() == 12 &&
                    (parcelId.trim().toUpperCase().startsWith("ND") || parcelId.trim().toUpperCase().startsWith("EF") || parcelId.trim().toUpperCase().startsWith("WF"))
                    &&
                    (parcelId.trim().toUpperCase().endsWith("00") || parcelId.trim().toUpperCase().endsWith("11")
                            || parcelId.trim().toUpperCase().endsWith("22") || parcelId.trim().toUpperCase().endsWith("33"))) {
                return true;
            }
        }
        return false;
    }
}
