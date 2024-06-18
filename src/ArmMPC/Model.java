package ArmMPC;

import ArmRacos.Componet.Instance;
import ArmRacos.Method.Continue;
import ArmRacos.ObjectiveFunction.Mission;
//import Racos.ObjectiveFunction.ObjectFunction;
import ArmRacos.ObjectiveFunction.Task;
import ArmRacos.Tools.ValueArc;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    public ArrayList<Automata> automatas;
    public ArrayList<String> commands1;
    public ArrayList<String> commands2;
    File output;
    BufferedWriter bufferedWriter;
    ArrayList<ArrayList<String>>PathMap=new ArrayList<>();
    HashMap<Integer,int[]> encodeMap;
    int[] info_path;
    double[] ans;

    public Model(){

    }

    Automata findAutomata(String labelname){
        for(int i=0;i<automatas.size();i++){
            if(automatas.get(i).labelasName.equals(labelname))
                return automatas.get(i);
        }
        return null;
    }

    boolean runRacos(int autonum) {
        int samplesize = 8;       // parameter: the number of samples in each iteration
        int iteration = 5000;       // parameter: the number of iterations for batch racos
        int budget = 2000;         // parameter: the budget of sampling for sequential racos
        int positivenum = 3;       // parameter: the number of positive instances in each iteration
        double probability = 0.95; // parameter: the probability of sampling from the model
        int uncertainbit = 1;      // parameter: the number of sampled dimensions
        Instance ins = null;
        int repeat = 1;
        Task t = new Mission(autonum);
        ArrayList<Instance> result = new ArrayList<>();
        ArrayList<Instance> feasibleResult = new ArrayList<>();
        double feasibleResultAllTime = 0;
        boolean pruning = true;
        for (int i = 0; i < repeat; i++) {
            double currentT = System.currentTimeMillis();
            Continue con=new Continue(t);
            con.setMaxIteration(iteration);
            con.setSampleSize(samplesize);      // parameter: the number of samples in each iteration
            con.setBudget(budget);              // parameter: the budget of sampling
            con.setPositiveNum(positivenum);    // parameter: the number of positive instances in each iteration
            con.setRandProbability(probability);// parameter: the probability of sampling from the model
            con.setUncertainBits(uncertainbit); // parameter: the number of samplable dimensions
            ValueArc valueArc = con.run();                          // call sequential Racos              // call Racos
//            ValueArc valueArc = con.RRT();                          // call sequential Racos              // call Racos
//            ValueArc valueArc = con.monte();                          // call sequential Racos              // call Racos
//            ValueArc valueArc = con.run2();
            double currentT2 = System.currentTimeMillis();
            ins = con.getOptimal();
            System.out.println("\n【RESULT】");
            System.out.print("best function value:");
            System.out.println(ins.getValue() + "     ");
            result.add(ins);
            info_path=new int[2];
            info_path[0]=valueArc.pathinfo[2];
            info_path[1]=valueArc.pathinfo[3];

        }
        System.out.println("\n【TOTAL TIME COST】");
        return pruning;

    }

    private void createMap() {
        ArrayList<String> tmp=new ArrayList<>();
        tmp.add("fast");
        PathMap.add(tmp);//0

        tmp=new ArrayList<>();
        tmp.add("forward");
        PathMap.add(tmp);//1

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        PathMap.add(tmp);//2

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//3

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//4

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//5

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//6

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//7

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//8

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//9

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//10

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//11

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//12

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//13

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//14

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//15

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//16

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//17

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//18

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//19

        tmp=new ArrayList<>();
        tmp.add("fast");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//20

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//21

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//22

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//23

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//24

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//25

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//26

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//27

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//28

        tmp=new ArrayList<>();
        tmp.add("forward");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//29

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("fast");
        PathMap.add(tmp);//30

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("forward");
        PathMap.add(tmp);//31

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("fast");
        tmp.add("doorlike");
        PathMap.add(tmp);//32

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("fast");
        PathMap.add(tmp);//33

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("forward");
        PathMap.add(tmp);//34

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("forward");
        tmp.add("doorlike");
        PathMap.add(tmp);//35

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("fast");
        PathMap.add(tmp);//36

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("forward");
        PathMap.add(tmp);//37

        tmp=new ArrayList<>();
        tmp.add("doorlike");
        tmp.add("doorlike");
        tmp.add("doorlike");
        PathMap.add(tmp);//38

        create();
    }

    private void create(){
        encodeMap=new HashMap<>();
        int encode=0;
        for(int i=0;i< PathMap.size();i++){
            for(int j=0;j< PathMap.size();j++){
                encodeMap.put(encode,new int[]{i,j});
                encode++;
            }
        }
    }

    public static void main(String[] args) {
        int num=5;
        int time=10;
        File result=new File("result1.txt");
        try{
            BufferedWriter buffer=new BufferedWriter(new FileWriter(result));
            while(time-->0){
                System.out.println(time);
                Runtime r=Runtime.getRuntime();
                r.gc();
                long startMem=r.totalMemory()-r.freeMemory();
                Model model=new Model();
                double currentTime = System.currentTimeMillis();
                model.runRacos(num);
                double endTime = System.currentTimeMillis();
                double timecost=(endTime - currentTime) / 1000;
                System.out.println("Total Time cost :" + timecost + " seconds\n");

                long endMen=r.totalMemory()-r.freeMemory()-startMem;
                endMen=endMen/1024/1024;

                String tmp=Double.toString(timecost)+" ";
                tmp+=Integer.toString(model.info_path[0])+" ";

                tmp+= Integer.toString((int) endMen)+" ";

                tmp+=Integer.toString(model.info_path[1])+" "+Integer.toString((int) Math.pow(39,num));

                tmp+="\n";

                try {
                    buffer.write(tmp);
                } catch (IOException e) {
                    System.out.println("write to file error!");
                }

            }
            buffer.close();
        }catch (IOException e) {
            System.out.println("Open result.txt fail!");
        }

    }
}
