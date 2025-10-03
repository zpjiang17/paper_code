package com.pbft;

import com.pbft.Utils.sendUtil;
import com.pbft.Utils.timeTaskUtil;
import com.pbft.constant.Constant;
import com.pbft.constant.Varible;
import com.pbft.pojo.Node;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class pbftMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        PbftNode pbftNode1 = new PbftNode(0, "127.0.0.1", 9001, true);
        pbftNode1.start();
        PbftNode pbftNode2 = new PbftNode(1, "127.0.0.1", 9002, true);
        pbftNode2.start();
        PbftNode pbftNode3 = new PbftNode(2, "127.0.0.1", 9003, true);
        pbftNode3.start();
        PbftNode pbftNode4 = new PbftNode(3, "127.0.0.1", 9004, false);
        pbftNode4.start();
        //实际client
        PbftNode pbftNode = new PbftNode(-1, "127.0.0.1", 9000, true);
        pbftNode.start();
        if (pbftNode.getNode() == -1) {
            //请求view
            Message msgClientQuest = new Message();
            msgClientQuest.setClientPort(pbftNode.getPort());
            msgClientQuest.setClientIp(pbftNode.getIp());
            msgClientQuest.setType(Constant.GETVIEW);
            msgClientQuest.setNumber(Constant.CLIENTGETVIEW);
            msgClientQuest.setValue("请求获得集群的view");
            msgClientQuest.setOrgNode(pbftNode.getNode());

            Random random = new Random();
            List<Node> listNode = pbftNode.getNodeList();
            int i = random.nextInt(listNode.size());
            msgClientQuest.setToNode(listNode.get(i).getNode());
            sendUtil.sendNode(listNode.get(i).getIp(), listNode.get(i).getPort(), msgClientQuest);

            //使用线程专门处理重发消息
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        while(!pbftNode.getQueue().isEmpty()){
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("重发节点开始");
                            Message messageTop = pbftNode.getQueue().poll();
                            int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
                            messageTop.setToNode(mainIndex);
                            messageTop.setTime(LocalDateTime.now());
                            messageTop.setView(pbftNode.getView());
                            pbftNode.getReplyVoteList().remove(messageTop.getNumber());
                            try {

                                sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(),pbftNode.getNodeList().get(mainIndex).getPort(),messageTop);
                                timeTaskUtil.addTimeTask(messageTop.getNumber(), pbftNode, messageTop,false);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("序号为"+messageTop.getNumber()+"的消息重发开始！！！");
                        }
                    }
                }
            }).start();

//            Scanner scanner = new Scanner(System.in);
//            while (true) {
//                String value = scanner.next();
//                    Message msgClient = new Message();
//                    msgClient.setType(Constant.REQUEST);
//                    msgClient.setToNode(pbftNode.getView() % pbftNode.getNodeList().size());
//                    msgClient.setTime(LocalDateTime.now());
//                    msgClient.setOrgNode(pbftNode.getNode());
//                    msgClient.setNumber(Varible.number++);
//                    msgClient.setView(pbftNode.getView());
//                    msgClient.setTime(LocalDateTime.now());
//                    msgClient.setValue(value);
//                    msgClient.setClientIp(pbftNode.getIp());
//                    msgClient.setClientPort(pbftNode.getPort());
//                    int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
//                    sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(), pbftNode.getNodeList().get(mainIndex).getPort(), msgClient);
//                    timeTaskUtil.addTimeTask(Varible.number - 1, pbftNode, msgClient);
//                    pbftNode.getMessageValueCheckList().put(msgClient.getNumber(), value);
//                }
            for (int ii=0;ii<20;ii++){
                String value="hello"+ii;
                Message msgClient = new Message();
                msgClient.setType(Constant.REQUEST);
                msgClient.setToNode(pbftNode.getView() % pbftNode.getNodeList().size());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setOrgNode(pbftNode.getNode());
                msgClient.setNumber(Varible.number++);
                msgClient.setView(pbftNode.getView());
                msgClient.setTime(LocalDateTime.now());
                msgClient.setValue(value);
                msgClient.setClientIp(pbftNode.getIp());
                msgClient.setClientPort(pbftNode.getPort());
                int mainIndex = pbftNode.getView() % pbftNode.getNodeList().size();
                System.out.println("*********************发送hello"+ii);
                sendUtil.sendNode(pbftNode.getNodeList().get(mainIndex).getIp(), pbftNode.getNodeList().get(mainIndex).getPort(), msgClient);
                                timeTaskUtil.addTimeTask(Varible.number - 1, pbftNode, msgClient,true);
                pbftNode.getMessageValueCheckList().put(msgClient.getNumber(), value);
                Thread.sleep(30);
            }
            System.out.println(pbftNode.getQueue());

        }

        Thread.sleep(10000);
    }
}
