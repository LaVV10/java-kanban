public class TaskId {

    private static long id;

    public static long getNewId() {
        return ++id;
    }
}
