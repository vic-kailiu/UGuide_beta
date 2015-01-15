/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kai.uGuide.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;

import com.kai.uGuide.R;
import com.kai.uGuide.ui.adapter.Home;
import com.kai.uGuide.ui.adapter.ListItem;
import com.kai.uGuide.ui.adapter.MeatAdapter;
import com.kai.uGuide.ui.adapter.Result;

public class ResultAdapterTransitionFragment extends AdapterTransitionFragment {
    private static final String ARG_POSITION = "position";

    private int position;

    public static ResultAdapterTransitionFragment newInstance(int position) {
        ResultAdapterTransitionFragment fragment = new ResultAdapterTransitionFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected ListItem[] getData(int position) {
        switch (position)
        {
            case 0: return Result.Attractions;
            case 1: return Result.Tours;
            case 2: return Result.Foods;
            case 3: return Result.Hotels;
            case 4: return Result.Events;
        }
        return Result.Attractions;
    }
}
