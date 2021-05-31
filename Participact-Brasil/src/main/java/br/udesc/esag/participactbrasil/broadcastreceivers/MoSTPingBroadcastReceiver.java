package br.udesc.esag.participactbrasil.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.most.MoSTService;
import org.most.pipeline.Pipeline;

import java.util.List;

public class MoSTPingBroadcastReceiver extends BroadcastReceiver {

    public static long moSTlastResponsePing = 0L;
    public static List<Pipeline.Type> activePipeline = null;

    @SuppressWarnings("unchecked")
    @Override
    public void onReceive(Context context, Intent intent) {
        moSTlastResponsePing = intent.getExtras().getLong(MoSTService.KEY_PING_TIMESTAMP);
        activePipeline = (List<Pipeline.Type>) intent.getExtras().getSerializable(MoSTService.KEY_PING_RESULT);
    }

}
