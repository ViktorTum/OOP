package ru.nsu.tumilevich.network;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Type { CONNECT, DISCONNECT, INPUT, STATE_UPDATE }
    public Type type;
    public Object data;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
