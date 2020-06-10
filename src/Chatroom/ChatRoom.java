package Chatroom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.Color;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ChatRoom extends Application implements MessageProcessor{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        GridPane root = fxmlLoader.load();
        ChatController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);

//        //获取参数
//        List<String> parameters = getParameters().getUnnamed();
//        if(parameters.size()!=1){
//            System.out.println("Run Configuration Parameters: MyName");
//            System.exit(-1);
//        }
//        String  myName   = parameters.get(0); //我的名字

        controller.sendButton.setOnAction(e->{
//            jButton1ActionPerformed();
            // TODO add your handling code here:
            try{
                String dest_name = controller.nameToSend.getText();
                String message = controller.msgToSend.getText();

                String serverTo = "sip:" + "SERVER" + "@" + SERVERHOST + ":" + SERVERPORT;
                siplayer.sendMessage(serverTo, "MESSAGE:" + dest_name + "\n" + username + "@" + message);
            } catch (Throwable a)
            {
                a.printStackTrace();
                insertDocument1("ERROR sending message: " + a.getMessage() + "\n", Color.red);
            }
        });

        //最小化按钮
        Image minImg = new Image("pic/min.png");
        ImageView minButtonView = new ImageView(minImg);
        controller.minButton.setGraphic(minButtonView);
        controller.minButton.setOnAction(e->{
            primaryStage.setIconified(true);
        });

        //退出按钮
        Image closeImg = new Image("pic/close.png");
        ImageView closeButtonView = new ImageView(closeImg);
        controller.closeButton.setGraphic(closeButtonView);
        controller.closeButton.setOnAction(e->{
            System.exit(0);
        });
        //显示GUI界面
        primaryStage.setTitle("ChatRoom: " );

        //设定窗口无边框
        primaryStage.initStyle(StageStyle.UNDECORATED);

        //设置窗口可拖动
        AtomicReference<Double> xOffSet = new AtomicReference<>((double) 0);
        AtomicReference<Double> yOffSet = new AtomicReference<>((double) 0);
        root.setOnMousePressed(event -> {
            xOffSet.set(event.getSceneX());
            yOffSet.set(event.getSceneY());
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffSet.get());
            primaryStage.setY(event.getScreenY() - yOffSet.get());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void jButton1ActionPerformed() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        ChatController controller = fxmlLoader.getController();
        // TODO add your handling code here:
        try{
            String dest_name = controller.nameToSend.getText();
            String message = controller.msgToSend.getText();

            String serverTo = "sip:" + "SERVER" + "@" + SERVERHOST + ":" + SERVERPORT;
            siplayer.sendMessage(serverTo, "MESSAGE:" + dest_name + "\n" + username + "@" + message);
        } catch (Throwable e)
        {
            e.printStackTrace();
            insertDocument1("ERROR sending message: " + e.getMessage() + "\n", Color.red);
        }
    }

    public void insertDocument1(String text, Color textColor)//根据传入的颜色及文字，将文字插入文本域
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        ChatController controller = fxmlLoader.getController();
        controller.msgToDisplay.setText(text);
    }

    public void insertDocument2(String text, Color textColor)//根据传入的颜色及文字，将文字插入文本域
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));

        ChatController controller = fxmlLoader.getController();
        controller.msgToDisplay.appendText(text);
    }
    public ChatRoom()
    {
        super();
        try {
            UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        String from = "sip:" + sip.getUsername() + "@" + sip.getHost() + ":" + sip.getPort();
//        this.jLabel3.setText(sip.getUsername());
//        this.jLabel5.setText(sip.getHost());
//        this.jLabel7.setText(""+sip.getPort());
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        if(args.length != 2) {
            printUsage();
            System.exit(-1);
        }

        try
        {
            username = args[0];
            int port = Integer.parseInt(args[1]);
            String ip = InetAddress.getLocalHost().getHostAddress();

            siplayer = new SipLayer(username, ip, port);
//            jainSipClient jsc = new jainSipClient(sipLayer);
//            sipLayer.setMessageProcessor(jsc);
            ChatRoom jr = new ChatRoom();
            jr.sipLayer = siplayer;
            siplayer.setMessageProcessor(jr);
//            jsc.show();

            String clientMessage = "CLIENTINFO:" + username + "@" + ip + ":" + port;
            String to = "sip:" + "SERVER" + "@" + SERVERHOST + ":" + SERVERPORT;
            siplayer.sendMessage(to, clientMessage);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        launch(args);
    }

    private static void printUsage()
    {
        System.out.println("Syntax:");
        System.out.println("  java -jar textclient.jar <username> <port>");
        System.out.println("where <username> is the nickname of this user");
        System.out.println("and <port> is the port number to use. Usually 5060 if not used by another process.");
        System.out.println("Example:");
        System.out.println("  java -jar textclient.jar snoopy71 5061");
    }



    private static String username = "alice";
    public static SipLayer sipLayer;
    public static SipLayer siplayer;
    private static final String SERVERHOST = "192.168.0.103";
    private static final int SERVERPORT = 8848;
    // End of variables declaration//GEN-END:variables
    @Override
    public void processError(String errorMessage) {
        // TODO Auto-generated method stub
        insertDocument1("ERROR: " + errorMessage + "\n", Color.red);
    }

    @Override
    public void processInfo(String infoMessage) {
        // TODO Auto-generated method stub
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ChatRoomGUI.fxml"));
        ChatController controller = fxmlLoader.getController();
        if(infoMessage.equals("WHISPER")){
            System.out.println("111111111111111");
            insertDocument1("To " + controller.nameToSend.getText() + ": " + controller.msgToSend.getText() + "\n", Color.magenta);
            //controller.msgToDisplay.setCaretPosition(jTextPane1.getDocument().getLength());
            controller.msgToSend.setText("");
        }else if(infoMessage.equals("GROUP")){
            insertDocument1("To " + "ALL" + ": " + controller.msgToSend.getText() + "\n", Color.black);
            //jTextPane1.setCaretPosition(jTextPane1.getDocument().getLength());
            controller.msgToSend.setText("");
        }else {
			/*insertDocument1("已连接服务器\n", Color.black);
			jTextPane1.setCaretPosition(jTextPane1.getDocument().getLength());
			*/
            controller.msgToSend.setText("");
            insertDocument2(infoMessage, Color.green);
            //jTextPane2.setCaretPosition(jTextPane2.getDocument().getLength());
        }
    }

    @Override
    public void processWhisperMessage(String sender, String message) {
        // TODO Auto-generated method stub
        insertDocument1("From " + sender + ": " + message + "\n", Color.magenta);
        //jTextPane1.setCaretPosition(jTextPane1.getDocument().getLength());
    }

    @Override
    public void processGroupMessage(String sender, String message) {
        // TODO Auto-generated method stub
        insertDocument1("From " + sender + ": " + message + "\n", Color.black);
        //jTextPane1.setCaretPosition(jTextPane1.getDocument().getLength());
    }

}

