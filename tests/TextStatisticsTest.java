import service.TextStatisticsService;

public class TextStatisticsTest {
    public static void main(String[] args) {
        var s=new TextStatisticsService().calculate("Hello world");
        if(s.words()!=2 || s.characters()!=11) throw new AssertionError(s);
        System.out.println("TextStatisticsTest: PASS");
    }
}
