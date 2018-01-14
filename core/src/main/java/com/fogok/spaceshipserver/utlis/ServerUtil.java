package com.fogok.spaceshipserver.utlis;

import java.security.SecureRandom;
import java.util.InvalidPropertiesFormatException;

public class ServerUtil {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    public static String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


    public static class IPComponents{
        private String ip;
        private int port;

        public IPComponents(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public int getPort() {
            return port;
        }

        public String getIp() {
            return ip;
        }
    }

    public static IPComponents parseIpComponents(String fullIp) throws InvalidPropertiesFormatException {

        String[] rawComponentsIp = fullIp.split(":");
        if (rawComponentsIp.length != 2)
            throw new InvalidPropertiesFormatException("Invalid ip");
        return new IPComponents(rawComponentsIp[0], Integer.parseInt(rawComponentsIp[1]));
    }
}
