package roomcord;

import static java.util.List.of;

import java.util.List;

public class CheckPatternMatching {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

//        printFirst(new ArrayList<>(of("1", "2")));
//        printFirst(new ArrayList<>(of(1, 2)));
        printFirst(of("1", "2"));
        printFirst(of(1, 2));
    }

    private static void printFirst(List<?> list) {
        if (list instanceof List<String> al) {
            String s = al.get(0);
            System.out.println(s);
        } else if (list instanceof List<Integer> al) {
            int i = al.get(0);
            System.out.println("int: " + i);
        }
    }

}
