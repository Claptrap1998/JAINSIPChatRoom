package Chatroom;


public interface MessageProcessor
{
    public void processWhisperMessage(String sender, String message);
    public void processGroupMessage(String sender, String message);
    public void processError(String errorMessage);
    public void processInfo(String infoMessage);
}
