package com.davymoreau.android.beerbook.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by davy on 08/10/2017.
 */

public class FileUtil {

    public static final void rawFileToInternalStorage(Context context, int id, String destFileName) {

        InputStream is;

        try {
            is = context.getResources().openRawResource(id);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(is));
            FileOutputStream fileout = context.openFileOutput(destFileName, Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fileout);

            String line;
            while ((line = input.readLine()) != null) {
                pw.println(line);
            }

            input.close();
            pw.close();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static final ArrayList<String> InternalStorageToArray(Context context, String file) {

        ArrayList<String> list = new ArrayList<>();

        try {
            FileInputStream fis = context.openFileInput(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }

            fis.close();
            isr.close();
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }

    public static final void ArrayToInternalStorage(Context context, ArrayList<String> list, String file) {

        try {
            FileOutputStream fileout = context.openFileOutput(file, Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fileout);

            for (String line : list) {
                pw.println(line);
            }

            pw.close();
            fileout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
