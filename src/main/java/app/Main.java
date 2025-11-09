package app;

public class Main {
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        appBuilder.build().setVisible(true);
    }
}
