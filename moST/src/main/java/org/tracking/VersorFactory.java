/*
 * Copyright (C) 2012 <copyright_owner>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author marcomoschettini
 */

package org.tracking;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class VersorFactory {

    private Context context;

    public VersorFactory(Context context) {
        this.context = context;

    }

    @SuppressWarnings("resource")
    public List<Versor> getVersors() throws IOException {
        List<Versor> versors = new ArrayList<Versor>();
        AssetManager am = context.getAssets();
        String[] versors_names = am.list("versors");
        String activity_pole = "";
        BufferedReader buf;

        for (String versor_name : versors_names) {

            String activity = versor_name.split("\\.")[0];
            try {
                activity_pole = versor_name.split("\\.")[1];
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            try {
                InputStream is = context.getAssets().open("versors/" + versor_name);
                buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line = buf.readLine();
                StringTokenizer st = new StringTokenizer(line, ";");
                Double amp_average = Double.parseDouble((String) st.nextElement());
                Double amp_std = Double.parseDouble((String) st.nextElement());
                Double shape_std = Double.parseDouble((String) st.nextElement());

                line = buf.readLine();
                st = new StringTokenizer(line, ",");
                List<Double> fourier_shape = new ArrayList<Double>();
                while (st.hasMoreElements()) {
                    fourier_shape.add(Double.parseDouble((String) st.nextElement()));
                }
                double[] fourier_array = new double[fourier_shape.size()];
                for (int i = 0; i < fourier_shape.size(); i++)
                    fourier_array[i] = fourier_shape.get(i);
                versors.add(new Versor(amp_average, amp_std, shape_std, fourier_array, activity, activity_pole));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return versors;
    }

}
