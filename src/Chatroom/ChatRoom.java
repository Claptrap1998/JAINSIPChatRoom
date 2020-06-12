package Chatroom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Color;
import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ChatRoom extends Application implements MessageProcessor {

    private static String username;
    private static SipLayer sipLayer;
    private static final String SERVERHOST = "172.17.24.113";
    private static final int SERVERPORT = 5061;
//    private static final String SERVERHOST = "39.96.223.157";
//    private static final int SERVERPORT = 7654;

    static FXMLLoader fxmlLoader;
    static GridPane root;
    static ChatController controller;

    static List<String> friendList;
    String userlist_show;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root);
//        System.out.println("start method");

        controller.nameToSend.getItems().add("所有人");
        controller.nameToSend.setValue("所有人");

        //发送按钮
        controller.sendButton.setOnAction(e -> {
            // TODO add your handling code here:
            try {
//                String dest_name = controller.nameToSend.getOnShown();
                String dest_name = "";
                if (!controller.nameToSend.getSelectionModel().getSelectedItem().toString().equals("所有人")) {
                    dest_name = controller.nameToSend.getSelectionModel().getSelectedItem().toString();
                }
                String message = controller.msgToSend.getText();

                System.out.println(dest_name + message);
                String serverTo = "sip:" + "SERVER" + "@" + SERVERHOST + ":" + SERVERPORT;
                System.out.println(serverTo);
                sipLayer.sendMessage(serverTo, "MESSAGE:" + dest_name + "\n" + username + "@" + message);
            } catch (Throwable a) {
                a.printStackTrace();
                printMessage("ERROR sending message: " + a.getMessage() + "\n");
            }
        });

//         & !friendList.contains(friend_toAdd)
        //最小化按钮
        Image minImg = new Image("pic/min.png");
        ImageView minButtonView = new ImageView(minImg);
        controller.minButton.setGraphic(minButtonView);
        controller.minButton.setOnAction(e -> {
            primaryStage.setIconified(true);
        });

        //退出按钮
        Image closeImg = new Image("pic/close.png");
        ImageView closeButtonView = new ImageView(closeImg);
        controller.closeButton.setGraphic(closeButtonView);
        controller.closeButton.setOnAction(e -> {
            System.exit(0);
        });
        //显示GUI界面
        primaryStage.setTitle("ChatRoom: "+username);

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

    public void printMessage(String text)//根据传入的颜色及文字，将文字插入文本域
    {
        controller.msgToDisplay.appendText(text);
    }

    public void printUserList(String text)//根据传入的颜色及文字，将文字插入文本域
    {
        // 用户列表去重
        String[] userlist = text.split("\n");
        List<String> list = new ArrayList<String>();
        for (String user : userlist) {
            if (!list.contains(user))
                list.add(user);
        }
        String[] userlist_final = list.toArray(new String[list.size()]);
        userlist_show = new String();
        for (String user : userlist_final) {
            userlist_show += user + "\n";
        }
        controller.data1.setText(userlist_show.replace(username,username+"(我)"));

        controller.addBtn.setOnAction(event -> {
            String friend_toAdd = controller.addInput.getText();
//            System.out.println(userlist_show);
            String[] arr = userlist_show.split("\n");
            if (Arrays.asList(arr).contains(friend_toAdd) && !friendList.contains(friend_toAdd) && !friend_toAdd.equals(username)) {
                friendList.add(friend_toAdd);
//                System.out.println(friend_toAdd);
            } else {
                Alert _alert = new Alert(Alert.AlertType.INFORMATION);
                _alert.setTitle("信息");
                _alert.setHeaderText("出错了！");
                _alert.setContentText("您输入的用户当前不在线或已经是您的好友了！");
                _alert.show();
            }
            String ss = String.join("\n", friendList);
            controller.friendShow.setText(ss.replaceAll("所有人\n", ""));

            List<String> choice = friendList;
            if (!choice.contains("所有人")) {
                choice.add(0, "所有人");
            }
            controller.nameToSend.getItems().setAll(choice);
            controller.nameToSend.setValue("所有人");

        });
    }


    public ChatRoom() throws IOException {
        fxmlLoader = new FXMLLoader(ChatRoom.class.getResource("ChatRoomGUI.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("请输入参数！");
            System.exit(-1);
        }

        try {
            username = args[0];
            int port = Integer.parseInt(args[1]);
            String ip = InetAddress.getLocalHost().getHostAddress();
            friendList = new ArrayList<String>();
            sipLayer = new SipLayer(username, ip, port);
            ChatRoom jr = new ChatRoom(sipLayer);
            sipLayer.setMessageProcessor(jr);
            String clientMessage = "CLIENTINFO:" + username + "@" + ip + ":" + port;
            String to = "sip:" + "SERVER" + "@" + SERVERHOST + ":" + SERVERPORT;
            sipLayer.sendMessage(to, clientMessage);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        launch(args);
    }


    public ChatRoom(SipLayer sip) throws IOException {
        super();
//        System.out.println("含参构造");
        sipLayer = sip;
//        System.out.println("初始化sipLayer完成");
        fxmlLoader = new FXMLLoader(ChatRoom.class.getResource("ChatRoomGUI.fxml"));
        root = fxmlLoader.load();
        controller = fxmlLoader.getController();
    }

    // End of variables declaration//GEN-END:variables
    @Override
    public void processError(String errorMessage) {
        // TODO Auto-generated method stub
        System.out.println("处理错误");
        printMessage("ERROR: " + errorMessage + "\n");
    }

    @Override
    public void processInfo(String infoMessage) {
        // TODO Auto-generated method stub

        //发送信息
        if (infoMessage.equals("WHISPER")) {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");//设置日期格式
            printMessage(df.format(new Date()));
            printMessage("\n发给" + controller.nameToSend.getSelectionModel().getSelectedItem().toString() + "的信息: \n" + controller.msgToSend.getText() + "\n\n");
            controller.msgToSend.setText("");
        } else if (infoMessage.equals("GROUP")) {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");//设置日期格式
            printMessage(df.format(new Date()));
            printMessage("\n发给所有人的信息: \n" + controller.msgToSend.getText() + "\n\n");
            controller.msgToSend.setText("");
        } else {
            System.out.println("处理用户信息");
            controller.msgToSend.setText("");
            printUserList(infoMessage);
        }
    }

    @Override
    public void processWhisperMessage(String sender, String message) {
        // TODO Auto-generated method stub
        System.out.println("处理私聊-信息");
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");//设置日期格式
        printMessage(df.format(new Date()));
        printMessage("\n来自" + sender + "的私聊信息:\n" + message + "\n\n");
    }

    @Override
    public void processGroupMessage(String sender, String message) {
        // TODO Auto-generated method stub
        System.out.println("处理群聊-信息");
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");//设置日期格式
        printMessage(df.format(new Date()));
        printMessage("\n来自" + sender + "的群聊信息:\n" + message + "\n\n");
    }

}

