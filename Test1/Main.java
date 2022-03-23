package example;
import java.util.*;
public class Main {

    public static void main(String[] args) {

      Matcher.testMatch();
      TaskFinder.testFindTaskHavingMaxPriorityInGroup();

    }
}


class TestRunner {
    TestRunner(String name) {
        this.name = name;
    }

    interface BooleanTestCase {
        boolean run();
    }

    void expectTrue(BooleanTestCase cond) {
        try {
            if (cond.run()) {
                pass();
            }
            else {
                fail();
            }
        }
        catch (Exception e) {
            fail(e);
        }
    }

    void expectFalse(BooleanTestCase cond) {
        expectTrue(() -> !cond.run());
    }

    interface ThrowingTestCase {
        void run();
    }

    void expectException(ThrowingTestCase block) {
        try {
            block.run();
            fail();
        }
        catch (Exception e) {
            pass();
        }
    }

    private void fail() {
        System.out.printf("FAILED: Test %d of %s\n", testNo++, name);
    }

    private void fail(Exception e) {
        fail();
        e.printStackTrace(System.out);
    }

    private void pass() {
        System.out.printf("PASSED: Test %d of %s\n", testNo++, name);
    }

    private String name;
    private int testNo = 1;
}


class Matcher {
    static boolean match(String string, String pattern)  {
        if(string.length()==pattern.length())
        {
            for(int i=0;i<string.length();i++){
                char strChar = string.charAt(i);
                char patChar = pattern.charAt(i);

                    switch (patChar) {
                        case 'd':
                            if (!Character.isDigit(strChar))
                                return false;
                            break;
                        case 'a':
                            if (!Character.isLowerCase(strChar))
                                return false;
                            break;
                        case '*':
                            if (!Character.isLowerCase(strChar)){
                                if(!Character.isDigit(strChar))
                                return false;
                                }
                            break;
                        case ' ':
                            if (!Character.isWhitespace(strChar))
                                return false;
                            break;
                        default:
                            throw new RuntimeException();
                    }
            }
            return  true;
        }
        return  false;
    }

    static void testMatch() {
        TestRunner runner = new TestRunner("match");

        runner.expectFalse(() -> match("xy", "a"));
        runner.expectFalse(() -> match("x", "d"));
        runner.expectFalse(() -> match("0", "a"));
        runner.expectFalse(() -> match("*", " "));
        runner.expectFalse(() -> match(" ", "a"));

        runner.expectTrue(() -> match("01 xy", "dd aa"));
        runner.expectTrue(() -> match("1x", "**"));

        runner.expectException(() -> {
            match("x", "w");
        });
    }
}

class TaskFinder {
    static class Node {
        Node(int id, String name, Integer priority, List<Node> children) {
            this.id = id;
            this.name = name;
            this.priority = priority;
            this.children = children;
        }

        boolean isGroup() {
            return children != null;
        }

        int id;
        String name;
        Integer priority;
        List<Node> children;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return id == node.id
                    && name.equals(node.name)
                    && Objects.equals(priority, node.priority)
                    && Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, priority, children);
        }
    }

    static Node task(int id, String name, int priority) {
        return new Node(id, name, priority, null);
    }

    static  Node group(int id, String name, Node... children) {
        return new Node(id, name, null, Arrays.asList(children));
    }


    static Node tasks =
            group(0, "Все задачи",
                    group(1, "Разработка",
                            task(2, "Планирование разработок", 1),
                            task(3, "Подготовка релиза", 4),
                            task(4, "Оптимизация", 2)),
                    group(5, "Тестирование",
                            group(6, "Ручное тестирование",
                                    task(7, "Составление тест-планов", 3),
                                    task(8, "Выполнение тестов", 6)),
                            group(9, "Автоматическое тестирование",
                                    task(10, "Составление тест-планов", 3),
                                    task(11, "Написание тестов", 3))),
                    group(12, "Аналитика"));


    static void findMaxPriority(Node tasks){
            if(tasks.isGroup()){
                for(int i=0;i<tasks.children.size();i++) {
                    if(tasks.children.get(i).priority!=null) {
                        if (tasks.children.get(i).priority > maxPriority) {
                            maxPriority = tasks.children.get(i).priority;
                            maxNode = tasks.children.get(i);
                        }
                    }
                    if(tasks.children.get(i).isGroup())
                    findMaxPriority(tasks.children.get(i));
            }
            }
    }
    static  Node maxNode;
    static  int maxPriority=-1;
    static  Node findNodeById(Node tasks, int groupId){
        if(tasks.id==groupId) return tasks;
        if(tasks.isGroup()) {
            for (int i = 0; i < tasks.children.size(); i++) {
                Node result = findNodeById(tasks.children.get(i), groupId);
                if (result != null) return result;
            }
        }
        return null;
    }

    static Optional<Node> findTaskHavingMaxPriorityInGroup(Node tasks, int groupId) {
        maxNode=null;
        maxPriority=-1;
//        Поиск группы с соответствующим id
        Node result = findNodeById(tasks,groupId);
//        не удалось найти группу
      if(result==null) throw  new RuntimeException();
      if(result.priority!=null) throw  new RuntimeException();
      findMaxPriority(result);
      if(maxNode==null) return Optional.empty();
      return  Optional.of(maxNode);
    }


    static void testFindTaskHavingMaxPriorityInGroup() {
        TestRunner runner = new TestRunner("findTaskHavingMaxPriorityInGroup");

        runner.expectException(() -> findTaskHavingMaxPriorityInGroup(tasks, 13));
        runner.expectException(() -> findTaskHavingMaxPriorityInGroup(tasks, 2));

        runner.expectFalse(() -> findTaskHavingMaxPriorityInGroup(tasks, 12).isPresent());

        runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 0).get()
                .equals(task(8, "Выполнение тестов", 6)));
        runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 1).get()
                .equals(task(3, "Подготовка релиза", 4)));

        runner.expectTrue(() -> findTaskHavingMaxPriorityInGroup(tasks, 9).get().priority == 3);
    }
}
