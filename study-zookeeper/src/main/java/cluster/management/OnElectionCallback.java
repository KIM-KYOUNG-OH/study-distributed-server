package cluster.management;

/**
 * 이벤트 기반
 * LeaderElection의 코드 변경을 최소화
 * 생성자로 생성된 참조 값만을 통해 동작이 달라짐
 */
public interface OnElectionCallback {

    void onElectedToBeLeader();

    void onWorker();
}
