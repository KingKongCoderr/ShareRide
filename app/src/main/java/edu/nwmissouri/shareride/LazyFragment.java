package edu.nwmissouri.shareride;

import android.support.v4.app.Fragment;

/**
 * Created by s525339 on 4/23/2016.
 */
public abstract class LazyFragment extends Fragment {

    protected boolean isVisible;
    private boolean loaded = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
<<<<<<< HEAD
        if(getUserVisibleHint()&&!loaded) {
=======
        if(getUserVisibleHint()) {
>>>>>>> d52e45c3e5920137fe65d039e1357ddaa965f69d
            loaded = true;
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
        }
    }
    protected void onVisible(){
        lazyLoad();
    }
    protected abstract void lazyLoad();
}
