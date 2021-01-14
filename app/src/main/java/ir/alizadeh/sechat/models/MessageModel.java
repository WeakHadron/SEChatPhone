package ir.alizadeh.sechat.models;

public class MessageModel {
    private boolean self;
    private String msg;

    public MessageModel(String msg, boolean self){
        this.msg = msg;
        this.self = self;
    }

    public boolean isSelf() {
        return self;
    }

    public String getMsg(){
        return msg;
    }
}
