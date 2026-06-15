package com.optcg.app.di;

import android.app.Application;

/**
 * Application entry point and composition root. Owns the {@link ServiceLocator}, which
 * constructs and exposes the data layer (database, data sources, repositories). UI
 * classes reach the data layer through {@code ServiceLocator.get(context)} rather than
 * instantiating databases, scrapers or parsers themselves.
 */
public class OptcgApplication extends Application {

    private ServiceLocator serviceLocator;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceLocator = new ServiceLocator(this);
    }

    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
