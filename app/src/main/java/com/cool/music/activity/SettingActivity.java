package com.cool.music.activity;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.cool.music.R;

/**
 * Created by hu.qinghui on 2019/6/17.
 */

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }


    /**
     * A corresponding FragmentManager class exists in the activity class.The FragmentManager class
     * is responsible for managing fragments and adding their views to the activity's view hierarchy.
     * You can use {@link #getFragmentManager()} or {@link #getSupportFragmentManager()} to get
     * Native or supported library versions (API level more than 11) FragmentManager.FragmentManager
     * specifically manages fragment queues and fragment transaction fallback stacks.The following
     * procedure creates and commits a fragment transaction.Fragment transactions are used to add,
     * remove, attach, detach, or replace fragments in the fragment queue, and fragmentManager
     * manages the fragment transaction fallback stack.FragmentManager. BeginTransaction () method
     * creates and returns FragmentTransaction instance.it supports fluent interface chained method call.
     * First, use the view resource id of R.id.xxx to request FragmentManager and get fragment.
     * If fragment already exists in the queue, FragmentManager returns it directly.Why might fragments exist?
     * When the device rotates or recycles memory, Android destroys the activity and calls the onCreate()
     * method when rebuilding.When the activity is destroyed, its FragmentManager saves the fragment queue,
     * so when rebuilding, the new FragmentManager first gets the saved queue, then restore the fragment
     * queue to its original state.If the fragment variable that specifies the container view resource
     * id does not exist, then the fragment variable is null. At this point, create a new fragment and
     * start a new transaction, and add the fragment to the queue.<br>
     * FragmentManager is responsible for calling the lifecycle methods of fragments in the queue.
     * What happens when you add fragments when your activity is running? In this case, FragmentManager
     * immediately scatters fragments, calling a series of lifecycle methods to quickly keep up with
     * the activity (in sync with its latest state), such as when adding fragment to a running activity,
     * the following fragment lifecycle method calls onAttach(Context)、onCreate(Context)、onCreateView(...)
     * 、onActivityCreated(Bundle)、onStart() and onResume() in turn.Once caught up, the FragmentManager
     * of the managed activity calls the fragment's state with other lifecycle methods while receiving
     * the operating system's call instructions.
     */
    @Override
    protected void onServiceBound() {
        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_fragment_container, settingFragment)
                .commit();
    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            return false;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }
    }
}
