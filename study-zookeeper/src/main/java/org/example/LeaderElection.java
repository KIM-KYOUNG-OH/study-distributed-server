package org.example;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class LeaderElection implements Watcher {
    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        LeaderElection leaderElection = new LeaderElection();

        leaderElection.connectToZookeeper();
        leaderElection.run();
        leaderElection.close();
        System.out.println("Disconnected from Zookeeper, exiting application");
    }

    /**
     * 주키퍼 요청은 동기적으로 실행됨
     *
     */
    public void connectToZookeeper() throws IOException {
        this.zookeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    /**
     * 주키퍼는 이벤트 기반으로 동작하기 때문에 main 스레드를 대기 상태로 둠
     * 주키퍼 서버가 주기적으로 PING을 보내 서버가 정상적으로 동작하고있는지 확인함
     */
    public void run() throws InterruptedException {
        synchronized (zookeeper) {
            zookeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zookeeper.close();
    }

    /**
     * 주키퍼 서버의 별도의 이벤트가 발생할 때마다 호출됨
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    System.out.println("Successfully connected to Zookeeper");
                } else {
                    synchronized (zookeeper) {
                        System.out.println("Disconnected from Zookeeper event");
                        zookeeper.notifyAll();
                    }
                }
        }
    }
}
