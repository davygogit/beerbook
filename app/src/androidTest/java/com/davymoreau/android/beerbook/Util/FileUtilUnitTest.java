package com.davymoreau.android.beerbook.Util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.davymoreau.android.beerbook.R;
import com.davymoreau.android.beerbook.util.FileUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static com.davymoreau.android.beerbook.util.FileUtil.ArrayToInternalStorage;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class FileUtilUnitTest {
    @Test
    public void readfile() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        String destFileName = "test.txt";

        int id = R.raw.beertypes;
        FileUtil.rawFileToInternalStorage(appContext, id, destFileName);
        ArrayList<String> array = FileUtil.InternalStorageToArray(appContext, destFileName);

        assertEquals(array.get(0) ,"blonde");
        assertEquals(array.get(1) ,"brune");
    }


    @Test
    public void writefile() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        ArrayList<String> list1 = new ArrayList<>();
        list1.add("pale ale");
        list1.add("strong ale");

        String fileName = "test2";

        ArrayToInternalStorage(appContext, list1, fileName);

        ArrayList<String> list2 = FileUtil.InternalStorageToArray(appContext, fileName);

        assertEquals(list2.get(0) ,"pale ale");
        assertEquals(list2.get(1) ,"strong ale");
    }

}
