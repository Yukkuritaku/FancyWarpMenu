package com.github.yukkuritaku.modernwarpmenu.state;

import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentDetails {

    //TODO provide the support link
    public static final String SUPPORT_LINK = "";

    public static boolean isDevelopmentEnvironment(){
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
