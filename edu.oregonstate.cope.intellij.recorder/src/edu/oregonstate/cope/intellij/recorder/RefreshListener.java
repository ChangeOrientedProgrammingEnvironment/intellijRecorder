package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.vfs.VirtualFileManagerListener;

/**
* Created by mihai on 3/27/14.
*/
class RefreshListener implements VirtualFileManagerListener {
    private volatile boolean isRefresh;

    @Override
    public void beforeRefreshStart(boolean asynchronous) {
        isRefresh = true;
    }

    @Override
    public void afterRefreshFinish(boolean asynchronous) {
        isRefresh = false;
    }

    public boolean isRefresh(){
        return isRefresh;
    }
}
