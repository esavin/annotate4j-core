package annotate4j.core.runner;

import javax.management.*;


/**
 * @author Eugene Savin
 */
public class ConsoleRunner implements ConsoleRunnerMBean {

    private boolean stopFlag = false;

    public static void main(String args[]) {
        ConsoleRunner runner = new ConsoleRunner();
        MBeanServer server = MBeanServerFactory.createMBeanServer("ConsoleRunner");
        try {
            server.registerMBean(runner, new ObjectName("ConsoleRunnerAgent:name=consoleRunner"));
        } catch (MalformedObjectNameException mone) {
            mone.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
        runner.run();
    }

    public void run() {
        Runner r = new Runner();
        new Thread(r).start();

    }

    private class Runner implements Runnable {
        @Override
        public void run() {
            int counter = 0;
            synchronized (this) {
                while (!isStopFlag()) {
                    try {
                        this.wait();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    System.out.println("hello " + counter);
                    counter++;
                }
            }

        }


    }

    public synchronized void setStopFlag(boolean f) {
        stopFlag = f;
    }

    public boolean isStopFlag() {
        return stopFlag;
    }


}
