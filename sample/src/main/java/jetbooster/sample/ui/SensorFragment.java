/*
 * Copyright 2019 yinpinjiu@gmail.com
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
package jetbooster.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import jetbooster.JetBooster;
import jetbooster.annotations.ActivityScope;
import jetbooster.sample.BaseFragment;
import jetbooster.sample.R;
import jetbooster.sample.databinding.FragmentSensorBinding;
import jetbooster.sample.vm.GameViewModel;
import jetbooster.sample.vm.SensorViewModel;
import layoutbinder.LayoutBinder;
import layoutbinder.annotations.BindLayout;

import javax.inject.Inject;

public class SensorFragment extends BaseFragment {

    @BindLayout(R.layout.fragment_sensor)
    FragmentSensorBinding binding;

    @Inject
    SensorViewModel viewModel;

    @Inject
    @ActivityScope
    GameViewModel gameViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
