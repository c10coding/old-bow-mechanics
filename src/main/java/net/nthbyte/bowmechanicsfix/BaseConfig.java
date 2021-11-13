package net.nthbyte.bowmechanicsfix;

import net.dohaw.corelib.Config;

public class BaseConfig extends Config {

    public BaseConfig() {
        super("config.yml");
    }

    public double getVelocityScale(){
        return config.getDouble("velocity scale");
    }

}
