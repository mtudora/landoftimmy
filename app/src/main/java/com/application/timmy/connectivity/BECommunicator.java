package com.application.timmy.connectivity;


import java.util.List;


public interface BECommunicator {

    public void onDataItemUpdated(Object data);

    public void onDataArrayUpdated(Object[] data);

    public void onDataListUpdated(List<?> data);

    public void onDataFailed(BEResponseCode responseCode);

}
