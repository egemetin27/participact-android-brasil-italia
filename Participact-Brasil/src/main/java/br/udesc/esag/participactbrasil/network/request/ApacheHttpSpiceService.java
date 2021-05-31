package br.udesc.esag.participactbrasil.network.request;

import android.app.Application;
import android.util.Log;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.apache.http.client.HttpClient;

import java.util.Set;

import br.udesc.esag.participactbrasil.support.HttpUtils;
import roboguice.util.temp.Ln;

public class ApacheHttpSpiceService extends SpiceService {

    HttpClient httpClient;

    public ApacheHttpSpiceService() {
        Ln.getConfig().setLoggingLevel(Log.ERROR);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        httpClient = HttpUtils.getHttpClient(this);
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();
        // init
        JacksonObjectPersisterFactory jacksonObjectPersisterFactory = new JacksonObjectPersisterFactory(application);
        cacheManager.addPersister(jacksonObjectPersisterFactory);
        return cacheManager;
    }

    @Override
    public void addRequest(CachedSpiceRequest<?> request, Set<RequestListener<?>> listRequestListener) {
        if (request.getSpiceRequest() instanceof ApacheHttpSpiceRequest) {
            ((ApacheHttpSpiceRequest<?>) request.getSpiceRequest()).setHttpClient(httpClient);
        }
        super.addRequest(request, listRequestListener);
    }

}