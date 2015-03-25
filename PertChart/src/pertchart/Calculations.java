/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pertchart;

import java.util.ArrayList;

/**
 *
 * @author zac
 */
public class Calculations {
    
    protected ArrayList<Activity> activities;
    private  final Project project;
    private final ArrayList<Activity> completedActvities =  new ArrayList();
    private final ArrayList<Activity> activitiesQueue = new ArrayList();
    
    public Calculations(double a, double m, double b, String di) {
        this.project = new Project(a, m, b, di);
        this.activities = new ArrayList();
    }
    
    public ArrayList<Activity> generateHardCodedActivities() {
        ArrayList<Activity> activityList = new ArrayList();
        Activity a = new Activity("A", 2.0, "", 1.0);
        Activity b = new Activity("B", 1.0, "A", 2.0);
        Activity c = new Activity("C", 3.0, "A,B", 1.0);
        Activity d = new Activity("D", 2.0, "B", 4.0);
        Activity e = new Activity("E", 5.0, "D", 3.0);
        activityList.add(a);
        activityList.add(b);
        activityList.add(c);
        activityList.add(d);
        activityList.add(e);
        return activityList;
    }
    
    public void run() {
        //project = new Project(15, 20, 30, "days");
        this.activities = generateHardCodedActivities();
        determinePredecessors();
        determineSuccessors();
        calculateProjectCompletionTime();
        System.out.println(getPrintedResults());
    }
    
    public Activity getActivityById(String s) {
        Activity a = null;
        for (Activity b: this.activities) {
            if (s.equalsIgnoreCase(b.getActivityId()) == false) {
               // do nothing
            }
            else {
                a = b;
                break;
            }
        }
        return a;
    }
    
    public void determinePredecessors() {
        for (Activity a: this.activities) {
                String p = a.getPredecessorsInput();
                String[] pList = p.split(",");
                for (String s: pList) {
                    if (s.equalsIgnoreCase(a.getActivityId()) == false) {
                        Activity pa = getActivityById(s);
                        if (pa != null) {
                            a.getPredecessors().add(pa);
                        }
                    }
                }
        }
    }
    
    public void determineSuccessors() {
        for (Activity a: this.activities) {
            for (Activity b: this.activities) {
                ArrayList<Activity> pList  = b.getPredecessors();
                for (Activity c: pList) {
                    if (c.getActivityId().equalsIgnoreCase(a.getActivityId())) {
                        a.getSuccessors().add(b);
                        break;
                    }
                }
            }
        }
    }
    
    public void calculateProjectCompletionTime() {
        double projectCompletionTime = 0.0;
        // push all activities that have no predecessors into the queue
        for (Activity a: this.activities) {
            ArrayList<Activity> pList  = a.getPredecessors();
            if (pList.size() > 0) {
                // do nothing
            }
            else {
                a.setEarliestStartTime(0.0);
                activitiesQueue.add(a);
            }
        }
        
        while (activitiesQueue.size() > 0) {
            Activity a = activitiesQueue.remove(0);
            double completionTime = a.getEarliestStartTime() + a.getDuration();
            a.setEarliestCompletionTime(completionTime);
            completedActvities.add(a);
            
            // check if this activities completion time is higher than the project's
            if (completionTime > projectCompletionTime) {
                projectCompletionTime = completionTime;
            }

            // push next activities into queue whose successors have finished
            for (Activity successor: a.getSuccessors()) {
                ArrayList<Activity> predecessorList = successor.getPredecessors();
                int pListL = predecessorList.size();
                int completedPredecessors = 0;
                for (int i = 0; i < pListL; i++) {
                    Activity predecessor = predecessorList.get(i);
                    if (activityHasBeenCompleted(predecessor.getActivityId())) {
                        completedPredecessors = completedPredecessors + 1;
                    }
                }

                // if all predecessors have been completed then push the successor into the queue
                if (completedPredecessors == pListL) {
                    successor.setEarliestStartTime(a.getEarliestCompletionTime());
                    activitiesQueue.add(successor);
                }
            }
        }
        //double expectedActivityTime = (project.getOptimisticTime() + (4 * project.getMostLikelyTime()) + project.getPessimisticTime()) / 6;
        //double standardDev = Math.sqrt(Math.pow(project.getPessimisticTime() - project.getOptimisticTime(), 2) / 36);
        //System.out.println("Project expected time " + expectedActivityTime);
        //System.out.println("Project standardDev time " + standardDev);
        project.setExpectedDuration(projectCompletionTime);
    }
    
    public boolean activityHasBeenCompleted(String id) {
        boolean completed = false;
        int len = completedActvities.size();
        for (int i = 0; i < len; i++) {
            Activity completedActivity = completedActvities.get(i);
            if (completedActivity.getActivityId().equalsIgnoreCase(id) == false) {
                // do nothing
            }
            else {
                completed = true;
                break;
            }
        }
        return completed;
    }
    
    public String getPrintedResults() {
        String output = "Project completion time: " + project.getExpectedDuration() + " " + project.getDurationInterval();
        return output;
    }
}
