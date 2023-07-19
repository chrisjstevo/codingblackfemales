package codingblackfemales.container;

public class RunTrigger {

    private boolean shouldRun = false;

    public void triggerRun(){
        shouldRun = true;
    }

    public boolean shouldRun(){
        return shouldRun;
    }

}
