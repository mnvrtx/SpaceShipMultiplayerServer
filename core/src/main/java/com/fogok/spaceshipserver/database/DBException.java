package com.fogok.spaceshipserver.database;

public class DBException extends Exception{
    public DBException() { super(); }
    public DBException(String message) { super(message); }
    public DBException(String message, Throwable cause) { super(message, cause); }
    public DBException(Throwable cause) { super(cause); }
}
